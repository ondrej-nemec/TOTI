package toti.extension.validation.collections;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import toti.application.extensions.Translator;
import toti.extension.validation.Validator;
import toti.extension.validation.rules.MaxLengthRule;
import toti.extension.validation.rules.MinLengthRule;
import toti.extension.validation.rules.Rule;
import toti.extension.validation.rules.StructureListRule;
import toti.extension.validation.rules.StructureMapRule;
import toti.extension.validation.rules.StructureSortedMapRule;
import toti.lib.common.exceptions.LogicException;
import toti.lib.common.structures.MapInit;

public class StructureRules extends AbstractBaseRules<StructureRules> {

	private MaxLengthRule maxLengthRule;
	private MinLengthRule minLengthRule;
	
	private StructureMapRule mapRule;
	private StructureListRule listRule;
	private StructureSortedMapRule sortedMapRule;
	
	public StructureRules(String name, boolean required, Function<String, String> onRequiredError, Translator translate) {
		super(name, required, onRequiredError, translate);
	}

	public StructureRules setMinLength(int minLength) {
		return setMinLength(minLength, ()->translator.translate(
			"toti.validation.length-must-be-at-least", 
			new MapInit<String, Object>().append("minLength", minLength).toMap()
		)); // "Text length must be at least " + minLength
	}
	
	public StructureRules setMinLength(int minLength, Supplier<String> onMinLengthError) {
		if (this.minLengthRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.minLengthRule = new MinLengthRule(minLength, onMinLengthError);
		return this;
	}
	
	public StructureRules setMaxLength(int maxLength) {
		return setMaxLength(maxLength, ()->translator.translate(
			"toti.validation.length-must-be-max", 
			new MapInit<String, Object>().append("maxLength", maxLength).toMap()
		)); // "Text length must be maximal " + maxLength
	}
	
	public StructureRules setMaxLength(int maxLength, Supplier<String> onMaxLengthError) {
		if (this.maxLengthRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.maxLengthRule = new MaxLengthRule(maxLength, onMaxLengthError);
		return this;
	}
	
	public StructureRules setSortedMapRule(Validator validator) {
		this.sortedMapRule = new StructureSortedMapRule(validator, ()->translator.translate("toti.validation.parameter-cannot-be-converted"));
		return this;
	}
	
	public StructureRules setMapRule(Validator validator) {
		this.mapRule = new StructureMapRule(validator, ()->translator.translate("toti.validation.parameter-cannot-be-converted"));
		return this;
	}
	
	public StructureRules setListRule(Validator validator) {
		this.listRule = new StructureListRule(validator, ()->translator.translate("toti.validation.parameter-cannot-be-converted"));
		return this;
	}

	@Override
	protected StructureRules getThis() {
		return this;
	}
	
	@Override
	public List<Rule> getRules() {
		List<Rule> rules = super.getRules();
		if (maxLengthRule != null) {
			rules.add(maxLengthRule);
		}
		if (minLengthRule != null) {
			rules.add(minLengthRule);
		}
		if (sortedMapRule != null) {
			rules.add(sortedMapRule);
		}
		if (mapRule != null) {
			rules.add(mapRule);
		}
		if (listRule != null) {
			rules.add(listRule);
		}
		return rules;
	}
	
}
