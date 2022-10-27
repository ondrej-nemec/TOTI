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
		return "write(\"</div>\");";
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
		// TODO nebude uplne fungovat pri sdilenem tagu
		StringBuilder code = new StringBuilder("write(\"<div id='control-:name:'");
		params.forEach((k, v)->{
			switch (k) {
				case "optional":
				case "form":
				case "grid":
				case "name":
				case "id":
					break;
				default:
					code.append(String.format(" %s='%s'", k, v));
					break;
			}
		});
		code.append(" >\");");
		code.append(
			"{"
				+ "if(variables.get(\":name:\") == null && !%s) {throw new TemplateException(\"Tag Missing control variable: ':name:'\");}"
				+ "if(variables.get(\":name:\") != null) {"
					+ "toti.control.Control control=(toti.control.Control)(getVariable(\":name:\"));"
					+ "if(\"%s\".isEmpty() || control.getType().equals(\"%s\")){"
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
					+ "}"
				+ "}"
			+ "}"
		);
		return String.format(
			code.toString().replace(":name:", params.get("name")),
			params.containsKey("optional"),
			type,
			type
		);
	}
	
}
