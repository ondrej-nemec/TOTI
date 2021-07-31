package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class ControlTag implements Tag {

	@Override
	public String getName() {
		return "control";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		if (params.get("jsObject") != null) {
			return String.format("{"
					+ "if(variables.get(\"%s\") == null) {throw new RuntimeException(\"Missing control varialble: '%s'\");}"
					+ "toti.control.Control control=(toti.control.Control)(variables.get(\"%s\"));"
					+ "b.append(\"new Toti\"+control.getType()+\"(\"+control.toString()+\")\");"
					+ "}",
					params.get("name"),
					params.get("name"),
					params.get("name")
			);
		}
		return String.format(
				"{"
				+ "if(variables.get(\"%s\") == null) {throw new RuntimeException(\"Missing control varialble: '%s'\");}"
				+ "toti.control.Control control=(toti.control.Control)(variables.get(\"%s\"));"
				+ "b.append(\""
				+ "<script>"
				+ "new Toti\"+control.getType()+\"(\"+control.toString()+\")"
				+ ".init('%s', 'toti-\" + control.getType() + \"-%s', );"
				+ "</script>"
				+ "\");"
				+ "}"
				+ "b.append(\"<div id='%s' class='toti-control'>\");",
				params.get("name"),
				params.get("name"),
				params.get("name"),
				"div#control-" +params.get("name"),
				params.get("name"),
				"control-" +params.get("name")
		);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		if (params.get("jsObject") != null) {
			return "";
		}
		return "b.append(\"</div>\");";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params) + getPairEndCode(params);
	}

}
