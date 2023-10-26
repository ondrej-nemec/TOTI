package toti.ui.validation.collections;

import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.rules.MaxLengthRule;
import toti.ui.validation.rules.MinLengthRule;

public class StructureRules {
	
	private MaxLengthRule maxLengthRule;
	private MinLengthRule minLengthRule;

	public StructureRules setMinLength(int minLength) {
		return setMinLength(minLength, (t)->t.translate(
			"toti.validation.length-must-be-at-least", 
			new MapInit<String, Object>().append("minLength", minLength).toMap()
		)); // "Text length must be at least " + minLength
	}
	
	public StructureRules setMinLength(int minLength, Function<Translator, String> onMinLengthError) {
		if (this.minLengthRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.minLengthRule = new MinLengthRule(minLength, onMinLengthError);
		return this;
	}
	
	public StructureRules setMaxLength(int maxLength) {
		return setMaxLength(maxLength, (t)->t.translate(
			"toti.validation.length-must-be-max", 
			new MapInit<String, Object>().append("maxLength", maxLength).toMap()
		)); // "Text length must be maximal " + maxLength
	}
	
	public StructureRules setMaxLength(int maxLength, Function<Translator, String> onMaxLengthError) {
		if (this.maxLengthRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.maxLengthRule = new MaxLengthRule(maxLength, onMaxLengthError);
		return this;
	}
	
}
