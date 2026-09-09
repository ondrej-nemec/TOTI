package toti.extension.templating.parameters;

import toti.core.answers.session.Identity;
import toti.core.extensions.Translator;
import toti.lib.templating.Parameter;

public class PlaceholderParameter implements Parameter {

	@Override
	public String getName() {
		return "placeholder";
	}

	@Override
	public String getCode(String value) {
		return String.format(
			"escapeHtml("
				+ Identity.class.getCanonicalName()
				+ ".class.cast(getVariable(\"totiIdentity\"))"
				+ ".getScope("
				+ Translator.class.getCanonicalName()
				+ ".class)"
			+ ".translate(\"%s\"))",
			value
		);
	}

}