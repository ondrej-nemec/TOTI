package toti.extension.validation.collections.factory;

import java.util.function.Function;

import toti.core.extensions.Translator;
import toti.extension.validation.Validator;
import toti.extension.validation.ValidatorFactory;
import toti.extension.validation.collections.AlphaNumbericRules;
import toti.extension.validation.collections.BaseRules;
import toti.extension.validation.collections.FileRules;
import toti.extension.validation.collections.NumberRules;
import toti.extension.validation.collections.StructureRules;
import toti.lib.common.structures.MapInit;

public class RulesCollectionsFactory implements DefaultRulesCollectionsFactory, ValueRulesCollectionsFactory {

	private final Translator translator;

	public RulesCollectionsFactory(Translator translator) {
		this.translator = translator;
	}

	@Override
	public BaseRules booleanRules() {
		return booleanRules("", false, getDefaultRuleError());
	}
	
	@Override
	public BaseRules booleanRules(String name, boolean required) {
		return booleanRules(name, required, getRequiredError());
	}
	
	@Override
	public BaseRules booleanRules(String name, boolean required, Function<String, String> onRequiredError) {
		BaseRules rules = new BaseRules(name, required, onRequiredError, translator);
		rules._setType(Boolean.class);
		return rules;
	}
	
	/******************************/
	
	@Override
	public NumberRules numberRules(Class<? extends Number> clazz) {
		return numberRules("", false, clazz, getDefaultRuleError());
	}
	
	@Override
	public NumberRules numberRules(String name, boolean required, Class<? extends Number> clazz) {
		return numberRules(name, required, clazz, getRequiredError());
	}
	
	@Override
	public NumberRules numberRules(String name, boolean required, Class<? extends Number> clazz, Function<String, String> onRequiredError) {
		NumberRules rules = new NumberRules(name, required, onRequiredError, translator);
		rules._setType(clazz);
		return rules;
	}
	
	/******************************/
	
	@Override
	public AlphaNumbericRules objectRules() {
		return objectRules("", false, getDefaultRuleError());
	}
	
	@Override
	public AlphaNumbericRules objectRules(String name, boolean required) {
		return objectRules(name, required, getRequiredError());
	}
	
	@Override
	public AlphaNumbericRules objectRules(String name, boolean required, Function<String, String> onRequiredError) {
		return new AlphaNumbericRules(name, required, onRequiredError, translator);
	}
	
	/******************************/
	
	@Override
	public StructureRules mapRules(Function<ValidatorFactory, Validator> validator) {
		return mapRules("", false, validator, getDefaultRuleError());
	}
	
	@Override
	public StructureRules mapRules(String name, boolean required, Function<ValidatorFactory, Validator> validator) {
		return mapRules(name, required, validator, getRequiredError());
	}
	
	@Override
	public StructureRules mapRules(String name, boolean required, Function<ValidatorFactory, Validator> validator, Function<String, String> onRequiredError) {
		return StructureRules.map(name, required, onRequiredError, translator, validator.apply(new ValidatorFactory(translator)));
	}
	
	/******************************/
	
	@Override
	public StructureRules listRules(Function<ValidatorFactory, Validator> validator) {
		return listRules("", false, validator, getDefaultRuleError());
	}
	
	@Override
	public StructureRules listRules(String name, boolean required, Function<ValidatorFactory, Validator> validator) {
		return listRules(name, required, validator, getRequiredError());
	}
	
	@Override
	public StructureRules listRules(String name, boolean required, Function<ValidatorFactory, Validator> validator, Function<String, String> onRequiredError) {
		return StructureRules.list(name, required, onRequiredError, translator, validator.apply(new ValidatorFactory(translator)));
	}
	
	/******************************/
	
	@Override
	public StructureRules sortedMapRules(Function<ValidatorFactory, Validator> validator) {
		return sortedMapRules("", false, validator, getDefaultRuleError());
	}
	
	@Override
	public StructureRules sortedMapRules(String name, boolean required, Function<ValidatorFactory, Validator> validator) {
		return sortedMapRules(name, required, validator, getRequiredError());
	}
	
	@Override
	public StructureRules sortedMapRules(String name, boolean required, Function<ValidatorFactory, Validator> validator, Function<String, String> onRequiredError) {
		return StructureRules.sortedMap(name, required, onRequiredError, translator, validator.apply(new ValidatorFactory(translator)));
	}
	
	/******************************/

	@Override
	public FileRules fileRules() {
		return fileRules("", false, getDefaultRuleError());
	}
	
	@Override
	public FileRules fileRules(String name, boolean required) {
		return fileRules(name, required, getRequiredError());
	}

	@Override
	public FileRules fileRules(String name, boolean required, Function<String, String> onRequiredError) {
		return new FileRules(name, required, onRequiredError, translator);
	}
	
	/******************************/
	
	private Function<String, String> getRequiredError() {
		return (param)->translator.translate(
			"toti.validation.item-required", 
			new MapInit<String, Object>().append("parameter", param).toMap()
		); // "This item is required: " + param
	}
	
	private Function<String, String> getDefaultRuleError() {
		return (param)->translator.translate(
			"toti.validation.parameter-not-match-default-rule",
			new MapInit<String, Object>().append("parameter", param).toMap()
		);
	}
}
