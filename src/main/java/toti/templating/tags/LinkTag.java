package toti.templating.tags;

import java.util.Map;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;
import toti.templating.TagVariableMode;
import toti.url.Link;

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
        Link link = Link.get();
        String controller = params.remove("controller");
        if (controller != null) {
             link.setController(controller);
        }
        String method = params.remove("method");
        if (method == null || method.isEmpty()) {
             throw new LogicException("LinkTag: 'method' parameter is required and must be non-empty");
        }
        String[] function = method.split(":");
        link.setMethod(function[0]);
        for (int i = 1; i < function.length; i++) {
             link.addUrlParam(function[i]);
        }
        params.forEach((name, value)->{
             link.addGetParam(name, value);
        });
        return "write(\"" + link.create() + "\");";
    }

}
