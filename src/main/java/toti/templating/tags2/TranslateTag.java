package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class TranslateTag implements Tag {
	
	@Override
	public String getName() {		
		return "trans";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		if (params.get("variable") != null) {
			return String.format(
					"b.append(Template.escapeVariable(translator.translate(%s, common.MapInit.hashMap(/*",
					params.get("variable")
			);
		}
		return String.format(
				"b.append(Template.escapeVariable(translator.translate(\"%s\", common.MapInit.hashMap(/*",
				params.get("message")
		);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return String.format("*/common.MapInit.t(\"\",null)))));"); // empty tuple for closing
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		if (params.get("variable") != null) {
			return String.format("b.append(Template.escapeVariable(translator.translate(%s)));", params.get("variable"));
		}
		return String.format("b.append(Template.escapeVariable(translator.translate(\"%s\")));", params.get("message"));
	}

}
