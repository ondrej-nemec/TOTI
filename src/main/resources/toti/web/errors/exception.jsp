<html>
<head>
	<title>Exception ${code}</title>
</head>
<body>
	<h1>Exception occured: ${code.getCode()} ${code.getDescription()}</h1>
	<p>${t.getClass()}: ${t.getMessage()}</p>
	<h2>Request</h2>
	<table>
		<tr>
			<th>URL</th>
			<td>${url}</td>
		</tr>
		<tr>
			<th>Full URL</th>
			<td>${fullUrl}</td>
		</tr>
		<tr>
			<th>Method</th>
			<td>${method}</td>
		</tr>
		<tr>
			<th>Protocol</th>
			<td>${protocol}</td>
		</tr>
		<%--<tr>
			<th>IP</th>
			<td>${ip}</td>
		</tr>
		<tr>
			<th>Locale</th>
			<td>${locale}</td>
		</tr> --%>
	</table>	
	
	<h3>Identity</h3>
		<table>
		<tr>
			<th>IP</th>
			<td>${identity.getIP()}</td>
		</tr>
		<tr>
			<th>Locale</th>
			<td>${identity.getLocale()}</td>
		</tr>
	<t:if cond="(Boolean)${identity.isPresent()}" >
		<tr>
			<th>Is API allowed</th>
			<td>${identity.isApiAllowed()}</td>
		</tr>
		<tr>
			<th>Content</th>
			<td>${identity.getContent()}</td>
		</tr>
		<tr>
			<th>User</th>
			<td>${identity.getUser()}</td>
		</tr>
		<tr>
			<th>Allowed IDs</th>
			<td>${identity.getAllowedIds()}</td>
		</tr>
	</t:if>
	</table>
	
	<h3>Paramenters</h3>
	<table>
		<t:foreach key="String key" value="Object value" map="${parameters}">
			<tr>
				<th><t:out name="key" /></th>
				<t:if cond="value == null">
					<td><t:out name="value" /></td>
					<td></td>
				<t:else>
					<td><t:out name="value" /></td>
					<td><t:out name="value.getClass().getName()" /></td>
				</t:if>
				
			</tr>
		</t:foreach>
	</table>
	
	<h3>Headers</h3>
	<table>
		<t:foreach key="String key" value="Object value" map="${headers}">
			<tr>
				<th><t:out name="key" /></th>
				<t:if cond="value == null">
					<td><t:out name="value" /></td>
					<td></td>
				<t:else>
					<td><t:out name="value" /></td>
					<td><t:out name="value.getClass().getName()" /></td>
				</t:if>
				
			</tr>
		</t:foreach>
	</table>
	
	<h2>StackTrace</h2>
	<t:var type="java.lang.Throwable" name="t" value="(java.lang.Throwable)${t}" />
	<t:while cond="t != null">
		<p><t:out name="t.getClass()">: <t:out name="t.getMessage()"></p>
		<t:foreach item="java.lang.StackTraceElement el" collection="t.getStackTrace()">
			<t:out name="el.getClassName()">.<t:out name="el.getMethodName()">
			(<t:out name="el.getFileName()">:<t:out name="el.getLineNumber()">)
			<br>
		</t:foreach>
		<t:set name="t" value="t.getCause()" />
		<t:if cond="t != null">
			<h4>Caused:</h4>
		</t:if>
	</t:while>

</body>
</html>