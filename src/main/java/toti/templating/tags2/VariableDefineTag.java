package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

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
		String define = String.format("%s %s", params.get("type"), params.get("name"));
		if (params.get("final") != null) {
			define = "final " + define;
		}
		if (params.get("value") != null) {
			define += String.format("=%s", params.get("value"));
		}
		return define + ";"
				+ String.format("variables.put(\"%s\", %s);", params.get("name"), params.get("name"));
	}

}
