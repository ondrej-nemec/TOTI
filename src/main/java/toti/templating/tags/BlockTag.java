package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class BlockTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "block";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		String name = params.get("name");
		return String.format("addBlock(\"%s\", (%sParams)->{initNode(%sParams);", name, name, name);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();});";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
