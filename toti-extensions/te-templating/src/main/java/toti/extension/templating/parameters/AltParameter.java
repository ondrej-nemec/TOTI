package toti.extension.templating.parameters;

import toti.extension.templating.TemplateResponseContainer;
import toti.lib.templating.Parameter;

public class AltParameter implements Parameter {

	@Override
	public String getName() {
		return "alt";
	}

	@Override
	public String getCode(String value) {
		return String.format(
			"Template.escapeHtml("
			+ TemplateResponseContainer.class.getCanonicalName()
			+ ".class.cast(container)"
			+ ".translate(\"%s\"))",
			value
		);
	}

}
