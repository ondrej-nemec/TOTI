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
		/*if (params.get("jsObject") != null) {
			return String.format("{"
					+ "if(variables.get(\"%s\") == null) {throw new TemplateException(\"Tag Missing control varialble: '%s'\");}"
					+ "toti.control.Control control=(toti.control.Control)(getVariable(\"%s\"));"
					+ "write(control.toString());"
					+ "}",
					params.get("name"),
					params.get("name"),
					params.get("name")
			);
		}*/
		return String.format(
				"{"
				+ "if(variables.get(\"%s\") == null) {throw new TemplateException(\"Tag Missing control varialble: '%s'\");}"
				+ "toti.control.Control control=(toti.control.Control)(getVariable(\"%s\"));"
				+ "write(\""
				+ "<script>"
				+ "\"+control.toString()+\""
				+ ".init('%s', 'toti-\" + control.getType() + \"-%s', );"
				+ "</script>"
				+ "\");"
				+ "}"
				+ "write(\"<div id='%s' class='toti-control'>\");",
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
		return "write(\"</div>\");";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params) + getPairEndCode(params);
	}

}
