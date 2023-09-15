static věci
	TemplateFactory
	Link
	LoadUrls


.setTokenExpirationTime(30 * 1000) // 30s
.setMaxRequestBodySize(1024 * 10) // 10 kB - !! for all body
.setLanguageSettings(new LanguageSettings(Arrays.asList(new Locale("en", true, Arrays.asList()))))
// .setDevelopIpAdresses(Arrays.asList()) // no develop ips
.setUseProfiler(true)



	
	<script src="/js/totiSortedMap.js"></script>
	<script src="/js/totiImages.js" ></script>
	<script src="/js/totiUtils.js" ></script>
	<script src="/js/totiTranslations.js" ></script>
	<script src="/js/totiStorage.js" ></script>
	<script src="/js/totiProfiler.js" ></script>
	<script src="/js/totiLang.js" ></script>
	<script src="/js/totiLoad.js" ></script>
	<script src="/js/totiAuth.js" ></script>
	<script src="/js/totiFormDefaultTemplate.js" ></script>
	<script src="/js/totiGridDefaultTemplate.js" ></script>
	<script src="/js/totiFormCustomTemplate.js" ></script>
	<script src="/js/totiGridCustomTemplate.js" ></script>
	<script src="/js/totiDisplay.js" ></script>
	<script src="/js/totiControl.js" ></script>
	<script src="/js/totiForm.js" ></script>
	<script src="/js/totiGrid.js" ></script>


# TOTI TODO taskkist

## Known issue

* sync form - exclude not working
* dokumentace
  * popsat index při dir requestu
  * přidat placeholder parameter nebo ho smazat

## FIX and Bugs

* Check: Cross Site Scripting (DOM Based)
* documentation - filters and inputs - check methods(placeholder)
* incomplete HTML tag in JS comment cause exception
* divny batch log

Run server/application v omezeném režimu – jen db, Je migrate,….

## TODO

* Template
	* Correct HTML parsing - paired and unpaired tags
	* Tags: add alpha-numeric check for selected parameters
	* Variable: escape URL with check of local
	* Alphanumeric check:
		* Block
		* Catch
		* Include
		* variable define
* Control
	* Add title element (tooltip)
	* u gridu jsou vyžadovány proměné v body->nullpoinerexception
* JS
	* JS method: take html element or element content and use as link. Element will be invisible
	* Range, Color: actual value
* DB viewer
	* Db viewer by měl umět srazit db do několika málo migrací – view nechat samostatně - diff
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
		https://stackoverflow.com/questions/9368764/calculate-size-of-object-in-java/9368834#9368834

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
