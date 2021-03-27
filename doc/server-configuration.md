# Server Configuration

## HTTP server

All values are optional. If no value specified, default used.

<table>
	<tr>
		<th>Name</th>
		<th>Default value</th>
		<th>Description</th>
		<th>Method</th>
		<th>Property name</th>
	</tr>
	<tr>
		<td>Port</td>
		<td>80</td>
		<td>Port where will be application listening</td>
		<td>setPort(int port)</td>
		<td>http.port</td>
	</tr>
	<tr>
		<td>Thread pool size</td>
		<td>5</td>
		<td>How many thread can server use for web requests. Default value is good for depeloping not for production</td>
		<td>setThreadPool(int size)</td>
		<td>http.thread-pool</td>
	</tr>
	<tr>
		<td>Read timeout</td>
		<td>60000</td>
		<td>Reading timeout in miliseconds</td>
		<td>setReadTimeout(int timeout)</td>
		<td>http.read-timeout</td>
	</tr>
	<tr>
		<td>Response headers</td>
		<td>Arrays.asList("Access-Control-Allow-Origin: *")</td>
		<td>List of response headers. </td>
		<td>new ResponseHeaders(List<String> headers)<br>setHeaders(ResponseHeaders headers)</td>
		<td>http.headers - splited by '|'</td>
	</tr>
	<tr>
		<td>Charset</td>
		<td>UTF-8</td>
		<td>Charset</td>
		<td>setCharset(String charset)</td>
		<td>http.charset</td>
	</tr>
	<tr>
		<td>Temp folder</td>
		<td>temp</td>
		<td>Folder for TOTI temporary files</td>
		<td>setTempPath(String path)</td>
		<td>http.temp</td>
	</tr>
	<tr>
		<td>Resource folder</td>
		<td>www</td>
		<td>Resource (or 'www') folder. Files here will be available in brower</td>
		<td>setResourcesPath(String path)</td>
		<td>http.resource-path</td>
	</tr>
	<tr>
		<td>Dir response allowed</td>
		<td>true</td>
		<td>If URL is directory in resource folder, allow list files</td>
		<td>setDirResponseAllowed(boolean allowed)</td>
		<td>http.dir-allowed</td>
	</tr>
	<tr>
		<td>minimalize templates</td>
		<td>true</td>
		<td>Remove spaces and new lines from templates. <strong>NOTE: '//' in template JS will hide everything after.</strong></td>
		<td>setMinimalize(boolean minimalize)</td>
		<td>http.minimalize-templates</td>
	</tr>
	<tr>
		<td>Developing IP addresses</td>
		<td>Arrays.asList("127.0.0.1", "/0:0:0:0:0:0:0:1")</td>
		<td>Developing IP addresses. Allow show request exception in browser.</td>
		<td>setDevelopIpAddresses(List<String> ips)</td>
		<td>http.ip - splited by '|'</td>
	</tr>
	<tr>
		<td>Default locale</td>
		<td>Locale.getDefault()</td>
		<td>Used if request not contains language information.</td>
		<td>setDefLang(String lang)</td>
		<td>http.locale</td>
	</tr>
	<tr>
		<td>Token expiration</td>
		<td>600000 ms => 10 min</td>
		<td>Token expiration time in miliseconds</td>
		<td rowspan="2">setUserSecurity(userSecurityFactory.get(
			<br>
			long tokenExpirationTime, <br>
			String tokenSalt,<br>
			Logger logger<br>
		 ))</td>
		<td>http.token-expired</td>
	</tr>
	<tr>
		<td>Token salt</td>
		<td>--empty-string--</td>
		<td>Custom token salt</td>
		<td>http.token-salt</td>
	</tr>
	<tr>
		<td>Key store</td>
		<td>--not-used--</td>
		<td>If keystore is specified, the server can provide secured HTTPS connection. A JKS keystore is required</td>
		<td rowspan="4">
			setCerts(new ServerSecuredCredentials(
			<br>
				String keyStore,<br>
				String keyStorePassword,<br>
				Optional<String> trustStore,<br>
				Optional<String> trustStorePassword<br>
			))
		</td>
		<td>http.key-store</td>
	</tr>
	<tr>
		<td>Key store password</td>
		<td>--not-used--</td>
		<td>Password for key store</td>
		<td>http.key-store-password</td>
	</tr>
	<tr>
		<td>Trust store</td>
		<td>--not-used--</td>
		<td>A JKS store for trusted clients certificates. If not specified or is Optional.empty() all clients are allowed.</td>
		<td>http.trust-store</td>
	</tr>
	<tr>
		<td>Trust store password</td>
		<td>--not-used--</td>
		<td>Password for trust store</td>
		<td>http.trust-store-password</td>
	</tr>
</table>

## Database

Parameters for creating `Database` instance (see [JI Database](https://github.com/ondrej-nemec/javainit/ji-database)). If you wish not use `Database`, let `Database type` to be `null` (no property in file).

<table>
	<tr>
		<th>Name</th>
		<th>Property name</th>
	</tr>
	<tr>
		<td>Database type</td>
		<td>database.type</td>
	</tr>
	<tr>
		<td>URL to database</td>
		<td>database.url</td>
	</tr>
	<!-- <tr>
		<td>is db external</td>
		<td>database.external</td>
	</tr> -->
	<tr>
		<td>Schema name</td>
		<td>database.schema-name</td>
	</tr>
	<tr>
		<td>Login</td>
		<td>database.login</td>
	</tr>
	<tr>
		<td>password</td>
		<td>database.password</td>
	</tr>
	<tr>
		<td>Timezone</td>
		<td>database.timezone</td>
	</tr>
	<tr>
		<td>Database pool size</td>
		<td>database.pool-size</td>
	</tr>
</table>
