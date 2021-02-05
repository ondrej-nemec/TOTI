<html>
<head>
	<title>Exception ${code}</title>
</head>
<body>
	<h1>Exception occured: ${code}</h1>
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
		<tr>
			<th>IP</th>
			<td>${ip}</td>
		</tr>
	</table>
	<h3>Paramenters</h3>
	<p>${parameters}</p>
	
	<h3>Headers</h3>
	<p>${headers}</p>
	
	<%--
	<h3>Paramenters</h3>
	<table>
		<t:foreach item="Object key" collection="${parameters.keySet()}">
			<tr>
				<th><t:out name="key" /></th>
				<td><t:out name="${parameters.get(key)}" /></td>
			</tr>
		</t:foreach>
	</table>
	
	<h3>Headers</h3>
	<table>
		<t:foreach item="Object key" collection="${headers.keySet()}">
			<tr>
				<th><t:out name="key" /></th>
				<td><t:out name="${headers.get(key)}" /></td>
			</tr>
		</t:foreach>
	</table>
	--%>
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