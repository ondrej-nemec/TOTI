package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class FormError implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "error";
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
		StringBuilder code = new StringBuilder(
			String.format("write(\"<div toti-error='%s'", params.get("name"))
		);
		params.forEach((name, value)->{
			if ("name".equals(name)) {
				return;
			}
			code.append(String.format(" %s='%s'", name, value));
		});
		code.append("></div>\");");
		return code.toString();
		// return String.format("write(\"<div name='form-error-%s' class='dynamic-container-part'></div>\");", params.get("name"));
	}

}
