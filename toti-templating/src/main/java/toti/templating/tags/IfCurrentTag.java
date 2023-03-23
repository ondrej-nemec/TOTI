package toti.templating.tags;

import java.util.Map;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class IfCurrentTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "ifcurrent";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		StringBuilder result = new StringBuilder();
		result.append(String.format("if(%s", params.containsKey("not") ? "!":""));
		result.append("(true");
		String module = params.get("module");
		if (module != null) {
			result.append("&&");
			result.append("(\"" + module + "\").equals(container.getModuleName())");
		}
		String controller = params.get("controller");
		if (controller != null) {
			result.append("&&");
			result.append("(\"" + controller + "\").equals(container.getClassName())");
		}
		String method = params.get("method");
		if (method != null) {
			result.append("&&");
			result.append("(\"" + method + "\").equals(container.getMethodName())");
		}
		result.append(")){initNode(new HashMap<>());");
		return result.toString();
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		throw new LogicException("IfCurrent must be paired");
	}

}
