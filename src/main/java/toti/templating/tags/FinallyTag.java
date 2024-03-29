package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class FinallyTag implements Tag {
	
	public TagVariableMode getMode(String name) {
		return TagVariableMode.NOT_SUPPORTED;
	}

	@Override
	public String getName() {
		return "finally";
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
		return "flushNode();}finally{initNode(new HashMap<>());";
	}

}
