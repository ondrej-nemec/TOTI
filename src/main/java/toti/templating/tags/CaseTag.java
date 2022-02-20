package toti.templating.tags;

import java.util.Map;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;

public class CaseTag implements Tag {
	
	@Override
	public boolean splitTextForVariable(String name) {
		throw new LogicException("case expressions must be constant expressions");
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
