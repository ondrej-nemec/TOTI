package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class CaseTag implements Tag {

	@Override
	public String getName() {
		return "case";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "case:";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "break;";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params) + getPairEndCode(params);
	}
}
