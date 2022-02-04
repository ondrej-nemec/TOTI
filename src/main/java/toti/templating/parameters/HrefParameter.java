package toti.templating.parameters;

import toti.templating.Parameter;
import toti.url.Link;

public class HrefParameter implements Parameter {

	@Override
	public String getName() {
		return "href";
	}

	@Override
	public String getCode(String value) {
		String[] values = value.split(":");
		Link link = Link.get();
		if (values.length == 1) {
			link.setMethod(values[0]);
		} else if (values.length == 2) {
			link.setController(values[0]);
			link.setMethod(values[1]);
		}
		return link.create();
	}

}
