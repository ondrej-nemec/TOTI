package toti.extension.validation.collections;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import toti.application.extensions.Translator;
import toti.extension.validation.results.CustomValueValidationItem;
import toti.extension.validation.rules.CustomValidationRule;
import toti.extension.validation.rules.ExpectedTypeRule;
import toti.extension.validation.rules.RequiredItemRule;
import toti.extension.validation.rules.Rule;
import toti.lib.common.exceptions.LogicException;
import toti.lib.common.structures.MapInit;

public abstract class AbstractBaseRules<T> implements RulesCollection {
	
	private final String name;
	protected final Translator translator;
	
	private final RequiredItemRule requiredItemRule;
	private ExpectedTypeRule expectedRuleType;

	private Optional<CustomValidationRule> customValidationRule = Optional.empty();
	
	public AbstractBaseRules(String name, boolean required, Function<String, String> onRequiredError, Translator translator) {
		this.name = name;
		this.requiredItemRule = new RequiredItemRule(required, onRequiredError);
		this.translator = translator;
	}
	
	public T _setType(Class<?> clazz) {
		return _setType(clazz, ()->translator.translate(
			"toti.validation.value-type-must-be", 
			new MapInit<String, Object>().append("class", clazz.getCanonicalName()).toMap()
		)); // "Value must be " + clazz
	}
	
	public T _setType(Class<?> clazz, Supplier<String> onExpectedTypeError) {
		if (this.expectedRuleType != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.expectedRuleType = new ExpectedTypeRule(clazz, onExpectedTypeError);
		return getThis();
	}

	public T setCustomValidation(Consumer<CustomValueValidationItem> customValidation) {
		this.customValidationRule = Optional.of(new CustomValidationRule(customValidation));
		return getThis();
	}
	
	abstract protected T getThis();

	@Override
	public List<Rule> getRules() {
		List<Rule> rules = new LinkedList<>();
		if (requiredItemRule != null) {
			rules.add(requiredItemRule);
		}
		if (expectedRuleType != null) {
			rules.add(expectedRuleType);
		}
		// check required again - after change value, empty string can became null
		// emptry string is valid value for string, not for fe.number -> must be here
		if (requiredItemRule != null) {
			rules.add(requiredItemRule);
		}
		return rules;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Optional<CustomValidationRule> getCustomValidation() {
		return customValidationRule;
	}
	
}
