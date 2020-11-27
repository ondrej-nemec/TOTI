package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class ControlTag implements Tag {

	@Override
	public String getName() {
		return "control";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format(
				"{"
				+ "toti.control.Control control=(toti.control.Control)(variables.get(\"%s\"));"
				+ "b.append(\""
				+ "<script>"
				+ "toti\"+control.getType()+\".init('%s', 'toti-\" + control.getType + \"-%s', \"+control.toString()+\");"
				+ "</script>"
				+ "\");"
				+ "}"
				+ "b.append(\"<div id='%s'>\");",
				params.get("name"),
				"div#control-" +params.get("name"),
				params.get("name"),
				"control-" +params.get("name")
		);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "b.append(\"</div>\");";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params) + getPairEndCode(params);
	}

}
