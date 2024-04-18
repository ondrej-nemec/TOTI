package toti.extension.templating.tags;

import java.util.Map;

import toti.extension.templating.TemplateResponseContainer;
import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class TranslateTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}
	
	@Override
	public String getName() {		
		return "trans";
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
		StringBuilder variables = new StringBuilder();
		variables.append("new MapInit<String, Object>()");
		params.forEach((name, value)->{
			if (!name.equals("message")/* && !name.equals("variable")*/) {
				variables.append(String.format(".append(\"%s\", \"%s\")", name, value));
			}
		});
		variables.append(".toMap()");
		return String.format(
			"write(Template.escapeVariable("
				+ TemplateResponseContainer.class.getCanonicalName()
				+ ".class.cast(container)"
				+ ".translate(\"%s\", %s)"
			+ "));",
			params.get("message"), variables.toString()
		);
	}

}
