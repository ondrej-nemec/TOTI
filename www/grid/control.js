var totiLang = {
	"pages": {
		"title": "Pages:",
		"first": "First",
		"previous": "Previous",
		"next": "Next",
		"last": "Last"
	},
	"actions": {
		"select": "Select action",
		"execute": "Execute",
		"noSelectedItems": "No selected items"
	},
	"gridMessages": {
		"noItemsFound": "No Item Found",
		"loadingError": "Problem with data loading"
	}
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
		label: function (forInput, title) {
			return $('<label>').attr('for', forInput).text(title);
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
		button: function (asyncFunction, submitConfirmation, title, href, params = {}, renderer = null) {
			if (renderer === null) {
				renderer = $('<button>').text(title);
			}
			var button = $('<a>').attr("href", href).html(renderer);
			for ([key, name] of Object.entries(params)) {
				button.attr(key, name);
			}
			totiControl.inputs._onClick(button, asyncFunction, submitConfirmation);
			return button;
		},
		submit: function (asyncFunction, submitConfirmation, params = {}) {
			var submit = totiControl.inputs._createInput("submit", params);
			totiControl.inputs._onClick(submit, asyncFunction, submitConfirmation);
			return submit;
		},
		_onClick: function(element, asyncFunction, submitConfirmation) {
			element.click(function(event) {
				var element = $(this);
				if (submitConfirmation !== null && !submitConfirmation()) {
					event.preventDefault();
					return false;
				}
				if (asyncFunction) {
					event.preventDefault();
					if (element.attr("form")) {
						var form = $('form#' + element.attr("form"));
						asyncFunction(
							form.serialize(),
							form.attr("action"), 
							form.attr("method")
						);
					} else {
						asyncFunction({}, element.attr("href"), element.data("method"));
					}
					return false;
				}
				if (element.attr("href")) {
					element.attr("href", element.attr("href") + "?" + data);
				}
				return true;
			});
		}
	},
	load: {
		ajax: function(url, method, data, onSuccess, onFailure, headers) {
			$.ajax({
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
			
			// window.history.pushState({"html":window.location.href},"", "?" + jQuery.param(urlParams));
			xhr.onload = function() {
				document.documentElement.innerHTML = xhr.response;
				window.history.pushState({},"", xhr.responseURL);
			}
			xhr.send(jQuery.param(data));
			
		}
	},
	display: {
		prompt: function(message, defValue = "") {
			return prompt(message);
		},
		confirm: function(message, params = {}) {
			console.log("Params here!!", params);
			return confirm(message);
		}
	}
};

var totiAuth = {
	storage: {
		variableName: "authentication",
		saveVariable: function(value) {
			localStorage[totiAuth.storage.variableName] = JSON.stringify(value);
		},
		getVariable: function() {
			var name = totiAuth.storage.variableName;
			if (!localStorage[name] || localStorage[name] === null || (localStorage[name] == 'null') ) {
				return null;
			}
			return JSON.parse(localStorage[name]);
		},
		removeVariable: function() {
			localStorage.removeItem(totiAuth.storage.variableName);
		}
	},
	getAuthHeader: function(access = true) {
		if (totiAuth.storage.getVariable() === null) {
			return {};
		}
		var token = totiAuth.storage.getVariable();
		return {
			"Authorization": token.token_type + " " + (access ? token.access_token : token.refresh_token)
		};
	},
	// for public use
	getToken: function() {
		var token = totiAuth.storage.getVariable();
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
		token = token || totiAuth.storage.getVariable();
		if (!token) {
			console.log("No saved token");
			return false;
		}
		totiAuth.isRefreshActive = true;
		totiAuth.storage.saveVariable(token);
		if (period < 0) {
			period = token.expires_in * 3 / 2;
		}
		setTimeout(function() {
			totiControl.load.ajax(
				token.config.refreshUrl,
				token.config.refreshMethod, 
				{}, 
				function(gettedToken) {
					totiAuth.isRefreshActive = false;
					gettedToken.config = token.config;
					totiAuth.setTokenRefresh(gettedToken);
				}, 
				function(xhr, a, error) {
					totiAuth.isRefreshActive = false;
					console.log(xhr, a, error);
					//if (totiAuth.refreshCount > 5) {
						totiAuth.storage.removeVariable();
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
		if (totiAuth.storage.getVariable() === null) {
			console.log("No token");
			return;
		}
		var token = totiAuth.storage.getVariable();
		totiControl.load.ajax(
			token.config.logoutUrl,
			token.config.logoutMethod, 
			{}, 
			function(res) {}, 
			function(xhr, a, error) {
				console.log(xhr, a, error);
			}, 
			totiAuth.getAuthHeader()
		);
		totiAuth.storage.removeVariable();
	},
	login: function(authData, config, redirect = null) {
		totiControl.load.ajax(
			config.loginUrl,
			config.loginMethod, 
			authData, 
			function(token) {
				token.config = config;
				totiAuth.setTokenRefresh(token);
				if (redirect) {
					window.location = redirect;
				}
			}, 
			function(xhr, a, error) {
				console.log(xhr, a, error);
			}, 
			{}
		);		
	},
	onLoad: function() {
		totiAuth.setTokenRefresh(null, 0);
	}
};

var totiGrid = {
	config: {},
	init: function(elementIdentifier, uniqueName, config) {
		totiGrid.config[uniqueName] = config;
		var grid = totiGrid.print(
			uniqueName,
			totiGrid.config[uniqueName].columns,
			totiGrid.config[uniqueName].pages.pagesSizes, 
			totiGrid.config[uniqueName].pages.defaultSize, 
			totiGrid.config[uniqueName].pages.pagesButtonCount, 
			totiGrid.config[uniqueName].actions.actionsList, 
			totiGrid.config[uniqueName].actions.onError, 
			totiGrid.config[uniqueName].actions.onSuccess, 
			totiGrid.config[uniqueName].headers
		);
		$(elementIdentifier).html(grid);
		totiGrid.load(uniqueName, true)
	},
	load: function(uniqueName, initialLoad = false) {
		var urlParams = {};
		var search = decodeURIComponent(window.location.search.substring(1));
		if (initialLoad && search !== '') {
			urlParams = totiControl.load.parseUrlToObject(search);
			// JSON.parse('{"' + search.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
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
				// called from grid, must load config
				totiGrid._loadDataSuccess(
					body,
					uniqueName,
					response, 
					totiGrid.config[uniqueName].columns,
					totiGrid.config[uniqueName].headers,
					totiGrid.config[uniqueName].identifier				);
			},
			function(xhr, a, b) {
				body.html(totiGrid._loadDataFailure(xhr, a, b));
			},
			totiAuth.getAuthHeader()
			// totiGrid.config[uniqueName].headers
		);
	},
	_loadDataSuccess: function(body, uniqueName, response, columns, headers, identifier) {
		if (response.data.length === 0) {
			body.html($('<div>').text(totiLang.gridMessages.noItemsFound));
			return;
		}
		totiGrid.pages.onLoad(uniqueName, response.pageIndex, response.itemsCount);
		response.data.forEach(function(row, index) {
			var tableRow = $('<tr>');
			columns.forEach(function(column, index) {
				var td = $('<td>');
				if (column.type === 'actions') {
					td.html(totiControl.inputs.checkbox({
						"class": uniqueName + "-grid-action",
						"data-unique": row[identifier]
					}));
				} else if (column.type === 'buttons') {
					column.buttons.forEach(function(button, index) {
						var func = button.ajax ? function(data, url, method) {
							totiControl.load.ajax(
								url,
								method, 
								data,
								button.onSuccess(row), // TODO Flash
								button.onFailure(row), // TODO flash
								headers
							 );
						} : null;
						td.append(
							totiControl.inputs.button(
								func,
								function() {
									return totiControl.display.confirm(button.confirmation, row);
								},
								button.title,
								button.href(row),
								button.params,
								button.hasOwnProperty("renderer") ? button.renderer : null
							)
						);
					});
				} else if (column.hasOwnProperty("renderer")) {
					td.html(column["renderer"](row[column.name]));
				} else {
					td.text(row[column.name]);
				}
				tableRow.append(td);
			});
			body.append(tableRow);
		});
		return body;
	},
	_loadDataFailure: function(xhr, a, b) {
		console.log(xhr);
		return $('<div>').text(totiLang.gridMessages.loadingError);
	},
	print: function(uniqueName, columns, pageSizes, defaultSize, pagesButtonCount, actions, onError, onSuccess, headers = {}) {
		// filters: print: function(uniqueName, columns) columns: name, type (value, button, action), filter(optional)
		// sorting print: function(uniqueName, columns) columns: [ {name, title, useSorting} ]
		var head = $('<thead>')
			.append(totiGrid.sorting.print(uniqueName, columns))
			.append(totiGrid.filters.print(uniqueName, columns));
		var body = $('<tbody>');
		var footer = $('<div>')
			.append(totiGrid.actions.print(uniqueName, actions, onError, onSuccess, headers))
			.append(totiGrid.pages.print(uniqueName, pagesButtonCount, 1))
			.append(totiGrid.pagesSize.print(uniqueName, pageSizes, defaultSize));
		var table = $('<table>').append(head).append(body);
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
							$('.' + uniqueName + '-grid-action').prop('checked', $(this).prop('checked'));
						})
					);
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
		onLoad: function(uniqueName, urlParams) { // TODO maybe not all params but filtering only
			$('#' + uniqueName + "-filtering").children('td').each(function() {
				var name = $(this).data('name');
				if (urlParams["filters[" + name + "]"] !== undefined) {
					$(this).children().val(urlParams["filters[" + name + "]"]);
				}
			});
		},
		get: function(uniqueName) {
			var filters = {};
			$('#' + uniqueName + "-filtering").children('td').each(function(index) {
				var value = $(this).children().val();
				if ($(this).data('name') != '' && value !== undefined && value !== '') {
					filters[$(this).data('name')] = value;
				}
			});
			return filters;
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
				sortes.append($('<td>').attr("data-name", column.name).html(
					totiGrid.sorting._print(uniqueName, column.name, column.useSorting, column.title)
				));
			});
			return sortes;
		},
		_print: function(uniqueName, name, useSorting, title = null) {
			var cell = $('<a>');
			if (useSorting) {
				cell.attr("href", "").attr("data-sort", 0).click(function(e) {
					e.preventDefault();
					var sortType = $(this).data('sort') + 1;
					if (sortType === 3) {
						$(this).data('sort', 0);
					} else {
						$(this).data('sort', sortType);
					}
					$(this).children(".sortType").hide();
					$(this).children(".type" + sortType).show();
					totiGrid.load(uniqueName);
				});
				cell.append($('<i>').attr("class", "far fa-clock sortType type1").hide()); // TODO another icon
				cell.append($('<i>').attr("class", "fas fa-clock sortType type2").hide()); // TODO another icon
			}
			if (title !== null) {
				cell.append(title);
			} else {
				cell.append(name);
			}
			return $('<td>').attr("data-name", name).html(cell);
		},
		onLoad: function(uniqueName, urlParams) { // TODO maybe not all params but sorting only
			$('#' + uniqueName + "-sorting").children('td').each(function() {
				var name = $(this).data('name');
				if (urlParams["sorting[" + name + "]"] !== undefined) {
					var val = urlParams["sorting[" + name + "]"];
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
			$('#' + uniqueName + "-sorting").children('td').each(function() {
				var sort = $(this).children("a").data("sort");
				if ($(this).data('name') != '' && sort !== 0) {
					sorts[$(this).data("name")] = (sort === 1) ? 'ASC' : 'DESC';
				}
			});
			return sorts;
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
			// link to first page
			if (actualPage > 1) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(1),
					null, // confirm
					totiLang.pages.first,
					"" // href
				));
				pagesList.append('&nbsp;');
			}
			// link to previous page
			if (actualPage > 2) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(actualPage - 1),
					null, // confirm
					totiLang.pages.previous,
					"" // href
				));
				pagesList.append('&nbsp;');
			}
			// generated {pagesbuttoncount} pages links
			var lower = actualPage - Math.floor(pagesList.data("pagesbuttoncount") / 2);
			if (lower < 1) {
				lower = 1;
			}
			for (i = lower; i < Math.min(lower + pagesList.data("pagesbuttoncount"), pagesCount); i++) {
				var page = totiControl.inputs.button(
					onPageClick(i),
					null, // confirm
					i,
					"" // href
				);
				if (i === actualPage) {
					page.attr("class", "actualPage");
				}
				pagesList.append(page);
				pagesList.append('&nbsp;');
			}
			// next page link
			if (actualPage < pagesCount) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(actualPage + 1),
					null, // confirm
					totiLang.pages.next,
					"" // href
				));
				pagesList.append('&nbsp;');
			}
			// last page link
			if ((actualPage + 1) < pagesCount) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(pagesCount),
					null, // confirm
					totiLang.pages.last,
					"" // href
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
		print: function(uniqueName, actions, onError, onSuccess, headers = {}) {
			var options = [];
			options.push(totiControl.inputs.option('', totiLang.actions.select, {
					"data-ajax": true,
					"data-method": null
				}));
			actions.forEach(function(action) {
				options.push(totiControl.inputs.option(action.link, action.title, {
					"data-ajax": action.ajax,
					"data-method": action.method
				}));
			});
			var select = totiControl.inputs.select(options);
			select.change(function() {
				$(this).next("a").attr("href", $(this).val());
				$(this).next("a").attr("data-method", $(this).children("option:selected").data('method'));
				$(this).next("a").attr("data-ajax", $(this).children("option:selected").data('ajax'));
			});
			var execute = totiControl.inputs.button(
				function(data, url, method) {
					var ids = {};
					$('.' + uniqueName + "-grid-action:checked").each(function() {
						ids[$(this).data("unique")] = $(this).data("unique");
					});
					if (Object.keys(ids).length === 0) {
						onError(totiLang.actions.noSelectedItems);
						return false;
					}
					if (true) { // TODO solve
						totiControl.load.ajax(
							url,
							method,
							{ids: ids},
							onSuccess, // TODO flash
							onError, // TODO flash
							headers
						);
					} else {
						// FIX
						window.location = $(this).attr("href");
					}
				},
				null, /*confirm*/ totiLang.actions.execute, "" /*href*/
			);
			var actions = $('<div>');
			actions.append(select).append(execute);
			return actions;
		},
		onLoad: function(uniqueName) {
			// empty
		},
		get: function(uniqueName) {
			// empty
		}
	}
};

totiForm = {
	init: function(elementIdentifier, uniqueName, config) {
		$(elementIdentifier).html(totiForm.print(uniqueName, config));
		if (config.hasOwnProperty('bind')) {
			totiForm.bind(config.bind, config.formId);
		}
	},
	print: function(uniqueName, config) {
		var formId = config.formId;
		var errors = $('<div>').attr("id", config.formId + "-errors-form").append($('<span>'));
		var form = $('<form>')
			.attr("id", formId)
			.attr("action", config.action)
			.attr("method", config.method)
			.append(errors);
		config.fields.forEach(function(field, index) {
			field.form = formId;
			var label = null;
			if (field.hasOwnProperty('title')) {
				label = totiControl.inputs.label(field.id, field.title);
			}
			var input;
			if (field.type === 'submit') {
				input = totiControl.inputs.submit(
					function(data, url, method) {
						totiControl.load.ajax(
							url, 
							method, 
							totiControl.load.parseUrlToObject(data), 
							function(data) {
								if (field.hasOwnProperty('redirect')) {
									window.location = field.redirect(data);
								}
								// TODO flash
							}, 
							function(xhr) {
								if (xhr.status === 400) {
									for (const[key, list] of Object.entries(xhr.responseJSON)) {
										var ol = $('<ul>').attr("class", "error-list");
										list.forEach(function(item) {
											ol.append($('<li>').text(item));
										});
										$('#' + config.formId + '-errors-' + key + '').html(ol);
									}
								}
								//TODO flash
							}, 
							totiAuth.getAuthHeader()
						);
					},
					function() {
						return totiControl.display.confirm(field.confirmation, data);
					},
					field
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
			} else {
				var fieldType = field.type;
				delete field.type;
				input = totiControl.inputs[fieldType](field);
			}
			var inputTuple = $('<div>').attr('id', config.formId + '-errors-' + field.name).append(input).append($('<span>'));
			form.append(
				$('<div>')
					.append($('<div>').append(label).append(input))
					.append($('<div>').attr('id', config.formId + '-errors-' + field.name))
			);
		});
		return form;
	},
	bind: function(bind, formId) {
		totiControl.load.ajax(
			bind.url, 
			bind.method, 
			bind.params, 
			function(values) {
				for (const[key, value] of Object.entries(values)) {
					$('#' + formId + ' [name=' + key + ']').val(value);
				}
			}, 
			function(xhr, a, b) {
				console.log(xhr, a, b);
				bind.onFailure(xhr, a, b);
			}, 
			totiAuth.getAuthHeader
		);
		
	}
};