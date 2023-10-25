package toti.ui.validation.rules;

import java.util.Optional;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;

public class StructureRules {
	
	private Optional<Integer> maxLength = Optional.empty();
	private Function<Translator, String> onMaxLengthError = (t)->"";
	private Optional<Integer> minLength = Optional.empty();
	private Function<Translator, String> onMinLengthError = (t)->"";

	public StructureRules setMinLength(int minLength) {
		return setMinLength(minLength, (t)->t.translate(
			"toti.validation.length-must-be-at-least", 
			new MapInit<String, Object>().append("minLength", minLength).toMap()
		)); // "Text length must be at least " + minLength
	}
	
	public StructureRules setMinLength(int minLength, Function<Translator, String> onMinLengthError) {
		if (this.minLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onMinLengthError = onMinLengthError;
		this.minLength = Optional.of(minLength);
		return this;
	}
	
	public StructureRules setMaxLength(int maxLength) {
		return setMaxLength(maxLength, (t)->t.translate(
			"toti.validation.length-must-be-max", 
			new MapInit<String, Object>().append("maxLength", maxLength).toMap()
		)); // "Text length must be maximal " + maxLength
	}
	
	public StructureRules setMaxLength(int maxLength, Function<Translator, String> onMaxLengthError) {
		if (this.maxLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onMaxLengthError = onMaxLengthError;
		this.maxLength = Optional.of(maxLength);
		return this;
	}
	
}
