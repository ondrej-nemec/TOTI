package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class CatchTag implements Tag {

	@Override
	public String getName() {
		return "catch";
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
		String exception = params.get("exception") == null ? "Exception" : params.get("exception");
		return String.format("}catch(%s %s){", exception, params.get("name"));
	}

}
