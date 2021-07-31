package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class ContinueTag implements Tag {

	@Override
	public String getName() {
		return "continue";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "continue;";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params);
	}

}
