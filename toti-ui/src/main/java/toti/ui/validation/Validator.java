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
import toti.ui.validation.rules.Rule;

public class Validator implements Validate {
	
	private final List<ItemRules> rules;
	private final boolean strictList;
	private final Optional<ItemRules> defaultRule;
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
	
	public Validator(ItemRules defaultRule) {
		this(false, Optional.of(defaultRule), (trans, params)->trans.translate(
			"toti.validation.parameter-not-match-default-rule",
			new MapInit<String, Object>().append("parameter", params).toMap()
		)); // "Parameters not match default rule: " + params
	}
	
	public Validator(ItemRules defaultRule, BiFunction<Translator, List<String>, String> onStrictListError) {
		this(false, Optional.of(defaultRule), onStrictListError);
	}
	
	private Validator(boolean strictList, Optional<ItemRules> defaultRule, BiFunction<Translator, List<String>, String> onStrictListError) {
		this.strictList = strictList;
		this.onStrictListError = onStrictListError;
		this.rules = new LinkedList<>();
		this.defaultRule = defaultRule;
	}
	
	@Override
	public void validate(Request request, Translator translator, Identity identity) throws RequestInterruptedException {
		RequestParameters params = new RequestParameters();
		params.putAll(request.getBodyParams().toMap());
		params.putAll(request.getQueryParams().toMap());
		
		ValidationResult result = validate(params, translator);
		if (!result.isValid()) {
			throw new RequestInterruptedException(Response.create(StatusCode.BAD_REQUEST).getJson(result));
		}
	}
	
	public Validator addRule(ItemRules rule) {
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
	
	public ValidationResult validate(String format, RequestParameters prop, Translator translator) {
		ValidationResult result = new ValidationResult();
		List<String> names = new ArrayList<>();
		rules.forEach((rule)->{
			String newName = iterateRules(format, rule, prop, result, translator);
			
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
			ItemRules rule = defaultRule.get();
			notChecked.forEach((notCheckedName)->{
				iterateRules(format, rule, prop, result, translator);
				/*swichRules(String.format(format, notCheckedName), notCheckedName, rule, errors, prop, translator);
				Object newValue = rule.getChangeValue().apply(prop.get(notCheckedName));
				if (newValue != null) {
					prop.put(notCheckedName, newValue);
				}*/
			});
		}
		if (globalFunc.isPresent() && result.isValid()) {
			globalFunc.get().apply(prop, result);
		}
		return result;
	}
	
	private String iterateRules(
			String format, ItemRules itemRules, RequestParameters prop,
			ValidationResult result, Translator translator) {
		ValidationItem item = new ValidationItem(prop.getValue(itemRules.getName()), result, translator);
		List<Rule> rules = null; // TODO
		for (Rule singleRule : rules) {
			singleRule.check(String.format(format, itemRules.getName()), itemRules.getName(), item);
			if (!item.canValidationContinue()) {
				break;
			}
		}
		//prop.put(rule.getName(), item.getNewValue());
		String newName = itemRules.getRename().orElse(itemRules.getName());

		prop.remove(itemRules.getName());
		prop.put(newName, itemRules.getChangeValue().apply(item.getNewValue()));
		
		return newName;
	}
	
}
