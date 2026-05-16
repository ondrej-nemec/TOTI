package toti.extension.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import toti.application.extensions.Translator;
import toti.extension.validation.collections.RulesCollection;
import toti.extension.validation.rules.Rule;
import toti.lib.common.exceptions.LogicException;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.RequestParameters;

public class Validator {
	
	private final List<RulesCollection> rules;
	private final boolean strictList;
	private final Optional<RulesCollection> defaultRule;
	private final BiFunction<Translator, List<String>, String> onStrictListError;
	private Optional<GlobalFunction> globalFunc = Optional.empty();

	public Validator(boolean strictList) {
		this(strictList, (trans, params)->trans.translate(
			"toti.validation.not-expected-parameters",
			new MapInit<String, Object>().append("parameters", params).toMap()
		)); // "Not expected parameters: " + params
	}
	
	public Validator(boolean strictList, BiFunction<Translator, List<String>, String> onStrictListError) {
		this(strictList, Optional.empty(), onStrictListError);
	}
	
	public Validator(RulesCollection defaultRule) {
		this(false, Optional.of(defaultRule), (trans, params)->trans.translate(
			"toti.validation.parameter-not-match-default-rule",
			new MapInit<String, Object>().append("parameter", params).toMap()
		)); // "Parameters not match default rule: " + params
	}
	
	public Validator(RulesCollection defaultRule, BiFunction<Translator, List<String>, String> onStrictListError) {
		this(false, Optional.of(defaultRule), onStrictListError);
	}
	
	private Validator(boolean strictList, Optional<RulesCollection> defaultRule, BiFunction<Translator, List<String>, String> onStrictListError) {
		this.strictList = strictList;
		this.onStrictListError = onStrictListError;
		this.rules = new LinkedList<>();
		this.defaultRule = defaultRule;
	}
	
	/*
	@Override
	public void validate(Request request, Translator translator, Identity identity) throws RequestInterruptedException {
		getBodyValidate().validate(request, translator, identity);
	}
	
	public Validate getQueryValidate() {
		return getValidate(true);
	}
	
	public Validate getBodyValidate() {
		return getValidate(false);
	}
	
	private Validate getValidate(boolean query) {
		return (request, translator, identity)->{
			RequestParameters params = query ? new RequestParameters(request.getQueryParams().toMap()) : request.getBodyParams();
			
			ValidationResult result = validate(request, params, translator, identity);
			if (!result.isValid()) {
				throw new RequestInterruptedException(Response.create(StatusCode.BAD_REQUEST).getJson(result));
			}
		};
	}*/
	
	public Validator addRule(RulesCollection rule) {
		rules.add(rule);
		return this;
	}
	
	public Validator setGlobalFunction(GlobalFunction globalFunction) {
		if (this.globalFunc.isPresent()) {
			throw new LogicException("Global function is already set");
		}
		this.globalFunc = Optional.of(globalFunction);
		return this;
	}
	
	public ValidationResult validate(RequestParameters prop, Translator translator) {
		return validate("%s", prop, translator);
	}
	
	/** INTERNAL **/
	public ValidationResult validate(String format, RequestParameters prop, Translator translator) {
		ValidationResult result = new ValidationResult();
		List<String> names = new ArrayList<>();
		for (RulesCollection rule : rules) {
			String newName = iterateRules(format, rule.getName(), rule, prop, result, translator);
			names.add(newName);
		}
		List<String> notChecked = new ArrayList<>(prop.keySet());
		notChecked.removeAll(names);
		
		if (!notChecked.isEmpty() && strictList) {
			result.addError(onStrictListError.apply(translator, notChecked.stream().map(a->String.format(format, a)).collect(Collectors.toList())));
		}
		if (!strictList && defaultRule.isPresent()) {
			RulesCollection rule = defaultRule.get();
			for (String notCheckedName : notChecked) {
				iterateRules(format, notCheckedName, rule, prop, result, translator);
			}
		}
		if (globalFunc.isPresent() && result.isValid()) {
			globalFunc.get().apply(prop, result);
		}
		return result;
	}
	
	private String iterateRules(
			String format, String propertyName,
			RulesCollection collection, RequestParameters prop,
			ValidationResult result, Translator translator) {
		ValidationItem item = new ValidationItem(
			propertyName,
			prop.getValue(propertyName),
			result, translator
		);
		for (Rule singleRule : collection.getRules()) {
			singleRule.check(String.format(format, propertyName), propertyName, item);
			if (!item.canValidationContinue()) {
				break;
			}
		}
		if (item.canValidationContinue() && collection.getCustomValidation().isPresent()) {
			collection.getCustomValidation().get().accept(item);
		}
		if (result.isValid(propertyName) && collection.getChangeValue().isPresent()) {
			Object newValue = collection.getChangeValue().get().apply(item.getNewValue());
			// set only if origin and new value are not null
			if (newValue != null && item.getNewValue() != null) {
				item.setNewValue(newValue);
				prop.put(propertyName, newValue);
			}
		}
		String newName = collection.getRename().orElse(propertyName);
		if (prop.containsKey(propertyName)) {
			prop.remove(propertyName);
			prop.put(newName, item.getNewValue());
		}
		return newName;
	}
	
}
