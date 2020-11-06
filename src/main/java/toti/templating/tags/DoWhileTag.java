package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class DoWhileTag implements Tag {

	@Override
	public String getName() {
		return "dowhile";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "do{";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return String.format("}while(%s);", params.get("cond"));
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
