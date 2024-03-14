<html>
<head>
	<title>Response example - template response</title>
</head>
<body>
	<h1>${title}</h1>
	
	<t:if cond='${number|Integer} < 0' >
		Big nothing
	<t:elseif cond='${number|Integer} == 42' >
		Answer for life, space and everything.
	<t:else>
		Not negative number ${number}
	</t:if>
</body>
</html>