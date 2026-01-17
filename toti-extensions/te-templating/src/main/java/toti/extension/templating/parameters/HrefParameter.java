package toti.extension.templating.parameters;

import toti.extension.templating.TemplateResponseContainer;
import toti.lib.templating.Parameter;

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
        return String.format(
        	"\"%s\" + "
        	+ TemplateResponseContainer.class.getCanonicalName()
			+ ".class.cast(container)"
        	+ ".createLink(\"%s\")",
        	prefix, value
        );
        // return "\"" + link.create() + "\"";
    }

}
