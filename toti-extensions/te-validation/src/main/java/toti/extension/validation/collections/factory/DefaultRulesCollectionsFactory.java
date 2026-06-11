package toti.extension.validation.collections.factory;

import toti.extension.validation.Validator;
import toti.extension.validation.collections.AlphaNumbericRules;
import toti.extension.validation.collections.BaseRules;
import toti.extension.validation.collections.NumberRules;
import toti.extension.validation.collections.StructureRules;

public interface DefaultRulesCollectionsFactory {

	BaseRules booleanRules();
	
	NumberRules numberRules(Class<? extends Number> clazz);
	
	AlphaNumbericRules objectRules();
	
	StructureRules mapRules(Validator validator);
	
	StructureRules listRules(Validator validator);
	
	StructureRules sortedMapRules(Validator validator);

}
