package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class VariableSetTag implements Tag {

	@Override
	public String getName() {
		return "set";
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
		return String.format("%s=%s;", params.get("var"), params.get("value"));
	}

}
