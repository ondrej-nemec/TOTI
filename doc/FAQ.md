Parametrized URL in build
Button.create(
					Link.get()
					.addUrlParam("{id}")
					.addGetParam("name", "{text}")
					.create(getClass(), c->c.syncButtonLink(0, null)),
					"sync"
)


## How to do

### Login in JS

First of all, to all pages add this script:

```
totiAuth.onLoad();
```

**Only for asynchronious log in.**¨

After success login, call this:

```
totiAuth.login(response, {
	logout: {
		url: "url-for-log-out",
		method: "log-out-http-method"
	},
	refresh: {
		url: "url-for-refresh",
		method: "refresh-http-method"
	}
});
// optionaly
location.reload();
```

### Change language



FATAL 2022-06-14 09:33:27 toti [pool-client-request-thread-1] (ResponseFactory.java:138) - Uncaught exception

java.net.SocketException: Software caused connection abort: socket write error

     at java.net.SocketOutputStream.socketWrite0(Native Method) ~[?:1.8.0_201]

     at java.net.SocketOutputStream.socketWrite(SocketOutputStream.java:111) ~[?:1.8.0_201]

     at java.net.SocketOutputStream.write(SocketOutputStream.java:155) ~[?:1.8.0_201]

     at java.io.BufferedOutputStream.write(BufferedOutputStream.java:122) ~[?:1.8.0_201]

     at java.io.FilterOutputStream.write(FilterOutputStream.java:97) ~[?:1.8.0_201]

     at ji.socketCommunication.http.parsers.StreamReader.write(StreamReader.java:18) ~[main/:?]

     at ji.socketCommunication.http.parsers.ExchangeFactory.write(ExchangeFactory.java:163) ~[main/:?]

     at ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:66) ~[main/:?]

     at ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:40) ~[main/:?]

     at ji.socketCommunication.Server.lambda$1(Server.java:195) ~[main/:?]

     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[?:1.8.0_201]

     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[?:1.8.0_201]

     at java.lang.Thread.run(Thread.java:748) ~[?:1.8.0_201]

FATAL 2022-06-14 09:33:27 toti [pool-client-request-thread-1] (Server.java:202) - Reading from socket

java.io.IOException: java.net.SocketException: Software caused connection abort: socket write error

     at ji.socketCommunication.http.ResponseFactory.catchException(ResponseFactory.java:15) ~[main/:?]

     at toti.ResponseFactory.catchException(ResponseFactory.java:139) ~[main/:?]

     at ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:42) ~[main/:?]

     at ji.socketCommunication.Server.lambda$1(Server.java:195) ~[main/:?]

     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[?:1.8.0_201]

     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[?:1.8.0_201]

     at java.lang.Thread.run(Thread.java:748) ~[?:1.8.0_201]

Caused by: java.net.SocketException: Software caused connection abort: socket write error

     at java.net.SocketOutputStream.socketWrite0(Native Method) ~[?:1.8.0_201]

     at java.net.SocketOutputStream.socketWrite(SocketOutputStream.java:111) ~[?:1.8.0_201]

     at java.net.SocketOutputStream.write(SocketOutputStream.java:155) ~[?:1.8.0_201]

     at java.io.BufferedOutputStream.write(BufferedOutputStream.java:122) ~[?:1.8.0_201]

     at java.io.FilterOutputStream.write(FilterOutputStream.java:97) ~[?:1.8.0_201]

     at ji.socketCommunication.http.parsers.StreamReader.write(StreamReader.java:18) ~[main/:?]

     at ji.socketCommunication.http.parsers.ExchangeFactory.write(ExchangeFactory.java:163) ~[main/:?]

     at ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:66) ~[main/:?]

     at ji.socketCommunication.http.RestApiServer.serve(RestApiServer.java:40) ~[main/:?]

     ... 4 more
