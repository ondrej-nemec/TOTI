package toti.extension.templating.parameters;

import toti.extension.templating.TemplateResponseContainer;
import toti.templating.Parameter;

public class SrcParameter implements Parameter {

	@Override
	public String getName() {
		return "src";
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
    }

}
