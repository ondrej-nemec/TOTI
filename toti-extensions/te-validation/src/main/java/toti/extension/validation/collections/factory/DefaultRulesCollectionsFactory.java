package toti.extension.validation.collections.factory;

import java.util.function.Function;

import toti.extension.validation.Validator;
import toti.extension.validation.ValidatorFactory;
import toti.extension.validation.collections.AlphaNumbericRules;
import toti.extension.validation.collections.BaseRules;
import toti.extension.validation.collections.NumberRules;
import toti.extension.validation.collections.StructureRules;

public interface DefaultRulesCollectionsFactory {

	BaseRules booleanRules();
	
	NumberRules numberRules(Class<? extends Number> clazz);
	
	AlphaNumbericRules objectRules();
	
	StructureRules mapRules(Function<ValidatorFactory, Validator> validator);
	
	StructureRules listRules(Function<ValidatorFactory, Validator> validator);
	
	StructureRules sortedMapRules(Function<ValidatorFactory, Validator> validator);

}
