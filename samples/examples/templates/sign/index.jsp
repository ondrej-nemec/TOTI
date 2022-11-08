<html>
<head>
	<title>Security example - secured route</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<h1>Just check if user is logged</h1>
	
	<button id="asyncLogout" t:href='toti.samples.sign.SignExample:asyncLogout'>Logout async</button> <br>
	<a t:href="toti.samples.sign.SignExample:syncLogout"><button>Logout sync</button></a>

	<p>Identity variable: ${totiIdentity.isPresent()}</p>
	
	<p>JS: <span id="loginCheck"></span></p>
	
	<p>Visit page index2: ${totiIdentity.getUser().getProperty("index2").getBoolean()}</p>
	
	<p id="flash"></p>

	<script>
		document.getElementById("loginCheck").innerText = totiAuth.getToken !== null;
		var asyncLogoutButton = document.getElementById("asyncLogout");
		asyncLogoutButton.onclick = function() {
			totiAuth.logout(asyncLogoutButton.getAttribute('href'), "post");
			location.reload();
		};
	</script>

</body>
</html>