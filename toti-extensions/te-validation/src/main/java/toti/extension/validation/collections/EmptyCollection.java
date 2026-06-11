package toti.extension.validation.collections;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import toti.extension.validation.rules.CustomValidationRule;
import toti.extension.validation.rules.Rule;

public class EmptyCollection implements RulesCollection {

	@Override
	public List<Rule> getRules() {
		return new LinkedList<>();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Optional<CustomValidationRule> getCustomValidation() {
		return Optional.empty();
	}

}
