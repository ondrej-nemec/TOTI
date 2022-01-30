<html>
<head>
	<title>Security example - secured route</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<h1>Just check if user is logged</h1>
	
	<button id="asyncLogout">Logout async</button> <br>
	<a><button>Logout sync</button></a>

	<p>Identity variable: ${totiIdentity.isPresent()}</p>
	
	<p>JS: <span id="loginCheck"></span></p>
	
	<p id="flash"></p>

	<script>
		document.getElementById("loginCheck").innerText = totiAuth.getToken !== null;
		document.getElementById("asyncLogout").onclick = function() {
			totiAuth.logout('', "post");
		};
	</script>

</body>
</html>