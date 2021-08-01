package toti.templating.tags2;

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
               + "toti.security.Identity.class.cast("
                  + "getVariable(\"totiIdentity\")"
               + ").getUser(),"
               + " \"%s\", "
               + "toti.security.Action.valueOf(\"%s\"))"
            + ")",
			params.get("domain"),
			params.get("action").toUpperCase()
		);
	}

}
