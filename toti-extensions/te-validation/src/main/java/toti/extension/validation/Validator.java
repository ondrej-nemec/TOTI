package toti.extension.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import toti.application.extensions.Translator;
import toti.extension.validation.collections.EmptyCollection;
import toti.extension.validation.collections.RulesCollection;
import toti.extension.validation.collections.factory.DefaultRulesCollectionsFactory;
import toti.extension.validation.collections.factory.RulesCollectionsFactory;
import toti.extension.validation.collections.factory.ValueRulesCollectionsFactory;
import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.CustomCollectionValidationItem;
import toti.extension.validation.results.ValidationCollection;
import toti.extension.validation.results.ValidationItem;
import toti.extension.validation.rules.Rule;
import toti.lib.common.exceptions.LogicException;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.RequestParameters;

public class Validator {
	
	private final List<RulesCollection> rules;
	private final boolean strictList;
	private final Optional<Function<DefaultRulesCollectionsFactory, RulesCollection>> defaultRule;
	private final Function<List<String>, String> onStrictListError;
	private Optional<Consumer<CustomCollectionValidationItem>> customValidation = Optional.empty();
	private final Translator translator;

	public static Validator create(boolean strictList) {
		return create(strictList, createTranslator());
	}

	public static Validator create(boolean strictList, Translator translator) {
		return create(strictList, params->translator.translate(
			"toti.validation.not-expected-parameters",
			new MapInit<String, Object>().append("parameters", params).toMap()
		), translator);
	}

	public static Validator create(boolean strictList, Function<List<String>, String> onStrictListError) {
		return create(strictList, onStrictListError, createTranslator());
	}
	
	public static Validator create(boolean strictList, Function<List<String>, String> onStrictListError, Translator translator) {
		return new Validator(strictList, Optional.empty(), onStrictListError, translator);
	}

	public static Validator create(Function<DefaultRulesCollectionsFactory, RulesCollection> defaultRule) {
		return create(defaultRule, createTranslator());
	}

	public static Validator create(Function<DefaultRulesCollectionsFactory, RulesCollection> defaultRule, Translator translator) {
		return create(defaultRule, params->translator.translate(
			"toti.validation.parameter-not-match-default-rule",
			new MapInit<String, Object>().append("parameter", params).toMap()
		), translator);
	}

	public static Validator create(Function<DefaultRulesCollectionsFactory, RulesCollection> defaultRule, Function<List<String>, String> onStrictListError) {
		return create(defaultRule, onStrictListError, createTranslator());
	}

	public static Validator create(Function<DefaultRulesCollectionsFactory, RulesCollection> defaultRule, Function<List<String>, String> onStrictListError, Translator translator) {
		return new Validator(false, Optional.of(defaultRule), onStrictListError, translator);
	}

	private static Translator createTranslator() {
		return Translator.createDefault();
	}
	
	private Validator(
		boolean strictList, Optional<Function<DefaultRulesCollectionsFactory, RulesCollection>> defaultRule,
		Function<List<String>, String> onStrictListError,
		Translator translator
	) {
		this.strictList = strictList;
		this.onStrictListError = onStrictListError;
		this.rules = new LinkedList<>();
		this.defaultRule = defaultRule;
		this.translator = translator;
	}

	public Validator addRule(Function<ValueRulesCollectionsFactory, RulesCollection> rule) {
		rules.add(rule.apply(new RulesCollectionsFactory(translator)));
		return this;
	}
	
	public Validator setCustomValidation(Consumer<CustomCollectionValidationItem> customValidation) {
		if (this.customValidation.isPresent()) {
			throw new LogicException("Global function is already set");
		}
		this.customValidation = Optional.of(customValidation);
		return this;
	}
	
	public ValidationResult validate(RequestParameters prop) {
		return _validate("%s", prop, "", "");
	}
	
	/** INTERNAL **/
	public ValidationCollection _validate(String format, RequestParameters prop, String name, String extendedName) {
		ValidationCollection result = new ValidationCollection();
		for (RulesCollection rule : rules) {
			result.addItem(iterateRules(format, rule.getName(), rule, prop.getValue(rule.getName())));
		}
		List<String> notChecked = new ArrayList<>(prop.keySet());
		notChecked.removeAll(result.getItemsNames());
		
		if (!notChecked.isEmpty()) {
			if (strictList) {
				result.addError(
					onStrictListError.apply(notChecked.stream().map(a->String.format(format, a)).collect(Collectors.toList()))
				);
			} else if (defaultRule.isPresent()) {
				RulesCollection rule = defaultRule.get().apply(new RulesCollectionsFactory(translator));
				for (String notCheckedName : notChecked) {
					result.addItem(iterateRules(format, notCheckedName, rule, prop.getValue(notCheckedName)));
				}
			} else {
				for (String notCheckedName : notChecked) {
					result.addItem(iterateRules(format, notCheckedName, new EmptyCollection(), prop.getValue(notCheckedName)));
				}
			}
		}
		if (customValidation.isPresent() && result.isValid()) {
			customValidation.get().accept(result);
		}
		return result;
	}
	
	protected ValidationItem iterateRules(
			String format, String propertyName,
			RulesCollection collection, Object rawValue) {
		ValidationItem item = new ValidationItem(propertyName, String.format(format, propertyName), rawValue);
		boolean isMoreValidationPossible = true;
		for (Rule singleRule : collection.getRules()) {
			CheckResult singleResult = singleRule.check(item);
			isMoreValidationPossible = singleResult.isMoreValidationPossible();
			if (!isMoreValidationPossible) {
				break;
			}
		}
		if (isMoreValidationPossible && collection.getCustomValidation().isPresent()) {
			collection.getCustomValidation().get().check(item);
		}
		return item;
	}
	
}
