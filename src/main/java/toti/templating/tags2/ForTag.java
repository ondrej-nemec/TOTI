package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class ForTag implements Tag {

	@Override
	public String getName() {
		return "for";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("for(%s;%s;%s){", params.get("from"), params.get("to"), params.get("change"));
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
