package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class ElseIfTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.CODE;
	}

	@Override
	public String getName() {
		return "elseif";
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
		return String.format("flushNode();}else if((boolean)(%s)){initNode(new HashMap<>());", params.get("cond"));
	}

}
