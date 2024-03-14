package toti.ui.validation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.Validate;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.ui.validation.collections.RulesCollection;
import toti.ui.validation.rules.Rule;

public class Validator implements Validate {
	
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
	}
	
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
	
	public ValidationResult validate(Request request, RequestParameters prop, Translator translator, Identity identity) {
		return validate(request, "%s", prop, translator, identity);
	}
	
	/** INTERNAL */
	public ValidationResult validate(Request request, String format, RequestParameters prop, Translator translator, Identity identity) {
		ValidationResult result = new ValidationResult();
		List<String> names = new ArrayList<>();
		rules.forEach((rule)->{
			String newName = iterateRules(request, format, rule.getName(), rule, prop, result, translator, identity);
			
			/*
			Object newValue = rule.getChangeValue().apply(item.getNewValue());
			if (newValue != null) {
				prop.remove(rule.getName());
				prop.put(newName, newValue);
			} else if (rule.getRename().isPresent() && prop.containsKey(rule.getName())) {
				prop.put(newName, prop.remove(rule.getName()));
			}*/
			names.add(newName);
		});
		List<String> notChecked = new ArrayList<>(prop.keySet());
		notChecked.removeAll(names);
		
		if (notChecked.size() > 0 && strictList) {
			result.addError(onStrictListError.apply(translator, notChecked.stream().map(a->String.format(format, a)).collect(Collectors.toList())));
		}
		/*checkRule(
				Optional.of(notChecked),
				(incomingData)->{
					return incomingData.size() > 0 && strictList;
				},
				errors,
				"form",
				onStrictListError.apply(translator, notChecked.stream().map(a->String.format(format, a)).collect(Collectors.toList()))
		);*/
		if (!strictList && defaultRule.isPresent()) {
			RulesCollection rule = defaultRule.get();
			notChecked.forEach((notCheckedName)->{
				iterateRules(request, format, notCheckedName, rule, prop, result, translator, identity);
				/*swichRules(String.format(format, notCheckedName), notCheckedName, rule, errors, prop, translator);
				Object newValue = rule.getChangeValue().apply(prop.get(notCheckedName));
				if (newValue != null) {
					prop.put(notCheckedName, newValue);
				}*/
			});
		}
		if (globalFunc.isPresent() && result.isValid()) {
			globalFunc.get().apply(request, prop, result, translator, identity);
		}
		return result;
	}
	
	private String iterateRules(
			Request request, String format, String propertyName,
			RulesCollection collection, RequestParameters prop,
			ValidationResult result, Translator translator, Identity identity) {
		ValidationItem item = new ValidationItem(
			propertyName,
			prop.getValue(propertyName),
			result, translator, identity
		);
		for (Rule singleRule : collection.getRules()) {
			singleRule.check(request, String.format(format, propertyName), propertyName, item);
			if (!item.canValidationContinue()) {
				break;
			}
		}
		if (item.canValidationContinue() && collection.getCustomValidation().isPresent()) {
			collection.getCustomValidation().get().accept(item);
		}
		if (result.isValid(propertyName) && collection.getChangeValue().isPresent()) {
			Object newValue = collection.getChangeValue().get().apply(item.getNewValue());
			item.setNewValue(newValue);
			prop.put(propertyName, newValue);
		}
		String newName = collection.getRename().orElse(propertyName);
		if (prop.containsKey(propertyName)) {
			prop.remove(propertyName);
			prop.put(newName, item.getNewValue());
		}
		return newName;
	}
	
}
