package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class ConsoleOutputTag implements Tag{

	@Override
	public String getName() {
		return "console";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		if (params.get("text") != null) {
			return "System.out.println(\"" + params.get("text") + "\");";
		}
		return "System.out.println(" + params.get("value") + ");";
	}

}
