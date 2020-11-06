package toti.templating;

import java.util.Map;

public interface Tag {

	String getName();
	
	String getPairStartCode(Map<String, String> params);
	
	String getPairEndCode(Map<String, String> params);
	
	String getNotPairCode(Map<String, String> params);
	
}
