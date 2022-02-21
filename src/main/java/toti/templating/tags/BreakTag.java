package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

// this tag cannot be used for break switch - it is in case
public class BreakTag implements Tag {

	@Override
	public String getName() {
		return "break";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		// double flushNode - break usualy inside if
		return "if(true){flushNode();flushNode();break;}";
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
