package mvc.validation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
	
	private final List<Rules> rules;
	private final boolean strictList;
	private final String onStrictListError;
	
	public Validator(boolean strictList) {
		this(strictList, "Parameters names not match expectation");
	}
	
	public Validator(boolean strictList, String onStrictListError) {
		this.strictList = strictList;
		this.onStrictListError = onStrictListError;
		this.rules = new LinkedList<>();
	}
	
	public void addRule(Rules rule) {
		rules.add(rule);
	}
	
	public Map<String, List<String>> validate(Properties prop) {
		Map<String, List<String>> errors = new HashMap<>();
		List<String> names = new ArrayList<>();
		rules.forEach((rule)->{
			names.add(rule.getName());
			if (prop.get(rule.getName()) == null) {
				checkRule(
						Optional.of(rule.getRequired()),
						(required)->required,
						errors,
						rule.getName(), 
						rule.getOnRequiredError()
				);
			} else {
				Object o = prop.get(rule.getName());
				checkRule(
						rule.getExpectedType(), 
						(expectedType)->{
							try {
								ParseObject.parse(expectedType, o);
								return false;
							} catch (ClassCastException | NumberFormatException e) {
								return true;
							}
						},
						errors,
						rule.getName(),
						rule.getOnExpectedTypeError()
				);
				checkRule(
						rule.getAllowedValues(),
						(allowedList)->!allowedList.contains(o),
						errors,
						rule.getName(),
						rule.getOnAllowedValuesError()
				);
				checkRule(
						rule.getMaxLength(), 
						(maxLength)->maxLength.intValue() < o.toString().length(),
						errors,
						rule.getName(),
						rule.getOnMaxLengthError()
				);
				checkRule(
						rule.getMinLength(),
						(minLength)->minLength.intValue() > o.toString().length(),
						errors,
						rule.getName(),
						rule.getOnMinLengthError()
				);
				checkRule(
						rule.getMaxValue(),
						(maxValue)->maxValue.longValue() < new BigDecimal(o.toString()).longValue(),
						errors,
						rule.getName(),
						rule.getOnMinLengthError()
				);
				checkRule(
						rule.getMinValue(),
						(minValue)->minValue.longValue() > new BigDecimal(o.toString()).longValue(),
						errors,
						rule.getName(),
						rule.getOnMinLengthError()
				);
				checkRule(
						rule.getRegex(),
						(regex)->{
							Matcher m = Pattern.compile(regex).matcher(o.toString());
							return !m.find();
						},
						errors,
						rule.getName(),
						rule.getOnRegexError()
				);
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
				onStrictListError
		);
		return errors;
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
