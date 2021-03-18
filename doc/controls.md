# Filters and Inputs

The filters and inputs (controls) have some predefined parameters. If you need some other, you can use `addParam` method (more times for one control).

* [Textual and numeric](#textual-and-numeric)
	* [Text](#text)
	* [Textarea](#textarea)
	* [Email](#email)
	* [Password](#password)
	* [Number](#number)
	* [Range](#range)
* [Determination of time](#determination-of-time)
	* [Date](#date)
	* [Time](#time)
	* [Datetime](#datetime)
	* [Month](#month)
	* [Week](#week)
* [Action](#action)
	* [Submit](#submit)
	* [Button](#button)
	* [Reset](#reset)
* [Selection](#selection)
	* [Checkbox](#checkbox)
	* [Radiolist](#radiolist)
	* [Select](#select)
	* [Option](#option)
* [Special](#special)
	* [Hidden](#hidden)
	* [Color](#color)
	* [File](#file)
* [TOTI specific](#toti-specific)
	* [InputList](#inputlist)
	* [DynamicList](#dynamiclist)


## Textual and numeric

### Text

Simple text input. HMTL standard tag

Create filter: `Text.filter()`

Create input: `Text.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Size `setSize`: default text size
* Max Length `setMaxLength`: maximal allowed text size
* Min Length `setMinLength`: minimal required text length
* Default Value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled
* Placeholder `setPlaceholder`: placeholder

### Textarea

For large texts. HTML standard tag

Create input: `Textarea.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle`: label of input
* Max Length `setMaxLength`: maximal allowed text size
* Columns count `setCols`: count of columns
* Rows count `setRows`: count of rows
* Default Value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled
* Placeholder `setPlaceholder`: placeholder

### Email

Text input with check if contains '@'. HTML standard tag

Create input: `Email.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle`: label of input
* Default Value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled
* Placeholder `setPlaceholder`: placeholder

### Password

Text input where all letters are hidden. HTML standard tag

Create input: `Password.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle`: label of input
* Size `setSize`: default text size
* Max Length `setMaxLength`: maximal allowed text size
* Min Length `setMinLength`: minimal required text length
* Default Value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled
* Placeholder `setPlaceholder`: placeholder

### Number

Allow fill only numbers. HTML standard tag

Create filter: `Number.filter()`

Create input: `Number.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Step `setStep`: the legal number intervals
* Minimal value `setMin`: the minimal required value
* Maximal value `setMax`: the maximal allowed value
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

### Range

Allow fill only numbers. HTML standard tag

Create filter: `Range.filter()`

Create input: `Range.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Step `setStep`: the legal number intervals
* Minimal value `setMin`: the minimal required value
* Maximal value `setMax`: the maximal allowed value
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

## Determination of time

### Date

### Time
### Datetime
### Month
### Week

## Action

### Submit
### Button
### Reset

## Selection

### Checkbox
### Radiolist
### Select
### Option

## Special

### Hidden
### Color
### File

## TOTI specific

### Inputlist
### Dynamiclist