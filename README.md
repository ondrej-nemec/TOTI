<img src="logo.png" width="100">

# TOTI

[![](https://jitpack.io/v/ondrej-nemec/TOTI.svg)](https://jitpack.io/#ondrej-nemec/TOTI)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/ondrej-nemec/TOTI/blob/master/LICENSE)

TOTI - **T**ip **O**f **T**he **I**ceberg - is Java web framework. Provide HTTP(S) interface between users and your Java application. With TOTI you can create Rest API, generate HTML pages from templates or just web server (like f.e. Apache but begging)

Main purpose of this framework is create GUI for java servers f.e. in Industry 4.0 or IoT. TOTI framework can be used as MVC framework (like Symfony or Nette in PHP) but **this is not main purpose**.

* [Include in your project](#include-in-your-project)
* [Get started](#get-started)
	* [Initialize](#initialize)
	* [Modules](#modules)
	* [Register](#register)
	* [Tasks](#tasks)
	* [Controllers](#controllers)
		* [Request parameters validation](#request-parameters-validation)
		* [Routing](#routing)
		* [Responses](#responses)
		* [Permissions](#permissions)
		* [Injections](#injections)
	* [Templates](#templates)
	* [Grids](#grids)
	* [Forms](#forms)
	* [Exceptions](#exceptions)
* [How to do](#how-to-do)
	* [Login](#login)
	* [Change language](#change-language)
	* [Controller's method for grid and form](#controller-s-method-for-grid-and-form)
	* [Validator for Dynamic List](#validator-for-dynamic-list)
	* [Validator for Input List in Input List](#validator-for-input-list-in-input-list)
* Documentation
	* [List of filters and inputs](doc/controls.md)
	* [List of template tags](doc/tags.md)
	* [Validation rules](doc/validation-rules.md)
	
## Include in your project

TOTI uses for publication <a href="https://jitpack.io/">JitPack</a>. It allows you to include this project by using Gradle or Maven.

### Include using Gradle

Add this line to repositories section:

```gradle
maven { url 'https://jitpack.io' }
```
And this line to dependencies:

```gradle
implementation 'com.github.ondrej-nemec:TOTI:Tag'
```

If you would like use TOTI templating system on computer without JDK (JRE only) you have to add this line to dependencies, too:

```gradle
compile files("${System.properties['java.home']}/../lib/tools.jar")
```

For using TOTI JS functionality (forms, grids etc) include this in your HTML page:

```
<script src="/toti/totiJs.js"></script>
```

And optionally this:

```
<link rel="stylesheet" href="/toti/toti.css" />
```

## Get started

### Initialize

For initialization you have two ways. The first is quick and easy, the second allow you set everything in your way.

#### Quick initialization

Quick initialization is realized by `Application` class. After creating new instance, `Application`. Only one thing that is required is list of [modules](#modules).

```
Application app = new Application(modules);
```

1. Load configuration file. By default the file is `conf/app.properties` (can be in classpath or dir tree). Location can be changed before creating instance `Application.APP_CONFIG_FILE = "your conf file";`. See [List of server configuration](doc/server-configuration.md).
1. Prepare database and database migrations (see [JI Database](https://github.com/ondrej-nemec/javainit/tree/master/ji-database)).
1. Prepare all tasks defined in modules configurations.
1. Prepare class `HttpServer`

`Application` has two methods for controlling server: `start` and `stop`.

`Start` migrates all migrations, starts all tasks and then start the server.

`Stop` finish tasks and stop the server.

`Application` provide method `getTranslator`, too. This method returns translator initialized with translation files from modules (see [JI Translator](https://github.com/ondrej-nemec/javainit/tree/master/ji-translator)).

#### Custom initialization

If you do not want to use the first way, you can manage everythins yourself. For that you need `HttpServerFactory` class and - again - list of modules. After creating factory and before getting server, you can configure the server. [List of server configuration](doc/server-configuration.md)

```
HttpServerFactory factory = new HttpServerFactory();
// configuration
HttpServer server = factory.get(modules);
```

`HttpServer` has same methods like application: `start`, `stop` and `getTranslator`.

`Start` start the server.

`Stop` stop the server.

`getTranslator` returns translator initialized with translation files from modules (see [JI Translator](https://github.com/ondrej-nemec/javainit/tree/master/ji-translator)).

### Modules

`Module` represent one unit of your application, f.e. one gradle module. `Module` interface contains methods:

* `getName` - name of module. Have to be unique. If you have only one module, you can let it empty (not null).
* `getControllersPath` - path to module controllers in classpath. For example: directory tree `projectname/src/main/java/example/web/controllers` so here the path will be `example/web/controllers`. See [more about controllers](#controllers)
* `initInstances` -> `List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception;`

`Env` is class representing configuration file, `Registr` see [below](#register), `Database` was described upper and `Logger` is suggested logger - not nessessary need to use.

Example:

```
@Override
public List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception {
	ExampleDao dao = new ExampleDao(database);
	
	registr.addFactory(SignPageController.class, ()->new SignPageController());
	registr.addFactory(SignApiController.class, ()->new SignApiController());
	
	registr.addFactory(ExamplePageController.class, ()->new ExamplePageController());
	registr.addFactory(ExampleApiController.class, ()->new ExampleApiController(dao, logger));
	return Arrays.asList(new ExampleTask(dao));
}
```

Except of this, there are some optional methods:

* `addRouter` - you give `Router` as parameter. Allow you change one URL to another one. See [Router](#routing)
* `getTemplatePath` - specify path to module template folder. Default is `null` and means 'no folder'.
* `getTranslationPath` - specify path to module translations files. Default is `null` and means 'no translations'.
* `getMigrationsPath` - specify path to module Database migrations. Default is `null` and means 'no migrations'.

### Register

`Regist` is container for objects. Contains two types of objects: services and factories. Service is created by you and only once.
 On the other hand, factory is for creating new instance every time is needed (typically usage is for controllers).

Add service example:

```java
regist.addService("some-my-service", new MyService());
```

Get service example:

```
MyService myService = registr.getService("some-my-service", MyService.class);
```

Add factory example:

```
registr.addFactory(MyFactory.class, ()->{
	return new MyFactory();
});
// or
registr.addFactory("myFactory", ()->{
	return new MyFactory();
});
```

Get factory example (this is used internal very often, but probably no reason for using by you):

```
ThrowingSupplier<MyFactory> factory = registr.getFactory(MyFactory.class.getName());
```

### Tasks

Task in TOTI is something what starts with `Application` start and ends with `Application` stop. So `Task` interface contains only `start` and `stop` methods.
 **Threads are completely on you. This methods are blocking.**

### Controllers

Controller in TOTI accept client request and returns response. 
This type of class you have to develop and register to `Regist` as factory.
Every Controller is marked with `Controller` annotation. The annotation require URL as parameter. 
A method that can handle request is marked with `Action` annotation and returns `Response` instance (see [Response](#responses)). 
The annotation requires next part of URL as parameter.
Optionally you can specify which HTTP methods are allowed with `Method` annotation.
This annotation need list of `HttpMethod`. If is not this annotation present, all methods are allowed.

Both URLs filled in `Controller` and `Action` cannot start or end with `/`, but can be empty string.

Controller example:

```
@Controller("my-controller")
public class MyController {
	...
}
```

Method example:

```
@Action("my-action")
@Method({HttpMethod.GET, HttpMethod.POST})
public Response myMethod() {
	return null;
}
```

#### Request parameters

All request parameters can be passed to handling method.
You can ask for all request parameters:

```
public Response myMethod(@Params RequestParams params) {....
```

Or define each parameter separately:

```
public Response myMethod(@Param("id") Integer id, @Param("name") String name) {...
```

Or you can combine both ways:

```
public Response myMethod(@Param("id") Integer id, @Params RequestParams params) {...
```

If you wish read parameter from URL (for example `/entity/edit/1`), use instead of `@Param("id")` -> `@ParamUrl("id")`

[Validation](#request-parameters-validation) of `RequestParameters` is highly recommended.

Parameters can be casted to all primitives except `byte` and `char`. Next allow `List` and `Map` using [HTML list](https://github.com/ondrej-nemec/javainit/tree/master/ji-communication#request-parameters)
or from JSON. If you wish upload file, the parameter type is `UploadedFile` - see [Uploading files](https://github.com/ondrej-nemec/javainit/tree/master/ji-communication#request-parameters)

#### Request parameters validation

`Validator` class validate incoming values. Constructor requires one parameter: `boolean strict`. This parameter says if `RequestParameters` can contains more than specific (`strict == false`) values or only specified in this `Validator`  instance (`strict == true`). For validation of one request param you have to add `ItemRules` into `Validator`.

Example:

```
Validator v = new Validator(true)
	.addRule(ItemRules.forName("id", true).setType(Integer.class))
	.addRule(ItemRules.forName("name", false));
```

Here `RequestParameters` can contains only key `id` and optionally `name` but nothing more. And `id` must be castable to `Integer`. [Here](doc/validation-rules.md) is list of all `ItemRules` options.

However, instead of `boolean strict` you can specify default `ItemRules` that will be used if `ItemRules` is missing.

```
Validator v = new Validator(ItemRules.defaultRule().setType(Boolean.class))
	.addRule(ItemRules.forName("id", true).setType(Integer.class))
	.addRule(ItemRules.forName("name", false));
```

In this case, `RequestParameters` have to contains `id` as `Integer`, can contains `name` and can contains any other parameters if a value is `boolean`.

**How validate request:**

1. Inside you method calling `validate` method. This method returns Map of errors, where keys are input names. If empty, `RequestParameters` are OK.
1. Let TOTI validate parameters before calling your method:

Firstly add your `Validator` instance as service to register:

```
register.addService("uniqueServiceName", myValidatorInstance);
```

OR using `Validator` factory method

```
Validator.create("uniqueServiceName", true) // second parameter is 'strict'
	// ... add rule ...
```

And secondary add parameter to `Action` annotation of method:

```
// FROM
@Action("myAction")
// TO
@Action(value="myAction", validator="uniqueServiceName")
```

If `RequestParameters` will not valid, response with code 400 and list of errors is returned before calling your method.

#### Routing

Part of final URI is specified in `Controller` and `Action` annotations. If value is empty string, is ignored. Complete URI is looks like: `/<module-name>[/<path>]/<controller-anotation>/<action-annotation>[/<url-parameter>]`.

`module-name` - name of your module you specify in `Module`. If empty, is ignored.

`path` - the controller path specified in `Module` is base path for controllers for given module. Every sub packages are the path.

Example:

The controller path is `com.example.web.controllers`. This package contains `FirstController` class and sub package `api` with `SecondController` class. The module name is `example`. So, URL for `FirstController` will be `/example/first/...` and for second `/example/api/second/...`.

##### Changing one route to another

With `Router` class you can change one URI to another. Just:

```
router.addUrl("", "/home");
```

**NOTE:** `Router` is one instance for all modules, so one module can override configuration from another.
**NOTE:** This is not redirect. Just instead of content one URL use another.

#### Responses

1. File response - sends specified file as response (can be binary, absolute of relative path). Create: `Response.getFile(StatusCode code, String path)` OR `Response.getFile(String path)` (code is 200)
1. JSON response - sends given object (most time Map or List) as JSON see [JSON generation](https://github.com/ondrej-nemec/javainit/tree/master/ji-files#objects-and-json). Create: `Response.getJson(StatusCode code, Object toJson)` OR `Response.getJson(Object toJson)` (code is 202)
1. Text response - sends given text as response. Create: `Response.getText(StatusCode code, String text)` OR `Response.getText(String text)` (code is 200)
1. Redirect response - sends redirection response. Create: `Response.getRedirect(StatusCode code, String url)` OR `Response.getRedirect(String url)` (code is 307)
1. Template response - gives specified file from your template folder of current module and use as template to create response. See more about [Templates](#templates). Create: `Response.getTemplate(StatusCode code, String fileName, Map<String, Object> params)` OR `Response.getTemplate(String fileName, Map<String, Object> params)` (code is 200)
Example: module template path is `project-dir/core-module/templates` and `fileName` is `/entities/main.jsp`. In this case the file `project-dir/core-module/templates/entities/main.jsp` is used as template.

#### Permissions

TODO
* @secured
* user security factory
* identity
* odkaz na acl
* authenticator vx authorizator

#### Injections

Sometimes you need some class used by TOTI like `Translator`, `Authenticator` or something else. For that TOTI provide feature called Injection.

In your controller you define private/protected/public but not final attribute. Then you make standard setter (for `someVariable` -> `setSomeVariable`). At the end, annotate the variable. TOTI set value after creating controller instance but before calling action method.

Supported annotations:

* `Translate` - set `Translator` class with actual selected language.
* `Authenticate` - set `Authenticator` class
* `Authorize` - set `AuthorizatorHelper` class
* `ClientIdentity` - set `Identity` class

Example with `Translator`:

```
@Controller("url")
public class MyController {

	@Translate
	private Translator translator;
	
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}
	
}
```
### Templates

TOTI provide you simple templating system. Templates do not require special file format. You can create HTML template, JSON template or something else.

**Variables to template**

You can pass variables to your template. For printing variable in template, write: `${variable-name}`. If variable is object, you can call method on them. For example: variable with name `info` is instance of `Map`:

```
${info.get("overview")}<br>
${info.get("main")}
```

You can pass the variable to tags.

**NOTE:** TOTI automatically add parameter `nonce` and in response headers replace `{nonce}`. Nonce is some random string unique for request and is used secure your JS scripts.

```
<script src="..." nonce="${nonce}"></script>
```

Nonce is sended in header and same nonce is used on page. With this browser (check if your browser support it) will not link JS script with another nonce or without.

**Tags**

TOTI tags allow you f. e. merge more files to one, iterate `Map` or `Iterable` and define new variable. [List of all TOIT tags](doc/tags.md). This tags look very similar to HTML tags, but can be used in any types of templates.

Example of creating String variable `greetings`, set value to "Hello, World!" and print.

```
<t:var type="String" name="greetings" />
<t:set name="greetings" value="Hello, World" />
<t:out name="greetings" />
```

### Grids

Showing data in grid or table or dataset - what you like. For build grid on page, you have to do this steps:

1. Create `Grid` instance and configure. See [Grid configuration](#grid-configuration)
1. Pass your instance to template as parameter
1. Use `<t:control name="name-of-your-parameter" />` (print grid) or `<t:control name="name-of-your-parameter" jsObject/>` (get JS object - use only in JS) in template
1. Create Rest API for grid (technically this step should be first). Example:

```
public Response getAll(
		@Param("pageIndex") Integer pageIndex,
		@Param("pageSize") Integer pageSize,
		@Param("filters") Map<String, Object> filters,
		@Param("sorting") Map<String, Object> sorting
	) {
	// ...
}
```

What is what:
* `pageIndex`: index of current page
* `pageSize`: how many result rows are requested
* `filters`: condition for values. Keys are names of columns, values contains expected value to be exactly or like
* `sorting`: how order the data. Keys are columns, value is always "ASC" or "DESC"

This action has to return JSON response with this parameters:

* `itemsCount`: count of items that **can** be displayed - used for paging
* `pageIndex`: index of current page
* `data`: list of object. Each item in list is one row. 

[How to do it easy](#controller-s-method-for-grid-and-form)

#### Grid configuration

### Forms

### Exceptions

## How to do

### Login

### Change language

### Controller's method for grid and form

### Validator for Dynamic List

### Validator for Input List in Input List
– zmínit single value a skupinu hodnot
