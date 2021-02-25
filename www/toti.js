/* TOTI script version 0.1.0 */
var totiSettings = {
	flashTimeout: 0
};

var totiImages = {
	/* https://www.iconfinder.com/icons/186407/up_arrow_icon */
	"arrowUp": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xOC4yMjEsNy4yMDZsOS41ODUsOS41ODVjMC44NzksMC44NzksMC44NzksMi4zMTcsMCwzLjE5NWwtMC44LDAuODAxYy0wLjg3NywwLjg3OC0yLjMxNiwwLjg3OC0zLjE5NCwwICBsLTcuMzE1LTcuMzE1bC03LjMxNSw3LjMxNWMtMC44NzgsMC44NzgtMi4zMTcsMC44NzgtMy4xOTQsMGwtMC44LTAuODAxYy0wLjg3OS0wLjg3OC0wLjg3OS0yLjMxNiwwLTMuMTk1bDkuNTg3LTkuNTg1ICBjMC40NzEtMC40NzIsMS4xMDMtMC42ODIsMS43MjMtMC42NDdDMTcuMTE1LDYuNTI0LDE3Ljc0OCw2LjczNCwxOC4yMjEsNy4yMDZ6IiBmaWxsPSIjNTE1MTUxIi8+PC9zdmc+",
	/* https://www.iconfinder.com/icons/186411/down_arrow_icon */
	"arrowDown": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xNC43NywyMy43OTVMNS4xODUsMTQuMjFjLTAuODc5LTAuODc5LTAuODc5LTIuMzE3LDAtMy4xOTVsMC44LTAuODAxYzAuODc3LTAuODc4LDIuMzE2LTAuODc4LDMuMTk0LDAgIGw3LjMxNSw3LjMxNWw3LjMxNi03LjMxNWMwLjg3OC0wLjg3OCwyLjMxNy0wLjg3OCwzLjE5NCwwbDAuOCwwLjgwMWMwLjg3OSwwLjg3OCwwLjg3OSwyLjMxNiwwLDMuMTk1bC05LjU4Nyw5LjU4NSAgYy0wLjQ3MSwwLjQ3Mi0xLjEwNCwwLjY4Mi0xLjcyMywwLjY0N0MxNS44NzUsMjQuNDc3LDE1LjI0MywyNC4yNjcsMTQuNzcsMjMuNzk1eiIgZmlsbD0iIzUxNTE1MSIvPjwvc3ZnPg==",
	/* https://www.iconfinder.com/icons/186389/delete_remove_icon */
	"cross": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0yMC4zNzcsMTYuNTE5bDYuNTY3LTYuNTY2YzAuOTYyLTAuOTYzLDAuOTYyLTIuNTM5LDAtMy41MDJsLTAuODc2LTAuODc1Yy0wLjk2My0wLjk2NC0yLjUzOS0wLjk2NC0zLjUwMSwwICBMMTYsMTIuMTQyTDkuNDMzLDUuNTc1Yy0wLjk2Mi0wLjk2My0yLjUzOC0wLjk2My0zLjUwMSwwTDUuMDU2LDYuNDVjLTAuOTYyLDAuOTYzLTAuOTYyLDIuNTM5LDAsMy41MDJsNi41NjYsNi41NjZsLTYuNTY2LDYuNTY3ICBjLTAuOTYyLDAuOTYzLTAuOTYyLDIuNTM4LDAsMy41MDFsMC44NzYsMC44NzZjMC45NjMsMC45NjMsMi41MzksMC45NjMsMy41MDEsMEwxNiwyMC44OTZsNi41NjcsNi41NjYgIGMwLjk2MiwwLjk2MywyLjUzOCwwLjk2MywzLjUwMSwwbDAuODc2LTAuODc2YzAuOTYyLTAuOTYzLDAuOTYyLTIuNTM4LDAtMy41MDFMMjAuMzc3LDE2LjUxOXoiIGZpbGw9IiM1MTUxNTEiLz48L3N2Zz4="
};

var totiTranslations = {
	"pages": {
		"title": /* "<t:trans message='common.grid.paging.pages'/>", /*/ "Pages:", //*/
		"first": /* "<t:trans message='common.grid.paging.first' />", /*/ "First", //*/
		"previous": /* "<t:trans message='common.grid.paging.previous' />", /*/ "Previous", //*/
		"next": /* "<t:trans message='common.grid.paging.next' />", /*/ "Next", //*/
		"last": /* "<t:trans message='common.grid.paging.last' />" /*/ "Last" //*/
	},
	"actions": {
		"select": /* "<t:trans message='common.grid.action.select' />", /*/ "Select action", //*/
		"execute": /* "<t:trans message='common.grid.action.execute' />", /*/ "Execute", //*/
		"noSelectedItems": /* "<t:trans message='common.grid.action.no-selected-item' />" /*/ "No selected items" //*/
	},
	"gridMessages": {
		"noItemsFound": /* "<t:trans message='common.grid.no-item-found' />", /*/ "No Item Found", //*/
		"loadingError": /* "<t:trans message='common.grid.loading-error' />" /*/ "Problem with data loading" //*/
	},
	"formMessages": {
		"saveError": /* "<t:trans message='common.form.saving-problem' />", /*/ "Problem with form saving", //*/
		"bindError": /* "<t:trans message='common.form.binding-problem' />" /*/ "Loading data failure" //*/
	},
	/* TODO use translations with JSON.parse() ??? */
	"timestamp": {
		dateString: {
			"date": {"year": "numeric", "month": "long", "day": "numeric"},
			"datetime-local": {
				"year": "numeric", "month": "long", "day": "numeric",
				"hour": "numeric", "minute": "numeric", "second": "numeric"
			},
			"month": {"year": "numeric", "month": "long"},
		},
		timeString: {
			"time": {"hour": "numeric", "minute": "numeric", "second": "numeric"},
		},
		"week": {"year": "numeric", "week": "numeric"},
	}
};

var totiUtils = {
	/* TODO is used? */
	parseUrlToObject: function (data) {
		return JSON.parse('{"' + data.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
	},
	parametrizedString: function(string, params) {
		for(const[name, value] of Object.entries(params)) {
			string = string.replaceAll("\{" + name + "\}", value);
		}
		return string;
	},
	forEach: function(array, callback) {
		/*Array.prototype.forEach.call()*/
		if (typeof array === 'object') {
			for (const[key, item] of Object.entries(array)) {
				callback(key, item);
			}
		} else {
			array.forEach(function(item, index) {
				callback(index, item);
			});
		}
	}
};

var totiStorage = {
	saveVariable: function(name, value) {
		localStorage[name] = JSON.stringify(value);
	},
	getVariable: function(name) {
		if (!localStorage[name] || localStorage[name] === null || (localStorage[name] == 'null') || localStorage[name] === undefined) {
			return null;
		}
		return JSON.parse(localStorage[name]);
	},
	removeVariable: function(name) {
		localStorage.removeItem(name);
	}
};

var totiLang = {
	variableName: "language",
	changeLanguage: function (language) {
		totiStorage.saveVariable(totiLang.variableName, language);
		document.cookie = "Language=" + language + ";Path=/";
	},
	getLang: function() {
		var lang = totiStorage.getVariable(totiLang.variableName);
		if (lang === null) {
			return navigator.language.toLowerCase().replace("-", "_");
		}
		return lang;
	},
	getLangHeader: function() {
		var lang = totiStorage.getVariable(totiLang.variableName);
		if (lang === null) {
			return {};
		}
		return {
			"Accept-Language": lang
		};
	}
};

var totiLoad = {
	async: function(url, method, data, headers, onSuccess, onFailure, async = true) {
		var params = new URLSearchParams(data).toString();
		var xhr = new XMLHttpRequest();
		if (method.toLowerCase() === "get") {
			url += "?" + new URLSearchParams(data).toString();
		}
		xhr.open(method, url, async);
		for (const[name, value] of Object.entries(headers)) {
			xhr.setRequestHeader(name, value);
		}
		xhr.onload = function() {
			if (xhr.status >= 400) {
				onFailure(xhr);
			} else {
				var response = xhr.response;
				try {
	        		response = JSON.parse(xhr.response);
			    } catch (e) {
			    	response = xhr.response;
			    }
			    onSuccess(response, xhr);
			}
			/* TODO rs 300 ???*/
		};
		xhr.send(params);
	},
	link: function(url, method, data, headers) {
		window.location = url + "?ids=" + new URLSearchParams(data).toString();
/*
		var xhr = new XMLHttpRequest()
		xhr.open(method, url, true);
		for (const[name, value] of Object.entries(headers)) {
			xhr.setRequestHeader(name, value);
		}
		var onFinish = function() {
			document.documentElement.innerHTML = xhr.response;
			window.history.pushState({},"", xhr.responseURL);
			totiDisplay.printStoredFlash();
		};
		xhr.onload = onFinish();
		//xhr.onerror = onFinish();
		xhr.send(new URLSearchParams(data).toString());*/
		/* location.reload();
		 window.onload();
		 console.log("onload");*/
	},
	/* TODO wll be here ?*/
	getHeaders: function() {
		return {
			...totiAuth.getAuthHeader(),
			...totiLang.getLangHeader()
		};
	}
};

var totiAuth = {
	variableName: "authentication",
	getAuthHeader: function(access = true) {
		var token = totiStorage.getVariable(totiAuth.variableName);
		if (token === null) {
			return {};
		}
		return {
			"Authorization": token.token_type + " " + (access ? token.access_token : token.refresh_token)
		};
	},
	/* for public use */
	getToken: function() {
		var token = totiStorage.getVariable(totiAuth.variableName);
		if (token !== null) {
			delete token.config;
		}
		return token;
	},
	isRefreshActive: false,
	setTokenRefresh: function(token = null, period = -1) {
		if (totiAuth.isRefreshActive) {
			console.log("Another refresh is running");
			return false;
		}
		token = token || totiStorage.getVariable(totiAuth.variableName);
		if (!token) {
			console.log("No saved token");
			return false;
		}
		totiAuth.isRefreshActive = true;
		totiStorage.saveVariable(totiAuth.variableName, token);
		if (period < 0) {
			period = token.expires_in * 2 / 3;
		}
		setTimeout(function() {
			totiLoad.async(
				token.config.refresh.url,
				token.config.refresh.method, 
				{}, 
				totiAuth.getAuthHeader(false), 
				function(gettedToken) {
					totiAuth.isRefreshActive = false;
					gettedToken.config = token.config;
					totiAuth.setTokenRefresh(gettedToken, -1);
				}, 
				function(xhr) {
					totiAuth.isRefreshActive = false;
					/*if (period < 5000) {*/
						totiStorage.removeVariable(totiAuth.variableName);
					/*} else {
						totiAuth.setTokenRefresh(token, period / 2);
					}	*/				
				}
			);
		}, period);
		return true;
	},
	logout: function() {
		if (totiStorage.getVariable(totiAuth.variableName) === null) {
			console.log("No token");
			return;
		}
		var token = totiStorage.getVariable(totiAuth.variableName);
		totiLoad.async(
			token.config.logout.url,
			token.config.logout.method, 
			{}, 
			totiAuth.getAuthHeader(), 
			function(res) {}, 
			function(xhr, a, error) {
				console.log(xhr, a, error);
			}
		);
		totiStorage.removeVariable(totiAuth.variableName);
	},
	login: function(token, config) {
		token.config = config;
		totiAuth.setTokenRefresh(token);
	},
	onLoad: function() {
		totiAuth.setTokenRefresh(null, 0);
	}
};

var totiDisplay = {
	prompt: function(message, defValue = "") {
		return prompt(message, defValue);
	},
	confirm: function(message) {
		return confirm(message);
	},
	alert: function(message) {
		alert(message);
	},
	flash: function(severity, message) {
		var div = document.createElement("div");

		var img = document.createElement("img");
		img.setAttribute("src", totiImages.cross);
		img.setAttribute("alt", "");
		img.setAttribute("width", 15);
		img.onclick = function() {
			div.style.display = "none";
		};

		var span = document.createElement("span");
		span.innerHTML = '&nbsp;&nbsp;' + message;

		div.setAttribute("class", 'flash flash-' + severity);
		div.appendChild(img);
		div.appendChild(span);

		if (totiSettings.flashTimeout > 0) {
			setTimeout(function() {
				div.style.display = "none";
			}, totiSettings.flashTimeout);
		}
		document.getElementById('flash').appendChild(div);
		console.log("Flash " + severity + ":");
		console.log(message);
	},
	storedFlash: function(severity, message) {
		var name = 'flash';
		var actual = totiStorage.getVariable(name);
		if (actual === null) {
			actual = {};
		}
		if (actual[severity] === undefined) {
			actual[severity] = [];
		}
		actual[severity].push(message);
		totiStorage.saveVariable(name, actual);
	},
	printStoredFlash: function() {
		var name = 'flash';
		var actual = totiStorage.getVariable(name);
		if (actual !== null) {
			for (const[severity, messages] of Object.entries(actual)) {
				messages.forEach(function(message) {
					totiDisplay.flash(severity, message);
				});
			}
			totiStorage.removeVariable(name);
		}
	}
};

var totiControl = {
	label: function (forInput, title, params = {}) {
		var label = document.createElement("label");
		label.innerText = title;
		label.setAttribute("for", forInput);
		for ([key, name] of Object.entries(params)) {
			label.setAttribute(key, name);
		}
		return label;
	},
	button: function(attributes) {
		var button = document.createElement('button');
		for ([key, name] of Object.entries(attributes)) {
			if (key === "value") {
				button.innerHTML = name;
			} else {
				button.setAttribute(key, name);
			}
		}
		return button;
	},
	input: function (attributes) {
		if (!attributes.hasOwnProperty('type')) {
			console.error("Missing attribute type", attributes);
			return;
		}
		var type = attributes.type;
		attributes.originType = type;
		delete attributes.type;

		if (type === "checkbox" && attributes.value) {
			attributes.checked = "checked";
		}

		/* IMP datetime */
		/*if (type === 'datetime') {
			return totiControl.inputs._createInput("datetime-local", attributes);
		} else*/if (type === 'textarea') {
			return totiControl.inputs.textarea(attributes);
		} else if (type === 'select') {
			return totiControl.inputs.select(attributes);
		} else if (type === 'option') {
			return totiControl.inputs.option(attributes);
		} else if (type === 'radiolist') {
			return totiControl.inputs.radiolist(attributes);
		} else {
			return totiControl.inputs._createInput(type, attributes);
		}
	},
	inputs: {
		_selectCache: {},
		_createInput: function (type, attributes) {
			var input = document.createElement("input");
			input.setAttribute("type", type);
			for ([key, name] of Object.entries(attributes)) {
				input.setAttribute(key, name);
			}
			return input;
		},
		radiolist: function(params) {
			var input = document.createElement("div");
			params.radios.forEach(function(radio) {
				var item = document.createElement("div");
				var id = params.name + "-" + radio.id;
				item.setAttribute('id', id + "-block");
				if (radio.hasOwnProperty('title')) {
					item.appendChild(totiControl.label(params.id, radio.title, {
						id: id + "-label"
					}));
				}
				var settings = {
					id: id,
					name: params.name,
					form: params.formName,
					value: radio.value,
					type: "radio"
				};
				if (radio.value === params.value) {
					settings.checked = "checked";
				}
				if (params.hasOwnProperty('required')) {
					settings.required = params.required;
				}
				if (params.hasOwnProperty('disabled')) {
					settings.disabled = params.disabled;
				}
				item.appendChild(totiControl.input(settings));
				input.appendChild(item);
			});
			return input;
		},
		textarea: function(params) {
			var textarea = document.createElement('textarea');
			for ([key, name] of Object.entries(params)) {
				if (key === "value") {
					textarea.innerHTML = name;
				} else {
					textarea.setAttribute(key, name);
				}
			}
			return textarea;
		},
		select: function (params) {
			var select = document.createElement('select');
			for ([key, value] of Object.entries(params)) {
				if (key === "options" || key === "load") {
					/* ignored now, done soon*/
				} else {
					select.setAttribute(key, value);
				}
			}
			var addOption = function(option) {
				if (typeof option === 'object') {
					option.type = "option";
					select.appendChild(totiControl.input(option));
				} else {
					select.appendChild(option);
				}
			};
			totiUtils.forEach(params.options, function(v, option) {
				addOption(option);
			});
			if (params.hasOwnProperty("load")) {
				var cacheKey = JSON.stringify({
					"url": params.load.url,
					"method": params.load.method,
					"params": params.load.params
				});
				var onSuccess = function(loaded) {
					totiUtils.forEach(loaded, function(value, opt) {
						var option = { "value": value };
						if (typeof opt === "object") {
							option.title = opt.title;
							if (opt.disabled) {
								option.disabled = "disabled";
							}
						} else {
							option.title = opt;
						}
						params.options[value] = option; /* for value renderer*/
						addOption(option);
					});
				};
				if (totiControl.inputs._selectCache.hasOwnProperty(cacheKey)) {
					onSuccess(totiControl.inputs._selectCache[cacheKey]);
				} else {
					totiLoad.async(params.load.url, params.load.method, params.load.params, totiLoad.getHeaders(), function(loaded) {
						onSuccess(loaded);
						totiControl.inputs._selectCache[cacheKey] = loaded;
					}, function(xhr) {
						console.log(xhr);
					}, false);
				}
			}
			
			return select;
		},
		option: function(params) {
			var option = document.createElement('option');
			for ([key, value] of Object.entries(params)) {
				if (key === "title") {
					option.innerHTML = value;
				} else {
					option.setAttribute(key, value);
				}
				
			}
			return option;
		}
	},
	parseValue: function(type, value) {
		/* TODO week*/
		if (totiTranslations.timestamp.dateString.hasOwnProperty(type) && value !== '' && value !== null) {
			return new Date(value).toLocaleDateString(
            	totiLang.getLang().replace("_", "-"),
				totiTranslations.timestamp.dateString[type]
			);
		}
		if (totiTranslations.timestamp.timeString.hasOwnProperty(type) && value !== '' && value !== null) {
			value = "1970-01-01 " + value;
			return new Date(value).toLocaleTimeString(
            	totiLang.getLang().replace("_", "-"),
				totiTranslations.timestamp.timeString[type]
			);
		}
		return value;
	},
	getAction: function(clickSettings) {
		return function(event) {
			event.preventDefault();
			if (clickSettings.submitConfirmation !== null
				 && clickSettings.submitConfirmation !== undefined 
				 && !clickSettings.submitConfirmation()) {
				return false;
			}
			if (clickSettings.async) {
				totiLoad.async(clickSettings.href, clickSettings.method, {}, totiLoad.getHeaders(), function(res) {
					if (clickSettings.hasOwnProperty('onSuccess')) {
						window[clickSettings.onSuccess](res);
					} else {
						totiDisplay.flash("success", res);
					}
				}, function(xhr) {
					if (clickSettings.hasOwnProperty('onError')) {
						window[clickSettings.onError](xhr);
					} else {
						totiDisplay.flash("error", xhr);
					}
				});
			} else {
				/* totiControl.load.link(href, method, {}, totiControl.getHeaders()); */
				window.location = clickSettings.href;
			}
		};
	}
};

class TotiForm {

	constructor(config) {
		this.config = config;
	}

	init(elementIdentifier, uniqueName) {
		var object = this;
		document.addEventListener("DOMContentLoaded", function(event) { 
			document.querySelector(elementIdentifier).appendChild(
				object.create(uniqueName, document.querySelector(elementIdentifier))
			);
			if (object.config.hasOwnProperty('bind')) {
				object.bind(uniqueName, object.config.bind);
			}
		});
	}

	create(uniqueName, element) {
		var printSelectFunc = function (field, optionsName) {
			var input = document.createElement('div');
			var options = field[optionsName];
			delete field[optionsName];
			for ([key, name] of Object.entries(field)) {
				input.setAttribute(key, name);
			}
			var withOption = function(i, option) {
				var span = document.createElement('span');
				span.setAttribute("value", option.value);
				span.innerText = option.title;
				if (field.value !== option.value) {
					span.style.display = "none";
				}
				input.appendChild(span);
			};
			totiUtils.forEach(options, withOption);
			return input;
		}

		var form;
		if (this.config.editable) {
			var errors = document.createElement("div");
			errors.setAttribute("id", uniqueName + "-errors-form");
			errors.appendChild(document.createElement("span"));
			form = document.createElement("form");
			form.setAttribute("id", uniqueName);
			form.setAttribute("action", this.config.action);
			form.setAttribute("method", this.config.method);
			form.appendChild(errors);
		} else {
			form = document.createElement("div");
			form.setAttribute("id", uniqueName);
		}
		var table;
		var useTemplate = false;
		if (element.innerHTML.length === 0) {
			table = document.createElement("table");
		} else {
			useTemplate = true;
			table = document.createElement("div");
			table.innerHTML = element.innerHTML;
			element.innerHTML = "";
		}
		form.appendChild(table);
		
		var config = this.config;
		var object = this;
		var createInputs = function(fields, parent = null) {
			fields.forEach(function(field, index) {
				if (parent !== null) {
					field.name = parent + "[" + (field.hasOwnProperty("name") ? field.name : "") + "]";
				}
				if (field.type === "list") {
					createInputs(field.fields, field.name);
					return;
				}

				field.id = uniqueName + "-" + field.id;
				field.form = uniqueName;
				var label = null;
				if (field.hasOwnProperty('title')) {
					label = totiControl.label(field.id, field.title, {
						id: uniqueName +  "-" + field.id + "-label"
					});
				}
				var input;
				if (!config.editable && field.type !== 'button') {
					field.originType = field.type;
					if (field.type === 'select') {
						totiControl.inputs.select(field); /* load select options if are*/
						input = printSelectFunc(field, 'options');
					} else if (field.type === 'radiolist') {
						input = printSelectFunc(field, 'radios');
					} else if (field.type === 'checkbox') {
						input = printSelectFunc(field, 'values');
					} else if (field.type !== 'submit' && field.type !== 'hidden') {
						input = document.createElement("div");
						for ([key, name] of Object.entries(field)) {
							if (key === "value") {
								input.innerText = name;
							} else {
								input.setAttribute(key, name);
							}
						}				
					} else {
						input = document.createElement('span');
					}
				} else {
					if (field.type === 'file') {
						form.setAttribute("enctype", "multipart/form-data");
					} else if (field.type === 'radiolist') {
						field.formName = uniqueName;
					}
					var type = field.type;
					input = totiControl.input(field);

					if (type === 'submit' || type === 'image') {
						input.onclick = object.getSubmit(uniqueName, input);
					} else if (type === 'button') {
						var onClick = totiControl.getAction({
							href: field.href,
							method: field.method,
							async: field.ajax,
							submitConfirmation: function() {
								if (field.hasOwnProperty('confirmation')) {
									return totiDisplay.confirm(field.confirmation, row);
								}
								return true;
							}/*,
							onSuccess: "",
							onFailure: ""*/
						});
						input.onclick = onClick;
					}
				}
				var error = document.createElement("div");
				error.setAttribute("id", uniqueName + '-errors-' + field.name);
				if (useTemplate) {
					var labelElement = form.querySelector("#form-label-" + field.name);
					if (labelElement !== null && label !== null) {
						labelElement.appendChild(label);
					}
					var inputElement = form.querySelector("#form-input-" + field.name);
					if (inputElement !== null) {
						inputElement.appendChild(input);
					}
					var errorElement = form.querySelector("#form-error-" + field.name);
					if (config.editable && errorElement !== null) {
						errorElement.appendChild(error);
					}
				} else {
					var first = document.createElement("td");
					first.setAttribute("class", 'toti-form-label');
					if (label !== null) {
						first.appendChild(label);
					}

					var second = document.createElement("td");
					second.setAttribute("class", 'toti-form-input');
					second.appendChild(input);

					var third = document.createElement("td");
					third.setAttribute("class", 'toti-form-error');
					third.appendChild(error);

					var row = document.createElement("tr");
					row.appendChild(first);
					row.appendChild(second);
					row.appendChild(third);
					var tableContent = document.createElement("div");
					table.appendChild(row);
				}
			});
		}
		createInputs(this.config.fields);
		return form;
	}

	getSubmit(uniqueName, submit) {
		return function(event) {
			Array.prototype.forEach.call(document.getElementsByClassName('error-list'), function(el) {
			    el.remove();
			});

			var form = document.getElementById(uniqueName);
			if (!form.reportValidity()) {
				return false;
			}
			var data = new FormData(form); 
			Array.prototype.forEach.call(form.elements, function(input) {
				var type = input.getAttribute("type");
				var name = input.getAttribute("name");

				/*IMP datetime*/
				/*if (type === "datetime-local") {
					value = input.value;
					value = value.replace("T", " ");
					data.append(name, value);
				} else */if (type === "submit" || type === "button" || type === "reset") {
					/* ignored*/
					/* TODO img too ??*/
				} else if (type === "radio") {
					if (input.checked) {
						data.append(name, input.value);
					}
				} else if (type === "checkbox") {
					data.append(name, input.checked);
				} else if (type === "file") {
					if (input.files.length > 0) {
						data.append(name, input.value);
					}
				} else {
					data.append(name, input.value);
				}
			});

			var submitConfirmation = function() {
				if (submit.hasOwnProperty('confirmation')) {
					return totiDisplay.confirm(submit.confirmation);
				}
				return true;
			};
			if (submitConfirmation !== null && !submitConfirmation(data)) {
				event.preventDefault();
				return false;
			}
			if (submit.getAttribute("ajax")) {
				event.preventDefault();
				var header = totiLoad.getHeaders();
				if (form.getAttribute("enctype") !== null) {
					header.enctype = form.getAttribute("enctype");
				}
				totiLoad.async(
					form.getAttribute("action"), 
					form.getAttribute("method"), 
					data, 
					header, 
					function(response) {
						if (submit.getAttribute("onSuccess") != null) {
							window[submit.getAttribute("onSuccess")](response);
						} else {
							totiDisplay.flash('success', response.message);
						}
						if (submit.getAttribute("redirect") != null) {
							totiDisplay.storedFlash('success', response.message);
							window.location = submit.getAttribute("redirect").replace("{id}", response.id);
						}
					}, 
					function(xhr) {
						if (xhr.status === 400) {
							console.log(JSON.parse(xhr.responseText));
							for (const[key, list] of Object.entries(JSON.parse(xhr.responseText))) {
								var ol = document.createElement("ul");
								ol.setAttribute("class", "error-list");
								list.forEach(function(item) {
									var li = document.createElement("li");
									li.innerText = item;
									ol.appendChild(li);
								});
								var elementId = key.replaceAll("[", "\\[").replaceAll("]", "\\]");
								console.log(key, list, document.querySelectorAll('#' + uniqueName + '-errors-' + elementId + ''));
								/*TODO need solve id - list*/
								document.querySelector('#' + uniqueName + '-errors-' + elementId + '').appendChild(ol);
								//document.querySelector('#' + uniqueName + '-errors-' + key + '').appendChild(ol);
							}
						} else if (submit.getAttribute("onFailure") != null) {
							window[submit.getAttribute("onFailure")](xhr);
						} else {
							totiDisplay.flash('error', totiTranslations.formMessages.saveError);
						}
					}
				);
			}
		};
	}

	/**
	bind: url, method, jsonObject params, String beforeBind(optional), String afterBind(optional), String onFailure(optional)
	*/
	bind(formId, bind) {
		totiLoad.async(
			bind.url, 
			bind.method, 
			bind.params,
			totiLoad.getHeaders(), 
			function(values) {
				if (bind.hasOwnProperty("beforeBind")) {
					window[bind.beforeBind]();
				}
				var form  = document.getElementById(formId);
				for (const[key, val] of Object.entries(values)) {
					var value = val;
					var element = form.querySelector('[name="' + key + '"]');
					if (element === null) {
						continue;
					}
					if (element.type === undefined) { /* detail*/
						value = totiControl.parseValue(element.getAttribute("originType"), value);
						if (element.querySelector("span") != null) { /* select or radio list*/
							element.querySelectorAll("span").forEach(function(el) {
								el.style.display = "none";
							});
							if (value === null) {
								element.querySelector("[value=''").style.display = "block";
							} else {
								element.querySelector("[value='" + value + "']").style.display = "block";
							}
						} else {
							element.innerText = value;
						}
					} else { /*form*/
						/* IMP datetime*/
						/*if (element.type === "datetime-local" && value !== null) {
							value = value.replace(" ", "T");
						}*/
						if (element.type === "checkbox") {
							element.checked = value ? "checked" : false;
						} else if (element.type === "radio") {
							form.querySelector('[name="' + key + '"][value="' + value + '"]').setAttribute("checked", true);
						} else {
							element.value = value;
						}
					}
				}
				if (bind.hasOwnProperty("afterBind")) {
					window[bind.afterBind]();
				}
			}, 
			function(xhr) {
				if (bind.hasOwnProperty('onFailure')) {
					window[bind.onFailure](xhr);
				} else {
					totiDisplay.flash('error', totiTranslations.formMessages.bindError);
				}
			}
		);
	}
}

class TotiGrid {

	constructor(config) {
		this.config = config;
	}

	init(elementIdentifier, uniqueName) {
		var object = this;
		document.addEventListener("DOMContentLoaded", function(event) { 
			document.querySelector(elementIdentifier).appendChild(
				object.create(uniqueName, document.querySelector(elementIdentifier))
			);
			object.load(uniqueName, true);
		});
	}

	create(uniqueName, element) {
		var head = document.createElement("thead");
		head.appendChild(this.createSorting(uniqueName, this.config.columns));
		head.appendChild(this.createFiltering(uniqueName, this.config.columns));

		var body = document.createElement("tbody");
		/***********/
		var space = document.createElement("span");
		space.innerHTML = '&nbsp;';
		var footer = document.createElement("td");
		footer.setAttribute("colspan", 100);
		if (this.config.actions.length > 0) {
			footer.appendChild(this.createActions(uniqueName, this.config.actions));
			footer.appendChild(space);
			footer.appendChild(space);
		}
		footer.appendChild(this.createPages(uniqueName, this.config.pages.pagesButtonCount, 1));
		footer.appendChild(space);
		footer.appendChild(space);
		footer.appendChild(this.createPagesSize(uniqueName, this.config.pages.pagesSizes, this.config.pages.defaultSize));
		/***********/

		var table = document.createElement("table");
		table.setAttribute("class", "toti-table");

		table.appendChild(head);
		table.appendChild(body);
		var footerTr = document.createElement("tr");
		footerTr.appendChild(footer);
		var tFooter = document.createElement("tfoot");
		tFooter.appendChild(footerTr);

		document.createElement("tfooter");
		table.appendChild(tFooter);
		/* TODO caption s x from y*/

		var grid = document.createElement("div");
		grid.setAttribute("id", uniqueName + "-control");
		grid.appendChild(table);
		return grid;
	}

	createSorting(uniqueName, columns) {
		var object = this;
		var printCell = function(uniqueName, name, useSorting, title = null) {
			var cell = document.createElement('a');
			if (title !== null) {
				cell.innerText = title;
			} else {
				cell.innerText = name;
			}
			if (useSorting) {
				cell.setAttribute("href", "");
				cell.setAttribute("class", "toti-sortable");
				cell.setAttribute("data-sort", 0);
				cell.onclick = function(event) {
					event.preventDefault();
					var sortType = parseInt(cell.getAttribute('data-sort')) + 1;
					if (sortType === 3) {
						cell.setAttribute('data-sort', 0);
					} else {
						cell.setAttribute('data-sort', sortType);
					}
					cell.querySelector(".sortType").style.display = "none";
					cell.querySelector(".type" + sortType).style.display = "inline";
					object.load(uniqueName);
				};

				var imgUp = document.createElement("img");
				imgUp.setAttribute("src", totiImages.arrowUp);
				imgUp.setAttribute("alt", "");
				imgUp.setAttribute("width", 15);
				imgUp.setAttribute("class", "sortType type1 type3");

				var imgDown = document.createElement("img");
				imgDown.setAttribute("src", totiImages.arrowDown);
				imgDown.setAttribute("alt", "");
				imgDown.setAttribute("width", 15);
				imgDown.setAttribute("class", "sortType type2 type3");

				var div = document.createElement("div");
				div.setAttribute("class", "toti-sorting-arrows");
				div.appendChild(imgUp);
				div.appendChild(imgDown);
				cell.appendChild(div);
			}
			return cell;
		};

		var sortes = document.createElement("tr");
		sortes.setAttribute("id", uniqueName + "-sorting");
		columns.forEach(function(column) {
			var sort = document.createElement("th");
			sort.setAttribute("data-name", column.name);
			sort.appendChild(printCell(uniqueName, column.name, column.useSorting, column.title));

			sortes.appendChild(sort);
		});
		return sortes;
	}

	createFiltering(uniqueName, columns) {
		var object = this;
		var filters = document.createElement("tr");
		filters.setAttribute("id", uniqueName + "-filtering");
		columns.forEach(function (column, index) {
			var cell = document.createElement("th");
			cell.setAttribute("data-name", column.name);

			if (column.type === "actions") {
				var checkbox = totiControl.input({
					type: "checkbox"
				});
				checkbox.onclick = function() {
					var chBoxs = document.querySelectorAll("." + uniqueName + "-grid-action");
					if (chBoxs !== null) {
						chBox.forEach(function(el) {el.setAttribute("checked", checkbox.checked);});
					}
				};
				cell.appendChild(checkbox);
				cell.setAttribute("no-filters", "");
			} else if (column.type == "buttons") {
				var reset = totiControl.input({
					type: "reset"
				});
				cell.appendChild(reset);
			} else if (column.hasOwnProperty('filter')) {
				cell.appendChild(
					totiControl.input(column.filter)
				);
				cell.onchange = function() {
					object.load(uniqueName);
				};
			} else {
				cell.innerText = "";
			}
			filters.appendChild(cell);
		});
		return filters;
	}

	createActions(uniqueName, actions) {
		var options = [];
		options.push({
			"ajax": true,
			"method": null,
			"title": totiTranslations.actions.select,
			"value": ""
		});
		actions.forEach(function(action) {
			action.value = action.link;
			options.push(action);
		});
		var select = totiControl.input({
			options: options,
			type: "select"
		});
		var execute = totiControl.button({
			'class': 'toti-button-execute',
			value: totiTranslations.actions.execute
		});
		execute.onclick = function(event) {
			event.preventDefault();
			var option = select.querySelector("option[value='" + select.value + "']");
			if (option.value === '') {
				return false;
			}
			var url = option.value;
			var method = option.getAttribute("method");
			var ajax = option.getAttribute("ajax");
			var submitConfirmation = option.getAttribute("submitConfirmation");
			
			var ids = [];
			document.querySelectorAll('.' + uniqueName + "-grid-action:checked").forEach(function(checkbox) {
				ids.push(checkbox.getAttribute("data-unique"));
			});
			if (ids.length === 0) {
				totiDisplay.flash("error", totiTranslations.actions.noSelectedItems);
				return false;
			}
			var params = {"ids": ids};
			if (ajax === 'true') {
				if (submitConfirmation !== null
					&& submitConfirmation !== undefined
					&& !totiDisplay.confirm(submitConfirmation)) {
					event.preventDefault();
					return false;
				}
				totiLoad.async(
					url,
					method,
					params,
					totiLoad.getHeaders(),
					function(result) {
						if (option.getAttribute("onSuccess") != null) {
							window[option.getAttribute("onSuccess")](result);
						} else {
							totiDisplay.flash('success', result);
						}
					},
					function(xhr) {
						if (option.getAttribute("onFailure") != null) {
							window[option.getAttribute("onFailure")](xhr);
						} else {
							totiDisplay.flash('error', xhr);
						}
					}
				);
			} else {
				totiLoad.link(url, method, params, totiLoad.getHeaders());
			}
		};
		var actions = document.createElement("div");
		actions.setAttribute('class', "toti-actions");
		actions.setAttribute('style', "display: inline");
		actions.appendChild(select);
		actions.appendChild(execute);
		return actions;
	}

	createPages(uniqueName, pagesButtonCount, actualPage) {
		var pagging = document.createElement("div");
		pagging.setAttribute("id", uniqueName + "-pages");
		pagging.setAttribute("style", "display: inline");
		var span = document.createElement("span");
		span.innerText = totiTranslations.pages.title;
		pagging.appendChild(span);
		var space = document.createElement("span");
		space.innerHTML = '&nbsp;';
		pagging.appendChild(space);
		var list = document.createElement("span");
		list.setAttribute("id", uniqueName + "-pages-list");
		list.setAttribute("data-pagesbuttoncount", pagesButtonCount);
		list.setAttribute("data-actualpage", actualPage);
		pagging.appendChild(list);
		return pagging;
	}

	createPagesSize(uniqueName, pageSizes, defaultSize) {
		var object = this;
		var options = [];
		pageSizes.forEach(function(size, index) {
			options.push({title:size, value:size});
		});
		var select = totiControl.input({
			"id": uniqueName + "-pageSize",
			type: "select",
			options: options
		});
		select.value = defaultSize;
		select.onchange = function() {
			object.load(uniqueName);
		};
		return select;
	}

	load(uniqueName, initialLoad = false) {
		var object = this;
		var loadDataSuccess = function(body, uniqueName, response, columns, identifier) {
			if (response.data.length === 0) {
				var td = document.createElement("td");
				td.setAttribute("colspan", 100);
				td.innerText = totiTranslations.gridMessages.noItemsFound;
				body.appendChild(document.createElement("tr").appendChild(td));
				return;
			}
			object.pagesOnLoad(uniqueName, response.pageIndex, response.itemsCount / object.pagesSizeGet(uniqueName));
			response.data.forEach(function(row, rowIndex) {
				var tableRow = document.createElement("tr");
				tableRow.setAttribute("index", rowIndex);
				tableRow.setAttribute("class", "toti-row-" + (rowIndex %2) + " toti-row-" + uniqueName);
				tableRow.onclick = function(event) {
					/* TODO only if settings select row == true */
					if (event.target.type !== undefined) { /*is input*/
						return;
					}
					var actualClass = tableRow.getAttribute("class");
					Array.prototype.forEach.call(document.getElementsByClassName('row-selected'), function(element) {
			    		var clazz = element.getAttribute("class");
						element.setAttribute("class", clazz.replace("row-selected", ""));
					});
					var clazz = tableRow.getAttribute("class");
					if (!actualClass.includes("row-selected")) {
						tableRow.setAttribute("class", actualClass + " row-selected");
					}
				};

				columns.forEach(function(column, colIndex) {
					var td = document.createElement("td");
					td.setAttribute('index', colIndex);
					if (column.type === 'actions') {
						td.appendChild(totiControl.input({
							type: "checkbox",
							"class": uniqueName + "-grid-action",
							"data-unique": row[identifier]
						}));
					} else if (column.type === 'buttons') {
						column.buttons.forEach(function(button, index) {
							var settings = {
								href: totiUtils.parametrizedString(button.href, row),
								method: button.method,
								async: button.ajax,
								submitConfirmation: function() {
									if (button.hasOwnProperty('confirmation')) {
										var message = totiUtils.parametrizedString(button.confirmation, row);
										return totiDisplay.confirm(message);
									}
									return true;
								},
								type: button.hasOwnProperty('style') ? button.style : 'basic'
							};
							var buttonElement = totiControl.button(button);
							buttonElement.onclick = function(event) {
								totiControl.getAction(settings)(event);
								setTimeout(function(){
									object.load(uniqueName);
								}, 500);
							};
							td.appendChild(buttonElement);
						});
					} else if (column.hasOwnProperty("renderer")) {
						td.innerHTML = window[column.renderer](row[column.name], row);
					} else if (column.hasOwnProperty("filter") && column.filter.hasOwnProperty("options")) {
						var value = row[column.name];
						if (value !== null) {
							td.innerText = 
								column.filter.options.hasOwnProperty(value)
								 ? column.filter.options[value].title
								 : value;
						}
					} else if (column.hasOwnProperty("filter")  && column.filter.hasOwnProperty("originType")) {
						td.innerText = totiControl.parseValue(column.filter.originType, row[column.name]);
					} else {
						td.innerText = row[column.name];
					}
					tableRow.append(td);
				});
				body.append(tableRow);
			});
			return body;
		};

		var urlParams = {};
		var search = decodeURIComponent(window.location.search.substring(1));
		if (initialLoad && search !== '') {
			urlParams = totiUtils.parseUrlToObject(search);
			this.filtersOnLoad(uniqueName, urlParams);
			this.sortingOnLoad(uniqueName, urlParams);
			this.pagesSizeOnLoad(uniqueName, urlParams.pageSize);
		} else {
			var pageIndex = this.pagesGet(uniqueName);
			var pageSize = this.pagesSizeGet(uniqueName);
			urlParams = {
				pageIndex: pageIndex === undefined ? 1 : pageIndex,
				pageSize: pageSize === undefined ? this.config.pages.pageSizeDefault : pageSize,
				filters: this.filtersGet(uniqueName),
				sorting: this.sortingGet(uniqueName)
			};
		}
		var body = document.querySelector('#' + uniqueName + "-control").querySelector("table").querySelector("tbody");
		body.innerHTML = '';
		var onError = function(xhr) {
			var td = document.createElement("td");
			td.setAttribute("colspan", 100);
			td.innerText = totiTranslations.gridMessages.loadingError;
			var tr = document.createElement("tr");
			tr.appendChild(td);
			body.appendChild(tr);
		};
		totiLoad.async(
			this.config.dataLoadUrl,
			this.config.dataLoadMethod,
			urlParams,
			totiLoad.getHeaders(),
			function(response) {
				window.history.pushState({"html":window.location.href},"", "?" + new URLSearchParams(urlParams).toString());
				try {
					loadDataSuccess(
						body,
						uniqueName,
						response, 
						object.config.columns,
						object.config.identifier
					);
				} catch (e) {
					console.error(e);
					onError();
				}
			},
			onError
		);
	}
	
	filtersOnLoad(uniqueName, urlParams) {
		var data = {};
		if (urlParams.filters !== undefined) {
			data = JSON.parse(urlParams.filters);
		}
		document.getElementById(uniqueName + "-filtering").querySelectorAll("th").forEach(function(element) {
			var name = element.getAttribute('data-name');
			element.children.value = data[name];
		});
	}

	sortingOnLoad(uniqueName, urlParams) {
		var data = {};
		if (urlParams.sorting != undefined) {
			data = JSON.parse(urlParams.sorting);
		}
		document.getElementById( uniqueName + "-sorting").querySelectorAll('th').forEach(function(sort) {
			var name = sort.getAttribute('data-name');
			if (data.hasOwnProperty(name)) {
				var val = data[name];
				var sortType = 0;
				if (val == 'ASC') {
					sortType = 1;
				} else if (val == "DESC") {
					sortType = 2;
				}
				var a = sort.querySelector("a")
				a.setAttribute("data-sort", sortType);
				a.querySelector(".sortType").style.display = "none";
				a.querySelector(".type" + sortType).style.display = "inline";
			}
		});
	}

	pagesSizeOnLoad(uniqueName, pageSize) {
		document.createElement(uniqueName + "-pageSize").value = pageSize;
	}

	pagesOnLoad(uniqueName, actualPage, pagesCount) {
		var object = this;
		var pagesList = document.getElementById(uniqueName + "-pages-list");
		pagesList.getAttribute("data-actualpage", actualPage);
		pagesList.innerHTML = '';

		var onPageClick = function(newPage) {
			return function() {
				pagesList.setAttribute("data-actualpage", newPage);
				object.load(uniqueName);
				return false;
			};
		};

		var createButton = function(list, title, index, clazz = "") {
			var button = totiControl.button({
				'class': 'toti-button-pages' + clazz,
				value: title
			});
			button.onclick = onPageClick(index);
			list.appendChild(button);
			var span = document.createElement("span");
			span.innerHTML = '&nbsp;';
			list.appendChild(span);
		};

		/* link to first page */
		if (actualPage > 1) {
			createButton(pagesList, totiTranslations.pages.first, 1);
		}
		/* link to previous page */
		if (actualPage > 2) {
			createButton(pagesList, totiTranslations.pages.previous, actualPage - 1);
		}
		/* generated {pagesbuttoncount} pages links */
		var lower = actualPage - Math.floor(pagesList.getAttribute("data-pagesbuttoncount") / 2);
		if (lower < 1) {
			lower = 1;
		}
		for (var i = lower; i < Math.min(lower + pagesList.getAttribute("data-pagesbuttoncount"), pagesCount); i++) {
			var clazz = "";
			if (i === actualPage) {
				clazz = " actualPage";
			}
			createButton(pagesList, i, i, clazz);
		}
		/* next page link */
		if (actualPage < pagesCount) {
			createButton(pagesList, totiTranslations.pages.next, actualPage + 1);
		}
		/* last page link */
		if ((actualPage + 1) < pagesCount) {
			createButton(pagesList, totiTranslations.pages.last, pagesCount);
		}
	}
	
	pagesGet(uniqueName) {
		return document.getElementById(uniqueName + "-pages-list").getAttribute("data-actualpage");
	}

	pagesSizeGet(uniqueName) {
		return document.getElementById(uniqueName + "-pageSize").value;
	}
	
	sortingGet(uniqueName) {
		var sorts = {};
		document.getElementById(uniqueName + "-sorting").querySelectorAll('th').forEach(function(element) {
			var sort = element.querySelector("a").getAttribute("data-sort");
			if (sort === null) {
				return
			}
			sort = parseInt(sort);
			if (element.getAttribute('data-name') != '' && sort !== 0/* && sort != undefined*/) {
				sorts[element.getAttribute("data-name")] = (sort === 1) ? 'ASC' : 'DESC';
			}
		});
		return JSON.stringify(sorts);
	}
	
	filtersGet(uniqueName) {
		var filters = {};
		document.getElementById(uniqueName + "-filtering").querySelectorAll('th').forEach(function(element, index) {
			if (element.getAttribute('no-filters') !== null || element.children.length === 0) {
				return;
			}
			var value = element.children[0].value;
			if (element.getAttribute('data-name') != '' && value !== undefined && value !== '') {
				filters[element.getAttribute('data-name')] = value;
			}
		});
		return JSON.stringify(filters);
	}
}

document.addEventListener("DOMContentLoaded", function(event) { 
	totiDisplay.printStoredFlash();
});