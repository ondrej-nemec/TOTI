<html>
<head>
	<title>Security example - login</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<h1>${title}</h1>
	
	<%-- just for sync: print error message if exists  --%>
	<t:if cond="${errorMessage} != null">
		<p style="color:red">${errorMessage}</p>
	</t:if>
	
	<p id="flash"></p>

	<t:control name="loginForm" />

	<%-- just for async: show login error, success --%>
	<script>
		function asyncLoginOnFailure(xhr, submit, form) {
			alert(xhr.response);
		}
		function asyncLoginOnSuccess(response, submit, form) {
			/* very important line: */
			totiAuth.login(response /*, {
				// ping secured page for keep token alive
				"url": '<t:link module="examples" controller="samples.examples.sign.SignExample" method="index" />',
				'method': "get"
			}*/);
			alert("Successfully logged, now, you will be maybe redirect");
		}
	</script>

</body>
</html>