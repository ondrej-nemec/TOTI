package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class BlockTag implements Tag {

	@Override
	public String getName() {
		return "block";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("b=new StringBuilder();blocks.put(\"%s\",b);", params.get("name"));
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "b=main;";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
