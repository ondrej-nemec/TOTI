package toti.templating;

import java.util.List;
import java.util.Map;

public interface Tag {

	String getName();
	
	String getPairStartCode(Map<String, String> params);
	
	String getPairEndCode(Map<String, String> params);
	
	String getNotPairCode(Map<String, String> params);
	
	default String finishPaired() {
		return "flushNode();";
	}
	
	default String initNode(Map<String, String> params, List<String> excludes) {
		StringBuilder init = new StringBuilder();
		init.append("initNode(");
		init.append("new MapInit<String, Object>()");
		params.forEach((name, value)->{
			if (!excludes.contains(name)) {
				init.append(String.format(".append(\"%s\", \"%s\")", name, value));
			}
		});
		init.append(".toMap()");
		init.append(");");
		return init.toString();
	}
	
}
