package toti.url.mock;

import static java.lang.reflect.Modifier.PRIVATE;
import static net.bytebuddy.matcher.ElementMatchers.any;

import java.lang.reflect.Method;

import org.objenesis.ObjenesisStd;

import common.structures.ObjectBuilder;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;

public class MockCreator {

	private final ObjenesisStd objenesis = new ObjenesisStd();

    public <T> T createMock(Class<T> mockTargetClass, ObjectBuilder<Method> builder) {
        ByteBuddy byteBuddy = new ByteBuddy();

        Class<? extends T> classWithInterceptor = byteBuddy.subclass(mockTargetClass)
                .method(any())
                .intercept(MethodDelegation.to(InterceptorDelegate.class))
                .defineField("interceptor", MockyInterceptor.class, PRIVATE)
                .implement(Interceptable.class)
                .intercept(FieldAccessor.ofBeanProperty())
                .make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER).getLoaded();

        T mockTargetInstance = objenesis.newInstance(classWithInterceptor);
        ((Interceptable) mockTargetInstance).setInterceptor(new MockyInterceptor(builder));
        return mockTargetInstance;
    }
	
}
