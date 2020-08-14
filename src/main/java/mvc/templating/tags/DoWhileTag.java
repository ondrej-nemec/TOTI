package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

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
		return String.format("}while(%s)", params.get("exp"));
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
