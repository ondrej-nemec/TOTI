package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class VariableDefineTag implements Tag {

	@Override
	public String getName() {
		return "var";
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
		String define = String.format("%s %s=%s;", params.get("type"), params.get("name"), params.get("value"));
		if (params.get("final") != null) {
			define = "final " + define;
		}
		return define;
	}

}
