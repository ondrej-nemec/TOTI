package toti.extension.templating.parameters;

import toti.application.answers.router.Link;
import toti.extension.templating.TemplateExtension;
import toti.lib.templating.Parameter;

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
        	+ Link.class.getCanonicalName()
			+ ".class.cast(getVariable(%s))"
        	+ ".create(\"%s\")",
        	prefix, TemplateExtension.VARIABLE_NAME_LINK, value
        );
    }

}
