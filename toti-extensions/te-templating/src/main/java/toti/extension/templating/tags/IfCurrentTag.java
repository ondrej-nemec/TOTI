package toti.extension.templating.tags;

import java.util.Map;

import toti.application.application.register.MappedAction;
import toti.extension.templating.TemplateExtension;
import toti.lib.common.exceptions.LogicException;
import toti.lib.templating.Tag;
import toti.lib.templating.TagVariableMode;

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
		result.append(and(params.get("module"), "getModuleName"));
		result.append(and(params.get("controller"), "getClassName"));
		result.append(and(params.get("method"), "getMethodName"));
		result.append(")){initNode(new HashMap<>());");
		return result.toString();
	}

	private String and(String urlPart, String getter) {
		if (urlPart == null) {
			return "";
		}
		return String.format(
			"&& (\"%s\").equals(%s.class.cast(getVariable(\"%s\")).%s())",
			urlPart, MappedAction.class.getCanonicalName(), TemplateExtension.VARIABLE_NAME_MAPPED_ACTION, getter
		);

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
