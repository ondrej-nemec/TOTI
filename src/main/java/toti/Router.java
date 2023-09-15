package toti;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import toti.application.register.Register;
import toti.url.MappedAction;

public class Router {

	private Map<String, MappedAction> map = new HashMap<>();
	
	private String redirectOnNotLoggedInUser = null;
	
	private CustomExceptionResponse customExceptionResponse = null;
	
	private final Register register;
	
	public Router(Register register) {
		this.register = register;
	}
	
	public <T> void addUrl(String origin, Class<T> controller, Method method) {
		map.put(origin, register.createRoutedAction(controller, method));
	}
	
	public MappedAction getUrlMapping(String url) {
		return map.get(url);
	}
	
	@Override
	public String toString() {
		return "Router:" + map.toString();
	}

	public String getRedirectOnNotLoggedInUser() {
		return redirectOnNotLoggedInUser;
	}
	
	public CustomExceptionResponse getCustomExceptionResponse() {
		return customExceptionResponse;
	}

	public void setRedirectOnNotLoggedInUser(String redirectOnNotLoggedInUser) {
		this.redirectOnNotLoggedInUser = redirectOnNotLoggedInUser;
	}
	
	public void setCustomExceptionResponse(CustomExceptionResponse customExceptionResponse) {
		this.customExceptionResponse = customExceptionResponse;
	}
	
}
