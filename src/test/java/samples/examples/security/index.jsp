<html>
<head>
	<title>Security example - login</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<div>
		<h3>Login</h3>
		
		<div>
			<label>Select user for login</label>
			<select name="username">
				<option value="">---</option>
				<option value="user1">User 1</option>
				<option value="user2">User 2</option>
				<option value="user3">User 3</option>
			</select>
			<br>
			<button >Login</button>
		</div>
		
		<div>
			<div>Logged as: <span id="username"></span></div>
			<button t:href="samples.examples.security.SecurityExample:login">Logout</button>
		</div>
	</div>

	<form method="post" action='<t:link method="in" />'>
		<br>
		<input type="submit" value="Login"/>
	</form>


</body>
</html>