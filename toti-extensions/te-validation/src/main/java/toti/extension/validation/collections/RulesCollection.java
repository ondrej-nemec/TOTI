package toti.extension.validation.collections;

import java.util.List;
import java.util.Optional;

import toti.extension.validation.rules.CustomValidationRule;
import toti.extension.validation.rules.Rule;

public interface RulesCollection {

	List<Rule> getRules();
	
	String getName();
	
	Optional<CustomValidationRule> getCustomValidation();
}
