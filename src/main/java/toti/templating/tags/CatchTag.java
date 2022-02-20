package toti.templating.tags;

import java.util.Map;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;

public class CatchTag implements Tag {
	
	@Override
	public boolean splitTextForVariable(String name) {
		throw new LogicException("Variable or returning code cannot be resolved to a type");
	}

	@Override
	public String getName() {
		return "catch";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		String exception = params.get("exception") == null ? "Exception" : params.get("exception");
		return String.format(
			"flushNode();}catch(%s %s){initNode(new HashMap<>());addVariable(\"%s\", %s);",
			exception, params.get("name"),
			params.get("name"), 
			params.get("name")
		);
	}

}
