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

import json.JsonReader;
import json.JsonStreamException;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.UploadedFile;
import toti.registr.Registr;
import translator.Translator;

public class Validator {
	
	private final List<ItemRules> rules;
	private final boolean strictList;
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
		this.strictList = strictList;
		this.onStrictListError = onStrictListError;
		this.rules = new LinkedList<>();
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
			swichRules(rule, errors, prop, translator);
			Object newValue = rule.getChangeValue().apply(prop.get(rule.getName()));
			if (newValue != null) {
				prop.put(rule.getName(), newValue);
			}
		});
		checkRule(
				Optional.of(new ArrayList<>(prop.keySet())),
				(incomingData)->{
					incomingData.removeAll(names);
					return incomingData.size() > 0 && strictList;
				},
				errors,
				"form",
				onStrictListError.apply(translator)
		);
		return errors;
	}
	
	private void swichRules(ItemRules rule, Map<String, List<String>> errors, RequestParameters prop, Translator translator) {
		if (prop.get(rule.getName()) == null) {
			checkRule(
					Optional.of(rule.getRequired()),
					(required)->required,
					errors,
					rule.getName(), 
					rule.getOnRequiredError().apply(translator)
			);
		} else {
			Object o = prop.get(rule.getName());
			checkRule(
					rule.getExpectedType(), 
					(expectedType)->{
						try {
							// ParseObject.parse(expectedType, o);
							Object newO = ParseObject.parse(expectedType, o);
							if (rule.getChangeValueByType()) {
								prop.put(rule.getName(), newO);
							}
							return false;
						} catch (ClassCastException | NumberFormatException e) {
							return true;
						}
					},
					errors,
					rule.getName(),
					rule.getOnExpectedTypeError().apply(translator)
			);
			checkRule(
					rule.getAllowedValues(),
					(allowedList)->!allowedList.contains(o),
					errors,
					rule.getName(),
					rule.getOnAllowedValuesError().apply(translator)
			);
			checkRule(
					rule.getMaxLength(), 
					(maxLength)->maxLength.intValue() < o.toString().length(),
					errors,
					rule.getName(),
					rule.getOnMaxLengthError().apply(translator)
			);
			checkRule(
					rule.getMinLength(),
					(minLength)->minLength.intValue() > o.toString().length(),
					errors,
					rule.getName(),
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
					rule.getName(),
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
					rule.getName(),
					rule.getOnMinValueError().apply(translator)
			);
			checkRule(
					rule.getRegex(),
					(regex)->{
						Matcher m = Pattern.compile(regex).matcher(o.toString());
						return !m.find();
					},
					errors,
					rule.getName(),
					rule.getOnRegexError().apply(translator)
			);
			checkRule(
					rule.getFileMaxSize(),
					(maxSize)->{
						UploadedFile file = (UploadedFile)o;
						return file.getContent().size() > maxSize;
					},
					errors,
					rule.getName(),
					rule.getOnFileMaxSizeError().apply(translator)
			);
			checkRule(
					rule.getFileMinSize(),
					(minSize)->{
						UploadedFile file = (UploadedFile)o;
						return file.getContent().size() < minSize;
					},
					errors,
					rule.getName(),
					rule.getOnFileMinSizeError().apply(translator)
			);
			checkRule(
					rule.getAllowedFileTypes(),
					(type)->{
						UploadedFile file = (UploadedFile)o;
						return !type.contains(file.getContentType());
					},
					errors,
					rule.getName(),
					rule.getOnAllowedFileTypesError().apply(translator)
			);
			checkRule(
					rule.getMapSpecification(),
					(validator)->{
						if (o instanceof String) {
							try {
								Map<String, Object> json = new JsonReader().read(o.toString());
								RequestParameters fields = new RequestParameters();
								fields.putAll(json);
								prop.put(rule.getName(), fields);
								errors.putAll(validator.validate(fields, translator));
								return false;
							} catch (JsonStreamException e) {
								return true;
							}
						}
						RequestParameters fields = new RequestParameters();
						fields.putAll(getMap(o));
						errors.putAll(validator.validate(fields, translator));
						return false;
					},
					errors,
					rule.getName(),
					rule.getOnAllowedFileTypesError().apply(translator)
			);
		}
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
			String name, String message) {
		if (optional.isPresent() && condition.apply(optional.get())) {
			if (errors.get(name) == null) {
				errors.put(name, new LinkedList<>());
			}
			errors.get(name).add(message);
		}
	}
	
}
