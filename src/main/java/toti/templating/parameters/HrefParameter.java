package toti.templating.parameters;

import toti.templating.Parameter;

public class HrefParameter implements Parameter {

	@Override
	public String getName() {
		return "href";
	}

	@Override
	public String getCode(String value) {
        return String.format("Link.get().create(\"%s\")", value);
        // return "\"" + link.create() + "\"";
    }

}
