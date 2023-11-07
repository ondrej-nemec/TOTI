package toti.answers.router;

import toti.application.Module;

public class UriPattern {
	
	public static final String PARAM = "[param]";

	// /[module]/[controller]/[method]
	// /api/[module]/[controller]/<[param]>/<[method]>
	// /[module]/[controller].[method]
	// /[module]/[controller].<[method]>
	
	
	public String createUri(
			Module module, Class<?> controller,
			String moduleRoute, String controllerRoute, String actionRoute) {
		// TODO improve
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
