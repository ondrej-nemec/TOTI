<!-- TOTI Profiler version 1.0.0 -->
<html>
<head>
    <meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    
	<title>Profiler</title>
	<script src="/toti/totiJs.js"></script>

	<script>showTotiProfiler = false;</script>

	<style type="text/css">
		body {
			/*background-color: #cccccc;*/
			background-color: #7FC6CC;
		}
		h1 {
			text-align: center;
			padding: 0.5em;
			/*color: white;
			background-color: #3366ff;*/
			color: #e3ffff; /*#86daff;*/
			background-color: #017CA5;
		}
		h2 {
			margin-top: 0.5em;
			margin-bottom: 0.5em;
			font-size: 1.55em;
		}
		h3 {
			margin-top: 0.5em;
			margin-bottom: 0.5em;
			font-size: 1.45em;
		}
		h4 {
			font-size: 1.25em;
			margin-top: 0.5em;
			margin-bottom: 0.5em;
		}
		h5 {
			margin-block-end: 0;
			margin-block-start: 0;
			font-size: 1.05em;
			margin-top: 0.5em;
		}
		.button {
			cursor: pointer;
		}
		.hidden {
			display: none;
		}
		#menu ul {
			list-style-type: none;
			font-size: 1.15em;
		  padding: 0;
		  margin: 0;
		  padding-top: 0.5em;
		  padding-left: 0.5em;
		  background-color: #595959;
		  color: #d9d9d9;
		}
		#menu ul li {
		  color: #d9d9d9;
		  word-wrap: break-word;
		  cursor: pointer;
		  margin-bottom: 0.5em;
		}
		#menu ul li:hover {
		  color: #d9ffd9;
		  word-wrap: break-word;
		}
		#menu ul #title {
		  font-weight: bold;
		  word-wrap: break-word;
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
		.status {
			padding-top: 0.5em;
			padding-bottom: 0.5em;
		}
		.status span {
			padding: 0.5em;
		}
	</style>
</head>
<body>
	<div>
		<h1>TOTI Profiler</h1>
	</div>
	<div id="menu">
		<ul></ul>
	</div>
	<div id="content"></div>

	<template id="request-template">
		<div>
			<h3>Request</h3>
			<div>
				<strong>Try authenticate with:</strong> <span id="auth"></span>
			</div>
			<div>
				<strong>Process time:</strong> <span id="time"></span> ms
			</div>
			<div>
				<strong>Selected locale:</strong> <span id="locale"></span>
			</div>
			<div id="parameters">
				<h4>URL parameters</h4>
				<table></table>
			</div>
		</div>
	</template>

	<template id="controller-template">
		<div>
			<h3>Controller</h3>
			<div>
				<strong>Module:</strong> <span id="module"></span>
			</div>
			<div>
				<strong>Class:</strong> <span id="class"></span>
			</div>
			<div>
				<strong>Method:</strong> <span id="method"></span>
			</div>
			<div>
				<strong>Auth mode:</strong> <span id="auth"></span>
			</div>
		</div>
	</template>

	<template id="templates-template">
			<div class="hidable">
				<div class="colapse">
					<h4>
						<img class="button icon" id="arrowRight">
						<span id="name-short"></span>
					</h4>
				</div>
				<div class="expand">
					<h4>
						<img class="button icon" id="arrowDown">
						<span id="name-full"></span>
					</h4>
					<div>
						<strong>Module:</strong> <span id="module"></span>
					</div>
					<div>
						<strong>Path:</strong> <span id="path"></span>
					</div>
					<div id="variables">
						<h5>Variables</h5>
						<table></table>
					</div>
				</div>
			</div>
	</template>

	<template id="queries-template">
			<div class="hidable">
				<div class="colapse">
					<h4>
						<img class="button icon" id="arrowRight">
						<span id="query-short"></span>
					</h4>
				</div>
				<div class="expand">
					<h4>
						<img class="button icon" id="arrowDown">
						<span id="query-full"></span>
					</h4>
					<div id="params-list">
						<h5>Parameters</h5>
						<ol></ol>
					</div>
					<div id="params-table">
						<h5>Parameters</h5>
						<table></table>
					</div>
					<div id="query-translated"></div>
					<div class="status">
						<span id="status"></span>
						<span id="time">---</span>
						ms
					</div>
					<div id="result">
						<strong>Result: </strong> <span></span>
					</div>
				</div>
			</div>
	</template>

	<template id="translations-template">
		<div>
			<h3>Translations</h3>

			<h4>Missing locale</h4>
			
			<div>
				<ul id="missing-locales"></ul>
			</div>

			<h4>Missing translations</h4>

			<div>
				<table id="missing-translations">
					<tr>
						<th>Locale</th>
						<th>Module/Domain</th>
						<th>Key</th>
						<th>Parameters</th>
					</tr>
				</table>
			</div>

		</div>
	</template>

	<template id="trans-variable-template">
		<tr>
			<td id="locale"></td>
			<td id="domain"></td>
			<td id="key"></td>
			<td id="params"></td>
		</tr>
	</template>

	<template id="user-template">
		<h3>User</h3>
		<div>
			<strong>ID:</strong> <span id="id"></span>
		</div>
		<div>
			<strong>Allowed Ids:</strong> <span id="ids"></span>
		</div>
		<div id="content">
			<h4>Content</h4>
		</div>
	</template>

	<!-- menu templates -->

	<template id="menu-item-template">
		<li>
			<span id="title"></span>
		</li>
	</template>

	<script type="text/javascript">

		function getTemplate(selector) {
			var element = document.getElementById(selector).content.cloneNode(true).querySelector(":first-child");
			element.querySelectorAll(".icon").forEach(function(img) {
				img.setAttribute("src", totiImages[img.getAttribute("id")]);
			});
			var hidables = [];
			document.querySelectorAll('.hidable').forEach((el)=>{hidables.push(el);});
			if (element.classList.contains('hidable')) {
				hidables.push(element);
			} 
			hidables.forEach((hidable)=>{
				var colapse = hidable.querySelector('.colapse');
				var expand =  hidable.querySelector('.expand');
				colapse.style.display = "none";
				colapse.querySelector('.button').addEventListener('click', ()=>{
					colapse.style.display = "none";
					expand.style.display = "block";
				});
				expand.querySelector('.button').addEventListener('click', ()=>{
					expand.style.display = "none";
					colapse.style.display = "block";
				});
			});
			return element;
		}

		function shortVersion(text) {
			var maxLength = 50;
			if (text.length <= maxLength) {
				return text;
			}
			return text.substring(0, maxLength - 3) + "...";
		}

		function fillParameterTable(table, params) {
			for (const[name, value] of Object.entries(params)) {
				var nameContainer = document.createElement('th');
				nameContainer.innerText = name;
				var valueContainer = document.createElement('td');
				valueContainer.innerText = value;

				var item = document.createElement('tr');
				item.appendChild(nameContainer);
				item.appendChild(valueContainer);

				table.appendChild(item);
			}
		}

		/********************/
		var pageId = new URL(window.location.href).searchParams.get("page");
		var body = {};
		if (pageId !== null) {
			body = {
				pageId: pageId
			};
		}
		totiLoad.anonymous("/toti/profiler", "post", {}, {}, body).then((res)=>{
			renderMenu(res);
			render(res[0]);
		}).catch((xhr)=>{
			console.error(xhr);
		});

		/********************/

		function renderMenu(res) {
			var menu = document.querySelector("#menu ul");
			res.forEach((row)=>{
				var menuItem = getTemplate("menu-item-template");
				menuItem.innerText = row.title;
				menuItem.onclick = ()=>{
					render(row);
				};
				menu.appendChild(menuItem);
			});
		}

		function render(row) {
			var content = document.getElementById("content");
			content.innerHTML = "";
			var title = document.createElement('h2');
			title.innerText = row.title;
			content.appendChild(title);

			content.appendChild(renderRequest(row));
			content.appendChild(renderUser(row));
			content.appendChild(renderController(row));
			content.appendChild(renderTemplates(row));
			content.appendChild(renderQueries(row));
			content.appendChild(renderTranslations(row));
		}

		function renderRequest(row) {
			if (!row.hasOwnProperty('request')) {
				return document.createElement('div');
			}
			var container = getTemplate('request-template');
			container.querySelector('#time').innerText = row.request.processTime;
			container.querySelector('#auth').innerText = row.request.loginMode;
			container.querySelector('#locale').innerText = row.request.locale.lang;
			var params = container.querySelector('#parameters');
			if (Object.keys(row.request.params).length === 0) {
				params.remove();
			} else {
				fillParameterTable(params.querySelector('table'), row.request.params);
			}
			return container;
		}

		function renderUser(row) {
			if (!row.hasOwnProperty('user')) {
				return document.createElement('div');
			}
			var container = getTemplate('user-template');
			container.querySelector('#id').innerText = row.user.id;
			container.querySelector('#ids').innerText = row.user.allowedIds.toString();
			if (Object.keys(row.user.content).length === 0) {
				container.querySelector('#content').remove();
			} else {
				fillParameterTable(container.querySelector('#content, table'), row.user.content);
			}
			return container;
		}

		function renderController(row) {
			if (!row.hasOwnProperty('controller')) {
				return document.createElement('div');
			}
			var container = getTemplate('controller-template');
			container.querySelector('#module').innerText = row.controller.module;
			container.querySelector('#class').innerText = row.controller.class;
			container.querySelector('#method').innerText = row.controller.method;
			container.querySelector('#auth').innerText = row.controller.authMode;
			return container;
		}

		function renderTemplates(row) {
			if (!row.hasOwnProperty('template') || row.template === null) {
				return document.createElement('div');
			}
			var container = document.createElement('div');
			var title = document.createElement('h3');
			title.innerText = "Templates";
			container.appendChild(title);
			renderTemplate(container, row.template, 0);
			return container;
		}

		function renderTemplate(container, template, level) {
			var element = getTemplate('templates-template');
			element.style['margin-left'] = (level * 1) + "em";
			element.querySelector('#name-short').innerText = shortVersion(template.file);
			element.querySelector('#name-full').innerText = template.file;
			element.querySelector('#module').innerText = template.module;
			element.querySelector('#path').innerText = template.path;
			
			delete template.variables.totiIdentity;
			delete template.variables.nonce;
			if (Object.keys(template.variables).length === 0) {
				element.querySelector('#variables').remove();
			} else {
				fillParameterTable(element.querySelector('#variables table'), template.variables);
			}
			container.appendChild(element);
			template.childs.forEach((child)=>{
				renderTemplate(container, child, level+1);
			});
		}

		function renderQueries(row) {
			if (!row.hasOwnProperty('queries')) {
				return document.createElement('div');
			}
			var container = document.createElement('div');
			var title = document.createElement('h3');
			title.innerText = "SQL Queries";
			container.appendChild(title);
			row.queries.forEach((query)=>{
				var element = getTemplate('queries-template');
				element.querySelector('#query-short').innerText = shortVersion(query.sql);
				element.querySelector('#query-full').innerText = query.sql;
				element.querySelector('#query-translated').innerText = query.replacedSql;
				var status = element.querySelector('#status');
				if (query.isExecuted) {
					element.querySelector('#time').innerText = query.executionTime;
					status.innerText = "Executed";
					status.style['background-color'] = "green";
					element.querySelector('#result span').innerText = query.result;
				} else {
					status.innerText = "Not executed";
					status.style['background-color'] = "red";
					element.querySelector('#result').remove();
				}
				
				
				var paramList = element.querySelector('#params-list');
				var paramTable = element.querySelector('#params-table');
				if (!query.hasOwnProperty('params')) {
					paramList.remove();
					paramTable.remove();
				} else if (Array.isArray(query.params)) {
					paramTable.remove();
					query.params.forEach((param)=>{
						var item = document.createElement('li');
						item.innerText = param;
						paramList.querySelector('ol').appendChild(item);
					});
				} else {
					paramList.remove();
					fillParameterTable(paramTable.querySelector('table'), query.params);
				}
				container.appendChild(element);
			});
			return container;

		}

		function renderTranslations(row) {
			if (!row.hasOwnProperty('translations')) {
				return document.createElement('div');
			}
			var container = getTemplate('translations-template');
			row.translations.missingFiles.forEach((file)=>{
				var element = document.createElement("li");
				element.innerText = file;
				container.querySelector('#missing-locales').appendChild(element);
			});
			row.translations.missingTranslations.forEach((trans)=>{
				var mod = document.createElement('td');
				mod.innerText = trans.module;
				var locale = document.createElement('td');
				locale.innerText = trans.locale;
				var key = document.createElement('td');
				key.innerText = trans.key;
				var params = document.createElement('td');
				for (const[name, value] of Object.entries(trans.params)) {
					var nameContainer = document.createElement('strong');
					nameContainer.innerText = name + ": ";
					var valueContainer = document.createElement('span');
					valueContainer.innerText = value;
					params.appendChild(nameContainer);
					params.appendChild(valueContainer);
					params.appendChild(document.createElement('br'));
				}

				var item = document.createElement('tr');
				item.appendChild(locale);
				item.appendChild(mod);
				item.appendChild(key);
				item.appendChild(params);
				container.querySelector('#missing-translations').appendChild(item);
			});
			return container;
		}
	</script>
</body>
</html>