package toti.ui.validation.rules;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;

public class AlphaNumbericRules {
	
	private Optional<Collection<Object>> allowedValues = Optional.empty();
	private Function<Translator, String> onAllowedValuesError = (t)->"";
	private Optional<Integer> maxLength = Optional.empty();
	private Function<Translator, String> onMaxLengthError = (t)->"";
	private Optional<Integer> minLength = Optional.empty();
	private Function<Translator, String> onMinLengthError = (t)->"";
	
	private Optional<String> regex = Optional.empty();
	private Function<Translator, String> onRegexError = (t)->"";
	
	public AlphaNumbericRules setRegex(String regex, Function<Translator, String> onRegexError) {
		if (this.regex.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onRegexError = onRegexError;
		this.regex = Optional.of(regex);
		return this;
	}
	
	public AlphaNumbericRules setAllowedValues(Collection<Object> values) {
		return setAllowedValues(values, (t)->t.translate(
			"toti.validation.value-must-be-one-of", 
			new MapInit<String, Object>().append("values", values).toMap()
		)); // "Value must be one of: " + values
	}
	
	public AlphaNumbericRules setAllowedValues(Collection<Object> values, Function<Translator, String> onAllowedValuesError) {
		if (this.allowedValues.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onAllowedValuesError = onAllowedValuesError;
		this.allowedValues = Optional.of(values);
		return this;
	}

	public AlphaNumbericRules setMinLength(int minLength) {
		return setMinLength(minLength, (t)->t.translate(
			"toti.validation.length-must-be-at-least", 
			new MapInit<String, Object>().append("minLength", minLength).toMap()
		)); // "Text length must be at least " + minLength
	}
	
	public AlphaNumbericRules setMinLength(int minLength, Function<Translator, String> onMinLengthError) {
		if (this.minLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onMinLengthError = onMinLengthError;
		this.minLength = Optional.of(minLength);
		return this;
	}
	
	public AlphaNumbericRules setMaxLength(int maxLength) {
		return setMaxLength(maxLength, (t)->t.translate(
			"toti.validation.length-must-be-max", 
			new MapInit<String, Object>().append("maxLength", maxLength).toMap()
		)); // "Text length must be maximal " + maxLength
	}
	
	public AlphaNumbericRules setMaxLength(int maxLength, Function<Translator, String> onMaxLengthError) {
		if (this.maxLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onMaxLengthError = onMaxLengthError;
		this.maxLength = Optional.of(maxLength);
		return this;
	}

	public AlphaNumbericRules setRegex(String regex) {
		return setRegex(regex, (t)->t.translate(
			"toti.validation.text-not-match-pattern", 
			new MapInit<String, Object>().append("regex", regex.replace("\\", "\\\\")).toMap()
		)); // "Text must looks like " + regex.replace("\\", "\\\\")
	}
}
