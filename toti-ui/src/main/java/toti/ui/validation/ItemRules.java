package toti.ui.validation;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;

public class ItemRules {

	public static ItemRules defaultRule() {
		return new ItemRules("", false, (t, param)->t.translate(
			"toti.validation.parameter-not-match-default-rule",
			new MapInit<String, Object>().append("parameter", param).toMap()
		));
	}
	
	public static ItemRules forName(String name, boolean required) {
		return new ItemRules(name, required, (t, param)->t.translate(
			"toti.validation.item-required", 
			new MapInit<String, Object>().append("parameter", param).toMap()
		)); // "This item is required: " + param
	}

	public static ItemRules forName(String name, boolean required, BiFunction<Translator, String, String> onRequiredError) {
		return new ItemRules(name, required, onRequiredError);
	}
	
	private Function<Object, Object> changeValue = (o)->o;
	
	private final String name;
	private final Boolean required;
	private final BiFunction<Translator, String, String> onRequiredError;
	private Optional<Class<?>> expectedType = Optional.empty();
	private Function<Translator, String> onExpectedTypeError = (t)->"";
	private boolean changeValueByType = false;
	
	private Optional<BiFunction<Object, Translator, Set<String>>> customValidation = Optional.empty();
	
	private Optional<Validator> mapSpecification = Optional.empty();
	private Optional<Validator> listSpecification = Optional.empty();
	private Optional<Validator> sortedMapSpecification = Optional.empty();
	
	private Optional<String> rename = Optional.empty();
	
	private ItemRules(String name, Boolean required, BiFunction<Translator, String, String> onRequiredError) {
		this.name = name;
		this.required = required;
		this.onRequiredError = onRequiredError;
	}

	public ItemRules setType(Class<?> clazz) {
		return setType(clazz, true);
	}
	
	public ItemRules setType(Class<?> clazz, Function<Translator, String> onExpectedTypeError) {
		return setType(clazz, true, onExpectedTypeError);
	}
	
	public ItemRules setType(Class<?> clazz, boolean changeValueByType) {
		return setType(clazz, changeValueByType, (t)->t.translate(
			"toti.validation.value-type-must-be", 
			new MapInit<String, Object>().append("class", clazz).toMap()
		)); // "Value must be " + clazz
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

	public ItemRules setSortedMapSpecification(Validator sortedMapSpecification) {
		this.sortedMapSpecification = Optional.of(sortedMapSpecification);
		return this;
	}

	public ItemRules setChangeValue(Function<Object, Object> changeValue) {
		this.changeValue = changeValue;
		return this;
	}
	
	public ItemRules setCustomValidation(BiFunction<Object, Translator, Set<String>> customValidation) {
		this.customValidation = Optional.of(customValidation);
		return this;
	}
	
	public ItemRules rename(String rename) {
		this.rename = Optional.of(rename);
		return this;
	}
	
	/*******************/

	public String getName() {
		return name;
	}
	
	public Optional<String> getRename() {
		return rename;
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
/*
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
*/
	public BiFunction<Translator, String, String> getOnRequiredError() {
		return onRequiredError;
	}

	public Function<Translator, String> getOnExpectedTypeError() {
		return onExpectedTypeError;
	}
/*
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
*/
	public Optional<Validator> getMapSpecification() {
		return mapSpecification;
	}

	public Optional<Validator> getListSpecification() {
		return listSpecification;
	}
	
	public Optional<Validator> getSortedMapSpecification() {
		return sortedMapSpecification;
	}

	public Function<Object, Object> getChangeValue() {
		return changeValue;
	}
	
	public Optional<BiFunction<Object, Translator, Set<String>>> getCustomValidation() {
		return customValidation;
	}
	
}
