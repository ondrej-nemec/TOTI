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
		return getTagStart(params, false);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		StringBuilder code = new StringBuilder("flushNode();});");
		code.append("getBlock(\"totiLastControl\", false)");
		code.append(".accept(new MapInit<String, Object>()");
		params.forEach((name, value)->{
			if (!name.equals("name") && !name.equals("optional") && !name.equals("form") && !name.equals("grid")) {
				code.append(String.format(".append(\"%s\", \"%s\")", name, value));
			}
		});
		code.append(".toMap());");
		return code.toString();
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getTagStart(params, true) + getPairEndCode(params);
	}

	private String getTagStart(Map<String, String> params, boolean useDefaultTemplate) {
		String type = "";
		if (params.containsKey("form")) {
			type = "Form";
		} else if (params.containsKey("grid")) {
			type = "Grid";
		}
		StringBuilder code = new StringBuilder();
		code.append("addBlock(\"totiLastControl\", (%sParams)->{");
		code.append("initNode(%sParams);");
		code.append(
			"if(variables.get(\":name:\") == null && !%s) {"
					+ "throw new TemplateException(\"Tag Missing control variable: ':name:'\");"
			+ "}"
			+ "if(variables.get(\":name:\") == null) {"
				+ "flushNode();"
				+ "return;"
			+ "}"
			+ "toti.control.Control control=(toti.control.Control)(getVariable(\":name:\"));"
			+ "if(!\"%s\".isEmpty() && !control.getType().equals(\"%s\")){"
				+ "flushNode();"
				+ "return;"
			+ "}"
			+ "write(\"<div id='control-:name:' >\");"
			+ "write(\""
				+ "<script> {"
					+ "window.addEventListener('load', ()=> {"
						+ "try { "
							+ "\"+control.toString()+\""
							+ ".render('div#control-:name:', 'toti-\" + control.getType() + \"-:name:'"
							+ (useDefaultTemplate ? "" : ", false")
							+ ");"
						+ " } catch(e) { console.error(e); }"
					+ "});"
				+ "} </script>"
			+ "\");"
		);
		String paramsName = params.get("name").replace("-", "_");
		return String.format(
			code.toString().replace(":name:", params.get("name")),
			paramsName,
			paramsName,
			params.containsKey("optional"),
			type,
			type
		);
	}
	
}
