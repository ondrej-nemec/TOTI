<html>
<head>
	<title>Exception ${code}</title>
	<style>
	 h2, h3 {
	 	display: inline-block;
	 }
	</style>
</head>
<body>
	<h1>Exception occured: ${code.getCode()} ${code.getDescription()}</h1>
	<p>${t.getClass()}: ${t.getMessage()}</p>
	
	<div class="section">
		<div>
			<h2>Request</h2>
			<img src="" class="block-show" width="20px">
			<img src="" class="block-hide" width="20px">
		</div>
		<div class="block">
			<div class="section">
				<div>
					<h3>Info</h3>
					<img src="" class="block-show" width="20px">
					<img src="" class="block-hide" width="20px">
				</div>
				<div class="block">
					<table>
						<tr>
							<th>URL</th>
							<td>${url}</td>
						</tr>
						<tr>
							<th>Full URL</th>
							<td>${fullUrl}</td>
						</tr>
						<tr>
							<th>Method</th>
							<td>${method}</td>
						</tr>
						<tr>
							<th>Protocol</th>
							<td>${protocol}</td>
						</tr>
					</table>
				</div>
			</div>
			<t:if cond='${mappedUrl} != null'>
			<div class="section">
				<div>
					<h3>Mapping</h3>
					<img src="" class="block-show" width="20px">
					<img src="" class="block-hide" width="20px">
				</div>
				<div class="block">
					<table>
						<tr>
							<th>Module</th>
							<td>${mappedUrl.getModuleName()}</td>
						</tr>
						<tr>
							<th>Controller</th>
							<td>${mappedUrl.getClassName()}</td>
						</tr>
						<tr>
							<th>Action</th>
							<td>${mappedUrl.getMethodName()}</td>
						</tr>
					</table>
					${mappedUrl}
				</div>
			</div>
			</t:if>
			
			<div class="section">
				<div>
					<h3>Identity</h3>
					<img src="" class="block-show" width="20px">
					<img src="" class="block-hide" width="20px">
				</div>
				<div class="block">
					<table>
						<tr>
							<th>IP</th>
							<td>${identity.getIP()}</td>
						</tr>
						<tr>
							<th>Locale</th>
							<td>${identity.getLocale()}</td>
						</tr>
					<t:if cond="${identity.isPresent()}" >
						<tr>
							<th>Login method</th>
							<td>${identity.getLoginMode()}</td>
						</tr>
						<tr>
							<th>Content</th>
							<td>${identity.getUser().getContent()}</td>
						</tr>
						<tr>
							<th>User: Id</th>
							<td>${identity.getUser().getId()}</td>
						</tr>
						<tr>
							<th>User: Allowed Owners</th>
							<td>${identity.getUser().getAllowedIds()}</td>
						</tr>
					</t:if>
					</table>
				</div>
			</div>
			
			<div class="section">
				<div>
					<h3>Paramenters</h3>
					<img src="" class="block-show" width="20px">
					<img src="" class="block-hide" width="20px">
				</div>
				<div class="block">
					<h3>URL parameters</h3>
					<table>
						<t:foreach key="String key" value="Object value" map="${urlParameters}">
							<tr>
								<th><t:out name="${key}" /></th>
								<t:if cond="value == null">
									<td><t:out name="${value}" /></td>
									<td></td>
								<t:else>
									<td><t:out name="${value}" /></td>
									<td><t:out name="${value.getClass().getName()}" /></td>
								</t:if>
								
							</tr>
						</t:foreach>
					</table>
					<h3>Body parameters</h3>
					<table>
						<t:foreach key="String key" value="Object value" map="${bodyParameters}">
							<tr>
								<th><t:out name="${key}" /></th>
								<t:if cond="value == null">
									<td><t:out name="${value}" /></td>
									<td></td>
								<t:else>
									<td><t:out name="${value}" /></td>
									<td><t:out name="${value.getClass().getName()}" /></td>
								</t:if>
								
							</tr>
						</t:foreach>
					</table>
					<h3>Body</h3>
					<div>${body}</div>
				</div>
			</div>
			
			<div class="section">
				<div>
					<h3>Headers</h3>
					<img src="" class="block-show" width="20px">
					<img src="" class="block-hide" width="20px">
				</div>
				<div class="block">
					<table>
						<t:foreach key="String key" value="Object list" map="${headers.getHeaders()}">
							<t:foreach item="Object value" collection="${list}">
								<tr>
									<th><t:out name="${key}" /></th>
									<t:if cond="value == null">
										<td><t:out name="${value}" /></td>
										<td></td>
									<t:else>
										<td><t:out name="${value}" /></td>
										<td><t:out name="${value.getClass().getName()}" /></td>
									</t:if>
								</tr>
							</t:foreach>
						</t:foreach>
					</table>
				</div>
			</div>
		</div>
	</div>
	
	<div>
		<h2>StackTrace</h2>
		<div>
			<t:var type="java.lang.Throwable" name="t" value="${t|Throwable}" />
			<t:while cond="t != null">
				<p><t:out name="${t.getClass()}">: <t:out name="${t.getMessage()}"></p>
				<t:foreach item="java.lang.StackTraceElement el" collection="${t.getStackTrace()}">
					<t:out name="${el.getClassName()}">.<t:out name="${el.getMethodName()}">
					(<t:out name="${el.getFileName()}">:<t:out name="${el.getLineNumber()}">)
					<br>
				</t:foreach>
				<t:set name="t" value="${t.getCause()|Throwable}" />
				<t:if cond="t != null">
					<h4>Caused:</h4>
				</t:if>
			</t:while>
		</div>
	</div>

	<script>
		/* https://www.iconfinder.com/icons/186407/up_arrow_icon */
		var arrowUp = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xOC4yMjEsNy4yMDZsOS41ODUsOS41ODVjMC44NzksMC44NzksMC44NzksMi4zMTcsMCwzLjE5NWwtMC44LDAuODAxYy0wLjg3NywwLjg3OC0yLjMxNiwwLjg3OC0zLjE5NCwwICBsLTcuMzE1LTcuMzE1bC03LjMxNSw3LjMxNWMtMC44NzgsMC44NzgtMi4zMTcsMC44NzgtMy4xOTQsMGwtMC44LTAuODAxYy0wLjg3OS0wLjg3OC0wLjg3OS0yLjMxNiwwLTMuMTk1bDkuNTg3LTkuNTg1ICBjMC40NzEtMC40NzIsMS4xMDMtMC42ODIsMS43MjMtMC42NDdDMTcuMTE1LDYuNTI0LDE3Ljc0OCw2LjczNCwxOC4yMjEsNy4yMDZ6IiBmaWxsPSIjNTE1MTUxIi8+PC9zdmc+";
		/* https://www.iconfinder.com/icons/186411/down_arrow_icon */
		var arrowDown = "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xNC43NywyMy43OTVMNS4xODUsMTQuMjFjLTAuODc5LTAuODc5LTAuODc5LTIuMzE3LDAtMy4xOTVsMC44LTAuODAxYzAuODc3LTAuODc4LDIuMzE2LTAuODc4LDMuMTk0LDAgIGw3LjMxNSw3LjMxNWw3LjMxNi03LjMxNWMwLjg3OC0wLjg3OCwyLjMxNy0wLjg3OCwzLjE5NCwwbDAuOCwwLjgwMWMwLjg3OSwwLjg3OCwwLjg3OSwyLjMxNiwwLDMuMTk1bC05LjU4Nyw5LjU4NSAgYy0wLjQ3MSwwLjQ3Mi0xLjEwNCwwLjY4Mi0xLjcyMywwLjY0N0MxNS44NzUsMjQuNDc3LDE1LjI0MywyNC4yNjcsMTQuNzcsMjMuNzk1eiIgZmlsbD0iIzUxNTE1MSIvPjwvc3ZnPg==";
	
		document.querySelectorAll(".section").forEach(function(section) {
			var block = section.querySelector(".block");
			var hide = section.querySelector(".block-hide");
			var show = section.querySelector(".block-show");
	
			show.setAttribute("src", arrowDown);
			hide.setAttribute("src", arrowUp);
			show.style.display = "none";
			hide.onclick = function() {
				show.style.display = "inline-block";
				block.style.display = "none";
				hide.style.display = "none";
			};
			show.onclick = function() {
				show.style.display = "none";
				block.style.display = "block";
				hide.style.display = "inline-block";
			};
		});
	</script>

</body>
</html>