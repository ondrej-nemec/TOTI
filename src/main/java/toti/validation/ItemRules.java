package toti.validation;

import java.util.Collection;
import java.util.Optional;

import common.exceptions.LogicException;

public class ItemRules {

	public static ItemRules forName(String name, boolean required) {
		return new ItemRules(name, required, "This item is required");
	}

	public static ItemRules forName(String name, boolean required, String onRequiredError) {
		return new ItemRules(name, required, onRequiredError);
	}
	
	private final String name;
	private final Boolean required;
	private final String onRequiredError;
	private Optional<Class<?>> expectedType = Optional.empty();
	private String onExpectedTypeError;
	
	private Optional<String> regex = Optional.empty();
	private String onRegexError;
	private Optional<Integer> maxLength = Optional.empty();
	private String onMaxLengthError;
	private Optional<Integer> minLength = Optional.empty();
	private String onMinLengthError;
	
	private Optional<Number> maxValue = Optional.empty();
	private String onMaxValueError;
	private Optional<Number> minValue = Optional.empty();
	private String onMinValueError;
	
	private Optional<Integer> fileMaxSize = Optional.empty();
	private String onFileMaxSizeError;
	private Optional<Integer> fileMinSize = Optional.empty();
	private String onFileMinSizeError;
	private Optional<Collection<Object>> allowedFileTypes = Optional.empty();
	private String onAllowedFileTypesError;
	
	private Optional<Collection<Object>> allowedValues = Optional.empty();
	private String onAllowedValuesError;
	
	private Optional<Validator> mapSpecification = Optional.empty();
	
	private ItemRules(String name, boolean required, String onRequiredError) {
		this.name = name;
		this.required = required;
		this.onRequiredError = onRequiredError;
	}
	
	public ItemRules setAllowedValues(Collection<Object> values) {
		return setAllowedValues(values, "Value must be one of: " + values);
	}
	
	public ItemRules setAllowedValues(Collection<Object> values, String onAllowedValuesError) {
		if (this.allowedValues.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onAllowedValuesError = onAllowedValuesError;
		this.allowedValues = Optional.of(values);
		return this;
	}
	
	public ItemRules setMaxValue(Number maxValue) {
		return setMaxValue(maxValue, "Value must be less or equals " + maxValue);
	}
	
	public ItemRules setMaxValue(Number maxValue, String onMaxValueError) {
		if (this.maxValue.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for number
		this.onMaxValueError = onMaxValueError;
		this.maxValue = Optional.of(maxValue);
		return this;
	}

	public ItemRules setFileMaxSize(Integer fileMaxSize) {
		return setFileMaxSize(fileMaxSize, "File size can be max " + fileMaxSize + "b");
	}

	public ItemRules setFileMaxSize(Integer fileMaxSize, String onFileMaxSizeError) {
		if (this.fileMaxSize.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.fileMaxSize = Optional.of(fileMaxSize);
		this.onFileMaxSizeError = onFileMaxSizeError;
		return this;
	}

	public ItemRules setFileMinSize(Integer fileMinSize) {
		return setFileMinSize(fileMinSize, "File size must be at least " + fileMinSize + "b");
	}

	public ItemRules setFileMinSize(Integer fileMinSize, String onFileMinSizeError) {
		if (this.fileMinSize.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.fileMinSize = Optional.of(fileMinSize);
		this.onFileMinSizeError = onFileMinSizeError;
		return this;
	}

	public ItemRules setAllowedFileTypes(Collection<Object> allowedFileTypes) {
		return setAllowedFileTypes(allowedFileTypes, "File type is not allowed. Allowed: " + allowedFileTypes);
	}

	public ItemRules setAllowedFileTypes(Collection<Object> allowedFileTypes, String onAllowedFileTypesError) {
		if (this.allowedFileTypes.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.allowedFileTypes = Optional.of(allowedFileTypes);
		this.onAllowedFileTypesError = onAllowedFileTypesError;
		return this;
	}
	
	public ItemRules setMinValue(Number minValue) {
		return setMinValue(minValue, "Value must be equals or higher " + minValue);
	}
	
	public ItemRules setMinValue(Number minValue, String onMinValueError) {
		if (this.minValue.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for number
		this.onMinValueError = onMinValueError;
		this.minValue = Optional.of(minValue);
		return this;
	}
	
	public ItemRules setMinLength(int minLength) {
		return setMinLength(minLength, "Text length must be at least " + minLength);
	}
	
	public ItemRules setMinLength(int minLength, String onMinLengthError) {
		if (this.minLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for string
		this.onMinLengthError = onMinLengthError;
		this.minLength = Optional.of(minLength);
		return this;
	}
	
	public ItemRules setMaxLength(int maxLength) {
		return setMaxLength(maxLength, "Text length must be maximal " + maxLength);
	}
	
	public ItemRules setMaxLength(int maxLength, String onMaxLengthError) {
		if (this.maxLength.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for string
		this.onMaxLengthError = onMaxLengthError;
		this.maxLength = Optional.of(maxLength);
		return this;
	}
	
	public ItemRules setRegex(String regex) {
		return setRegex(regex, "Text must looks like " + regex.replace("\\", "\\\\"));
	}
	
	public ItemRules setRegex(String regex, String onRegexError) {
		if (this.regex.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		// TODO only for string
		this.onRegexError = onRegexError;
		this.regex = Optional.of(regex);
		return this;
	}
	
	public ItemRules setType(Class<?> clazz) {
		return setType(clazz, "Value must be " + clazz);
	}
	
	public ItemRules setType(Class<?> clazz, String onExpectedTypeError) {
		if (this.expectedType.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		this.onExpectedTypeError = onExpectedTypeError;
		this.expectedType = Optional.of(clazz);
		return this;
	}

	public ItemRules setMapSpecification(Validator mapSpecification) {
		this.mapSpecification = Optional.of(mapSpecification);
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

	public String getOnRequiredError() {
		return onRequiredError;
	}

	public String getOnExpectedTypeError() {
		return onExpectedTypeError;
	}

	public String getOnRegexError() {
		return onRegexError;
	}

	public String getOnMaxLengthError() {
		return onMaxLengthError;
	}

	public String getOnMinLengthError() {
		return onMinLengthError;
	}

	public String getOnMaxValueError() {
		return onMaxValueError;
	}

	public String getOnMinValueError() {
		return onMinValueError;
	}

	public String getOnAllowedValuesError() {
		return onAllowedValuesError;
	}

	public Optional<Integer> getFileMaxSize() {
		return fileMaxSize;
	}

	public String getOnFileMaxSizeError() {
		return onFileMaxSizeError;
	}

	public Optional<Integer> getFileMinSize() {
		return fileMinSize;
	}

	public String getOnFileMinSizeError() {
		return onFileMinSizeError;
	}

	public Optional<Collection<Object>> getAllowedFileTypes() {
		return allowedFileTypes;
	}

	public String getOnAllowedFileTypesError() {
		return onAllowedFileTypesError;
	}

	public Optional<Validator> getMapSpecification() {
		return mapSpecification;
	}
	
}
