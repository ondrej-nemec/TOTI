<!-- TOTI Profiler version 0.0.2 -->
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
	<title>Profiler</title>
	<script src="/toti/totiJs.js"></script>
	<script>totiSettings.showProfiler = false;</script>

	<style type="text/css">
		body {
			background-color: #cccccc;
		}
		h1 {
			text-align: center;
			color: white;
			background-color: #3366ff;
			padding: 0.5em;
		}
		.hidden {
			display: none;
		}
		ul {
			list-style-type: none;
			font-size: 1.15em;
		}
		ul.main-list {
		  padding: 0;
		  margin: 0;
		  padding-top: 0.5em;
		  padding-left: 0.5em;
		  background-color: #595959;
		  color: #d9d9d9;
		}
		ul a, ul a:hover {
		  color: #d9d9d9;
		}
		ul.main-list #title {
		  font-weight: bold;
		}
		ul.sub-list {
		  padding: 0;
		  margin: 0;
		}
		.sub-list li {
			padding: 0.25em;
			padding-left: 1em;
		}
		#menu {
			float: left;
			width: 25%;
		  background-color: #595959;
		}
		#content {
			margin-left: 25%;
			padding-left: 1em;
		}
		.container {
			padding: 0.5em;
			margin-bottom: 0.5em;
		}
		.container, .container table {
			background-color: #0066cc;
			color: #d9d9d9;
		}
		h3 {
			margin-top: 0;
		}
		.clear  {
			background-color: #ff3333;			
			border-radius: 4px;
			border: 3px solid #f5c6cb;
			color: black;
		}
		dt {
			font-weight: bold;
		}
		/*#tableInfoMenu li {
			display: inline-block;
			padding: 0.40em;
		}*/
	</style>
</head>
<body>
	<div>
		<h1>TOTI Profiler</h1>
	</div>
	<div id="menu">
		<button class="clear">Clear All</button>
		<ul class="main-list">

		</ul>
	</div>
	<div>
		<div>
			<label><input type="checkbox" id="chb-page-request" {{ ${enable|Boolean} ? "checked" : ""}}>Log page requests</label>
		</div>
		<div id="content"></div>
	</div>

	<template id="menu-item-template">
		<li>
			<span id="title"></span> <button class="clear">Clear</button>
			<ul class="sub-list"></ul>
		</li>
	</template>

	<template id="sub-menu-item-template">
		<li><a href="" id="link"></a></li>
	</template>

	<template id="log-info-template">
		<div>
			<h2 id="data-name"></h2>
			<h3 id="data-createdAt"></h3>
		</div>
	</template>

	<template id="translations-template">
		<div>
			<h2>Localization: <span id="data-trans-locale"></span></h2>
			<p>
				<strong>Left To Right:</strong>&nbsp;<span id="data-trans-ltr"></span> <br>
				<strong>Substitutions:</strong>&nbsp;<span id="data-trans-substitutions"></span>
			</p>
			<table id="data-trans-missingTranslations">
			  	<tr>
			  		<th>Locale</th>
			  		<th>Module</th>
					<th>Key</th>
					<th>Parameters</th>
				</tr>
				<!-- TODO add parameters -->
			</table>
			<ul id="data-trans-missingFiles"></ul>
		</div>
	</template>

	<template id="sql-log-template">
		<div>
			<h4>SQL</h4>
			<dl>
			  <dt>Prepared SQL</dt>
			  <dd id="data-sql-prepared">---</dd>
			  <dt>Replaced SQL</dt>
			  <dd id="data-sql-builded">---</dd>
			  <dt>Builder Parameters</dt>
			  <dd>
			  	<table id="data-sql-builderParams">
			  		<tr>
			  			<th>Name</th>
			  			<th>Value</th>
			  		</tr>
			  	</table>
			  </dd>
			  
			  <dt>Query</dt>
			  <dd id="data-sql-sql">---</dd>
			  <dt>Parameters</dt>
			  <dd>
			  	  <ol id="data-sql-params"></ol>
			  </dd>
			  <dt>Is Executed</dt>
			  <dd id="data-sql-executed"></dd>
			</dl>
		</div>
	</template>

	<template id="request-template">
		<div>
			<h2>
				<span id="data-request-method"></span>: 
				<span id="data-request-fullUrl"></span>
			</h2>
			<p><strong>IP:</strong>&nbsp;&nbsp;<span id="data-request-ip">/span></p>
		</div>
	</template>

	<template id="request-parameters-template">
			<div>
				<h2>Parameters</h2>
				<table id="data-params">
					<tr>
						<th>Name</th>
						<th>Value</th>
					</tr>
				</table>
			</div>
	</template>

	<template id="identity-template">
			<div>
				<h2>Identity</h2>
				<dl>
				  <dt>Is API allowed</dt>
				  <dd id="data-identity-apiAllowed"></dd>
				  <dt>Content</dt>
				  <dd id="data-identity-content"></dd>
				  <dt>User</dt>
				  <dd>
				  	 <dl>
						  <dt>ID</dt>
						  <dd id="data-identity-user-id">---</dd>
						  <dt>Allowed IDs</dt>
						  <dd id="data-identity-user-ids">---</dd>
				  	 </dl>
				  </dd>
				</dl>
			</div>
	 </template>

	 <template id="rendering-template">
			<div>
				<h2>Server processing time</h2>
				<h3>Total processing time: <span id="data-rendering-render"></span></h3>

				<table id="data-rendering-times">
					<tr>
						<th>Name</th>
						<th>Time</th>
					</tr>
				</table>
			</div>
	</template>

	<script type="text/javascript">

		function loadProfilerData(method, params, onSuccess) {
			totiLoad.async(
				"/toti/profiler", method, params, {}, 
				function(res) { onSuccess(res); }, 
				function(xhr) { console.log(xhr); },
				false
			);
		}

		function getTemplate(template) {
			return template.content.cloneNode(true).querySelector(":first-child");
		}

		document.getElementById("chb-page-request").onchange = function() {
			loadProfilerData("put", {}, function(res) {});
		};

		loadProfilerData("post", {}, function(res) {
			printResult(res, window.location.hash.substring(1));
		});

		function printResult(res, hash) {
			var clear = function(pageId) {
				return function() {
					var message = pageId === null ? "All logs " : "The log for page '" + pageId + "'";
					var params = pageId === null ? {} : {id: pageId};
					if (totiDisplay.confirm(message + " will be deleted. Continue?")) {
						loadProfilerData("delete", params, function(res) {
							window.location.reload();
						});
					};
				};
			};
			var menu = document.getElementById("menu");
			menu.querySelector(".clear").onclick = clear(null);

			var menuItemTemplate = document.getElementById("menu-item-template");
			var subMenuItemTemplate = document.getElementById("sub-menu-item-template");
			for (const[pageId, logs] of Object.entries(res.logByPage)) {
				addMenuItem(menu, pageId, logs, clear, menuItemTemplate, subMenuItemTemplate, hash, true);
			}
			for (const[pageId, log] of Object.entries(res.noPageLog)) {
				addMenuItem(menu, pageId, [log], clear, menuItemTemplate, subMenuItemTemplate, hash, false);
			}
		}

		function addMenuItem(menu, pageId, logs, clear, menuItemTemplate, subMenuItemTemplate, hash, isPage) {
			var menuItem = menuItemTemplate.content.cloneNode(true).querySelector("li");
			var title = menuItem.querySelector("#title");
			title.innerText = isPage ? pageId : "Not Page " + pageId;

			menuItem.querySelector(".clear").onclick = clear(pageId);

			var subListElement = menuItem.querySelector("ul");
			subListElement.setAttribute("id", pageId);
			if (hash !== '' && hash !== pageId) {
				subListElement.classList.add("hidden");
			}
			logs.forEach(function(log) {
				var subMenuItem = subMenuItemTemplate.content.cloneNode(true).querySelector("li");
				menuItem.querySelector(".sub-list").appendChild(subMenuItem);
				var link = subMenuItem.querySelector("#link");
				link.innerText = 
					"[" 
					+ new Date(log.created).toISOString().replace('T', " ").substring(0, 23)
					+ "] "
					+ (isPage ? log.requestInfo.method + ": " + log.requestInfo.url : "");
				link.onclick = function(e) {
					e.preventDefault();
					document.getElementById("content").innerHTML = '';
					fillContent(document.getElementById("content"), log, isPage);
					return false;
				};
			});

			title.onclick = function(event) {
				var element = document.getElementById(pageId);
				if (element.classList.contains("hidden")) {
					element.classList.remove("hidden");
				} else {
					element.classList.add("hidden");
				}
				return true;
			};
			menu.querySelector(".main-list").appendChild(menuItem);
		}

		/***************************************/

		function getRequestLog(request) {
			var container = getTemplate(document.getElementById("request-template"));
			container.querySelector("#data-request-method").innerText = request.method;
			container.querySelector("#data-request-fullUrl").innerText = request.fullUrl;
			container.querySelector("#data-request-ip").innerText = request.IP;
			return container;
		}

		function getTranslationsLog(trans) {
			var container = getTemplate(document.getElementById("translations-template"));
			if (trans.locale !== null) {
				container.querySelector("#data-trans-locale").innerText = trans.locale.lang;
				container.querySelector("#data-trans-ltr").innerText = trans.locale.isLeftToRight;
				container.querySelector("#data-trans-substitutions").innerText = JSON.stringify(trans.locale.substitution);
			}
			
			var table = container.querySelector("#data-trans-missingTranslations");
			if (trans.missingTranslations.length === 0) {
				table.remove();
			}
			trans.missingTranslations.forEach(function(missing) {
				var createTd = function(value) {
					var td = document.createElement("td");
					td.innerText = value;
					return td;
				};
				var tr = document.createElement("tr");
				tr.appendChild(createTd(missing.locale));
				tr.appendChild(createTd(missing.module));
				tr.appendChild(createTd(missing.key));
				tr.appendChild(createTd(JSON.stringify(missing.params)));
				table.appendChild(tr);
			});

			var ul = container.querySelector("#data-trans-missingFiles");
			trans.missingFiles.forEach(function(item) {
				var li = document.createElement("li");
				li.innerText = item;
				ul.appendChild(li);
			});
			return container;
		}

		function getSqlLog(queries) {
			var template = document.getElementById("sql-log-template");
			var div = document.createElement("div");
			var headline = document.createElement("h3");
			headline.innerText = "SQL Queries";
			div.appendChild(headline);

			for (const[logId, log] of Object.entries(queries)) {
				var container = getTemplate(template);
				container.querySelector("#data-sql-prepared").innerText = log.preparedSql;
				container.querySelector("#data-sql-builded").innerText = log.replacedSql;
				addParametersToTable(
					container.querySelector("#data-sql-builderParams"),
					log.builderParams
				);

				var ol = container.querySelector("#data-sql-params");
				log.params.forEach(function(item) {
					var li = document.createElement("li");
					li.innerText = item;
					ol.appendChild(li);
				});
				container.querySelector("#data-sql-sql").innerText = log.sql;
				container.querySelector("#data-sql-executed").innerText = log.isExecuted;

				div.appendChild(container);
			}
			return div;
		}

		function getLogInfo(info) {
			var container = getTemplate(document.getElementById("log-info-template"));
			container.querySelector("#data-name").innerText = info.name;
			container.querySelector("#data-createdAt").innerText = new Date(info.created);
			return container;
		}

		function getIdentityLog(identity) {
			var container = getTemplate(document.getElementById("identity-template"));
			container.querySelector("#data-identity-apiAllowed").innerText = identity.isApiAllowed;
			container.querySelector("#data-identity-content").innerText = identity.content;
			if (identity.hasOwnProperty("user")) {
				container.querySelector("#data-identity-user-id").innerText = identity.user.id;
				container.querySelector("#data-identity-user-ids").innerText = JSON.stringify(identity.user.allowedIds);
			}
			return container;
		}

		function getRequestParametersLog(params) {
			var container = getTemplate(document.getElementById("request-parameters-template"));
			addParametersToTable(
				container.querySelector("#data-params"),
				params
			);
			return container;
		}

		function getRenderingLog(rendering) {
			var container = getTemplate(document.getElementById("rendering-template"));
			container.querySelector("#data-rendering-render").innerText = rendering.render;
			addParametersToTable(
				container.querySelector("#data-rendering-times"),
				rendering.times
			);
			return container;
		}

		/******************/

		function fillContent(target, log, isPage) {
			var div = document.createElement("div");
			if (isPage) {
				div.appendChild(getRequestLog(log.requestInfo));
			}
			div.appendChild(getLogInfo(log));
			div.appendChild(getTranslationsLog(log.trans));
			div.appendChild(getSqlLog(log.queries));
			if (isPage) {
				div.appendChild(getIdentityLog(log.identity));
				div.appendChild(getRenderingLog(log.rendering));
				div.appendChild(getRequestParametersLog(log.params));
			}

			target.appendChild(div);
		}

		function addParametersToTable(table, parameters, renderFunc = null) {
			parameters = parameters === null ? {} : parameters;
			var createTd = function(value) {
				var td = document.createElement("td");
				if (renderFunc !== null) {
					td.innerHTML = renderFunc(value);
				} else if (typeof value === 'object') {
					td.innerHTML = "<i>"  + JSON.stringify(value) + "</i>";
				} else {
					td.innerText = value;
				}
				
				return td;
			};
			for(const[name, value] of Object.entries(parameters)) {
				var tr = document.createElement("tr");
				tr.appendChild(createTd(name));
				tr.appendChild(createTd(value));
				table.appendChild(tr);
			}
		}
	</script>
</body>
</html>