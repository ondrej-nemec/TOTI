package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class WhileTag implements Tag {
	
	@Override
	public String getName() {
		return "while";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("while((boolean)(%s)){initNode(new HashMap<>());", params.get("cond"));
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
