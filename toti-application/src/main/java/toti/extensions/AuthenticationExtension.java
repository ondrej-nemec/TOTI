package toti.extensions;

import java.util.Map;

public interface AuthenticationExtension extends Extension {

	// for template
	boolean isAllowed(Object identity, Map<String, Object> params);
	
	String getNotLoggedUserRedirect(String backlink);
	
}
