package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class ForTag implements Tag {

	@Override
	public String getName() {
		return "for";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		String declated = params.get("from").split(" ", 2)[1].split("=", 2)[0].trim();
		return String.format(
			"for(%s;%s;%s){initNode(new HashMap<>());"
			+ "addVariable(\"%s\",%s);", 
			params.get("from"), params.get("to"), params.get("change"),
			declated, declated
		);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
