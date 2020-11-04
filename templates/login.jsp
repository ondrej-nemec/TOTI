<t:layout path="layout.jsp" />
<t:block name="content" >
	<h1>Login</h1>
	<t:control name="loginForm" />
	<script>
		function loginUser(response) {
			totiAuth.login(response, {
				logout: {
					url: "/security/logout",
					method: "post"
				},
				refresh: {
					url: "/security/refresh",
					method: "post"
				}
			});
		}
	</script>
</t:block>