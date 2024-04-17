package toti.validation.collections;

import java.util.function.BiFunction;

import toti.extensions.Translator;

public class BaseRules extends AbstractBaseRules<BaseRules> {

	public BaseRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		super(name, required, onRequiredError);
	}

	@Override
	protected BaseRules getThis() {
		return this;
	}
}
