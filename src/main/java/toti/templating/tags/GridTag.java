package toti.templating.tags;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class GridTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "grid";
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
			String.format("write(\"<div toti-grid-%s='%s'", type, params.get(type))
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
		List<String> names = Arrays.asList(
			"filter", "checkbox", "buttons"
		);
		for (String name : names) {
			if (params.containsKey(name)) {
				return name;
			}
		}
		throw new RuntimeException("Grid tag requires one of attributes: " + names);
	}


}
