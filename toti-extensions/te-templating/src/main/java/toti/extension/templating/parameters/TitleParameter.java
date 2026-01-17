package toti.extension.templating.parameters;

import toti.extension.templating.TemplateResponseContainer;
import toti.lib.templating.Parameter;

public class TitleParameter implements Parameter {

	@Override
	public String getName() {
		return "title";
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
