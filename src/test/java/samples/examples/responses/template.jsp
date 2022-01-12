<html>
<head>
	<title>Response example - template response</title>
</head>
<body>
	<h1>${title}</h1>
	
	<t:if cond='${number.equals("0")}' >
		Big nothing
	<t:else>
		Some value: ${number}
	</t:if>
</body>
</html>