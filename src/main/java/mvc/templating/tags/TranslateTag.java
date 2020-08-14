package mvc.templating.tags;

import java.util.Map;

import mvc.templating.Tag;

public class TranslateTag implements Tag {

	@Override
	public String getName() {		
		return "trans";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return String.format("b.append(Template.escapeVariable(translator.translate(%s, common.MapInit.MapInit.hashMap(", params.get("message"));
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return String.format("))));");
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return String.format("b.append(Template.escapeVariable(translator.translate(%s)));", params.get("message"));
	}

}
