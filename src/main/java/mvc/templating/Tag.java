package mvc.templating;

import java.util.Map;

public interface Tag {

	String getName();
	
	String getStartingCode(Map<String, String> params);
	
	String getClosingCode(Map<String, String> params);
	
}
