package toti.url;

import java.lang.reflect.Method;
import java.util.Map;

import toti.application.register.Register;

public class L2 {
	
	private final Register register;
	
	public L2( Register register) {
		this.register = register;
	}

	protected <T> String create(Class<T> controller, Method method, Map<String, Object> queryParams, Object... pathParams) {
		
		return null;
	}
	
}
