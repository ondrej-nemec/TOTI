<html>
<head>
	<title><t:include block="title" optional></title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
	
	<script>
		totiLang.changeLanguage('en');
	</script>
</head>
<body>

	<div class="menu">
		<div>
			<t:if cond="${totiIdentity.isPresent()}">
				<span>${totiIdentity.getUser().getProperty("name")}</span>
				<button onclick="logout()"><t:trans message="messages.sign.out"></button>
			<t:else>
				<a t:href="toti.tutorial1.web.SignPageController:loginPage">
					<t:trans message="messages.sign.in">
				</a>
			</t:if>
		</div>
	</div>

	<div id="flash"></div>
	
	<div class="body">
		<t:include block="content" >
	</div>

	<script>
		function logout() {
			totiAuth.logout(
				'<t:link module="main" controller="toti.tutorial1.web.api.SignApiController" method="logout">',
				'post'
			);
			window.location.reload();
		}
	</script>

</body>
</html>