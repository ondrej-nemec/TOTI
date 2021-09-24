package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class BreakTag implements Tag {

	@Override
	public String getName() {
		return "break";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
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
