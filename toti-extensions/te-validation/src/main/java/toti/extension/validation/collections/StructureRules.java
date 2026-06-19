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

	private final Rule structureRule;
	
	public static StructureRules map(
		String name, boolean required, Function<String, String> onRequiredError, Translator translate, Validator validator
	) {
		return new StructureRules(
			name, required, onRequiredError, translate,
			new StructureMapRule(validator, ()->translate.translate("toti.validation.parameter-cannot-be-converted"))
		);
	}
	
	public static StructureRules list(
		String name, boolean required, Function<String, String> onRequiredError, Translator translate, Validator validator
	) {
		return new StructureRules(
			name, required, onRequiredError, translate,
			new StructureListRule(validator, ()->translate.translate("toti.validation.parameter-cannot-be-converted"))
		);
	}
	
	public static StructureRules sortedMap(
		String name, boolean required, Function<String, String> onRequiredError, Translator translate, Validator validator
	) {
		return new StructureRules(
			name, required, onRequiredError, translate,
			new StructureSortedMapRule(validator, ()->translate.translate("toti.validation.parameter-cannot-be-converted"))
		);
	}

	private StructureRules(String name, boolean required, Function<String, String> onRequiredError, Translator translate, Rule structureRule) {
		super(name, required, onRequiredError, translate);
		this.structureRule = structureRule;
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

	@Override
	protected StructureRules getThis() {
		return this;
	}
	
	@Override
	public List<Rule> getRules() {
		List<Rule> rules = super.getRules();
		rules.add(structureRule);
		if (maxLengthRule != null) {
			rules.add(maxLengthRule);
		}
		if (minLengthRule != null) {
			rules.add(minLengthRule);
		}
		return rules;
	}
	
}
