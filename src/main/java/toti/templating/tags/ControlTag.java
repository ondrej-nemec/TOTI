package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class ControlTag implements Tag {
	
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "control";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format(
				"{"
				+ "if(variables.get(\"%s\") == null) {throw new TemplateException(\"Tag Missing control varialble: '%s'\");}"
				+ "toti.control.Control control=(toti.control.Control)(getVariable(\"%s\"));"
				+ "write(\""
				+ "<script>"
				+ "\"+control.toString().replace(\"</script>\", \"\")+\""
				+ ".init('%s', 'toti-\" + control.getType() + \"-%s');"
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
		return "write(\"</div>\");";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params) + getPairEndCode(params);
	}

}
