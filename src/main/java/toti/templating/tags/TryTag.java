package toti.templating.tags;

import java.util.Map;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;

public class TryTag implements Tag {
	
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
