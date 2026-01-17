package toti.lib.templating.tags;

import java.util.Map;

import toti.lib.common.exceptions.LogicException;
import toti.lib.templating.Tag;
import toti.lib.templating.TagVariableMode;

public class TryTag implements Tag {
	
	public TagVariableMode getMode(String name) {
		return TagVariableMode.NOT_SUPPORTED;
	}
	
	@Override
	public String getName() {
		return "try";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "try{initNode(new HashMap<>());";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		throw new LogicException("Try tag must be paired");
	}

}
