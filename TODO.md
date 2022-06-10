# TOTI TODO taskkist

## Known issue

* sync form - exclude not working

## FIX and Bugs

* Check: Cross Site Scripting (DOM Based)
* documentation - filters and inputs - check methods(placeholder)
* JS lib doc
* missing styles after error page

## TODO

* Template
	* Tags: add alpha-numeric check for selected parameters
	* Variable: escape URL with check of local
	* Alphanumeric check:
		* Block
		* Catch
		* Include
		* variable define
* Control
	* Check firefox datetime with step 60
	* Add title element (tooltip)
	* Grid: page size hidden value. set at init, user cannot change
* JS
	* TotiLoad: remove deprecated sync calling Synchronous XMLHttpRequest on the main thread is deprecated because of its detrimental effects to the end user's experience. For more help, check https://xhr.spec.whatwg.org/.
	* JS method: take html element or element content and use as link. Element will be invisible
	* Range, Color: actual value
* DB viewer
* Profiler - memory and CPU graphs, log dir requests
* Permissions - Rule - owner ids will not be supplier but list ?
* Routing
	* add lang to route
	* translate routes
* Tests:
	* Improve parsing tests - TODOs
	* Register
	* Security
	* Load Urls
	* Validator

## Improvement

* Security
	* Domains with OR. Domain group?
* Template <%!  <%@  -- import?
* Rename User to TotiUser ?
* JS
	* conditions - relations between inputs (if filled - display/max-length/...)
	* datalist - will be working like select - defined data + load
* Control
	* Add ZonedDateTIme - need something in JS
	* Grid
		* Filtering: switch for like/equals
		* autorefresh
	* Form
		* submit url - replace 'id' - custumize pattern, keys
		* some simple api for dynamic input displaying
		* DynamicList: JS api for add/remove
* Validator
	* Relation between parameters
	* Size - not less not more
	* Max/Min value include/exclude
	* Automatic cast map to list - maybe in JI
* EntityDao
	* column names add alpha-numeric check
* User data saved in token/loaded by service from register + switch
* Allow xml language configuration
* Profiler
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();

## Proposal

* Set access rules for dirs and files in www folder
* TemplateParser: charset for template reading TemplateParser:87
* HTML to PDF
* Grid: inrow form, dropdown rows
* More authentication types
* Add HTML tags:
	* fieldset (optionally legend)
	* progress
	* input
		* url
		* image
		* search
		* tel
