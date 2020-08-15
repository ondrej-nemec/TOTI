package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class ElseIfTag implements Tag {

	@Override
	public String getName() {
		return "elseif";
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
		return String.format("}else if(%s){", params.get("cond"));
	}

}
