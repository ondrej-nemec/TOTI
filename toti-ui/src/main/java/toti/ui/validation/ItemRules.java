package toti.ui.validation;

import java.util.function.BiFunction;

import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.collections.AlphaNumbericRules;
import toti.ui.validation.collections.BaseRules;
import toti.ui.validation.collections.NumberRules;
import toti.ui.validation.collections.StructureRules;

public class ItemRules {
	
	public static BaseRules defaultBooleanRules() {
		return booleanRules("", false, getDefaultRuleError());
	}
	
	public static BaseRules booleanRules(String name, boolean required) {
		return booleanRules(name, required, getRequiredError());
	}
	
	public static BaseRules booleanRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		BaseRules rules = new BaseRules(name, required, onRequiredError);
		rules.setType(Boolean.class);
		return rules;
	}
	
	/******************************/
	
	public static NumberRules defaultNumberRules(Class<? extends Number> clazz) {
		return numberRules("", false, clazz, getDefaultRuleError());
	}
	
	public static NumberRules numberRules(String name, boolean required, Class<? extends Number> clazz) {
		return numberRules(name, required, clazz, getRequiredError());
	}
	
	public static NumberRules numberRules(String name, boolean required, Class<? extends Number> clazz, BiFunction<Translator, String, String> onRequiredError) {
		NumberRules rules = new NumberRules(name, required, onRequiredError);
		rules.setType(clazz);
		return rules;
	}
	
	/******************************/
	
	public static AlphaNumbericRules defaultObjectRules() {
		return objectRules("", false, getDefaultRuleError());
	}
	
	public static AlphaNumbericRules objectRules(String name, boolean required) {
		return objectRules(name, required, getRequiredError());
	}
	
	public static AlphaNumbericRules objectRules(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		return new AlphaNumbericRules(name, required, onRequiredError);
	}
	
	/******************************/
	
	public static StructureRules defaultMapRules(Validator validator) {
		return mapRules("", false, validator, getDefaultRuleError());
	}
	
	public static StructureRules mapRules(String name, boolean required, Validator validator) {
		return mapRules(name, required, validator, getRequiredError());
	}
	
	public static StructureRules mapRules(String name, boolean required, Validator validator, BiFunction<Translator, String, String> onRequiredError) {
		StructureRules rules = new StructureRules(name, required, onRequiredError);
		rules.setMapRule(validator);
		return rules;
	}
	
	/******************************/
	
	public static StructureRules defaultListRules(Validator validator) {
		return listRules("", false, validator, getDefaultRuleError());
	}
	
	public static StructureRules listRules(String name, boolean required, Validator validator) {
		return listRules(name, required, validator, getRequiredError());
	}
	
	public static StructureRules listRules(String name, boolean required, Validator validator, BiFunction<Translator, String, String> onRequiredError) {
		StructureRules rules = new StructureRules(name, required, onRequiredError);
		rules.setListRule(validator);
		return rules;
	}
	
	/******************************/
	
	public static StructureRules defaultSortedMapRules(Validator validator) {
		return sortedMapRules("", false, validator, getDefaultRuleError());
	}
	
	public static StructureRules sortedMapRules(String name, boolean required, Validator validator) {
		return sortedMapRules(name, required, validator, getRequiredError());
	}
	
	public static StructureRules sortedMapRules(String name, boolean required, Validator validator, BiFunction<Translator, String, String> onRequiredError) {
		StructureRules rules = new StructureRules(name, required, onRequiredError);
		rules.setSortedMapRule(validator);
		return rules;
	}
	
	/******************************/
	
	private static BiFunction<Translator, String, String> getRequiredError() {
		return (t, param)->t.translate(
			"toti.validation.item-required", 
			new MapInit<String, Object>().append("parameter", param).toMap()
		); // "This item is required: " + param
	}
	
	private static BiFunction<Translator, String, String> getDefaultRuleError() {
		return (t, param)->t.translate(
			"toti.validation.parameter-not-match-default-rule",
			new MapInit<String, Object>().append("parameter", param).toMap()
		);
	}
}
