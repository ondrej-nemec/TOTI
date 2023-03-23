package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class VariableDefineTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		if ("type".equalsIgnoreCase(name)) {
			return TagVariableMode.CODE;
		}
		if ("name".equalsIgnoreCase(name)) {
			return TagVariableMode.STRING;
		}
		return TagVariableMode.CODE;
	}
	
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
		String name = params.get("name");
		String type = params.get("type") == null ? "Object" : params.get("type");
		// String value = params.get("value") == null ? "null" : String.format(getValue(type), params.get("value"));
		String value = params.get("value") == null ? "null" : params.get("value");
		
		String define = String.format("%s %s", type, name);
		if (params.get("final") != null) {
			define = "final " + define;
		}
		if (params.get("value") == null) {
			define = String.format("addVariable(\"class_%s\", %s.class);", name, type) + define;
		}
		return String.format("%s=(%s)%s;", define, type, value)
				+ String.format("addVariable(\"%s\", %s);", name, value);
	}
/*
	private String getValue(String type) {
		if (type.equals("String") || type.endsWith(".String")) {
			return "\"%s\"";
		}
		if (type.equals("char") || type.equals("Character") || type.endsWith(".Character")) {
			return "'%s'";
		}
		return "%s";
	}
*/
}
