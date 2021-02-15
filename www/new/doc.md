# TOTI JS

## Settings

* flashTimeout, default 0

------------------------

## Images

* arrowUp
* arrowDown
* cross

------------------------

## Translations

* pages
  * title
  * first
  * previous
  * next
  * last
* actions
  * select
  * execute
  * noSelectedItems
 * gridMessages
   * noItemsFound
   * loadingError
 * formMessages
   * saveError
   * bindError

------------------------

## Browser storage
* totiStorage

### Save

void saveVariable(String name, Object value)

### Get

Object getVariable(String name)

### Remove

void removeVariable(String name)

------------------------

## Language
* totiLang
* Depends: storage

**Language in format:** *lang*_*country*

### Change language

void changeLanguage(String language)

### Get language

String getLang()

### Get language headers

jsonObject getLangHeader()

------------------------

## TOTI Load
* totiLoad
* Depends: auth and lang - can be removed

### Asynchronious request

void async(String url, String method, jsonObject data, jsonObject headers, function onSuccess, function onFailure)

### Synchronious request

//void sync(String url, String method, jsonObject data, jsonObject headers, function onSuccess, function onFailure)

### Request

//void request(boolean async, String url, String method, jsonObject data, jsonObject headers, function onSuccess, function onFailure)

### Link

**TODO**

### Get Headers

**TODO move somewhere** depends on totiAuth and totiLang
getHeaders()

------------------------

## Authentication
* Depends: storage, translation and load

### Get Authentication header

jsonObject getAuthHeader()

### Get token

String|null getToken()

### Set token refresh
*internal*

setTokenRefresh(String token = null, int period = -1)

### Logout

void logout()

### Login

void login(String token, jsonObject config)

**Config**

* logout
  * url
  * method
* refresh
  * url
  * method

### Check logged user

void onLoad()

------------------------

## TOTI Display
* totiDispaly
* Require: HTML element with id 'flash'
* Depends: image, storage

### Prompt

String prompt(String message, String defValue = "")

### Confirm

boolean confirm(String message)

### Alert

void alert(String message)

### Flash message

void flash(String severity, String message)

### Add stored flash message

void storedFlash(String severity, String message)

### Show stored flash messages

* called automatically after document loaded

void printStoredFlash()

------------------------

## Control
* totiControl
* Depends: load, display

### Create label

HtmlElement label(String forInput, String title, jsonObject params = {})

### Create button
* Recomended attribute: title
HtmlElement button(jsonObject attributes)

### Create any other input

Create input include textarea, select and option

* attributes must contains 'type'*
* Recomended attribute for select: array options
* Recomended attribute for option: String title

HtmlElement input(jsonObject attributes)

### internal

* inputs._createInput(String type, jsonObject attributes) 
* inputs.textarea(jsonObject params)
* inputs.select(jsonObject params)
* inputs.option(jsonObject params)
* inputs.radiolist(jsonObject params)

## Form

```
var form = new TotiForm(config);

form.init(String elementIdWhereWillBePrinted, String uniqueFormName);
```

### Config

* boolean editable
* string action (only if editable)
* string method (only if editable)
* fields - array of jsonObject:
  * string id
  * string title (optional)
  * string type - checkbox, textarea, select, radiolist,....
    * button
      * href, method, boolean ajax, string confirmation (optional)
* bind (optional)
  * string url
  * string method
  * jsonObject params
  * string beforeBind (optional, function name)
  * string afterBind (optional, function name)
  * string onFailure (optional, function name)

