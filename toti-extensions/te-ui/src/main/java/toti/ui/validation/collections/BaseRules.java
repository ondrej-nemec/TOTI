package toti.ui.validation.collections;

import java.util.function.BiFunction;

import ji.translator.Translator;

public class BaseRules extends AbstractBaseRules<BaseRules> {

	public BaseRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		super(name, required, onRequiredError);
	}

	@Override
	protected BaseRules getThis() {
		return this;
	}
}
