package toti.extension.templating.parameters;

import toti.application.answers.request.Identity;
import toti.application.extensions.Translator;
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
				+ Identity.class.getCanonicalName()
				+ ".class.cast(getVariable(\"totiIdentity\"))"
				+ ".getScope("
				+ Translator.class.getCanonicalName()
				+ ")"
			+ ".translate(\"%s\"))",
			value
		);
	}

}
