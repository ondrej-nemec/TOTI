package toti.extension.validation.collections;

import java.util.function.Function;
import java.util.function.Supplier;

import toti.application.extensions.Translator;

public class AlphaNumbericRules extends AbstractAlphaNumbericRules<AlphaNumbericRules> {

	public AlphaNumbericRules(String name, boolean required, Function<String, String> onRequiredError, Translator translator) {
		super(name, required, onRequiredError, translator);
	}

	@Override
	protected AlphaNumbericRules getThis() {
		return this;
	}

	public AlphaNumbericRules setType(Class<?> clazz) {
		return _setType(clazz);
	}
	
	public AlphaNumbericRules setType(Class<?> clazz, Supplier<String> onExpectedTypeError) {
		return _setType(clazz, onExpectedTypeError);
	}
	
	public AlphaNumbericRules setType(Class<?> clazz, boolean changeValueByType) {
		return _setType(clazz, changeValueByType);
	}
	
	public AlphaNumbericRules setType(Class<?> clazz, boolean changeValueByType, Supplier<String> onExpectedTypeError) {
		return _setType(clazz, changeValueByType, onExpectedTypeError);
	}
	
}
