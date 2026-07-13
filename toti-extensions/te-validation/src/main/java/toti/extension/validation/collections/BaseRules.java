package toti.extension.validation.collections;

import java.util.function.Function;

import toti.core.extensions.Translator;

public class BaseRules extends AbstractBaseRules<BaseRules> {

	public BaseRules(String name, boolean required, Function<String, String> onRequiredError, Translator translator) {
		super(name, required, onRequiredError, translator);
	}

	@Override
	protected BaseRules getThis() {
		return this;
	}

}
