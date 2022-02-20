package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class TranslateTag implements Tag {
	
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
			if (!name.equals("message") && !name.equals("variable")) {
				variables.append(String.format(".append(\"%s\", \"%s\")", name, value));
			}
		});
		variables.append(".toMap()");
		if (params.get("variable") != null) { // TODO remove it after code/variable handling
			return String.format(
				"write(Template.escapeVariable(translator.translate(%s, %s)));",
				params.get("variable"), variables.toString()
			);
		}
		return String.format(
			"write(Template.escapeVariable(translator.translate(\"%s\", %s)));",
			params.get("message"), variables.toString()
		);
	}

}
