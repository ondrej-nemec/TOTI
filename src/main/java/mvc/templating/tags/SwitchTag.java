package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class SwitchTag implements Tag {

	@Override
	public String getName() {
		return "tag";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("switch(%s){", params.get("object"));
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
