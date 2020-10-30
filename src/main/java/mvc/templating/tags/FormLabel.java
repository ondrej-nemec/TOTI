package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class FormLabel implements Tag {

	@Override
	public String getName() {
		return "label";
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
		return String.format("b.append(\"<div id='form-label-%s'></div>\");", params.get("name"));
	}

}
