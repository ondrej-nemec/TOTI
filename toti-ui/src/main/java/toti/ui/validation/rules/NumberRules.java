package toti.ui.validation.rules;

import java.util.Optional;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;

public class NumberRules extends AlphaNumbericRules {
	
	private Optional<Number> maxValue = Optional.empty();
	private Function<Translator, String> onMaxValueError = (t)->"";
	private Optional<Number> minValue = Optional.empty();
	private Function<Translator, String> onMinValueError = (t)->"";

	public NumberRules setMaxValue(Number maxValue) {
		return setMaxValue(maxValue, (t)->t.translate(
			"toti.validation.value-must-be-less-or-equals", 
			new MapInit<String, Object>().append("maxValue", maxValue).toMap()
		)); // "Value must be less or equals " + maxValue
	}
	
	public NumberRules setMaxValue(Number maxValue, Function<Translator, String> onMaxValueError) {
		if (this.maxValue.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onMaxValueError = onMaxValueError;
		this.maxValue = Optional.of(maxValue);
		return this;
	}
	
	public NumberRules setMinValue(Number minValue) {
		return setMinValue(minValue, (t)->t.translate(
			"toti.validation.value-must-be-equals-or-higher", 
			new MapInit<String, Object>().append("minValue", minValue).toMap()
		)); // "Value must be equals or higher " + minValue
	}
	
	public NumberRules setMinValue(Number minValue, Function<Translator, String> onMinValueError) {
		if (this.minValue.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onMinValueError = onMinValueError;
		this.minValue = Optional.of(minValue);
		return this;
	}
	
}
