package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;
import toti.templating.TemplateException;

public class LayoutTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "layout";
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
		StringBuilder code = new StringBuilder();
		code.append(
			"if(layout!=null){throw new TemplateException(\"There could be only one layout\");"
			+ "} else {"
		);
		if (!params.containsKey("path")) {
			throw new TemplateException("Missing parameter 'path' in 'layout' tag.");
		}
		if (params.get("module") == null) {
			code.append(String.format("layout=templateFactory.getTemplate(\"%s\");", params.get("path")));
		} else {
			code.append(String.format(
				"layout=templateFactory.getModuleTemplate(\"%s\", \"%s\");",
				params.get("path"), params.get("module")
			));
		}
		code.append("}");
		return code.toString();
		
	}

}
