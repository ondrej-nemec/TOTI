# TOTI Application

Main part of TOTI. Using this package, you can start your own TOTI application.

* [Include in project](#include-in-project)
* [Initialize](#initialize)
    * [Server](#server)
    * [Application](#application)

## Include in project

```
dependencies {
		implementation 'com.github.ondrej-nemec:TOTI:toti-application:Tag'
}
```

## Initialize

### Server

First of all we need create instance of `TotiServerFactory`.

```
TotiServerFactory serverFactory = new TotiServerFactory();
// here we can configure or let in default
```

Optionally, configuration can be automatically loaded from `.properties` file.

```
TotiServerFactory serverFactory = new TotiServerFactory(path/to/config);
// here we can configure - it overrides configuration from file
```

<table>
	<tr>
		<th>Name</th>
		<th>Method</th>
		<th>Config key</th>
		<th>Default value</th>
		<th>Explonation</th>
	</tr>
	<tr>
		<td>Application port</td>
		<td>setPort</td>
		<td>http.port</td>
		<td>80</td>
		<td>On which port will be application listening.</td>
	</tr>
	<tr>
		<td>Charset</td>
		<td>setCharset</td>
		<td>http.charset</td>
		<td>UTF-8</td>
		<td>Request and response encoding.</td>
	</tr>
	<tr>
		<td>Maximal body size</td>
		<td>setMaxRequestBodySize</td>
		<td>http.max-request-size</td>
		<td>NULL</td>
		<td>Limit for request body size in bytes. NULL means no limit.</td>
	</tr>
	<tr>
		<td>Read timeout</td>
		<td>setReadTimeout</td>
		<td>http.read-timeout</td>
		<td>60000</td>
		<td>How long will connection persist if no communication. In ms.</td>
	</tr>
	<tr>
		<td>Thread pool</td>
		<td>setThreadPool</td>
		<td>http.thread-pool</td>
		<td>5</td>
		<td>Thread pool for incoming request.</td>
	</tr>
</table>


After the configuration is finished, we can create `TotiServer`. Method requires instance of `org.apache.logging.log4j.Logger`.

```
TotiServer server = serverFactory.create(logger);
```

Now, we can run server.

```
server.start();
```

TOTI is running and is listening on given port. But still is it not hadling any request. To do it, we need add some applications.

Server can be stopped using `stop` method.

### Application

Application is - in term of web servers - virtual host. TOTI server selects between applications using `Host` header.

We can add application to server before server start and after server start, too.

How add application:

```
Application application = server.addApplication(applicationId, (env, applicationFactory)->{
	// here we can configure
	return applicationFactory.create(Arrays.asList(), logger);
}, host1, host2);
```

* `applicationId`: unique string representing application.
* `host{n}`: one or more host names. Application is called if `Host` header is in this array.

Server provides us `Env` and `ApplicationFactory`.



