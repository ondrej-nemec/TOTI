<html>
<head>
	<title>Template example - form for OWASP</title>
</head>
<body>

	<h1>Form for OWASP</h1>
	
	<form action="${action|noescape}">
		<input type="text" name="first"/> <br>
		<input type="text" name="second"/> <br>
		<input type="submit" value="Submit"/>
	</form>
	
</body>
</html>