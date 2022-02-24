package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class DefaultTag implements Tag {
	
	public TagVariableMode getMode(String name) {
		return TagVariableMode.NOT_SUPPORTED;
	}

	@Override
	public String getName() {
		return "default";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return "/**/default:";
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "break;/*"; // TODO will be here break???
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return getPairStartCode(params); // + getPairEndCode(params);
	}
}
