package toti.templating.parameters;

import toti.templating.Parameter;

public class AltParameter implements Parameter {

	@Override
	public String getName() {
		return "alt";
	}

	@Override
	public String getCode(String value) {
		return String.format("Template.escapeHtml(container.translate(\"%s\"))", value);
	}

}
