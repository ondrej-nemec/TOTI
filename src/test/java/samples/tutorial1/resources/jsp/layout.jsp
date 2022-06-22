<html>
<head>
	<title><t:include block="title" optional></title>
	<link rel="stylesheet" href="/toti/totiStyle.css" />
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<div>
		<t:if cond="${totiIdentity.isPresent()}">
			<span>${totiIdentity.getUser().getProperty("name")}</span>
			<button onclick="logout()"><t:trans message="messages.sign.out"></button>
		<t:else>
			<a t:href="samples.tutorial1.web.SignPageController:loginPage">
				<t:trans message="messages.sign.in">
			</a>
		</t:if>
	</div>

	<div id="flash"></div>
	
	<t:include block="content" >

	<script>
		function logout() {
			totiAuth.logout(
				'<t:link module="main" controller="samples.tutorial1.web.api.SignApiController" method="logout">',
				'post'
			);
			window.location.reload();
		}
	</script>

</body>
</html>