package toti.ui.validation.collections;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.Validator;
import toti.ui.validation.rules.MaxLengthRule;
import toti.ui.validation.rules.MinLengthRule;
import toti.ui.validation.rules.Rule;
import toti.ui.validation.rules.StructureListRule;
import toti.ui.validation.rules.StructureMapRule;
import toti.ui.validation.rules.StructureSortedMapRule;

public class StructureRules extends AbstractBaseRules<StructureRules> {

	private MaxLengthRule maxLengthRule;
	private MinLengthRule minLengthRule;
	
	private StructureMapRule mapRule;
	private StructureListRule listRule;
	private StructureSortedMapRule sortedMapRule;
	
	public StructureRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		super(name, required, onRequiredError);
	}

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
	
	public StructureRules setSortedMapRule(Validator validator) {
		this.sortedMapRule = new StructureSortedMapRule(validator, (t)->"toti.validation.parameter-cannot-be-converted");
		return this;
	}
	
	public StructureRules setMapRule(Validator validator) {
		this.mapRule = new StructureMapRule(validator, (t)->"toti.validation.parameter-cannot-be-converted");
		return this;
	}
	
	public StructureRules setListRule(Validator validator) {
		this.listRule = new StructureListRule(validator, (t)->"toti.validation.parameter-cannot-be-converted");
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
