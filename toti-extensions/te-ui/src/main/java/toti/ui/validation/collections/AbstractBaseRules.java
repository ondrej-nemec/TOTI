package toti.ui.validation.collections;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.ValidationItem;
import toti.ui.validation.rules.ExpectedTypeRule;
import toti.ui.validation.rules.RequiredItemRule;
import toti.ui.validation.rules.Rule;

public abstract class AbstractBaseRules<T> implements RulesCollection {
	
	private final String name;
	
	private final RequiredItemRule requiredItemRule;
	private ExpectedTypeRule expectedRuleType;

	private Optional<String> rename = Optional.empty();
	private Optional<Function<Object, Object>> changeValue = Optional.empty();
	private Optional<Consumer<ValidationItem>> customValidation = Optional.empty();
	
	public AbstractBaseRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		this.name = name;
		this.requiredItemRule = new RequiredItemRule(required, onRequiredError);
	}
	
	public T renameTo(String name) {
		if (this.rename.isPresent()) {
			throw new LogicException("Rename already set to: '" + rename.get() + "'");
		}
		this.rename = Optional.of(name);
		return getThis();
	}
	
	public T changeValue(Function<Object, Object> changeValue) {
		if (this.changeValue.isPresent()) {
			throw new LogicException("Change function already set");
		}
		this.changeValue = Optional.of(changeValue);
		return getThis();
	}
	
	public T _setType(Class<?> clazz) {
		return _setType(clazz, true);
	}
	
	public T _setType(Class<?> clazz, Function<Translator, String> onExpectedTypeError) {
		return _setType(clazz, true, onExpectedTypeError);
	}
	
	public T _setType(Class<?> clazz, boolean changeValueByType) {
		return _setType(clazz, changeValueByType, (t)->t.translate(
			"toti.validation.value-type-must-be", 
			new MapInit<String, Object>().append("class", clazz).toMap()
		)); // "Value must be " + clazz
	}
	
	public T _setType(Class<?> clazz, boolean changeValueByType, Function<Translator, String> onExpectedTypeError) {
		if (this.expectedRuleType != null) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO use it ? this.changeValueByType = changeValueByType;
		this.expectedRuleType = new ExpectedTypeRule(clazz, onExpectedTypeError);
		return getThis();
	}

	public T setCustomValidation(Consumer<ValidationItem> customValidation) {
		this.customValidation = Optional.of(customValidation);
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
	public Optional<String> getRename() {
		return rename;
	}

	@Override
	public Optional<Function<Object, Object>> getChangeValue() {
		return changeValue;
	}

	@Override
	public Optional<Consumer<ValidationItem>> getCustomValidation() {
		return customValidation;
	}
	
}
