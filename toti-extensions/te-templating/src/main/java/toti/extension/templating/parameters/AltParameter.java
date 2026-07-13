package toti.extension.templating.parameters;

import toti.core.answers.request.Identity;
import toti.core.extensions.Translator;
import toti.lib.templating.Parameter;

public class AltParameter implements Parameter {

	@Override
	public String getName() {
		return "alt";
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
