package toti.ui.validation.collections;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import toti.ui.validation.ValidationItem;
import toti.ui.validation.rules.Rule;

public interface RulesCollection {

	List<Rule> getRules();
	
	String getName();
	
	Optional<String> getRename();

	Optional<Function<Object, Object>> getChangeValue();
	
	Optional<Consumer<ValidationItem>> getCustomValidation();
}
