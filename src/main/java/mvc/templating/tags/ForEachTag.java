package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class ForEachTag implements Tag {

	@Override
	public String getName() {
		return "foreach";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		String[] item = params.get("item").trim().split(" +");
		if (item.length != 2) {
			throw new RuntimeException("Incorrect item");
		}
		return String.format(
				"for(%s %s:Template.toIterable(%s,%s.class)){",
				item[0], item[1], params.get("collection"), item[0]
		);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
