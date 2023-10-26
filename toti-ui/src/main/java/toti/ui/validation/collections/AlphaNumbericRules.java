package toti.ui.validation.collections;

import java.util.Collection;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.rules.AllowedValuesRule;
import toti.ui.validation.rules.MaxLengthRule;
import toti.ui.validation.rules.MinLengthRule;
import toti.ui.validation.rules.RegexRule;

public class AlphaNumbericRules {
	
	private AllowedValuesRule allowedValuesRule;
	private MaxLengthRule maxLengthRule;
	private MinLengthRule minLengthRule;
	private RegexRule regexRule;
	
	public AlphaNumbericRules setRegex(String regex, Function<Translator, String> onRegexError) {
		if (this.regexRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.regexRule = new RegexRule(regex, onRegexError);
		return this;
	}
	
	public AlphaNumbericRules setAllowedValues(Collection<Object> values) {
		return setAllowedValues(values, (t)->t.translate(
			"toti.validation.value-must-be-one-of", 
			new MapInit<String, Object>().append("values", values).toMap()
		)); // "Value must be one of: " + values
	}
	
	public AlphaNumbericRules setAllowedValues(Collection<Object> values, Function<Translator, String> onAllowedValuesError) {
		if (this.allowedValuesRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.allowedValuesRule = new AllowedValuesRule(values, onAllowedValuesError);
		return this;
	}

	public AlphaNumbericRules setMinLength(int minLength) {
		return setMinLength(minLength, (t)->t.translate(
			"toti.validation.length-must-be-at-least", 
			new MapInit<String, Object>().append("minLength", minLength).toMap()
		)); // "Text length must be at least " + minLength
	}
	
	public AlphaNumbericRules setMinLength(int minLength, Function<Translator, String> onMinLengthError) {
		if (this.minLengthRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.minLengthRule = new MinLengthRule(minLength, onMinLengthError);;
		return this;
	}
	
	public AlphaNumbericRules setMaxLength(int maxLength) {
		return setMaxLength(maxLength, (t)->t.translate(
			"toti.validation.length-must-be-max", 
			new MapInit<String, Object>().append("maxLength", maxLength).toMap()
		)); // "Text length must be maximal " + maxLength
	}
	
	public AlphaNumbericRules setMaxLength(int maxLength, Function<Translator, String> onMaxLengthError) {
		if (this.maxLengthRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.maxLengthRule = new MaxLengthRule(maxLength, onMaxLengthError);
		return this;
	}

	public AlphaNumbericRules setRegex(String regex) {
		return setRegex(regex, (t)->t.translate(
			"toti.validation.text-not-match-pattern", 
			new MapInit<String, Object>().append("regex", regex.replace("\\", "\\\\")).toMap()
		)); // "Text must looks like " + regex.replace("\\", "\\\\")
	}
}
