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
		<td></td>
		<td>How many thread can server use for web requests</td>
		<td>setThreadPool(int size)</td>
		<td>http.thread-pool</td>
	</tr>
	<tr>
		<td>read timeout</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>headers split |</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>charset</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>temp folder</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>resource path -- www folder</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>dir allowed</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>allowed ips split |</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>token expired</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>token-salt</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>key store</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>keystore password</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>thust store</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
	<tr>
		<td>trust store password</td>
		<td></td>
		<td></td>
		<td></td>
		<td>http.</td>
	</tr>
</table>

## Database

Parameters for creating `Database` instance (see [JI Database](https://github.com/ondrej-nemec/javainit/ji-database)). If you wish not use `Database`, let `Database type` to be `null`.

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
