package toti.templating.tags;

import java.util.Map;

import common.exceptions.LogicException;
import toti.templating.Tag;
import toti.url.Link;

public class LinkTag implements Tag {

	@Override
	public String getName() {
		return "link";
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
		Link link = Link.get();
		String controller = params.get("controller");
		if (controller != null) {
			link.setController(controller);
		}
		String method = params.get("method");
		if (method == null) {
			throw new LogicException("LinkTag: 'method' parameter is required");
		}
		link.setMethod(method);
		return "write(\"" + link.create() + "\");";
	}

}
