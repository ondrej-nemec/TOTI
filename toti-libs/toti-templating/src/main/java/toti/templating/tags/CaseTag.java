package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class CaseTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.CODE;
		// throw new LogicException("case expressions must be constant expressions");
	}

	@Override
	public String getName() {
		return "case";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("/**/case %s:", params.get("cond"));
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "break;/*";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params); // + getPairEndCode(params);
	}
}
