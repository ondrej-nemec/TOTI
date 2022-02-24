package toti.templating.tags;

import java.util.Map;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class SwitchTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.CODE;
		// throw new LogicException("case expressions must be constant expressions");
	}

	@Override
	public String getName() {
		return "switch";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("switch(%s){/*", params.get("object"));
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "/**/}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		throw new LogicException("Switch tag must be paired");
	}

}
