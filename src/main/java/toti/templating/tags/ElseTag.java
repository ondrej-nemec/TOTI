package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class ElseTag implements Tag {

	@Override
	public String getName() {
		return "else";
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
		return "}else { ";
	}

}
