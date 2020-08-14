package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class ForEachTag implements Tag {

	@Override
	public String getName() {
		return "foreach";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("for(%s:%s){", params.get("item"), params.get("collection"));
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
