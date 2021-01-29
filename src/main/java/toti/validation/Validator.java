package toti.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.UploadedFile;
import toti.registr.Registr;
import translator.Translator;

public class Validator {
	
	private final List<ItemRules> rules;
	private final boolean strictList;
	private final Optional<ItemRules> defaultRule;
	private final Function<Translator, String> onStrictListError;
	
	public static Validator create(String uniqueName, boolean strictList, Function<Translator, String> onStrictListError) {
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
		this(strictList, (trans)->"Parameters names not match expectation");
	}
	
	public Validator(boolean strictList, Function<Translator, String> onStrictListError) {
		this(strictList, Optional.empty(), onStrictListError);
	}
	
	public Validator(ItemRules defaultRule) {
		this(false, Optional.of(defaultRule), (trans)->"Parameters not match default rule");
	}
	
	public Validator(ItemRules defaultRule, Function<Translator, String> onStrictListError) {
		this(false, Optional.of(defaultRule), onStrictListError);
	}
	
	private Validator(boolean strictList, Optional<ItemRules> defaultRule, Function<Translator, String> onStrictListError) {
		this.strictList = strictList;
		this.onStrictListError = onStrictListError;
		this.rules = new LinkedList<>();
		this.defaultRule = defaultRule;
	}
	
	public Validator addRule(ItemRules rule) {
		rules.add(rule);
		return this;
	}
	
	public Map<String, List<String>> validate(RequestParameters prop, Translator translator) {
		Map<String, List<String>> errors = new HashMap<>();
		List<String> names = new ArrayList<>();
		rules.forEach((rule)->{
			names.add(rule.getName());
			swichRules(rule.getName(), rule, errors, prop, translator);
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
				onStrictListError.apply(translator)
		);
		if (!strictList && defaultRule.isPresent()) {
			ItemRules rule = defaultRule.get();
			notChecked.forEach((notCheckedName)->{
				swichRules(notCheckedName, rule, errors, prop, translator);
				Object newValue = rule.getChangeValue().apply(prop.get(notCheckedName));
				if (newValue != null) {
					prop.put(notCheckedName, newValue);
				}
			});
		}
		return errors;
	}
	
	private void swichRules(
			String propertyName,
			ItemRules rule,
			Map<String,
			List<String>> errors,
			RequestParameters prop, 
			Translator translator) {
		if (prop.get(propertyName) == null) {
			checkRule(
					Optional.of(rule.getRequired()),
					(required)->required,
					errors,
					propertyName, 
					rule.getOnRequiredError().apply(translator)
			);
		} else {
			Object o = prop.get(propertyName);
			checkRule(
					rule.getExpectedType(), 
					(expectedType)->{
						try {
							Object newO = ParseObject.parse(expectedType, o);
							if (rule.getChangeValueByType()) {
								prop.put(propertyName, newO);
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
						prop.put(propertyName, fields);
						errors.putAll(validator.validate(fields, translator));
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
							errors.putAll(validator.validate(fields, translator));
							prop.put(propertyName, new ArrayList<>(fields.values()));
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
			Map<String, 
			List<String>> errors, 
			String name,
			String message) {
		if (optional.isPresent() && condition.apply(optional.get())) {
			if (errors.get(name) == null) {
				errors.put(name, new LinkedList<>());
			}
			errors.get(name).add(message);
		}
	}
	
}
