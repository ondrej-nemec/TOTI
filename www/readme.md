# TOTI Control Documentation

## TOTI Lang
**Object:** totiLang

* pages
  * title - *Translations messages*
  * first - *Translations messages*
  * previous - *Translations messages*
  * next - *Translations messages*
  * last - *Translations messages*
* actions
  * select - *Translations messages*
  * execute - *Translations messages*
  * noSelectedItems - *Translations messages*
* gridMessages
  * noItemFound - *Translations messages*
  * loadingError - *Translations messages*
* formMessages
  * saveError - *Translations messages*
  * bindError - *Translations messages*

### totiLang

variableName - *internal* <br>
function changeLanguage(string language) <br>
function getLang(): string <br>
function getLangHeader(): object - key: Accept-Language <br>

## TOTI Images
**Object:** totiImages

* arrowUp - *base64 image*
* arrowDown - *base64 image*
* cross - *base64 image*

## TOTI Control
**Object:** totiControl

### Inputs
**Object:** inputs <br>

#### _createInput: function (type, attributes, data = {})

Internal

#### function label(string forInput, string title, params = {})

#### function file(params = {})

#### textarea: function(params = {})
 sugested params: cols, rows

#### function hidden(params = {})

#### function radio(params = {})

#### function checkbox(params = {})

#### function number(params = {})

sugested params: step, max, min

#### function text(params = {})

sugested params: size, minlength, maxlength 

#### function password(params = {})

sugested params: size, minlength, maxlength

#### function email(params = {})

#### function datetime(params = {})

#### function select(options, params = {})

options: list of options objects

#### function option(string value, string title, params = {})

#### function button(onClick, title = "", params = {}, renderer = null)

onClick: function OR object
	* string href
	* string method
	* boolean async
	* function submitConfirmation() [optional]: boolean
	* string onSuccess [optional]
	* string onError [optional]
  * string type [optional] - values: basic, danger, warning, info, success

renderer: html

#### function submit(async = true, submitConfirmation = null, params = {})

function submitConfirmation(): boolean <br>
parent form element require:
* action
* method

params contains:
* string onSuccess[optional] - name of custom js function
* string onFailure [optional] - name of custom js function
* string redirect [optional] - {x} will be replaced

### load
// TODO