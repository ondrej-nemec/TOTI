package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class IncludeTag implements Tag {
	
	private final String actualFileDir;

	public IncludeTag(String actualFile) {
		this.actualFileDir = actualFile;
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
		if (params.get("block") != null) {
			return String.format("b.append(blocks.get(\"%s\").toString());", params.get("block"));
		}
		return String.format(
				"{"
				+ "Template temp = templateFactory.getTemplate(\"%s\");"
				+ "temp.getClass().getDeclaredField(\"b\").set(temp,b);"
				+ "temp.getClass().getDeclaredField(\"blocks\").set(temp,blocks);"
				+ "temp.create(templateFactory,variables,translator);"
				+ "}",
				actualFileDir + "/" + params.get("file")
		);
	}

}
