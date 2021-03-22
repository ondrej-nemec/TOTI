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

Picker for date with localized messages. HTML standard tag

Create filter: `Date.filter();`

Create input: `Date.filter(String name, boolean required);`

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

### Time

Picker for time with localized messages. HTML standard tag

Create filter: `Time.filter();`

Create input: `Time.filter(String name, boolean required);`

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled
* Time deep `setStep`: "1" means use hours, minutes and hours, "60" means use hours and minutes, "3600" means use only hours

### Datetime

Picker for date and time with localized messages. Some browsers like Firefox not support `datetime-local` so for Firefox is used little TOTI modification.

Create filter: `Datetime.filter();`

Create input: `Datetime.input(String name, boolean required);`

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled
* Time deep `setStep`: "1" means use hours, minutes and hours, "60" means use hours and minutes, "3600" means use only hours
* Use full value `setStrict` (effect filter only): if false allows use only date or only time

### Month

Create filter: `Month.filter();`

Create input: `Month.filter(String name, boolean required);`

Picker for selecting month with localized messages. HTML standard tag. Not supported by some browsers like Firefox.

### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

### Week

Picker for selecting week with localized messages. HTML standard tag

Create filter: `Week.filter();`

Create input: `Week.filter(String name, boolean required);`

### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

## Action

### Submit

Submit form. HTML standard tag

Create input `Submit.create(String title, String name);

### Optional parameters
* Send form asynchroniously `setAsync`

Only for async:

* Redirect after success `setRedirect`: URL redirect to. If URL `{id}` is it replaced with input 'id' from form.
* Do after success `setOnSuccess`: name of JS function. As parameters are used (in order) response, submit and form.
* Do after failure `setOnFailure`: name of JS function. As parameters are used (in order) response, submit and form.
* Confirmation before `setConfirmation`: text of confirmation. If no text set, no confirmation appear.

### Button

Navigate from form but not submit values. HTML standard tag

Create input: `Button.create(String url, String name);`

If used in grid, you can parametrize URL with '{*name-of-parameter*}'. F.e. '/{name}/edit/{id}' will be '/cars/edit/2'

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Asynchronious request `setAsync`: if send request synchroniously (redirect) or not
* Style `setType`: set type of button, just visual

Settings only for async:

* Request method `setMethod`: GET, POST,...
* Do after success response `setOnSuccess`: name of JS function called after success response. Response is used as parameter
* Do after failure response `setOnFailure`: name of JS function called after failure response. Response is used as parameter
* Confirmation before `setConfirmation`: text of confirmation. If no text set, no confirmation appear. If used in grid, you can parametrize URL with '{*name-of-parameter*}'.

### Reset

Return form state to default values. HTML standard tag

Create input: `Reset.create(String name);`

#### Optional parameters

* Title `setTitle` (effect input only): label of input

## Selection

### Checkbox

For Yes/No selection. HTML standard tag

Create input: `Checkbox.input(String name, boolea required)`

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled
* Values render `setValuesRender(String checked, String notChecked)`: how will be displayed in not-editable form. Default Yes and No

### Radiolist

Show list of radio (HTML standard input).

Create input: `RadioList.input(String name, boolean required, Map<String, String> radios)`. Radios are value-title map.

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

### Select

Select from list of values. HTML standard tag

Create filter: `Select.filter(List<Option> options)`. For `Option` see [Option](#option)

Create input: `Select.input(String name, boolean required, List<Option> options)`. For `Option` see [Option](#option)

#### Optional parameters

* Title `setTitle` (effect input only): label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

Options into select can be loaded from URL. For required data format see TODO EntityDao.

Load data from URL:

```
select.setLoadData(String url, String method);
// or with request parameters
select.setLoadData(String url, String method, Map<String, String> params);
```

If you load data from URL, you can specify which Option Group have to be showed in this select:

```
select.setShowedOptionGroup(String optionGroup);
```

### Option

Child element of select. HTML standard tag

Create into filter/input: `Option.create(String value, String title)`

#### Optional parameters

* Option Group `setOptGroup`: name of Option Group this option belong
* Disabled `setDisabled`: change disabled

## Special

### Hidden

Value is hidden. HTML standard tag

Create input: `Range.input(String name)`

#### Optional parameters

* Default value `setDefaultValue`: default value

### Color

Show color picker. HTML standard tag

**NOTE:** this tag not support empty value

Create input: `Color.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle`: label of input
* Default value `setDefaultValue`: default value
* Disabled `setDisabled`: change disabled

### File

For uploading file. HTML standard tag

Create input: `File.input(String name, boolean required)`

#### Optional parameters

* Title `setTitle`: label of input
* Disabled `setDisabled`: change disabled

## TOTI specific

### InputList

`InputList` is container for other inputs. These inputs are available on page with name `InputListName[indexOfInput][inputName]`.

Create input: `InputList.input(String name)`

Add input to list: `inputList.addInput(Input input);`

### DynamicList

In `DynamicList` you define input. This input appears on page n-times depends on Add/Remove buttons and binding. If you wish bind values to `DynamicList`, in values is required List on DynamicListName key.

Example:

```
{
	first: "some-value",
	second: "another-value",
	dynamic: [
		"first",
		123,
		false
	]
}
```

Add input to list: `dynamicList.addInput(Input input);`

#### Optional parameters

* Title `setTitle`: label of input
* Use Add Button `useAddButton`: if user can add input
* Use Remote Button `useRemoteButton`: if user can remove input
