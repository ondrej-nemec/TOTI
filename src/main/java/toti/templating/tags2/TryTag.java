package toti.templating.tags2;

import java.util.Map;

import toti.templating.Tag;

public class TryTag implements Tag {
	
	@Override
	public String getName() {
		return "try";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "try{";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
