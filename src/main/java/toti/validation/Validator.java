package toti.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import common.exceptions.LogicException;
import common.structures.DictionaryValue;
import common.structures.MapInit;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.UploadedFile;
import toti.registr.Registr;
import translator.Translator;

public class Validator {
	
	private final List<ItemRules> rules;
	private final boolean strictList;
	private final Optional<ItemRules> defaultRule;
	private final BiFunction<Translator, List<String>, String> onStrictListError;
	private Optional<GlobalFunction> globalFunc = Optional.empty();
	
	public static Validator create(String uniqueName, boolean strictList, BiFunction<Translator, List<String>, String> onStrictListError) {
		Validator val = new Validator(strictList, onStrictListError);
		Registr.get().addService(uniqueName, val);
		return val;
	}

	public static Validator create(String uniqueName, boolean strictList) {
		Validator val = new Validator(strictList);
		Registr.get().addService(uniqueName, val);
		return val;
	}
	
	public Validator(boolean strictList) {
		this(strictList, (trans, params)->trans.translate(
			"common.validation.not-expected-parameters",
			new MapInit<String, Object>().append("parameters", params).toMap()
		)); // "Not expected parameters: " + params
	}
	
	public Validator(boolean strictList, BiFunction<Translator, List<String>, String> onStrictListError) {
		this(strictList, Optional.empty(), onStrictListError);
	}
	
	public Validator(ItemRules defaultRule) {
		this(false, Optional.of(defaultRule), (trans, params)->trans.translate(
			"common.validation.parameters-not-match-default-rule",
			new MapInit<String, Object>().append("parameters", params).toMap()
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
	
	public Validator addRule(ItemRules rule) {
		rules.add(rule);
		return this;
	}
	
	@Deprecated
	public Validator setGlobalFunction(BiFunction<RequestParameters, Translator, Set<String>> globalFunction) {
		return setGlobalFunction((r,v,t)->globalFunction.apply(r, t));
	}
	
	public Validator setGlobalFunction(GlobalFunction globalFunction) {
		if (this.globalFunc.isPresent()) {
			throw new LogicException("Global function is already set");
		}
		this.globalFunc = Optional.of(globalFunction);
		return this;
	}
	
	public Map<String, Set<String>> validate(RequestParameters prop, RequestParameters validatorParams, Translator translator) {
		return validate("%s", prop, validatorParams, translator);
	}
	
	private Map<String, Set<String>> validate(String format, RequestParameters prop, RequestParameters validatorParams, Translator translator) {
		Map<String, Set<String>> errors = new HashMap<>();
		List<String> names = new ArrayList<>();
		rules.forEach((rule)->{
			names.add(rule.getName());
			swichRules(String.format(format, rule.getName()), rule.getName(), rule, errors, prop, validatorParams, translator);
			Object newValue = rule.getChangeValue().apply(prop.get(rule.getName()));
			if (newValue != null) {
				prop.put(rule.getName(), newValue);
			}
		});
		List<String> notChecked = new ArrayList<>(prop.keySet());
		notChecked.removeAll(names);
		checkRule(
				Optional.of(notChecked),
				(incomingData)->{
					return incomingData.size() > 0 && strictList;
				},
				errors,
				"form",
				onStrictListError.apply(translator, notChecked.stream().map(a->String.format(format, a)).collect(Collectors.toList()))
		);
		if (!strictList && defaultRule.isPresent()) {
			ItemRules rule = defaultRule.get();
			notChecked.forEach((notCheckedName)->{
				swichRules(String.format(format, notCheckedName), notCheckedName, rule, errors, prop, validatorParams, translator);
				Object newValue = rule.getChangeValue().apply(prop.get(notCheckedName));
				if (newValue != null) {
					prop.put(notCheckedName, newValue);
				}
			});
		}
		if (globalFunc.isPresent() && errors.isEmpty()) {
			Set<String> globalErrors = globalFunc.get().apply(prop, validatorParams, translator);
			if (!globalErrors.isEmpty()) {
				errors.put("form", globalErrors);
			}
		}
		return errors;
	}
	
	private void swichRules(
			String propertyName,
			String ruleName,
			ItemRules rule,
			Map<String, Set<String>> errors,
			RequestParameters prop,
			RequestParameters validatorParams,
			Translator translator) {
		if (prop.get(ruleName) == null) {
			checkRule(
					Optional.of(rule.getRequired()),
					(required)->required,
					errors,
					"form",
					rule.getOnRequiredError().apply(translator, propertyName)
			);
		} else {
			Object o = prop.get(ruleName);
			checkRule(
					rule.getExpectedType(), 
					(expectedType)->{
						try {
							Object newO = new DictionaryValue(o).getValue(expectedType);
							if (rule.getChangeValueByType()) {
								prop.put(ruleName, newO);
							}
							return false;
						} catch (ClassCastException | NumberFormatException e) {
							return true;
						}
					},
					errors,
					propertyName,
					rule.getOnExpectedTypeError().apply(translator)
			);
			checkRule(
					rule.getAllowedValues(),
					(allowedList)->!allowedList.contains(prop.get(ruleName)),
					errors,
					propertyName,
					rule.getOnAllowedValuesError().apply(translator)
			);
			checkRule(
					rule.getMaxLength(), 
					(maxLength)->maxLength.intValue() < o.toString().length(),
					errors,
					propertyName,
					rule.getOnMaxLengthError().apply(translator)
			);
			checkRule(
					rule.getMinLength(),
					(minLength)->minLength.intValue() > o.toString().length(),
					errors,
					propertyName,
					rule.getOnMinLengthError().apply(translator)
			);
			checkRule(
					rule.getMaxValue(),
					(maxValue)->{
						try {
							Long value = new DictionaryValue(o).getLong();
							return value == null || maxValue.longValue() < value;
						} catch (ClassCastException | NumberFormatException e) {
							return true;
						}
					},
					errors,
					propertyName,
					rule.getOnMaxValueError().apply(translator)
			);
			checkRule(
					rule.getMinValue(),
					(minValue)->{
						try {
							Long value = new DictionaryValue(o).getLong();
							return value == null || minValue.longValue() > value.longValue();
						} catch (ClassCastException | NumberFormatException e) {
							return true;
						}
					},
					errors,
					propertyName,
					rule.getOnMinValueError().apply(translator)
			);
			checkRule(
					rule.getRegex(),
					(regex)->{
						Matcher m = Pattern.compile(regex).matcher(o.toString());
						return !m.find();
					},
					errors,
					propertyName,
					rule.getOnRegexError().apply(translator)
			);
			checkRule(
					rule.getFileMaxSize(),
					(maxSize)->{
						UploadedFile file = (UploadedFile)o;
						return file.getContent().size() > maxSize;
					},
					errors,
					propertyName,
					rule.getOnFileMaxSizeError().apply(translator)
			);
			checkRule(
					rule.getFileMinSize(),
					(minSize)->{
						UploadedFile file = (UploadedFile)o;
						return file.getContent().size() < minSize;
					},
					errors,
					propertyName,
					rule.getOnFileMinSizeError().apply(translator)
			);
			checkRule(
					rule.getAllowedFileTypes(),
					(type)->{
						UploadedFile file = (UploadedFile)o;
						return !type.contains(file.getContentType());
					},
					errors,
					propertyName,
					rule.getOnAllowedFileTypesError().apply(translator)
			);
			checkRule(
					rule.getMapSpecification(),
					(validator)->{
						RequestParameters fields = new RequestParameters();
						fields.putAll(new DictionaryValue(o).getMap());
						RequestParameters global = new RequestParameters();
						errors.putAll(validator.validate(propertyName + "[%s]", fields, global, translator));
						prop.put(ruleName, fields);
						validatorParams.put(ruleName, global);
						return false;
					},
					errors,
					propertyName,
					rule.getOnAllowedFileTypesError().apply(translator)
			);
			checkRule(
					rule.getListSpecification(), 
					(validator)->{
						try {
							List<Object> list = new DictionaryValue(o).getList();
							RequestParameters fields = new RequestParameters();
							for (int i = 0; i < list.size(); i++) {
								fields.put(i + "", list.get(i));
							}
							RequestParameters vParam = new RequestParameters();
							errors.putAll(validator.validate(
								(propertyName.contains(":") ? "" : "%s:") + propertyName + "[]",
								fields,
								vParam,
								translator
							));
							validatorParams.put(ruleName, vParam);
							prop.put(ruleName, new ArrayList<>(fields.values()));
							return false;
						} catch (ClassCastException | NumberFormatException e) {
							return true;
						}
					},
					errors,
					propertyName,
					rule.getOnExpectedTypeError().apply(translator)
			);
		}
	}
	
	private <T> void checkRule(
			Optional<T> optional,
			Function<T, Boolean> condition,
			Map<String, Set<String>> errors, 
			String name,
			String message) {
		if (optional.isPresent() && condition.apply(optional.get())) {
			if (errors.get(name) == null) {
				errors.put(name, new HashSet<>());
			}
			errors.get(name).add(message);
		}
	}
	
}
