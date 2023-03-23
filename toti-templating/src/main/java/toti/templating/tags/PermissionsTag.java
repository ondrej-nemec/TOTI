package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class PermissionsTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "allowed";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params) + "{initNode(new HashMap<>());";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return String.format(
			"if(%scontainer.isAllowed("
			+ "getVariable(\"totiIdentity\"),"
			+ "\"%s\",\"%s\""
			+ "))",
            params.containsKey("not") ? "!":"",
			params.get("domain"),
			params.get("action").toUpperCase()
		);
	}

}
