# Validation rules

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

## Value type checks

### Required type

Define required value type. If type is specified, validator retype from string to given type. If value cannot be cast, false is returned.

```
setType(Class<?> clazz);
// specify error message
setType(Class<?> clazz, Function<Translator, String> onExpectedTypeError);
```

### Allowed values

Specify which values are allowed to returns true.

```
setAllowedValues(Collection<Object> values);
// specify error message
setAllowedValues(Collection<Object> values, Function<Translator, String> onAllowedValuesError);
```

## Strings

### Maximal length

```

// specify error message

```

### Minimal length

```

// specify error message

```

### Regex

```

// specify error message

```

## Numbers

### Maximal value

```

// specify error message

```

### Minimal value

```

// specify error message

```

## Files

### Maximal file size

```

// specify error message

```

### Minimal file size

```

// specify error message

```

### Allowed file types
not 100%sure

```

// specify error message

```

## Map Specification

## List Specification