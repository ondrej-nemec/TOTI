package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

public class PermissionsTag implements Tag {

	@Override
	public String getName() {
		return "allowed";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params) + "{";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return String.format(
			"if(authorizator.isAllowed("
				+ "toti.security.User.class.cast(variables.get(\"totiUser\")),"
				+ " \"%s\", "
				+ "toti.security.Action.valueOf(\"%s\"))"
			+ ")",
			params.get("domain"),
			params.get("action").toUpperCase()
		);
	}

}
