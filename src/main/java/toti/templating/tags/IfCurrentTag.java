package toti.templating.tags;

import java.util.Map;

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
		result.append("true");
		String module = params.get("module");
		if (module != null) {
			result.append("&&");
			result.append("\"" + module + "\".equals(current.getModuleName())");
		}
		String controller = params.get("controller");
		if (controller != null) {
			result.append("&&");
			result.append("\"" + controller + "\".equals(current.getClassName())");
		}
		String method = params.get("method");
		if (method != null) {
			result.append("&&");
			result.append("\"" + method + "\".equals(current.getMethodName())");
		}
		result.append(")");
		return result.toString();
	}

}
