<html>
<head>
	<title>Security example - login</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<form method="post" action='<t:link method="in" />'>
		<label>Username</label>
		<select name="username">
			<option value="user1">User 1</option>
			<option value="user2">User 2</option>
			<option value="user3">User 3</option>
		</select>
		<br>
		<input type="submit" value="Login"/>
	</form>


</body>
</html>