package toti.templating;

import java.util.Map;

public interface TemplateContainer {
	
	String createLink(String controllerName, String methodName, Map<String, Object> getParams, Object... urlParams);

	String createLink(String url);
	
	String translate(String key);
	
	String translate(String key, Map<String, Object> variables);
	
	String getModuleName();
	
	String getClassName();
	
	String getMethodName();
	
	/*
	return String.format(
			"if(%sauthorizator.isAllowed("
               + "toti.security.Identity.class.cast("
                  + "getVariable(\"totiIdentity\")"
               + ").getUser(),"
               + " \"%s\", "
               + "toti.security.Action.valueOf(\"%s\"))"
            + ")",
            params.containsKey("not") ? "!":"",
			params.get("domain"),
			params.get("action").toUpperCase()
		);
	*/
	boolean isAllowed(Object identity, String domain, String action);
	
}
