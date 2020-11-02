package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class ControlTag implements Tag {

	@Override
	public String getName() {
		return "control";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format(
				"{"
				+ "mvc.control.Control control=(mvc.control.Control)(variables.get(\"%s\"));"
				+ "b.append(\""
				+ "<script id='pdf-ignored'>"
				+ "toti\"+control.getType()+\".init('%s', name, \"+control.toString()+\");"
				+ "</script>"
				+ "\");"
				+ "}"
				+ "b.append(\"<div id='%s'>\");",
				params.get("name"),
				"div#control-" +params.get("name"),
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
