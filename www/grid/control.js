var totiLang = {
	"pages": {
		"title": "Pages:",
		"first": "First",
		"previous": "Previous",
		"next": "Next",
		"last": "Last"
	},
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
		button: function (asyncFunction, submitConfirmation, title, href, params = {}) {
			// TODO renderer
			var button = $('<a>').attr("href", href).html(
					$('<button>').text(title)
			);
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
		print: function() {
			// TODO
		},
		onLoad: function(uniqueName) {
			// TODO
		},
		get: function() {
			// TODO
		}
	},
	sorting: {
		print: function() {
			// TODO
		},
		onLoad: function(uniqueName) {
			// TODO
		},
		get: function() {
			// TODO
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
	}
};