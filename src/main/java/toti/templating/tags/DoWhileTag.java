package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class DoWhileTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.CODE;
	}

	@Override
	public String getName() {
		return "dowhile";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "do{initNode(new HashMap<>());";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return String.format("flushNode();}while((boolean)(%s));", params.get("cond"));
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
