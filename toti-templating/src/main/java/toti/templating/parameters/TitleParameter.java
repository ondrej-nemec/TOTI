package toti.templating.parameters;

import toti.templating.Parameter;

public class TitleParameter implements Parameter {

	@Override
	public String getName() {
		return "title";
	}

	@Override
	public String getCode(String value) {
		return String.format("Template.escapeHtml(container.translate(\"%s\"))", value);
	}

}
