package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class IfTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.CODE;
	}

	@Override
	public String getName() {
		return "if";
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
		return String.format("if((boolean)(%s))", params.get("cond"));
	}

}
