package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

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
		return String.format("write(\"<div name='form-label-%s' class='dynamic-container-part'></div>\");", params.get("name"));
	}

}
