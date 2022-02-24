package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class FormInput implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

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
		return String.format("write(\"<div name='form-input-%s' class='dynamic-container-part'></div>\");", params.get("name"));
	}

}
