package mvc.templating;

import java.util.Map;

public interface Tag {

	String getName();
	
	String getCode(Map<String, String> params);
	
}
