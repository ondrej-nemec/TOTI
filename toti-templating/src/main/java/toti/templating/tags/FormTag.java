package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class FormTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "form";
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
		String type = getType(params);
		StringBuilder code = new StringBuilder(
			String.format("write(\"<div toti-form-%s='%s'", type, params.get(type))
		);
		params.forEach((name, value)->{
			if (type.equals(name)) {
				return;
			}
			code.append(String.format(" %s='%s'", name, value));
		});
		code.append("></div>\");");
		return code.toString();
		// return String.format("write(\"<div name='form-error-%s' class='dynamic-container-part'></div>\");", params.get("name"));
	}

	private String getType(Map<String, String> params) {
		if (params.containsKey("input")) {
			return "input";
		}
		if (params.containsKey("label")) {
			return "label";
		}
		if (params.containsKey("error")) {
			return "error";
		}
		throw new RuntimeException("Form tag requires one of attributes: input, label, error");
	}

}
