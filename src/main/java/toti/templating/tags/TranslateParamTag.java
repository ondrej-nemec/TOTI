package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;

@Deprecated
public class TranslateParamTag implements Tag {

	@Override
	public String getName() {
		return "param";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return String.format(
				"*/common.MapInit.t(\"%s\",%s),/*",
				params.get("key"), params.get("value")
		);
	}

}
