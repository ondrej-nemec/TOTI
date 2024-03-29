<html>
<head>
	<title>Security example - login</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<div>
		<h3>Login</h3>
		
		<div id="not-logged">
			<label>Select user for login</label>
			<select name="username">
				<option value="">---</option>
				<option value="user1">User 1</option>
				<option value="user2">User 2</option>
				<option value="user3">User 3</option>
			</select>
			<br>
			<button id="login" t:href="samples.examples.security.SecurityExample:login">Login</button>
		</div>
		
		<div id="logged" style="display: none;">
			<div>Logged as: <span id="username"></span></div>
			<button id="logout" t:href="samples.examples.security.SecurityExample:logout">Logout</button>
		</div>
	</div>

	<h3>Response overview</h3>
	<div id="request-result" style="width: 100%; height: 100px; background-color: #d1d1e0;"></div>

	<h3>Requests</h3>
	
	<label>ID: </label> <input type="text" id="id">
	<table>
		<tr>
			<th>Name</th>
			<th>URL</th>
			<th>Run with auth header</th>
			<th>Run with cookie and CSRF token</th>
			<th>Run with cookie</th>
		</tr>
		<t:foreach item="ji.common.structures.Tuple2 value" collection="${links}">
			<tr>
				<td>${value._1()}</td>
				<td>${value._2()}</td>
				<td><button href="${value._2()}" class="run-headers">Run</button></td>
				<td><button href="${value._2()}" class="run-csrf">Run</button></td>
				<td><button href="${value._2()}" class="run-cookies">Run</button></td>
			</tr>
		</t:foreach>
	</table>

	<script>
		/* totiLoad allways add authentication header, so here is simply override */
		totiLoad.getHeaders = function (realHeader = false) {
			if (realHeader) {
				return {
					...totiAuth.getAuthHeader(),
					...totiLang.getLangHeader()
				};
			}
			return {};
		};
		/* override end */
	
		var loginButton = document.getElementById("login");
		loginButton.onclick = function () {
			totiLoad.async(
				loginButton.getAttribute("href"),
				"post",
				{
					username: document.querySelector("[name=username]").value
				},
				{},
				function (response) {
					totiAuth.login(response);
					document.getElementById("not-logged").style.display = "none";
					document.getElementById("logged").style.display = "block";
					document.getElementById("username").innerText = document.querySelector("[name=username]").value;
					window.location.reload();
				},
				function (xhr) {
					console.log(xhr);
				}
			);
		};
		var logoutButton = document.getElementById("logout");
		logoutButton.onclick = function () {
			totiAuth.logout(logoutButton.getAttribute("href"), "post");
			document.getElementById("not-logged").style.display = "block";
			document.getElementById("logged").style.display = "none";
		};
		
		/* tests with auth header */
		document.querySelectorAll(".run-headers").forEach(function (button) {
			button.onclick = function() {
				run(button, totiLoad.getHeaders(true), false);
			};
		});
		/* tests with cookie auth and csrf */
		document.querySelectorAll(".run-csrf").forEach(function (button) {
			button.onclick = function() {
				run(button, {}, true);
			};
		});
		/* tests with cookie auth */
		document.querySelectorAll(".run-cookies").forEach(function (button) {
			button.onclick = function() {
				run(button, {}, false);
			};
		});
		
		function run(button, headers, useCsrf) {
			var display = document.getElementById("request-result");
			var requestBody = {
				id: document.getElementById("id").value
			};
			if (useCsrf) {
				/* FIX: CSRF token is usualy part of form, there is no form */
				requestBody._csrf_token = '${totiIdentity.getCsrfToken()}';
			}
			totiLoad.async(
				button.getAttribute("href"),
				"post",
				requestBody,
				headers,
				function (response) {
					display.innerText = response;
				},
				function (xhr) {
					console.log(xhr);
					display.innerText = "Error: " + xhr.response;
				}
			);
		}
	</script>

</body>
</html>