<t:layout path="Layout.jsp" />
<t:block name="content">
	<input type="button" value="LogIn" id="in" /><br>
	<input type="button" value="LogOut" id="out" /><br>
	<div id="login"></div>
	<script>
		if (totiAuth.getToken() !== null) {
			document.getElementById('login').innerText = "Logged";
		}
		document.getElementById('in').onclick = function() {
			totiLoad.async("/example-module/api/sign/in", "post", {
				username: "user-name"
			}, {}, function(response) {
				totiAuth.login(response, {
					url: "/example-module/api/sign/refresh",
					method: 'post'
				});
				location.reload();
			}, function (xhr) {
				console.log(xhr);
				totiDisplay.flash("error", "Cannot login user");
			});
		};
		document.getElementById('out').onclick = function() {
			totiAuth.logout();
			location.reload();
		};
	</script>
	<a t:href='example.web.controllers.ExamplePageController:grid'>List</a>

</t:block>
