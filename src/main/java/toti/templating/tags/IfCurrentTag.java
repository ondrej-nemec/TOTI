package toti.templating.tags;

import java.util.Map;

import common.exceptions.LogicException;
import toti.templating.Tag;

public class IfCurrentTag implements Tag {

	@Override
	public String getName() {
		return "ifcurrent";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params) + "{initNode(new HashMap<>());";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		StringBuilder result = new StringBuilder();
		result.append("if(");
		result.append("toti.url.Link.get()");
		String controller = params.get("controller");
		if (controller != null) {
			result.append(".setController(\"" + controller + "\")"); // TODO can be unsafe if parametrized
		}
		String method = params.get("method");
		if (method == null) {
			throw new LogicException("LinkTag: 'method' parameter is required");
		}
		result.append(".setMethod(\"" + method + "\")"); // TODO can be unsafe if parametrized
		result.append(".is(current)");
		result.append(")");
		return result.toString();
	}

}
