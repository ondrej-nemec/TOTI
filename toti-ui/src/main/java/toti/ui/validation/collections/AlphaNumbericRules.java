package toti.ui.validation.collections;

import java.util.function.BiFunction;

import ji.translator.Translator;

public class AlphaNumbericRules extends AbstractAlphaNumbericRules<AlphaNumbericRules> {

	public AlphaNumbericRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		super(name, required, onRequiredError);
	}

	@Override
	protected AlphaNumbericRules getThis() {
		return this;
	}

}
