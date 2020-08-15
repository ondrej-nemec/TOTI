package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class IncludeTag implements Tag {

	@Override
	public String getName() {
		return "include";
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
		if (params.get("block") != null) {
			return String.format("b.append(blocks.get(\"%s\").toString());", params.get("block"));
		}
		return null; // TODO file
	}

}
