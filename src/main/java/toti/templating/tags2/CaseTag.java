package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class CaseTag implements Tag {

	@Override
	public String getName() {
		return "case";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("/**/case %s:", params.get("cond"));
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "break;/*";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params) + getPairEndCode(params);
	}
}
