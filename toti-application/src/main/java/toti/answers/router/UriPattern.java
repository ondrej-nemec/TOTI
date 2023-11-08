package toti.answers.router;

import toti.application.Module;

public interface UriPattern {
	
	public static final String PARAM = "[param]";
	
	default String createUri(
			Module module, Class<?> controller,
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
		return uri.toString();
	}
}
