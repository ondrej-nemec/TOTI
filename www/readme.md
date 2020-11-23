# TOTI Control Documentation

## TOTI Lang

## TOTI Control
**Object:** totiControl

### Inputs
**Object:** inputs

label: function (forInput, title) <br>
radio: function (params = {}) <br>
checkbox: function (params = {}) <br>
hidden: function (params = {}) <br>
/* sugested params: step, max, min */ <br>
number: function (params = {}) <br>
/* sugested params: size, minlength, maxlength */ <br>
text: function (params = {}) <br>
/* sugested params: size, minlength, maxlength */ <br>
password: function (params = {}) <br>
email: function (params = {}) <br>
datetime: function (params = {}) <br>
select: function (options, params = {}) <br>
option: function(value, title, params = {}) <br>
onClick: function|object:href,method,async,submitConfirmation <br>
button: function (onClick, title = "", params = {}, renderer = null) <br>
submit: function (async = true, submitConfirmation = null, params = {}) <br>

### Make ajax request
**Object:** load
ajax: function(url, method, data, onSuccess, onFailure, headers)
link: function(url, method, data, headers)
