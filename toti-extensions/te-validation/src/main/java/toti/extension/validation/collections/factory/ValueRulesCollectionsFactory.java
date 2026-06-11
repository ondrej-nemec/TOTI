package toti.extension.validation.collections.factory;

import java.util.function.Function;

import toti.extension.validation.Validator;
import toti.extension.validation.collections.AlphaNumbericRules;
import toti.extension.validation.collections.BaseRules;
import toti.extension.validation.collections.FileRules;
import toti.extension.validation.collections.NumberRules;
import toti.extension.validation.collections.StructureRules;

public interface ValueRulesCollectionsFactory {

	BaseRules booleanRules(String name, boolean required);

	BaseRules booleanRules(String name, boolean required, Function<String, String> onRequiredError);

	NumberRules numberRules(String name, boolean required, Class<? extends Number> clazz);

	NumberRules numberRules(String name, boolean required, Class<? extends Number> clazz, Function<String, String> onRequiredError);

	AlphaNumbericRules objectRules(String name, boolean required);

	AlphaNumbericRules objectRules(String name, boolean required, Function<String, String> onRequiredError);

	StructureRules mapRules(String name, boolean required, Validator validator);

	StructureRules mapRules(String name, boolean required, Validator validator, Function<String, String> onRequiredError);

	StructureRules listRules(String name, boolean required, Validator validator);

	StructureRules listRules(String name, boolean required, Validator validator, Function<String, String> onRequiredError);

	StructureRules sortedMapRules(String name, boolean required, Validator validator);

	StructureRules sortedMapRules(String name, boolean required, Validator validator, Function<String, String> onRequiredError);

	FileRules fileRules(String name, boolean required);

	FileRules fileRules(String name, boolean required, Function<String, String> onRequiredError);
}
