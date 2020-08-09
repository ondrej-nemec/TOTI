package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class IfTag implements Tag {

	@Override
	public String getName() {
		return "if";
	}

	@Override
	public String getStartingCode(Map<String, String> params) {
		return String.format("if(%s){", params.get("cond"));
	}

	@Override
	public String getClosingCode(Map<String, String> params) {
		return "}";
	}

}
