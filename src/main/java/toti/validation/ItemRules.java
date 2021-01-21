package toti.validation;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import common.exceptions.LogicException;
import translator.Translator;

public class ItemRules {

	public static ItemRules defaultRule() {
		return new ItemRules("", false, (t)->"");
	}
	
	public static ItemRules forName(String name, boolean required) {
		return new ItemRules(name, required, (t)->"This item is required");
	}

	public static ItemRules forName(String name, boolean required, Function<Translator, String> onRequiredError) {
		return new ItemRules(name, required, onRequiredError);
	}
	
	private Function<Object, Object> changeValue = (o)->o;
	
	private final String name;
	private final Boolean required;
	private final Function<Translator, String> onRequiredError;
	private Optional<Class<?>> expectedType = Optional.empty();
	private Function<Translator, String> onExpectedTypeError = (t)->"";
	private boolean changeValueByType = false;
	
	private Optional<String> regex = Optional.empty();
	private Function<Translator, String> onRegexError = (t)->"";
	private Optional<Integer> maxLength = Optional.empty();
	private Function<Translator, String> onMaxLengthError = (t)->"";
	private Optional<Integer> minLength = Optional.empty();
	private Function<Translator, String> onMinLengthError = (t)->"";
	
	private Optional<Number> maxValue = Optional.empty();
	private Function<Translator, String> onMaxValueError = (t)->"";
	private Optional<Number> minValue = Optional.empty();
	private Function<Translator, String> onMinValueError = (t)->"";
	
	private Optional<Integer> fileMaxSize = Optional.empty();
	private Function<Translator, String> onFileMaxSizeError = (t)->"";
	private Optional<Integer> fileMinSize = Optional.empty();
	private Function<Translator, String> onFileMinSizeError = (t)->"";
	private Optional<Collection<Object>> allowedFileTypes = Optional.empty();
	private Function<Translator, String> onAllowedFileTypesError = (t)->"";
	
	private Optional<Collection<Object>> allowedValues = Optional.empty();
	private Function<Translator, String> onAllowedValuesError = (t)->"";
	
	private Optional<Validator> mapSpecification = Optional.empty();
	private Optional<Validator> listSpecification = Optional.empty();
	
	private ItemRules(String name, Boolean required, Function<Translator, String> onRequiredError) {
		this.name = name;
		this.required = required;
		this.onRequiredError = onRequiredError;
	}
	
	public ItemRules setAllowedValues(Collection<Object> values) {
		return setAllowedValues(values, (t)->"Value must be one of: " + values);
	}
	
	public ItemRules setAllowedValues(Collection<Object> values, Function<Translator, String> onAllowedValuesError) {
		if (this.allowedValues.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onAllowedValuesError = onAllowedValuesError;
		this.allowedValues = Optional.of(values);
		return this;
	}
	
	public ItemRules setMaxValue(Number maxValue) {
		return setMaxValue(maxValue, (t)->"Value must be less or equals " + maxValue);
	}
	
	public ItemRules setMaxValue(Number maxValue, Function<Translator, String> onMaxValueError) {
		if (this.maxValue.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for number
		this.onMaxValueError = onMaxValueError;
		this.maxValue = Optional.of(maxValue);
		return this;
	}

	public ItemRules setFileMaxSize(Integer fileMaxSize) {
		return setFileMaxSize(fileMaxSize, (t)->"File size can be max " + fileMaxSize + "b");
	}

	public ItemRules setFileMaxSize(Integer fileMaxSize, Function<Translator, String> onFileMaxSizeError) {
		if (this.fileMaxSize.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.fileMaxSize = Optional.of(fileMaxSize);
		this.onFileMaxSizeError = onFileMaxSizeError;
		return this;
	}

	public ItemRules setFileMinSize(Integer fileMinSize) {
		return setFileMinSize(fileMinSize, (t)->"File size must be at least " + fileMinSize + "b");
	}

	public ItemRules setFileMinSize(Integer fileMinSize, Function<Translator, String> onFileMinSizeError) {
		if (this.fileMinSize.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.fileMinSize = Optional.of(fileMinSize);
		this.onFileMinSizeError = onFileMinSizeError;
		return this;
	}

	public ItemRules setAllowedFileTypes(Collection<Object> allowedFileTypes) {
		return setAllowedFileTypes(allowedFileTypes, (t)->"File type is not allowed. Allowed: " + allowedFileTypes);
	}

	public ItemRules setAllowedFileTypes(Collection<Object> allowedFileTypes, Function<Translator, String> onAllowedFileTypesError) {
		if (this.allowedFileTypes.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.allowedFileTypes = Optional.of(allowedFileTypes);
		this.onAllowedFileTypesError = onAllowedFileTypesError;
		return this;
	}
	
	public ItemRules setMinValue(Number minValue) {
		return setMinValue(minValue, (t)->"Value must be equals or higher " + minValue);
	}
	
	public ItemRules setMinValue(Number minValue, Function<Translator, String> onMinValueError) {
		if (this.minValue.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for number
		this.onMinValueError = onMinValueError;
		this.minValue = Optional.of(minValue);
		return this;
	}
	
	public ItemRules setMinLength(int minLength) {
		return setMinLength(minLength, (t)->"Text length must be at least " + minLength);
	}
	
	public ItemRules setMinLength(int minLength, Function<Translator, String> onMinLengthError) {
		if (this.minLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for string
		this.onMinLengthError = onMinLengthError;
		this.minLength = Optional.of(minLength);
		return this;
	}
	
	public ItemRules setMaxLength(int maxLength) {
		return setMaxLength(maxLength, (t)->"Text length must be maximal " + maxLength);
	}
	
	public ItemRules setMaxLength(int maxLength, Function<Translator, String> onMaxLengthError) {
		if (this.maxLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for string
		this.onMaxLengthError = onMaxLengthError;
		this.maxLength = Optional.of(maxLength);
		return this;
	}
	
	public ItemRules setRegex(String regex) {
		return setRegex(regex, (t)->"Text must looks like " + regex.replace("\\", "\\\\"));
	}
	
	public ItemRules setRegex(String regex, Function<Translator, String> onRegexError) {
		if (this.regex.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for string
		this.onRegexError = onRegexError;
		this.regex = Optional.of(regex);
		return this;
	}
	
	public ItemRules setType(Class<?> clazz) {
		return setType(clazz, (t)->"Value must be " + clazz);
	}
	
	public ItemRules setType(Class<?> clazz, Function<Translator, String> onExpectedTypeError) {
		return setType(clazz, true, onExpectedTypeError);
	}
	
	public ItemRules setType(Class<?> clazz, boolean changeValueByType) {
		return setType(clazz, changeValueByType, (t)->"Value must be " + clazz);
	}
	
	public ItemRules setType(Class<?> clazz, boolean changeValueByType, Function<Translator, String> onExpectedTypeError) {
		if (this.expectedType.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.changeValueByType = changeValueByType;
		this.onExpectedTypeError = onExpectedTypeError;
		this.expectedType = Optional.of(clazz);
		return this;
	}

	public ItemRules setMapSpecification(Validator mapSpecification) {
		this.mapSpecification = Optional.of(mapSpecification);
		return this;
	}

	public ItemRules setListSpecification(Validator listSpecification) {
		this.listSpecification = Optional.of(listSpecification);
		return this;
	}

	public ItemRules setChangeValue(Function<Object, Object> changeValue) {
		this.changeValue = changeValue;
		return this;
	}
	
	/*******************/

	public String getName() {
		return name;
	}

	public Boolean getRequired() {
		return required;
	}

	public Optional<Class<?>> getExpectedType() {
		return expectedType;
	}
	
	public boolean getChangeValueByType() {
		return changeValueByType;
	}

	public Optional<String> getRegex() {
		return regex;
	}

	public Optional<Integer> getMaxLength() {
		return maxLength;
	}

	public Optional<Integer> getMinLength() {
		return minLength;
	}

	public Optional<Number> getMaxValue() {
		return maxValue;
	}

	public Optional<Number> getMinValue() {
		return minValue;
	}

	public Optional<Collection<Object>> getAllowedValues() {
		return allowedValues;
	}

	public Function<Translator, String> getOnRequiredError() {
		return onRequiredError;
	}

	public Function<Translator, String> getOnExpectedTypeError() {
		return onExpectedTypeError;
	}

	public Function<Translator, String> getOnRegexError() {
		return onRegexError;
	}

	public Function<Translator, String> getOnMaxLengthError() {
		return onMaxLengthError;
	}

	public Function<Translator, String> getOnMinLengthError() {
		return onMinLengthError;
	}

	public Function<Translator, String> getOnMaxValueError() {
		return onMaxValueError;
	}

	public Function<Translator, String> getOnMinValueError() {
		return onMinValueError;
	}

	public Function<Translator, String> getOnAllowedValuesError() {
		return onAllowedValuesError;
	}

	public Optional<Integer> getFileMaxSize() {
		return fileMaxSize;
	}

	public Function<Translator, String> getOnFileMaxSizeError() {
		return onFileMaxSizeError;
	}

	public Optional<Integer> getFileMinSize() {
		return fileMinSize;
	}

	public Function<Translator, String> getOnFileMinSizeError() {
		return onFileMinSizeError;
	}

	public Optional<Collection<Object>> getAllowedFileTypes() {
		return allowedFileTypes;
	}

	public Function<Translator, String> getOnAllowedFileTypesError() {
		return onAllowedFileTypesError;
	}

	public Optional<Validator> getMapSpecification() {
		return mapSpecification;
	}

	public Optional<Validator> getListSpecification() {
		return listSpecification;
	}

	public Function<Object, Object> getChangeValue() {
		return changeValue;
	}
	
}
