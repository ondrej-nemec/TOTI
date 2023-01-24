package toti.templating.tags;

import java.util.Map;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class LinkTag implements Tag {
	
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "link";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
        String controller = params.remove("controller");
        if (params.get("method") == null || params.get("method").isEmpty()) {
             throw new LogicException("LinkTag: 'method' parameter is required and must be non-empty");
        }
        String[] function = params.remove("method").split(":");
        String method = function[0];
		
		StringBuilder result = new StringBuilder(String.format(
			"Link.get().create(\"%s\", \"%s\", MapInit.create()", controller, method
		));
		
	    params.forEach((name, value)->{
	    	result.append(String.format(".append(\"%s\",\"%\")", name, value));
	    });
	    result.append(".toMap()");
        for (int i = 1; i < function.length; i++) {
            result.append(String.format(",\"%s\"", function[i]));
        }
		result.append(")");
        return "write(" + result.toString() + ");";
    }

}
