package toti.ui.validation.collections;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import ji.translator.Translator;
import toti.ui.validation.rules.ExpectedTypeRule;
import toti.ui.validation.rules.RequiredItemRule;

public class BaseRules {
	
	protected final String name;
	
	private final RequiredItemRule requiredItemRule;
	private ExpectedTypeRule expectedRuleType;

	private Optional<String> rename = Optional.empty();
	private Function<Object, Object> changeValue = (o)->o;
	private Optional<BiFunction<Object, Translator, Set<String>>> customValidation = Optional.empty();

	// TODO je potreba provest kontrolovaci sekvenci:
	// required, expectedType, required
	// check required again - after change value, empty string can became null
	// emptry string is valid value for string, not for fe.number -> must be here
	
	public BaseRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		this.name = name;
		this.requiredItemRule = new RequiredItemRule(required, onRequiredError);
	}
	
}
