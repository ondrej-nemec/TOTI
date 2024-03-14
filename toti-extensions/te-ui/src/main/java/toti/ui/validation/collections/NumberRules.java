package toti.ui.validation.collections;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.rules.MaxValueRule;
import toti.ui.validation.rules.MinValueRule;
import toti.ui.validation.rules.Rule;

public class NumberRules extends AbstractAlphaNumbericRules<NumberRules> {

	private MaxValueRule maxValueRule;
	private MinValueRule minValueRule;
	
	public NumberRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		super(name, required, onRequiredError);
	}

	public NumberRules setMaxValue(Number maxValue) {
		return setMaxValue(maxValue, (t)->t.translate(
			"toti.validation.value-must-be-less-or-equals", 
			new MapInit<String, Object>().append("maxValue", maxValue).toMap()
		)); // "Value must be less or equals " + maxValue
	}
	
	public NumberRules setMaxValue(Number maxValue, Function<Translator, String> onMaxValueError) {
		if (this.maxValueRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.maxValueRule = new MaxValueRule(maxValue, onMaxValueError);
		return this;
	}
	
	public NumberRules setMinValue(Number minValue) {
		return setMinValue(minValue, (t)->t.translate(
			"toti.validation.value-must-be-equals-or-higher", 
			new MapInit<String, Object>().append("minValue", minValue).toMap()
		)); // "Value must be equals or higher " + minValue
	}
	
	public NumberRules setMinValue(Number minValue, Function<Translator, String> onMinValueError) {
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
