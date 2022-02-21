package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class ContinueTag implements Tag {

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
