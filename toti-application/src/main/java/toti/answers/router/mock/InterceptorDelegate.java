package toti.answers.router.mock;

import java.lang.reflect.Method;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

public class InterceptorDelegate {

	@RuntimeType
    public static Object intercept(@This Object mock,
                                   @FieldValue("interceptor") MockyInterceptor interceptor,
                                   @Origin Method invokedMethod,
                                   @AllArguments Object[] arguments) {
        return interceptor.invoke(mock, invokedMethod, arguments);
    }
	
}
