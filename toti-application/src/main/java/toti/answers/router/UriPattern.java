package toti.answers.router;

import java.lang.reflect.Method;

import toti.application.Module;

public interface UriPattern {
	
	public static final String PARAM = "[param]";
	
	default String createUri(
			Module module, Class<?> controller, Method method,
			String moduleRoute, String controllerRoute, String actionRoute) {
		StringBuilder uri = new StringBuilder();
		if (moduleRoute != null && !moduleRoute.equals("")) {
			uri.append("/");
			uri.append(moduleRoute);
		}
		if (controllerRoute != null && !controllerRoute.equals("")) {
			uri.append("/");
			uri.append(controllerRoute);
		}
		if (actionRoute != null && !actionRoute.equals("")) {
			uri.append("/");
			uri.append(actionRoute);
		}
		for (int i = 0; i < method.getParameterCount(); i++) {
			uri.append("/");
			uri.append(PARAM);
		}
		return uri.toString();
	}
}
