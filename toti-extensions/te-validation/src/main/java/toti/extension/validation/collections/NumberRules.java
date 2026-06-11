package toti.extension.validation.collections;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import toti.application.extensions.Translator;
import toti.extension.validation.rules.MaxValueRule;
import toti.extension.validation.rules.MinValueRule;
import toti.extension.validation.rules.Rule;
import toti.lib.common.exceptions.LogicException;
import toti.lib.common.structures.MapInit;

public class NumberRules extends AbstractAlphaNumbericRules<NumberRules> {

	private MaxValueRule maxValueRule;
	private MinValueRule minValueRule;
	
	public NumberRules(String name, boolean required, Function<String, String> onRequiredError, Translator translator) {
		super(name, required, onRequiredError, translator);
	}

	public NumberRules setMaxValue(Number maxValue) {
		return setMaxValue(maxValue, ()->translator.translate(
			"toti.validation.value-must-be-less-or-equals", 
			new MapInit<String, Object>().append("maxValue", maxValue).toMap()
		)); // "Value must be less or equals " + maxValue
	}
	
	public NumberRules setMaxValue(Number maxValue, Supplier<String> onMaxValueError) {
		if (this.maxValueRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.maxValueRule = new MaxValueRule(maxValue, onMaxValueError);
		return this;
	}
	
	public NumberRules setMinValue(Number minValue) {
		return setMinValue(minValue, ()->translator.translate(
			"toti.validation.value-must-be-equals-or-higher", 
			new MapInit<String, Object>().append("minValue", minValue).toMap()
		)); // "Value must be equals or higher " + minValue
	}
	
	public NumberRules setMinValue(Number minValue, Supplier<String> onMinValueError) {
		if (this.minValueRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.minValueRule = new MinValueRule(minValue, onMinValueError);
		return this;
	}
	
	@Override
	protected NumberRules getThis() {
		return this;
	}
	
	@Override
	public List<Rule> getRules() {
		List<Rule> rules = super.getRules();
		if (minValueRule != null) {
			rules.add(minValueRule);
		}
		if (maxValueRule != null) {
			rules.add(maxValueRule);
		}
		return rules;
	}
	
}
