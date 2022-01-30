<html>
<head>
	<title>Security example - login</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<form method="post" action='<t:link method="in" />'>
		<label>Username:</label>
		<input name="username" type="text">  <br>
		<label>Password:</label>
		<input name="password" type="password">
		<br>
		<input type="submit" value="Login"/>
	</form>


</body>
</html>