package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class FormInput implements Tag {

	@Override
	public String getName() {
		return "input";
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
		return String.format("b.append(\"<div id='form-input-%s'></div>\");", params.get("name"));
	}

}
