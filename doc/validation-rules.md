# Validation rules

* [Creating ItemRules](#creating-itemrules)
* [Value type checks](#value-type-checks)
	* [Allowed values](#allowed-values)
	* [Required type](#required-type)
* [Strings](#strings)
	* [Maximal Length](#maximal-length)
	* [Minimal Length](#minimal-length)
	* [Regex](#regex)
* [Numbers](#numbers)
	* [Maximal Value](#maximal-value)
	* [Minimal Value](#minimal-value)
* [Files](#files)
	* [Maximal file size](#maximal-file-size)
	* [Minimal file size](#minimal-file-size)
	* [Allowed file types](#allowed-file-types)
* [Map Specification](#map-specification)
* [List Specification](#list-specification)
* [Change value](#change-value)
* [Global Rule](#global-rule)

## Creating ItemRules

`ItemRules` can be created by three factory methods. The first one specify name of input and if is required.

```
ItemRules rules = ItemRules.forName(String name, boolean required);
```

The second appends third parameter - function that is called if input is required but not filled.

```
ItemRules rules = ItemRules.forName(String name, boolean required, Function<Translator, String> onRequiredError);
```

The third and last method is **only** for creating default rule. This `ItemRules` can be used in `Validator` constructor. The default rule is used if rule for item name not founded.

```
ItemRules.defaultRule();
```

## Value type checks

### Required type

Define required value type. If type is specified, `Validator` retype from string to given type. Retyping can be disabled by parameter.

```
// basic
setType(Class<?> clazz);
// specify error message
setType(Class<?> clazz, Function<Translator, String> onExpectedTypeError);
// basic with retype settings
setType(Class<?> clazz, boolean changeValueByType);
// specify error message and retype settings
setType(Class<?> clazz, boolean changeValueByType, Function<Translator, String> onExpectedTypeError);
```

### Allowed values

Specify which values are allowed.

```
// basic
setAllowedValues(Collection<Object> values);
// specify error message
setAllowedValues(Collection<Object> values, Function<Translator, String> onAllowedValuesError);
```

## Strings

### Maximal length

Specify maximal length of input string.

```
// basic
setMaxLength(int maxLength);
// specify error message
setMaxLength(int maxLength, Function<Translator, String> onMaxLengthError);
```

### Minimal length

Specify minimal length of input string.

```
// basic
setMinLength(int minLength);
// specify error message
setMinLength(int minLength, Function<Translator, String> onMinLengthError);
```

### Regex

Set required format of input string.

```
// basic
setRegex(String regex);
// specify error message
setRegex(String regex, Function<Translator, String> onRegexError);
```

## Numbers

### Maximal value

Specify maximal `Number` value.

```
// basic
setMaxValue(Number maxValue);
// specify error message
setMaxValue(Number maxValue, Function<Translator, String> onMaxValueError);
```

### Minimal value

Specify minimal `Number` value.

```
// basic
setMinValue(Number minValue);
// specify error message
setMinValue(Number minValue, Function<Translator, String> onMinValueError);
```

## Files

### Maximal file size

Specify maximal allowed file size. **This not override server property 'Maximal file size'. Validation of this rule done after uploading file to memory.**

```
// basic
setFileMaxSize(Integer fileMaxSize)
// specify error message
setFileMaxSize(Integer fileMaxSize, Function<Translator, String> onFileMaxSizeError);
```

### Minimal file size

Specify minimal allowed file size. **Validation of this rule done after uploading file to memory.**

```
// basic
setFileMinSize(Integer fileMinSize)
// specify error message
setFileMinSize(Integer fileMinSize, Function<Translator, String> onFileMinSizeError);
```

### Allowed file types

Specify which types of file are allowed. **This is taken from  'Content-Type' property of form and is not 100% sure (someone can give fraudulent data).**

```
// basic
setAllowedFileTypes(Collection<Object> allowedFileTypes);
// specify error message
setAllowedFileTypes(Collection<Object> allowedFileTypes, Function<Translator, String> onAllowedFileTypesError);
```

## Map Specification

Specify validation for group of inputs that names starts with `rule-name[...]`. See [TOTI specific inputs](controls.md#toti-specific).

```
setMapSpecification(Validator mapSpecification);
```

## List Specification

Specify validation for group of inputs that names starts with `rule-name[]`. See [TOTI specific inputs](controls.md#toti-specific).

```
setListSpecification(Validator listSpecification);
```

## Change value

Allow change value by given function. Called after all other validation.

```
setChangeValue(Function<Object, Object> changeValue);
```

## Global Rule

If you wish compare two values or do anything else with parameters before passing to controller, you wil appraise `setGlobalFunction` on `Validator`. Parameter to this function is `BiFunction` that consume `RequestParameters` and `Translator` and returns `Set` of Strings (errors). Empty `Set` means 'no error'. This `BiFunction` is applied after all other validation rules and **only if no error appear before**.

```
new Validator(true)
	...
	.setGlobalFunction((parameters, translator)->{
		Set<String> set = new HashSet<>();
		set.add("You cannot save this form");
		return set;
	});
```
