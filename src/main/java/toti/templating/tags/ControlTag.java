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
		// TODO rozlisit mezi form a grid - v pripade, ze bude custom templ
		return String.format(
				"write(\"<div id='%s' class='toti-control'>\");"
				+ "{"
				+ "if(variables.get(\"%s\") == null) {throw new TemplateException(\"Tag Missing control varialble: '%s'\");}"
				+ "toti.control.Control control=(toti.control.Control)(getVariable(\"%s\"));"
				+ "write(\""
				+ "<script> {"
					+ "window.addEventListener('load', ()=> {"
						+ "try { "
							+ "\"+control.toString()+\""
							+ ".render('%s', 'toti-\" + control.getType() + \"-%s'"
							+ (useDefaultTemplate ? "" : ", false")
							+ ");"
						+ " } catch(e) { console.error(e); }"
					+ "});"
				+ "} </script>"
				+ "\");"
				+ "}",
				"control-" +params.get("name"),
				params.get("name"),
				params.get("name"),
				params.get("name"),
			//	"div#control-" + params.get("name"),
				"div#control-" + params.get("name"),
				params.get("name")
		);}
		/*return String.format(
				"write(\"<div id='%s' class='toti-control'>\");"
				+ "{"
				+ "if(variables.get(\"%s\") == null) {throw new TemplateException(\"Tag Missing control varialble: '%s'\");}"
				+ "toti.control.Control control=(toti.control.Control)(getVariable(\"%s\"));"
				+ "write(\""
				+ "<script> {"
					// observer func start
					+ "function handleControl() {"
						+ "var controlContainer = document.querySelector('%s');"
						+ "if (controlContainer === null) {"
							+ "return false;"
						+ "}"
						+ "try { "
						 	// init grid
							+ "\"+control.toString()+\""
							+ ".render('%s', 'toti-\" + control.getType() + \"-%s'"
							+ (useDefaultTemplate ? "" : ", false")
							+ ");" // control init end
						+ " } catch(e) { console.error(e); }"
						+ "return true;"
					+ "}" // observer func end
					// start observing
					+ "const observer = new MutationObserver(function (mutations, mutationInstance) {"
						+ "if (handleControl()) { "
							+ "mutationInstance.disconnect();"
						+ "}"
					+ "});"
				//	+ "if (!handleControl()) { "
						+ "observer.observe(document, { childList: true, subtree: true }); "
				//	+ "}"
				+ "} </script>"
				+ "\");"
				+ "}",
				"control-" +params.get("name"),
				params.get("name"),
				params.get("name"),
				params.get("name"),
				"div#control-" + params.get("name"),
				"div#control-" + params.get("name"),
				params.get("name")
		);}*/
	
}
