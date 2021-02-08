/* TOTI script version 0.0.9 */
var totiLang = {
	"pages": {
		"title": "<t:trans message='common.grid.paging.pages'/>",
		"first": "<t:trans message='common.grid.paging.first' />",
		"previous": "<t:trans message='common.grid.paging.previous' />",
		"next": "<t:trans message='common.grid.paging.next' />",
		"last": "<t:trans message='common.grid.paging.last' />"
	},
	"actions": {
		"select": "<t:trans message='common.grid.action.select' />",
		"execute": "<t:trans message='common.grid.action.execute' />",
		"noSelectedItems": "<t:trans message='common.grid.action.no-selected-item' />"
	},
	"gridMessages": {
		"noItemsFound": "<t:trans message='common.grid.no-item-found' />",
		"loadingError": "<t:trans message='common.grid.loading-error' />"
	},
	"formMessages": {
		"saveError": "<t:trans message='common.form.saving-problem' />",
		"bindError": "<t:trans message='common.form.binding-problem' />"
	},
	variableName: "language",
	changeLanguage: function (language) {
		totiControl.storage.saveVariable(totiLang.variableName, language);
		document.cookie = "Language=" + language + ";Path=/";
	},
	getLang: function() {
		var lang = totiControl.storage.getVariable(totiLang.variableName);
		if (lang === null) {
			return navigator.language.toLowerCase().replace("-", "_");
		}
		return lang;
	},
	getLangHeader: function() {
		var lang = totiControl.storage.getVariable(totiLang.variableName);
		if (lang === null) {
			return {};
		}
		return {
			"Accept-Language": lang
		};
	}
};

var totiImages = {
	/* https://www.iconfinder.com/icons/186407/up_arrow_icon */
	"arrowUp": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xOC4yMjEsNy4yMDZsOS41ODUsOS41ODVjMC44NzksMC44NzksMC44NzksMi4zMTcsMCwzLjE5NWwtMC44LDAuODAxYy0wLjg3NywwLjg3OC0yLjMxNiwwLjg3OC0zLjE5NCwwICBsLTcuMzE1LTcuMzE1bC03LjMxNSw3LjMxNWMtMC44NzgsMC44NzgtMi4zMTcsMC44NzgtMy4xOTQsMGwtMC44LTAuODAxYy0wLjg3OS0wLjg3OC0wLjg3OS0yLjMxNiwwLTMuMTk1bDkuNTg3LTkuNTg1ICBjMC40NzEtMC40NzIsMS4xMDMtMC42ODIsMS43MjMtMC42NDdDMTcuMTE1LDYuNTI0LDE3Ljc0OCw2LjczNCwxOC4yMjEsNy4yMDZ6IiBmaWxsPSIjNTE1MTUxIi8+PC9zdmc+",
	/* https://www.iconfinder.com/icons/186411/down_arrow_icon */
	"arrowDown": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xNC43NywyMy43OTVMNS4xODUsMTQuMjFjLTAuODc5LTAuODc5LTAuODc5LTIuMzE3LDAtMy4xOTVsMC44LTAuODAxYzAuODc3LTAuODc4LDIuMzE2LTAuODc4LDMuMTk0LDAgIGw3LjMxNSw3LjMxNWw3LjMxNi03LjMxNWMwLjg3OC0wLjg3OCwyLjMxNy0wLjg3OCwzLjE5NCwwbDAuOCwwLjgwMWMwLjg3OSwwLjg3OCwwLjg3OSwyLjMxNiwwLDMuMTk1bC05LjU4Nyw5LjU4NSAgYy0wLjQ3MSwwLjQ3Mi0xLjEwNCwwLjY4Mi0xLjcyMywwLjY0N0MxNS44NzUsMjQuNDc3LDE1LjI0MywyNC4yNjcsMTQuNzcsMjMuNzk1eiIgZmlsbD0iIzUxNTE1MSIvPjwvc3ZnPg==",
	/* https://www.iconfinder.com/icons/186389/delete_remove_icon */
	"cross": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0yMC4zNzcsMTYuNTE5bDYuNTY3LTYuNTY2YzAuOTYyLTAuOTYzLDAuOTYyLTIuNTM5LDAtMy41MDJsLTAuODc2LTAuODc1Yy0wLjk2My0wLjk2NC0yLjUzOS0wLjk2NC0zLjUwMSwwICBMMTYsMTIuMTQyTDkuNDMzLDUuNTc1Yy0wLjk2Mi0wLjk2My0yLjUzOC0wLjk2My0zLjUwMSwwTDUuMDU2LDYuNDVjLTAuOTYyLDAuOTYzLTAuOTYyLDIuNTM5LDAsMy41MDJsNi41NjYsNi41NjZsLTYuNTY2LDYuNTY3ICBjLTAuOTYyLDAuOTYzLTAuOTYyLDIuNTM4LDAsMy41MDFsMC44NzYsMC44NzZjMC45NjMsMC45NjMsMi41MzksMC45NjMsMy41MDEsMEwxNiwyMC44OTZsNi41NjcsNi41NjYgIGMwLjk2MiwwLjk2MywyLjUzOCwwLjk2MywzLjUwMSwwbDAuODc2LTAuODc2YzAuOTYyLTAuOTYzLDAuOTYyLTIuNTM4LDAtMy41MDFMMjAuMzc3LDE2LjUxOXoiIGZpbGw9IiM1MTUxNTEiLz48L3N2Zz4="
};

var totiSettings = {
	flashTimeout: 0
};

var totiControl = {
	inputs: {
		_createInput: function (type, attributes, data = {}) {
			var input = $("<input>").attr("type", type);
			for ([key, name] of Object.entries(attributes)) {
				input.attr(key, name);
			}
			for ([key, name] of Object.entries(data)) {
				input.attr("data-" + key, name);
			}
			return input;
		},
		label: function (forInput, title, params = {}) {
			var label = $('<label>').attr('for', forInput).text(title);
			for ([key, name] of Object.entries(params)) {
				label.attr(key, name);
			}
			return label;
		},
		color: function(params = {}) {
			return totiControl.inputs._createInput("color", params);
		},
		date: function(params = {}) {
			return totiControl.inputs._createInput("date", params);
		},
		week: function(params = {}) {
			return totiControl.inputs._createInput("week", params);
		},
		time: function(params = {}) {
			return totiControl.inputs._createInput("time", params);
		},
		month: function(params = {}) {
			return totiControl.inputs._createInput("month", params);
		},
		tel: function(params = {}) {
			return totiControl.inputs._createInput("tel", params);
		},
		search: function(params = {}) {
			return totiControl.inputs._createInput("search", params);
		},
		image: function(params = {}) {
			return totiControl.inputs._createInput("image", params);
		},
		url: function(params = {}) {
			return totiControl.inputs._createInput("url", params);
		},
		reset: function(params = {}) {
			return totiControl.inputs._createInput("reset", params);
		},
		range: function(params = {}) {
			return totiControl.inputs._createInput("range", params);
		},
		file: function(params = {}) {
			return totiControl.inputs._createInput("file", params);
		},
		hidden: function (params = {}) {
			return totiControl.inputs._createInput("hidden", params);
		},
		radio: function (params = {}) {
			return totiControl.inputs._createInput("radio", params);
		},
		checkbox: function (params = {}) {
			return totiControl.inputs._createInput("checkbox", params);
		},
		/* sugested params: step, max, min */
		number: function (params = {}) {
			return totiControl.inputs._createInput("number", params);
		},
		/* sugested params: size, minlength, maxlength */
		text: function (params = {}) {
			return totiControl.inputs._createInput("text", params);
		},
		/* sugested params: size, minlength, maxlength */
		password: function (params = {}) {
			return totiControl.inputs._createInput("password", params);
		},
		email: function (params = {}) {
			return totiControl.inputs._createInput("email", params);
		},
		datetime: function (params = {}) {
			return totiControl.inputs._createInput("datetime-local", params);
		},
		/* sugested params: cols, rows */
		textarea: function(params = {}) {
			var textarea = $('<textarea>');
			for ([key, name] of Object.entries(params)) {
				if (key === "value") {
					textarea.text(name);
				} else {
					textarea.attr(key, name);
				}
			}
			return textarea;
		},
		select: function (options, params = {}) {
			var select = $('<select>');
			for ([key, name] of Object.entries(params)) {
				select.attr(key, name);
			}
			options.forEach(function(option, index) {
				select.append(option);
			});
			return select;
		},
		option: function(value, title, params = {}) {
			var option = $('<option>').attr("value", value).text(title);
			for ([key, name] of Object.entries(params)) {
				option.attr(key, name);
			}
			return option;
		},
		/* onClick: function | object with settings: href, method, async, submitConfirmation (onSuccess, onFailure¨, type) */
		button: function (onClick, title = "", params = {}, renderer = null) {
			if (renderer === null) {
				renderer = $('<button>').text(title);
			}
			var button = renderer;
			if (typeof onClick === 'object') {
				var originalClass = button.attr("class");
				if (originalClass === undefined) {
					originalClass = "";
				}
				button.attr("class", originalClass + " toti-button-" + onClick.type);
				var clickSettings = onClick;
				onClick = function(event) {
					event.preventDefault();
					if (clickSettings.submitConfirmation !== null
						 && clickSettings.submitConfirmation !== undefined 
						 && !clickSettings.submitConfirmation()) {
						return false;
					}
					if (clickSettings.async) {
						totiControl.load.ajax(clickSettings.href, clickSettings.method, {}, function(res) {
							if (clickSettings.hasOwnProperty('onSuccess')) {
								window[clickSettings.onSuccess](res);
							} else {
								totiControl.display.flash("success", res);
							}
						}, function(xhr) {
							if (clickSettings.hasOwnProperty('onError')) {
								window[clickSettings.onError](xhr);
							} else {
								totiControl.display.flash("error", xhr);
							}
						}, totiControl.getHeaders());
					} else {
						/* totiControl.load.link(href, method, {}, totiControl.getHeaders());*/
						window.location = clickSettings.href;
					}
				};
			}
			button.click(onClick);
			return button;
		},
		submit: function (async = true, submitConfirmation = null, params = {}) {
			var submit = totiControl.inputs._createInput("submit", params);
			submit.click(function(event) {
				$('.error-list').remove();
				var element = $(this);
				var form = $('form#' + element.attr("form"));
				if (!form[0].reportValidity()) {
					return false;
				}

				var data = {};
				$.each(form.serializeArray(), function(index, item) {
					var input = form.find('[name="' + item.name + '"]');
					var value = item.value;
					if (input.attr('type') === 'datetime-local') {
						value = value.replace("T", " ");
					}
					data[item.name] = value;
				});
				var formConfig = {};
				var useFiles = false;
				form.find('[type="file"]').each(function() {
					useFiles = useFiles ? true : $(this).val().length > 0;
				});
				if (form.attr("enctype") !== undefined && useFiles) {
					var formData = new FormData(form[0]);
					for (var key of formData.keys()) {
						if (form.find('input[name="' + key + '"]').attr('type') === 'datetime-local') {
							formData.set(key, formData.get(key).replace("T", " "));
						}
					}
					formConfig = {
						cache: false,
					    contentType: false,
					    processData: false,
					    data: formData,
					    xhr: function () {
					      var myXhr = $.ajaxSettings.xhr();
					      if (myXhr.upload) {
					        myXhr.upload.addEventListener('progress', function (e) {
					            if (e.lengthComputable) {
						            $('progress').attr({
						               value: e.loaded,
						               max: e.total,
						            });
					            }
					        }, false);
					      }
					      return myXhr;
					    }
					};
				}

				if (submitConfirmation !== null && !submitConfirmation(data)) {
					event.preventDefault();
					return false;
				}
				if (async) {
					event.preventDefault();
					var header = totiControl.getHeaders();
					if (form.attr("enctype") !== undefined) {
						header.enctype = form.attr("enctype");
					}
					totiControl.load.ajax(
						form.attr("action"), 
						form.attr("method"), 
						data, 
						function(response) {
							if (element.attr("onSuccess") != null) {
								window[element.attr("onSuccess")](response);
							} else {
								totiControl.display.flash('success', response.message);
							}
							if (element.attr("redirect") != null) {
								totiControl.display.storedFlash('success', response.message);
								window.location = element.attr("redirect").replace("{id}", response.id);
							}
						}, 
						function(xhr) {
							if (xhr.status === 400) {
								for (const[key, list] of Object.entries(JSON.parse(xhr.responseText))) { /* xhr.responseJSON*/
									var ol = $('<ul>').attr("class", "error-list");
									list.forEach(function(item) {
										ol.append($('<li>').text(item));
									});
									$('#' + form.attr('id') + '-errors-' + key + '').html(ol);
								}
							} else if (element.attr("onFailure") != null) {
								window[element.attr("onFailure")](xhr);
							} else {
								totiControl.display.flash('error', totiLang.formMessages.saveError);
							}
						}, 
						header,
						formConfig
					);
				}
			});
			return submit;
		}
	},
	load: {
		ajax: function(url, method, data, onSuccess, onFailure, headers, ajaxConfig = {}) {
			var ajaxObject = {
				url: url,
				data: data,
				method: method,
				headers: headers,
				success: function(res) {
					onSuccess(res);
				},
				error: function(xhr, mess, errror) {
					onFailure(xhr, mess, errror);
				}
			};
			$.ajax({
				...ajaxObject,
				...ajaxConfig
			});
		},
		parseUrlToObject: function (data) {
			return JSON.parse('{"' + data.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
		},
		link: function(url, method, data, headers) {
			var xhr = new XMLHttpRequest()
			xhr.open(method, url, true);
			for (const[name, value] of Object.entries(headers)) {
				xhr.setRequestHeader(name, value);
			}
			xhr.onload = function() {
				document.documentElement.innerHTML = xhr.response;
				window.history.pushState({},"", xhr.responseURL);
			}
			xhr.send(jQuery.param(data));
			/* location.reload();
			 window.onload();
			 console.log("onload");*/
		}
	},
	display: {
		prompt: function(message, defValue = "") {
			return prompt(message, defValue);
		},
		confirm: function(message, params = {}) {
			return confirm(totiControl.utils.parametrizedString(message, params));
		},
		alert: function(message) {
			alert(message);
		},
		flash: function(severity, message) {
			var div = $('<div>').attr('class', 'flash flash-' + severity).append(
					$('<img>')
						.attr("src", totiImages.cross)
						.attr("alt", "")
						.attr("width", "15")
						.click(function() {
							$(this).parent().hide();
						})
				).append('&nbsp;&nbsp;')
				.append(
					$('<span>').text(message)
				);
			if (totiSettings.flashTimeout > 0) {
				setTimeout(function() {
					div.hide();
				}, totiSettings.flashTimeout);
			}
			$('#flash').append(div);
			console.log("Flash " + severity + ":");
			console.log(message);
		},
		storedFlash: function(severity, message) {
			var name = 'flash';
			var actual = totiControl.storage.getVariable(name);
			if (actual === null) {
				actual = {};
			}
			if (actual[severity] === undefined) {
				actual[severity] = [];
			}
			actual[severity].push(message);
			totiControl.storage.saveVariable(name, actual);
		},
		printStoredFlash: function() {
			var name = 'flash';
			var actual = totiControl.storage.getVariable(name);
			if (actual !== null) {
				for (const[severity, messages] of Object.entries(actual)) {
					messages.forEach(function(message) {
						totiControl.display.flash(severity, message);
					});
				}
				totiControl.storage.removeVariable(name);
			}
		}
	},
	utils: {
		parametrizedString: function(string, params) {
			for(const[name, value] of Object.entries(params)) {
				string = string.replaceAll("\{" + name + "\}", value);
			}
			return string;
		}
	},
	storage: {
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
	},
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
		var token = totiControl.storage.getVariable(totiAuth.variableName);
		if (token === null) {
			return {};
		}
		return {
			"Authorization": token.token_type + " " + (access ? token.access_token : token.refresh_token)
		};
	},
	/* for public use */
	getToken: function() {
		var token = totiControl.storage.getVariable(totiAuth.variableName);
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
		token = token || totiControl.storage.getVariable(totiAuth.variableName);
		if (!token) {
			console.log("No saved token");
			return false;
		}
		totiAuth.isRefreshActive = true;
		totiControl.storage.saveVariable(totiAuth.variableName, token);
		if (period < 0) {
			period = token.expires_in * 2 / 3;
		}
		setTimeout(function() {
			totiControl.load.ajax(
				token.config.refresh.url,
				token.config.refresh.method, 
				{}, 
				function(gettedToken) {
					totiAuth.isRefreshActive = false;
					gettedToken.config = token.config;
					totiAuth.setTokenRefresh(gettedToken, -1);
				}, 
				function(xhr) {
					totiAuth.isRefreshActive = false;
					/*if (period < 5000) {*/
						totiControl.storage.removeVariable(totiAuth.variableName);
					/*} else {
						totiAuth.setTokenRefresh(token, period / 2);
					}	*/				
				}, 
				totiAuth.getAuthHeader(false)
			);
		}, period);
		return true;
	},
	logout: function() {
		if (totiControl.storage.getVariable(totiAuth.variableName) === null) {
			console.log("No token");
			return;
		}
		var token = totiControl.storage.getVariable(totiAuth.variableName);
		totiControl.load.ajax(
			token.config.logout.url,
			token.config.logout.method, 
			{}, 
			function(res) {}, 
			function(xhr, a, error) {
				console.log(xhr, a, error);
			}, 
			totiAuth.getAuthHeader()
		);
		totiControl.storage.removeVariable(totiAuth.variableName);
	},
	login: function(token, config) {
		token.config = config;
		totiAuth.setTokenRefresh(token);
	},
	onLoad: function() {
		totiAuth.setTokenRefresh(null, 0);
	}
};

var totiGrid = {
	config: {},
	init: function(elementIdentifier, uniqueName, config) {
		//	console.log("init", uniqueName, elementIdentifier, config);
		$(document).ready(function() {
			// console.log("init after document ready", uniqueName);
			totiGrid.config[uniqueName] = config;
			var grid = totiGrid.print(
				uniqueName,
				totiGrid.config[uniqueName].columns,
				totiGrid.config[uniqueName].pages.pagesSizes, 
				totiGrid.config[uniqueName].pages.defaultSize, 
				totiGrid.config[uniqueName].pages.pagesButtonCount, 
				totiGrid.config[uniqueName].actions
			);
			$(elementIdentifier).html(grid);
			totiGrid.load(uniqueName, true)	
		});
		
	},
	load: function(uniqueName, initialLoad = false) {
		var urlParams = {};
		var search = decodeURIComponent(window.location.search.substring(1));
		if (initialLoad && search !== '') {
			urlParams = totiControl.load.parseUrlToObject(search);
			totiGrid.filters.onLoad(uniqueName, urlParams);
			totiGrid.sorting.onLoad(uniqueName, urlParams);
			totiGrid.pagesSize.onLoad(uniqueName, urlParams.pageSize);
		} else {
			var pageIndex = totiGrid.pages.get(uniqueName);
			var pageSize = totiGrid.pagesSize.get(uniqueName);
			urlParams = {
				pageIndex: pageIndex === undefined ? 1 : pageIndex,
				pageSize: pageSize === undefined ? totiGrid.config[uniqueName].pages.pageSizeDefault : pageSize,
				filters: totiGrid.filters.get(uniqueName),
				sorting: totiGrid.sorting.get(uniqueName)
			};
		}
		var body = $('#' + uniqueName + "-control table tbody");
		body.html('');
		totiControl.load.ajax(
			totiGrid.config[uniqueName].dataLoadUrl,
			totiGrid.config[uniqueName].dataLoadMethod,
			urlParams,
			function(response) {
				window.history.pushState({"html":window.location.href},"", "?" + jQuery.param(urlParams));
				/* called from grid, must load config */
				totiGrid._loadDataSuccess(
					body,
					uniqueName,
					response, 
					totiGrid.config[uniqueName].columns,
					totiGrid.config[uniqueName].headers,
					totiGrid.config[uniqueName].identifier
				);
			},
			function(xhr, a, b) {
				body.html(totiGrid._loadDataFailure(xhr, a, b));
			},
			totiControl.getHeaders()
		);
	},
	_loadDataSuccess: function(body, uniqueName, response, columns, headers, identifier) {
		if (response.data.length === 0) {
			body.html($('<tr>').html($('<td colspan=100>').text(totiLang.gridMessages.noItemsFound)));
			return;
		}
		totiGrid.pages.onLoad(uniqueName, response.pageIndex, response.itemsCount / totiGrid.pagesSize.get(uniqueName));
		response.data.forEach(function(row, rowIndex) {
			var tableRow = $('<tr>').attr('index', rowIndex).attr('class', 'toti-row-' + rowIndex % 2).attr("class", "toti-row-" + uniqueName);
			tableRow.click(function(e) {
				if (jQuery(e.target).is('input') ||jQuery(e.target).is('button')) {
					return;
				}
				var actualClass = $(this).attr("class");
				$('.toti-row-' + uniqueName).each(function() {
					$(this).attr("class", actualClass.replace(" row-selected", ""));
				});
				if (!actualClass.includes("row-selected")) {
					$(this).attr("class", actualClass + " row-selected");
				}
			});
			columns.forEach(function(column, colIndex) {
				var td = $('<td>').attr('index', colIndex);
				if (column.type === 'actions') {
					td.html(totiControl.inputs.checkbox({
						"class": uniqueName + "-grid-action",
						"data-unique": row[identifier]
					}));
				} else if (column.type === 'buttons') {
					column.buttons.forEach(function(button, index) {
						var settings = {
								href: totiControl.utils.parametrizedString(button.href, row),
								method: button.method,
								async: button.ajax,
								submitConfirmation: function() {
									if (button.hasOwnProperty('confirmation')) {
										return totiControl.display.confirm(button.confirmation, row);
									}
									return true;
								},
								type: button.hasOwnProperty('style') ? button.style : 'basic'
							};
						if (button.hasOwnProperty('onSuccess')) {
							settings.onSuccess = button.onSuccess;
						}
						if (button.hasOwnProperty('onError')) {
							settings.onError = button.onError;
						}
						var buttonElement = totiControl.inputs.button(
							settings,
							button.hasOwnProperty("title") ? button.title : "",
							button.params,
							button.hasOwnProperty("renderer") ? button.renderer : null
						);
						buttonElement.click(function() {
							setTimeout(function(){
								totiGrid.load(uniqueName);
							}, 500);
						});
						td.append(buttonElement);
					});
				} else if (column.hasOwnProperty("renderer")) {
					td.html(window[column.renderer](row[column.name], row));
				} else {
					td.text(row[column.name]);
				}
				tableRow.append(td);
			});
			body.append(tableRow);
		});
		return body;
	},
	_loadDataFailure: function(xhr) {
		console.log(xhr);
		return $('<tr>').html($('<td colspan=100>').text(totiLang.gridMessages.loadingError));
	},
	print: function(uniqueName, columns, pageSizes, defaultSize, pagesButtonCount, actions) {
		/* filters: print: function(uniqueName, columns) columns: name, type (value, button, action), filter(optional)
		 sorting print: function(uniqueName, columns) columns: [ {name, title, useSorting} ]*/
		var head = $('<thead>')
			.append(totiGrid.sorting.print(uniqueName, columns))
			.append(totiGrid.filters.print(uniqueName, columns));
		var body = $('<tbody>');
		var footer = $('<div>').attr('class', "toti-table-footer");
		if (actions.length > 0) {
			footer.append(totiGrid.actions.print(uniqueName, actions));
		}
		footer.append(totiGrid.pages.print(uniqueName, pagesButtonCount, 1))
			.append(totiGrid.pagesSize.print(uniqueName, pageSizes, defaultSize));
		var table = $('<table>').attr('class', 'toti-table').append(head).append(body);
		return $('<div>').attr("id", uniqueName + "-control").append(table).append(footer);
	},
	filters: { 
		/*columns: name, type (value, button, action), filter(optional) - standart input with type
		*/
		print: function(uniqueName, columns) {
			var filters = $('<tr>').attr("id", uniqueName + "-filtering");
			columns.forEach(function(column, index) {
				var cell = $('<td>').attr("data-name", column.name);


				if (column.type === "actions") {
					cell.html(
						totiControl.inputs.checkbox().click(function() {
							$('.' + uniqueName + '-grid-action').prop('checked', $(this).prop('checked'))
						})
					);
					cell.attr('no-filters', '');
				} else if (column.hasOwnProperty('filter')) {
					if (column.filter.type === 'select') {
						var options = [];
						column.filter.options.forEach(function(option) {
							var params = {};
							if (option.hasOwnProperty('params')) {
								params = option.params;
							}
							options.push(totiControl.inputs.option(option.value, option.title, params));
						});
						delete column.filter.options;
						cell.html(
							totiControl.inputs[column.filter.type](options, column.filter)
						);
					} else {
						var filterType = column.filter.type;
						delete column.filter.type;
						cell.html(
							totiControl.inputs[filterType](column.filter)
						);
					}
					cell.change(function() {
						totiGrid.load(uniqueName);
					});
				} else {
					cell.text('');
				}
				filters.append(cell);
			});
			return filters;
		},
		onLoad: function(uniqueName, urlParams) {
			var data = {};
			if (urlParams.filters !== undefined) {
				data = JSON.parse(urlParams.filters);
			}
			$('#' + uniqueName + "-filtering").children('td').each(function() {
				var name = $(this).attr('data-name');
				$(this).children().val(data[name]);
			});
		},
		get: function(uniqueName) {
			var filters = {};
			$('#' + uniqueName + "-filtering").children('td').each(function(index) {
				if ($(this).attr('no-filters') !== undefined) {
					return;
				}
				var value = $(this).children().val();
				if ($(this).data('name') != '' && value !== undefined && value !== '') {
					filters[$(this).data('name')] = value;
				}
			});
			return JSON.stringify(filters);
		}
	},
	sorting: {
		/*
		columns: [
			{name, title, useSorting}
		]
		*/
		print: function(uniqueName, columns) {
			var sortes = $('<tr>').attr("id", uniqueName + "-sorting");
			columns.forEach(function(column, index) {
				sortes.append($('<th>').attr("data-name", column.name).html(
					totiGrid.sorting._print(uniqueName, column.name, column.useSorting, column.title)
				));
			});
			return sortes;
		},
		_print: function(uniqueName, name, useSorting, title = null) {
			var cell = $('<a>');
			if (title !== null) {
				cell.append(title);
			} else {
				cell.append(name);
			}
			if (useSorting) {
				cell.attr("href", "").attr("class", "toti-sortable").attr("data-sort", 0).click(function(e) {
					e.preventDefault();
					var sortType = parseInt($(this).attr('data-sort')) + 1;
					if (sortType === 3) {
						$(this).attr('data-sort', 0);
					} else {
						$(this).attr('data-sort', sortType);
					}
					$(this).children().children(".sortType").hide();
					$(this).children().children(".type" + sortType).show();
					totiGrid.load(uniqueName);
				});
				cell.append(
					$('<div>').attr('class', 'toti-sorting-arrows')
					.append(
						$('<img>')
							.attr("src", totiImages.arrowUp)
							.attr("alt", "")
							.attr("width", "15")
							.attr("class", "sortType type1 type3")
					)
					.append(
						$('<img>')
							.attr("src", totiImages.arrowDown)
							.attr("alt", "")
							.attr("width", "15")
							.attr("class", "sortType type2 type3")
					)
				);
			}
			return cell;
		},
		onLoad: function(uniqueName, urlParams) {
			var data = {};
			if (urlParams.sorting != undefined) {
				data = JSON.parse(urlParams.sorting);
			}
			$('#' + uniqueName + "-sorting").children('td').each(function() {
				var name = $(this).data('name');
				if (data.hasOwnProperty(name)) {
					var val = data[name];
					var sortType = 0;
					if (val == 'ASC') {
						sortType = 1;
					} else if (val == "DESC") {
						sortType = 2;
					}
					var a = $(this).children("a")
					a.attr("data-sort", sortType);
					a.children(".sortType").hide();
					a.children(".type" + sortType).show();
				}
			});
		},
		get: function(uniqueName) {
			var sorts = {};
			$('#' + uniqueName + "-sorting").children('th').each(function() {
				var sort = $(this).children("a").attr("data-sort");
				if (sort === undefined) {
					return
				}
				sort = parseInt(sort);
				if ($(this).attr('data-name') != '' && sort !== 0/* && sort != undefined*/) {
					sorts[$(this).attr("data-name")] = (sort === 1) ? 'ASC' : 'DESC';
				}
			});
			return JSON.stringify(sorts);
		}
	},
	pages: {
		print: function(uniqueName, pagesButtonCount, actualPage) {
			var pagging = $('<div>')
				.attr("id", uniqueName + "-pages");
			pagging.append($('<span>').text(totiLang.pages.title));
			pagging.append('&nbsp;');
			var list = $('<span>')
					.attr("id", uniqueName + "-pages-list")
					.attr("data-pagesbuttoncount", pagesButtonCount);
			list.attr("data-actualpage", actualPage);
			pagging.append(list);
			return pagging;
		},
		onLoad: function(uniqueName, actualPage, pagesCount) {
			var pagesList = $('#' + uniqueName + "-pages-list");
			pagesList.attr("data-actualpage", actualPage);
			pagesList.html('');

			var onPageClick = function(newPage) {
				return function() {
					pagesList.attr("data-actualpage", newPage);
					totiGrid.load(uniqueName);
					return false;
				};
			};

			/* link to first page */
			if (actualPage > 1) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(1),
					totiLang.pages.first,
					{'class': 'toti-button-pages'}
				));
				pagesList.append('&nbsp;');
			}
			/* link to previous page */
			if (actualPage > 2) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(actualPage - 1),
					totiLang.pages.previous,
					{'class': 'toti-button-pages'}
				));
				pagesList.append('&nbsp;');
			}
			/* generated {pagesbuttoncount} pages links */
			var lower = actualPage - Math.floor(pagesList.data("pagesbuttoncount") / 2);
			if (lower < 1) {
				lower = 1;
			}
			for (i = lower; i < Math.min(lower + pagesList.data("pagesbuttoncount"), pagesCount); i++) {
				var page = totiControl.inputs.button(
					onPageClick(i),
					i,
					{'class': 'toti-button-pages'}
				);
				if (i === actualPage) {
					page.attr("class", "toti-button-pages actualPage");
				}
				pagesList.append(page);
				pagesList.append('&nbsp;');
			}
			/* next page link */
			if (actualPage < pagesCount) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(actualPage + 1),
					totiLang.pages.next,
					{'class': 'toti-button-pages'}
				));
				pagesList.append('&nbsp;');
			}
			/* last page link */
			if ((actualPage + 1) < pagesCount) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(pagesCount),
					totiLang.pages.last,
					{'class': 'toti-button-pages'}
				));
				pagesList.append('&nbsp;');
			}
		},
		get: function(uniqueName) {
			return $('#' + uniqueName + "-pages-list").attr("data-actualpage");
		}
	},
	pagesSize: {
		print: function(uniqueName, pageSizes, defaultSize) {
			var options = [];
			pageSizes.forEach(function(size, index) {
				options[index] = totiControl.inputs.option(size, size);
			});
			var select = totiControl.inputs.select(options, { "id": uniqueName + "-pageSize" });
			select.val(defaultSize);
			select.change(function() {
				totiGrid.load(uniqueName);
			});
			return select;
		},
		onLoad: function(uniqueName, pageSize) {
			$("#" + uniqueName + "-pageSize").val(pageSize);
		},
		get: function(uniqueName) {
			return $("#" + uniqueName + "-pageSize").val();
		}
	},
	actions: {
		/*
		actions: [
			{link, title, ajax, method}
		]
		*/
		print: function(uniqueName, actions) {
			var options = [];
			options.push(totiControl.inputs.option('', totiLang.actions.select, {
					"ajax": true,
					"method": null
				}));
			actions.forEach(function(action) {
				var params = {
					"ajax": action.ajax,
					"method": action.method
				};
				if (action.hasOwnProperty('onSuccess')) {
					params.onSuccess = action.onSuccess;
				}
				if (action.hasOwnProperty('onFailure')) {
					params.onFailure = action.onFailure;
				}
				if (action.hasOwnProperty('submitConfirmation')) {
					params.submitConfirmation = action.submitConfirmation;
				}
				options.push(totiControl.inputs.option(action.link, action.title, params));
			});
			var select = totiControl.inputs.select(options);
			var execute = totiControl.inputs.button(
				function(event) {
					event.preventDefault();
					var option = select.children('option:selected');
					if (option.val() === '') {
						return false;
					}
					var url = option.val();
					var method = option.attr("method");
					var ajax = option.attr("ajax");
					var submitConfirmation = option.attr("submitConfirmation");

					var ids = {};
					$('.' + uniqueName + "-grid-action:checked").each(function() {
						ids[$(this).data("unique")] = $(this).data("unique");
					});
					if (Object.keys(ids).length === 0) {
						totiControl.display.flash("error", totiLang.actions.noSelectedItems);
						return false;
					}
					if (ajax === 'true') {
						if (submitConfirmation !== null
							&& submitConfirmation !== undefined
							&& !totiControl.display.confirm(submitConfirmation)) {
							event.preventDefault();
							return false;
						}
						totiControl.load.ajax(
							url,
							method,
							{ids: ids},
							function(result) {
								if (option.attr("onSuccess") != null) {
									window[option.attr("onSuccess")](result);
								} else {
									totiControl.display.flash('success', result);
								}
							},
							function(xhr) {
								if (option.attr("onFailure") != null) {
									window[option.attr("onFailure")](xhr);
								} else {
									totiControl.display.flash('error', xhr);
								}
							},
							totiControl.getHeaders()
						);
					} else {
						/* TODO Improvement use link? now no params sended */
						window.location = url + "?ids=" + JSON.stringify(ids);
					}
				},
				totiLang.actions.execute,
				{'class': 'toti-button-execute'}
			);
			var actions = $('<div>').attr('class', "toti-actions");
			actions.append(select).append(execute);
			return actions;
		},
		onLoad: function(uniqueName) {
			/* empty */
		},
		get: function(uniqueName) {
			/* empty */
		}
	}
};

totiForm = {
	init: function(elementIdentifier, uniqueName, config) {
		$(document).ready(function() {
			var html = $(elementIdentifier).html();
			$(elementIdentifier).html(totiForm.print(uniqueName, config, $(elementIdentifier)));
			if (config.hasOwnProperty('bind')) {
				totiForm.bind(config.bind, uniqueName, function() {
					if (config.hasOwnProperty("beforeBind")) {
						window[config.beforeBind]();
					}
				}, function() {
					if (config.hasOwnProperty("afterBind")) {
						window[config.afterBind]();
					}
				});
			}
		});
	},
	print: function(uniqueName, config, element) {
		var formId = uniqueName;
		var form;
		if (config.editable) {
			var errors = $('<div>').attr("id", config.uniqueName + "-errors-form").append($('<span>'));
			form = $('<form>')
				.attr("id", formId)
				.attr("action", config.action)
				.attr("method", config.method)
				.append(errors);
		} else {
			form = $('<div>')
				.attr("id", formId);
		}
		var table = $('<table>');

		var printSelectFunc = function (field, optionsName) {
			var input = $('<div>');
			var options = field[optionsName];
			delete field[optionsName];
			for ([key, name] of Object.entries(field)) {
				input.attr(key, name);
			}
			options.forEach(function(option) {
				input.append($('<span>').attr("value", option.value).text(option.title).hide());
			});
			return input;
		}
		config.fields.forEach(function(field, index) {
			field.id = uniqueName + "-" + field.id;
			field.form = formId;
			var label = null;
			if (field.hasOwnProperty('title') && field.type !== 'button') {
				label = totiControl.inputs.label(field.id, field.title, {
					id: uniqueName +  "-" + field.id + "-label"
				});
			}
			var input;
			if (!config.editable && field.type !== 'button') {
				if (field.type === 'select') {
					input = printSelectFunc(field, 'options');
				} else if (field.type === 'radio') {
					input = printSelectFunc(field, 'radios');
				} else if (field.type === 'checkbox') {
					input = printSelectFunc(field, 'values');
				} else if (field.type !== 'submit' && field.type !== 'hidden') {
					input = $('<div>');
					for ([key, name] of Object.entries(field)) {
						input.attr(key, name);
					}
				}
			} else if (field.type === 'submit') {
				input = totiControl.inputs.submit(
					field.ajax, function(data) {
						if (field.hasOwnProperty("confirmation")) {
							return totiControl.display.confirm(field.confirmation, data);
						}
						return true;
					}, field
				);
			} else if (field.type === 'select') {
				var options = [];
				field.options.forEach(function(option) {
					var params = {};
					if (option.hasOwnProperty('params')) {
						params = option.params;
					}
					options.push(totiControl.inputs.option(option.value, option.title, params));
				});
				delete field.options;
				input = totiControl.inputs[field.type](options, field);
			} else if (field.type === 'button') {
				var onClick = {
						href: field.href,
						method: field.method,
						async: field.ajax,
						submitConfirmation: function() {
							if (field.hasOwnProperty('confirmation')) {
								return totiControl.display.confirm(field.confirmation, row);
							}
							return true;
						} 
					};
				if (field.hasOwnProperty('style')) {
					onClick.type = field.style;
				}
				input = totiControl.inputs.button(onClick, field.title, field.params, field.hasOwnProperty('renderer') ? field.renderer : null, field.preventDefault);
			} else if (field.type === 'radio') {
				input = $("<div>");
				field.radios.forEach(function(radio) {
					var item = $('<div>');
					var id = formId + "-" + radio.id;
					item.attr('id', id + "-block");
					if (radio.hasOwnProperty('title')) {
						item.append(totiControl.inputs.label(field.id, radio.title, {
							id: id + "-label"
						}));
					}
					var settings = {
							id: id,
							name: field.name,
							form: formId,
							value: radio.value
						};
					if (radio.value === field.value) {
						settings.checked = "checked";
					}
					if (field.hasOwnProperty('required')) {
						settings.required = field.required;
					}
					if (field.hasOwnProperty('disabled')) {
						settings.disabled = field.disabled;
					}
					item.append(totiControl.inputs.radio(settings));
					input.append(item);
				});
			} else {
				if (field.type === 'file') {
					form.attr("enctype", "multipart/form-data");
				}
				var fieldType = field.type;
				delete field.type;
				input = totiControl.inputs[fieldType](field);
			}

			var error = $('<div>').attr('id', uniqueName + '-errors-' + field.name);
			if (element.html().length > 0) {
				var labelElement = element.find("#form-label-" + field.name);
				if (labelElement.length > 0) {
					labelElement.html(label);
				}
				var inputElement = element.find("#form-input-" + field.name);
				if (inputElement.length > 0) {
					inputElement.html(input);
				}
				var errorElement = element.find("#form-error-" + field.name);
				if (config.editable && errorElement.length > 0) {
					errorElement.html(error);
				}
			} else {
				table.append($('<tr>')
					.append($('<td>').attr('class', 'toti-form-label').append(label))
					.append($('<td>').attr('class', 'toti-form-input').append(input))
					.append($('<td>').attr('class', 'toti-form-error').append(config.editable ? error : ""))
				);
			}
		});
		if (element.html().length > 0) {
			form.append(element.html());
		} else {
			form.append(table);
		}
		
		return form;
	},
	bind: function(bind, formId, beforeBind, afterBind) {
		totiControl.load.ajax(
			bind.url, 
			bind.method, 
			bind.params, 
			function(values) {
				beforeBind();
				for (const[key, value] of Object.entries(values)) {
					var val = value; /* TODO IMPROVEMENT escape */
					var id = '#' + formId + ' [name=' + key + ']';
					var element = $(id);
					if (element.attr("type") === 'datetime-local' && val !== null) {
						val = val.replace(" ", "T");
					}
					if (element.children('span').length > 0) { /* detail:select, checkbox, radio */
						element.children('span[value="' + val + '"]').show();
					} else if (element.length > 1) {
						$('#' + formId + ' #' + formId + '-id-' + val + '[name=' + key + ']').prop('checked', true); /* form: radio list */
					} else {
						element.val(val); /* form */
						if (element.text().length == 0) {
							element.text(val); /* detail */
						}
						element.prop('checked', val); /* form: checkbox */
					}
				}
				afterBind();
			}, 
			function(xhr) {
				if (bind.hasOwnProperty('onFailure')) {
					bind.onFailure(xhr);
				} else {
					totiControl.display.flash('error', totiLang.formMessages.bindError);
				}
			},
			totiControl.getHeaders()
		);
		
	}
};

$(document).ready(function() {
	totiControl.display.printStoredFlash();
});
