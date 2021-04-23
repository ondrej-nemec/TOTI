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
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.UploadedFile;
import toti.registr.Registr;
import translator.Translator;

public class Validator {
	
	private final List<ItemRules> rules;
	private final boolean strictList;
	private final Optional<ItemRules> defaultRule;
	private final BiFunction<Translator, List<String>, String> onStrictListError;
	private Optional<BiFunction<RequestParameters, Translator, Set<String>>> globalFunc = Optional.empty();
	
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
		this(strictList, (trans, params)->"Not expected parameters: " + params);
	}
	
	public Validator(boolean strictList, BiFunction<Translator, List<String>, String> onStrictListError) {
		this(strictList, Optional.empty(), onStrictListError);
	}
	
	public Validator(ItemRules defaultRule) {
		this(false, Optional.of(defaultRule), (trans, params)->"Parameters not match default rule: " + params);
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
	
	public Validator setGlobalFunction(BiFunction<RequestParameters, Translator, Set<String>> globalFunction) {
		if (this.globalFunc.isPresent()) {
			throw new LogicException("Global function is already set");
		}
		this.globalFunc = Optional.of(globalFunction);
		return this;
	}
	
	public Map<String, Set<String>> validate(RequestParameters prop, Translator translator) {
		return validate("%s", prop, translator);
	}
	
	private Map<String, Set<String>> validate(String format, RequestParameters prop, Translator translator) {
		Map<String, Set<String>> errors = new HashMap<>();
		List<String> names = new ArrayList<>();
		rules.forEach((rule)->{
			names.add(rule.getName());
			swichRules(String.format(format, rule.getName()), rule.getName(), rule, errors, prop, translator);
			Object newValue = rule.getChangeValue().apply(prop.get(rule.getName()));
			if (newValue != null) {
				prop.put(rule.getName(), newValue);
			}
		});
		List<String> notChecked = new ArrayList<>(prop.keySet());
		notChecked.removeAll(names);
		notChecked = notChecked.stream().map(a->String.format(format, a)).collect(Collectors.toList());
		checkRule(
				Optional.of(notChecked),
				(incomingData)->{
					return incomingData.size() > 0 && strictList;
				},
				errors,
				"form",
				onStrictListError.apply(translator, notChecked)
		);
		if (!strictList && defaultRule.isPresent()) {
			ItemRules rule = defaultRule.get();
			notChecked.forEach((notCheckedName)->{
				swichRules(String.format(format, notCheckedName), notCheckedName, rule, errors, prop, translator);
				Object newValue = rule.getChangeValue().apply(prop.get(notCheckedName));
				if (newValue != null) {
					prop.put(notCheckedName, newValue);
				}
			});
		}
		if (globalFunc.isPresent() && errors.isEmpty()) {
			Set<String> globalErrors = globalFunc.get().apply(prop, translator);
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
							Object newO = ParseObject.parse(expectedType, o);
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
					(allowedList)->!allowedList.contains(o),
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
							Long value = (Long)ParseObject.parse(Long.class, o);
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
							Long value = (Long)ParseObject.parse(Long.class, o);
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
						fields.putAll(getMap(ParseObject.parse(Map.class, o)));
						errors.putAll(validator.validate(propertyName + "[%s]", fields, translator));
						prop.put(ruleName, fields);
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
							List<Object> list = getList(ParseObject.parse(List.class, o));
							RequestParameters fields = new RequestParameters();
							for (int i = 0; i < list.size(); i++) {
								fields.put(i + "", list.get(i));
							}
							errors.putAll(validator.validate(
								(propertyName.contains(":") ? "" : "%s:") + propertyName + "[]",
								fields,
								translator
							));
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

	@SuppressWarnings("unchecked")
	private List<Object> getList(Object o) {
		return List.class.cast(o);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMap(Object o) {
		return Map.class.cast(o);
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
