# TOTI TODO taskkist

## Known issue

## FIX and Bugs

* sync form sending is not working
* create style of exception and error page, profiler, viewer, profiler bar, welcome page
* Check: Cross Site Scripting (DOM Based)
* JS not editable form: select and date time not working
* Range, Color: actual value
* examples/samples/grid/filters.jsp remove develop links

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
	* Grid: add on row translation etc.
	* Add title element (tooltip)
	* Grid: page size hidden value. set at init, user cannot change
* JS
	* TotiLoad: remove deprecated sync calling Synchronous XMLHttpRequest on the main thread is deprecated because of its detrimental effects to the end user's experience. For more help, check https://xhr.spec.whatwg.org/.
	* JS method: take html element or element content and use as link. Element will be invisible
* EntityDao
	* some substituion in sorting for substituted items
	* allow create entity with translator
* DB viewer
* Profiler - improve
* Logging - new Log4j or some general way
* Routiong - add lang to route
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
	* Grid
		* Filtering: switch for like/equals, add to EntityDao
		* Sorting: sort by something replaced, like sort by id but displayed name
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
	* Support for grid like/equals
	* column names add alpha-numeric check
* Allow user defined error pages
* User data saved in token/loaded by service from register + switch
* Allow xml language configuration
* "Nice URL" - translations, language in URL
* Profiler
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();

## Proposal

* Set access rules for dirs and files in www folder
* TemplateParser: charset for template reading TemplateParser:87
* HTML to PDF
* Split validator by type? I need check number length...
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
