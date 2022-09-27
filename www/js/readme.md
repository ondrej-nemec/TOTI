# Priprava dokumentace

## TotiGrid



## totiControl

// params - all added to label
label(String forInput, String tille, object params = {}): Element (<label>)

// see inputs.button
button(object attributes, async = true): Element (<button>|<a>)

/* attributes (type required, others optional):
* type: String - select, text, number, button (inputs.button)...
*/
input(object attributes): Element|null

inputs: object - internal

	_createInput(String object, object attributes): Element
	_createOptionalInput(String object, object attributes): Element
	radioList(object attributes): Element (<div>)
	textarea(object attributes): Element
	datetime(object attributes): Element - jen pokud datetime-local není podporován nebo u gridu

	/* attributes (all optional):
	* value|title: String - inner text
	* tooltip: String - hover text
	* action: String|object|function - onClick
		* string - call function
		* function - see getAction
		* object - see getAction
	* class: String|list
	* icon: String (create <i> and set class)
	* other added
	*/
	button(Element button, object attributes): Element (button)

	/*
	* list of:
		* value: String
		* title: String
		* disabled (optional): boolean
		* optgroup (optional): String
	*/
	option(object attributes): Element(<option>)

	/*
	* opions: List of object, see option
	* load (optional): List of 
		* url: String
		* method: String
		* params: object
	* depends
	* optionGroup (optional): String
	*/
	select(object attributes): Element (<select>)

// display date and time to local string
parseValue(String type, String value): String

/*
* submitConfirmation: function (Optional)
* href: String
* method: String
* params: object
* onSuccess: function|string (see totiUtils.execute) - optional
* onFailure: function|string (see totiUtils.execute) - optional
*/
getAction(object settings): function
	- function return false if not confirmed


## totiDisplay

prompt(String message, String defValue = ""): String

confirm(String message): boolean

alert(String message): void

flash(String severity, String message): void

## totiAuth

-variableToken
-variableConfig
-function(response): customRefreshHandler

getToken(): String|null

logout(String url = null, String method = "post"): void

login(String token, Object config = null): void
config:
	- url: refresh url
	- method: refresh method

startTokenRefresh(): boolean - true started, false not start

// expiredIn in ms
// single shot
// pokud je volano rucne, muze zlobit dalsi pusteni
refresh(String url, String method, int expredIn): void


## totiUtils

sleep(ms): Promise

// get params from url
parseUrlToObject(String url): object

// replace variables in string with values '{}'
parametrizedString(String string, Object params): String

browser(): String: opera|firefox|safari|IE|edge|edge-chromium|chrome

// run callback with given args
// run function given by name with given args
// run JS code (eval === true)
execute(function|string callback, array args = [], boolean evaluate = false): Object|void

clone(Object object): Object

getCookie(Stringg name): String|null

// if path is null, current url is used
setCookie: function(name, value, maxAge = null, path = '/') | null

// called after DOMContentLoaded
printStoredFlash(): void

// replace element in DOM, old element params are copied to new one
replaceElement: function(container, selector, newElement, excludeAttributes = [])

## totiLang

-variableName

changeLanguage(String language): void

getLang(): String


## totiStorage

saveVariable(String name, Object value): void

getVariable(String name): Object

removeVariable(String name): void


## settings

až po refactoringu

## translations

 až po refactoringu

## images

až po refactoringu