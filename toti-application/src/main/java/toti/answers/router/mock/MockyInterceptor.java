package toti.answers.router.mock;

import java.lang.reflect.Method;

import ji.common.structures.ObjectBuilder;

public class MockyInterceptor {

	private ObjectBuilder<Method> builder;

    public MockyInterceptor(ObjectBuilder<Method> builder) {
        this.builder = builder;
    }

    public Object invoke(Object mock, Method invokedMethod, Object[] arguments) {
    	builder.set(invokedMethod);
    	return invokedMethod.getDefaultValue();
    }
	
}
