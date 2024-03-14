package toti.ui.validation.collections;

import java.util.function.BiFunction;
import java.util.function.Function;

import ji.translator.Translator;

public class AlphaNumbericRules extends AbstractAlphaNumbericRules<AlphaNumbericRules> {

	public AlphaNumbericRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		super(name, required, onRequiredError);
	}

	@Override
	protected AlphaNumbericRules getThis() {
		return this;
	}

	public AlphaNumbericRules setType(Class<?> clazz) {
		return _setType(clazz);
	}
	
	public AlphaNumbericRules setType(Class<?> clazz, Function<Translator, String> onExpectedTypeError) {
		return _setType(clazz, onExpectedTypeError);
	}
	
	public AlphaNumbericRules setType(Class<?> clazz, boolean changeValueByType) {
		return _setType(clazz, changeValueByType);
	}
	
	public AlphaNumbericRules setType(Class<?> clazz, boolean changeValueByType, Function<Translator, String> onExpectedTypeError) {
		return _setType(clazz, changeValueByType, onExpectedTypeError);
	}
	
}
