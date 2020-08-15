package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;
import mvc.templating.TemplateFactory;

public class LayoutTag implements Tag {
	
	private final String actualFileDir;

	public LayoutTag(String actualFile) {
		this.actualFileDir = actualFile;
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
		return "";
		/*return String.format(
				"if(layout!=null){throw new RuntimeException(\"There could be only one layout\");"
				+ "} else {layout=templateFactory.getTemplate(\"%s\");}",
				actualFileDir + "/" + params.get("path")
		);*/
		
	}

}
