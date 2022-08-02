package toti.templating.parameters;

import toti.templating.Parameter;

public class HrefParameter implements Parameter {

	@Override
	public String getName() {
		return "href";
	}

	@Override
	public String getCode(String value) {
		String prefix = "";
        if (value != null && value.startsWith("A:")) {
             prefix = value.substring(2, value.indexOf(":", 3));
             value = value.substring(value.indexOf(":", 3)+1);
        }
        return String.format("\"%s\" + Link.get().create(\"%s\")", prefix, value);
        // return "\"" + link.create() + "\"";
    }

}
