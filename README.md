<img src="logo.jpg" width="100">

# TOTI

[![](https://jitpack.io/v/ondrej-nemec/TOTI.svg)](https://jitpack.io/#ondrej-nemec/TOTI)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg) ](https://github.com/ondrej-nemec/TOTI/blob/master/LICENSE)

TOTI - **T**ip **O**f **T**he **I**ceberg - is Java web framework. Provide HTTP(S) interface between users and your Java application. With TOTI you can create Rest API for your app, generate HTML pages from templates or just run web server (like f.e. Apache but begging).

Main purpose of this framework is create GUI for java server applications f.e. in Industry 4.0 or IoT. And is prepared for easy embedded into desktop applications. TOTI framework can be used as MVC framework (like Symfony or Nette in PHP, or ASP.NET in C#).


TOTI is modular. Except base part, contains several libraries and extensions. Libraries can be used separately, independently of TOTI. Users can create custom extensions.

## Get started

* Libraries
	* [Templating](toti-libs/toti-templating): generate HTML (as string) from template
* [Application](toti-application): base part, nessessary for running TOTI application
* Extensions
	* [Templating](toti-extensions/te-templating): add templating system using *Templating* library
	* [Translator](toti-extensions/te-translator): add implementation of *Translator* 
	* [Validator](toti-extensions/te-validator): add tools for validating request parameters
	* [Database](toti-extensions/te-database): 
	* [UI](toti-extensions/te-ui): add UI elements configured in Java and printed using JS


## Add to your application

We use [JitPack](https://jitpack.io) for publishing.

Following code add full TOTI to your application. For adding just part, see given part.

If you are not using Gradle, see [TOTI on JitPack](https://jitpack.io/#ondrej-nemec/TOTI) for more options.


Add the JitPack repository to your build file:

```
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency:

```
dependencies {
		implementation 'com.github.ondrej-nemec:TOTI:Tag'
}
```


<hr>

Documentation is available on [Official website](https://ondrej-nemec.github.io/TOTI)
