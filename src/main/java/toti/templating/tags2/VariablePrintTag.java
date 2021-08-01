package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class VariablePrintTag implements Tag {
	
	@Override
	public String getName() {
		return "out";
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
		if (params.get("nonescape") != null) {
			return String.format("write(%s);", params.get("name")); 
		}
		return String.format("write(Template.escapeVariable(%s));", params.get("name"));
	}
		
}
