package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class LayoutTag implements Tag {
	/*
	private final String actualFileDir;

	public LayoutTag(String actualFile) {
		this.actualFileDir = actualFile;
	}
*/
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
			"if(layout!=null){throw new RuntimeException(\"There could be only one layout\");"
			+ "} else {"
		);
		if (!params.containsKey("path")) {
			throw new RuntimeException("Missing parameter 'path' in 'layout' tag.");
		}
		if (params.get("module") == null) {
			code.append(String.format("layout=templateFactory.getTemplate(\"%s\");", params.get("path")));
		} else {
			code.append(String.format(
				"layout=templateFactory.getModuleTemplate(\"%s\", \"%s\");",
				params.get("path"), params.get("module")
			));
		}
		code.append(
			"layout.getClass().getDeclaredField(\"b\").set(layout,b);"
			+ "layout.getClass().getDeclaredField(\"blocks\").set(layout,blocks);"
			+ "}"	
		);
		return code.toString();
		/*
		return String.format(
				"if(layout!=null){throw new RuntimeException(\"There could be only one layout\");"
				+ "} else {"
				+ "layout=templateFactory.getTemplate(\"%s\");"
				+ "layout.getClass().getDeclaredField(\"b\").set(layout,b);"
				+ "layout.getClass().getDeclaredField(\"blocks\").set(layout,blocks);"
				+ "}",
				//actualFileDir + "/" +
				 params.get("path")
		);*/
		
	}

}
