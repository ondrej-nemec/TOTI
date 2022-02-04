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
		StringBuilder code = new StringBuilder();
		code.append(String.format("%s.get()", Link.class.getCanonicalName()));
		if (values.length == 1) {
			code.append(String.format(".setMethod(\"%s\")", values[0]));
		} else if (values.length == 2) {
			code.append(String.format(".setController(\"%s\")", values[0]));
			code.append(String.format(".setMethod(\"%s\")", values[1]));
		}
		code.append(".create()");
		return code.toString();
	}

}
