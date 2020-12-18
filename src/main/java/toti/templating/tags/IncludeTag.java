package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

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
		if (params.get("block") != null) {
			StringBuilder code = new StringBuilder("{");
			code.append(String.format("Object block = blocks.get(\"%s\");", params.get("block")));
			code.append(String.format("if (block == null && %s) {", params.get("optional") == null ? "true" : "false"));
			code.append(String.format("throw new RuntimeException(\"Missing block: %s\");", params.get("block")));
			code.append("} else if (block != null) {");
			code.append("b.append(block.toString());");
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
		code.append(
				"temp.getClass().getDeclaredField(\"b\").set(temp,b);"
				+ "temp.getClass().getDeclaredField(\"blocks\").set(temp,blocks);"
				+ "temp.create(templateFactory,variables,translator);"
				+ "}"
		);
		return code.toString();
		/*
		return String.format(
				"{"
				+ "Template temp = templateFactory.getTemplate(\"%s\");"
				+ "temp.getClass().getDeclaredField(\"b\").set(temp,b);"
				+ "temp.getClass().getDeclaredField(\"blocks\").set(temp,blocks);"
				+ "temp.create(templateFactory,variables,translator);"
				+ "}",
				//actualFileDir + "/" +
				params.get("file")
		);*/
	}

}
