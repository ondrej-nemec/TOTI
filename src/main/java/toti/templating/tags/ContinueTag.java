package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class ContinueTag implements Tag {
	
	public TagVariableMode getMode(String name) {
		return TagVariableMode.NOT_SUPPORTED;
	}

	@Override
	public String getName() {
		return "continue";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		// double flushNode - continue usualy inside if
		return "if(true){flushNode();flushNode();continue;}";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params);
	}

}
