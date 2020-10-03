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
			// TODO renderer
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
						asyncFunction({}, null, null);
					}
					return false;
				}
				if (element.attr("href")) {
					element.attr("href", element.attr("href") + "?" + data);
				}
				return true;
			});
		}
		/*,
		// deprecated
		form: function(action, method, inputs, params = {}) {
			var form = $('<form>')
				.attr("action", action)
				.attr("method", method);
			for ([key, name] of Object.entries(params)) {
				form.attr(key, name);
			}
			inputs.forEach(function (input, index) {
				if (form.hasOwnProperty("id")) {
					input.attr("form", form.id);
				}
				form.append(input);
			});
			return form;
		}
		*/
	},
	load: {
		ajax: function(url, method, data, onSuccess, onFailure, headers = {}) {
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
		}
	}
};

var totiGrid = {
	init: function() {
		// TODO
	},
	load: function(uniqueName) {

	},
	filters: { 
		/*columns: name, type (value, button, action), filter(optional):
			radio: function (params = {})
			checkbox: function (params = {})
			number: function (params = {}) - sugested params: step, max, min
			text: function (params = {}) - sugested params: size, minlength, maxlength
			password: function (params = {}) - sugested params: size, minlength, maxlength
			email: function (params = {})
			datetime: function (params = {})
			select: function (options, params = {}) 
			option: function(value, title, params = {})
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
					cell.html(
						totiControl.inputs[column.filter.type](column.filter)
					);
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
		print: function(uniqueName, pagesButtonCount, actualPage = null) {
			var pagging = $('<div>')
				.attr("id", uniqueName + "-pages");
			pagging.append($('<span>').text(totiLang.pages.title)); // TODO lang
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
					pagesList.data("actualpage", newPage);
					totiGrid.load(uniqueName);
					return false;
				};
			};
			// link to first page
			if (actualPage > 1) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(1),
					null, // confirm
					totiLang.pages.first, // TODO lang
					"" // href
				));
				pagesList.append('&nbsp;');
			}
			// link to previous page
			if (actualPage > 2) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(actualPage - 1),
					null, // confirm
					totiLang.pages.previous, // TODO lang
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
					totiLang.pages.next, // TODO lang
					"" // href
				));
				pagesList.append('&nbsp;');
			}
			// last page link
			if ((actualPage + 1) < pagesCount) {
				pagesList.append(totiControl.inputs.button(
					onPageClick(pagesCount),
					null, // confirm
					totiLang.pages.last, // TODO lang
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
		onLoad: function(uniqueName) {
			// empty
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
				$(this).next("a").attr("data-method", $(this).data('method'));
			});
			var execute = totiControl.inputs.button(
				function() {
					var ids = {};
					$('.' + uniqueName + "-grid-action:checked").each(function() {
						ids[$(this).data("unique")] = $(this).data("unique");
					});
					if (Object.keys(ids).length === 0) {
						onError(totiLang.actions.noSelectedItems);
						return false;
					}
					
					if ($(this).parent().children('select').children('option:selected').data("ajax")) {
						totiControl.load(
							$(this).attr("href"),
							$(this).attr("method"),
							{ids: ids},
							onSuccess,
							onError,
							headers
						);
					} else {
						// FIX
						window.location = $(this).attr("href");
					}
				},
				"", // confirm
				totiLang.actions.execute,
				"", // href
				{}
			);
			var actions = $('<div>');
		},
		onLoad: function(uniqueName) {
			// empty
		},
		get: function(uniqueName) {
			// empty
		}
	}
};