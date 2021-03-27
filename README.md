<img src="logo.png" width="100">

# TOTI

[![](https://jitpack.io/v/ondrej-nemec/TOTI.svg)](https://jitpack.io/#ondrej-nemec/MVC)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/ondrej-nemec/TOTI/blob/master/LICENSE)

TOTI - **T**ip **O**f **T**he **I**ceberg - is Java web framework. Provide HTTP(S) interface between users and your Java application. With TOTI you can create Rest API, generate HTML pages from templates or just web server (like f.e. Apache but begging)

Main purpose of this framework is create GUI for java servers f.e. in Industry 4.0 or IoT. TOTI framework can be used as MVC framework (like Symfony or Nette in PHP) but **this is not main purpose**.

* [Include in your project](#include-in-your-project)
* [Get started](#get-started)
	* [Initialize](#initialize)
	* [Modules](#modules)
	* [Registr](#registr)
	* [Tasks](#tasks)
	* [Controllers](#controllers)
	* [Templates](#templates)
	* [Grids](#grids)
	* [Forms](#forms)
	* [Routing](#routing)
	* [Responses](#responses)
	* [Request parameters validation](#request-parameters-validation)
	* [Injections](#injections)
	* [Permissions](#permissions)
* [How to do](#how-to-do)
	* [Login](#login)
	* [Change language](#change-language)
	* [Controllers method for grid and form](#controllers-method-for-grid-and-form)
* Documentation
	* [List of filters and inputs](doc/controls.md)
	* [List of template tags](doc/tags.md)
	* [Validation rules](doc/validation-rules.md)
	
## Include in your project

TOTI uses for publication <a href="https://jitpack.io/">JitPack</a>. Allow you include this project by using Gradle or Maven.

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

``getTranslator` returns translator initialized with translation files from modules (see [JI Translator](https://github.com/ondrej-nemec/javainit/tree/master/ji-translator)).

### Modules

`Module` represent one unit of your application, f.e. one gradle module. `Module` interface contains methods:

* `getName` - name of module. Have to be unique. If you have only one module, you can let it empty (not null).
* `getControllersPath` - path to module controllers in classpath. For example: directory tree `projectname/src/main/java/example/web/controllers` so here the path will be `example/web/controllers`. See [more about controllers](#controllers)
* `initInstances` -> `List<Task> initInstances(Env env, Registr registr, Database database, Logger logger) throws Exception;`

`Env` is class representing configuration file, `Registr` see below, `Database` was described upper and `Logger` is suggested logger - not nessessary need to use.

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

### Registr

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

#### Routing

#### Responses

#### Request parameters validation

#### Injections

#### Permissions

### Grids

### Forms

### Templates

## How to do

### Login

### Change language

### Controllers method for grid and form