<t:layout path="/layout.jsp">

<t:block name="title">
	<t:trans message="messages.sign.login-page">
</t:block>

<t:block name="content">
	<h1><t:trans message="messages.sign.login-page"></h1>
	<div>
		<t:control name="loginForm" />
	</div>
	
	<script>
		function setLogin(response) {
			totiAuth.login(response, {
				"url": ${refreshLink},
				"method": "post"
			});
		}
	</script>
</t:block>