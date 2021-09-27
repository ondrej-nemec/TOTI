<t:layout path="layout.jsp" />
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
					logout: {
						url: "/example-module/api/sign/out",
						method: "post"
					},
					refresh: {
						url: "/example-module/api/sign/refresh",
						method: "post"
					}
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
	<a href='<t:link controller="example.web.controllers.ExamplePageController" method="grid"/>'>List</a>

</t:block>
