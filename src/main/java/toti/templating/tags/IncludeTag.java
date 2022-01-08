package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TemplateException;

public class IncludeTag implements Tag {
	/*
	private final String actualFileDir;

	public IncludeTag(String actualFile) {
		this.actualFileDir = actualFile;
	}
*/
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
			StringBuilder code = new StringBuilder("{");
			code.append(String.format(
				"ThrowingConsumer<Map<String, Object>,Exception> %s = getBlock(\"%s\");",
				params.get("block"), params.get("block")
			));
			code.append(String.format("if (%s == null && %s) {", params.get("block"), params.get("optional") == null ? "true" : "false"));
			code.append(String.format("throw new TemplateException(\"Missing block: %s\");", params.get("block")));
			code.append(String.format("} else if (%s != null) {", params.get("block")));
			code.append(String.format("%s.accept(new MapInit<String, Object>()", params.get("block")));
			params.forEach((name, value)->{
				if (!name.equals("block") && !name.equals("optional")) {
					code.append(String.format(".append(\"%s\", \"%s\")", name, value));
				}
			});
			code.append(".toMap());");
			code.append("}");
			code.append("}");
			return code.toString(); // String.format("b.append(blocks.get(\"%s\").toString());", params.get("block"));
		}
		StringBuilder code = new StringBuilder("{");
		if (params.get("module") == null) {
			code.append(String.format("Template temp = templateFactory.getTemplate(\"%s\");", params.get("file")));
		} else {
			code.append(String.format(
					"Template temp = templateFactory.getModuleTemplate(\"%s\", \"%s\");",
					params.get("file"), params.get("module")
			));
		}
		code.append("temp._create(templateFactory,variables,translator, authorizator, this.nodes,current);" + "}");
		return code.toString();
	}

}
