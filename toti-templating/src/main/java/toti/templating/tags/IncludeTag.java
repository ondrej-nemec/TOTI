package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;
import toti.templating.TemplateException;

public class IncludeTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}
	
	@Override
	public String getName() {
		return "include";
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
		if (!params.containsKey("block") && !params.containsKey("file")) {
			throw new TemplateException("Tag 'include' has to contains parameter 'block' or 'file'");
		}
		if (params.get("block") != null) {
			StringBuilder code = new StringBuilder();
			code.append(String.format(
				"getBlock(\"%s\",%s)", 
				params.get("block"),
				params.get("optional") == null ? "true" : "false"
			));
			code.append(".accept(new MapInit<String, Object>()");
			params.forEach((name, value)->{
				if (!name.equals("block") && !name.equals("optional")) {
					code.append(String.format(".append(\"%s\", \"%s\")", name, value));
				}
			});
			code.append(".toMap());");
			return code.toString();
		}
		StringBuilder code = new StringBuilder("{");
        code.append("initNode(new MapInit<String, Object>()");
        params.forEach((name, value)->{
             if (!name.equals("file") && !name.equals("module")) {
                  code.append(String.format(".append(\"%s\", \"%s\")", name, value));
             }
        });
        code.append(".toMap());");
		if (params.get("module") == null) {
			code.append(String.format("Template temp = templateFactory.getTemplate(\"%s\");", params.get("file")));
		} else {
			code.append(String.format(
					"Template temp = templateFactory.getModuleTemplate(\"%s\", \"%s\");",
					params.get("file"), params.get("module")
			));
		}
		code.append("temp._create(templateFactory,variables,container,this.nodes,this.hashCode());");
        code.append("flushNode();");
		code.append("}");
		return code.toString();
	}

}
