var gridLang = gridLang || {
	"pages": {
		"title": "Pages:",
		"first": "First",
		"previous": "Previous",
		"next": "Next",
		"last": "Last"
	},
	"loading": {
		"noItemsFound": "No items founded",
		"loadingError": "Loading error"
	},
	"execute": "Execute..."
};

var simpleGrid = {};
simpleGrid.data = {};
simpleGrid.data.onPageClick = function(config, newPage, pages) {
	return function() {
		pages.data("actualPage", newPage);
		simpleGrid.data.load(config);
		return false;
	};
};
simpleGrid.data.paging = function(config, actualPage, pageCount) {
	var pages = $("#" + config.Name + "-actualPage");
	pages.html('');
	pages.data("actualPage", actualPage);

	pages.append($('<scan>').text(gridLang.pages.title));
	pages.append('&nbsp;');
	if (actualPage > 1) {
		pages.append($('<a>').text(gridLang.pages.first).click(simpleGrid.data.onPageClick(config, 1, pages)));
		pages.append('&nbsp;');
	}
	if (actualPage > 2) {
		pages.append($('<a>').text(gridLang.pages.previous).click(simpleGrid.data.onPageClick(config, actualPage - 1, pages)));
		pages.append('&nbsp;');
	}

	var lower = actualPage - Math.floor(config.pagesButton / 2);
	if (lower < 1) {
		lower = 1;
	}
	for (i = lower; i < Math.min(lower + config.pagesButton, pageCount); i++) {
		var page = $('<a>').text(i);
		if (i === actualPage) {
			page.attr("class", "actualPage");
		}
		page.click(simpleGrid.data.onPageClick(config, i, pages));
		pages.append(page);
		pages.append('&nbsp;');
	}

	if (actualPage < pageCount) {
		pages.append($('<a>').text(gridLang.pages.next).click(simpleGrid.data.onPageClick(config, actualPage+1, pages)));
		pages.append('&nbsp;');
	}
	if ((actualPage + 1) < pageCount) {
		pages.append($('<a>').text(gridLang.pages.last).click(simpleGrid.data.onPageClick(config, pageCount, pages)));
		pages.append('&nbsp;');
	}
	pages.append('&nbsp;');
	var pageSizeSelect = $('<select>').attr("id", config.Name + "-pageSize");
	config.pageSizes.forEach(function(size, index) {
		pageSizeSelect.append($('<option>').attr("value", size).text(size));
	});
	pageSizeSelect.change(function() {
		simpleGrid.data.load(config);
	});
	pageSizeSelect.val(config.pageSizeDefault);
	pages.append(pageSizeSelect);
};
simpleGrid.data.firstLoad = function(config, search) {
	var urlParams = {};
	urlParams = JSON.parse('{"' + search.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
	// filters
	$('#' + config.Name + "-filtering").children('td').each(function() {
		var name = $(this).data('name');
		if (urlParams["filters[" + name + "]"] !== undefined) {
			$(this).children().val(urlParams["filters[" + name + "]"]);
		}
	});
	//sorting
	$('#' + config.Name + "-sorting").children('td').each(function() {
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
	// pagging
	$("#" + config.Name + "-pageSize").val(urlParams.pageSize);
	return urlParams;
}

simpleGrid.data.otherLoads = function(config, search) {
	var urlParams = {};
	var filters = {};
	$('#' + config.Name + "-filtering").children('td').each(function(index) {
		var value = $(this).children().val();
		if ($(this).data('name') != '' && value !== undefined && value !== '') {
			filters[$(this).data('name')] = value;
		}
	});
	var sorts = {};
	$('#' + config.Name + "-sorting").children('td').each(function() {
		var sort = $(this).children("a").data("sort");
		if ($(this).data('name') != '' && sort !== 0) {
			sorts[$(this).data("name")] = (sort === 1) ? 'ASC' : 'DESC';
		}
	});	
	var pageSize = $("#" + config.Name + "-pageSize").val();
	var pageIndex = $("#" + config.Name + "-actualPage").data('actualPage');

	var urlParams = {
			pageIndex: pageIndex === undefined ? 1 : pageIndex,
			pageSize: pageSize === undefined ? config.pageSizeDefault : pageSize,
			filters: filters,
			sorting: sorts
	};
	return urlParams;
}

simpleGrid.data.load = function(config, firstLoad) {
	var urlParams = {};
	var search = decodeURIComponent(window.location.search.substring(1));
	if (firstLoad && search !== '') {
		urlParams = simpleGrid.data.firstLoad(config, search);
	} else {
		urlParams = simpleGrid.data.otherLoads(config, search);
	}
	$.ajax({
		url: config.DataUrl,
		method: "GET",
		data: urlParams,
		success: function(response) {
			var body = $('#' + config.Name + "-grid table tbody");
			body.html('');
			if (response.data.length === 0) {
				body.html($('<div>').text(gridLang.loading.noItemsFound));
				return;
			}
			// load data
			response.data.forEach(function(row, index) {
				var tableRow = $('<tr>');
				config.Columns.forEach(function(column, index) {
					var td = $('<td>');
					if (column.type === 'actions') {
						td.html(
							$("<input>").attr("type", "checkbox")
								.attr("class", config.Name + '-grid-action')
								.attr("data-unique", row[config.Unique])
						);
					} else if (column.hasOwnProperty("renderer")) {
						if (column.type === 'buttons') {
							td.html(column["renderer"](row));
						} else {
							td.html(column["renderer"](row[column.name]));
						}
					} else {
						td.text(row[column.name]);
					}
					tableRow.append(td);
				});
				body.append(tableRow);
			});
			// reload pages
			simpleGrid.data.paging(config, response.pageIndex, response.pageCount);
			// set params in url
			window.history.pushState({"html":window.location.href},"", "?" + jQuery.param(urlParams));
		},
		error: function(xhr,status,error) {
			console.log(xhr, status, error);
			$('#' + response.uniqueName + "-grid table tbody").html($('<div>').text(gridLang.loading.loadingError));
		}
	});
};
simpleGrid.gui = {};
simpleGrid.gui.sorting = function(config) {
	var names = $('<tr>').attr("id", config.Name + "-sorting");
	config.Columns.forEach(function(column, index) {
		var cell = $('<a>');
		if (column.sorting) {
			cell.attr("href", "").attr("data-sort", 0).click(function() {
				var sortType = $(this).data('sort') + 1;
				if (sortType === 3) {
					$(this).data('sort', 0);
				} else {
					$(this).data('sort', sortType);
				}
				$(this).children(".sortType").hide();
				$(this).children(".type" + sortType).show();
				simpleGrid.data.load(config);
				return false;
			});
			cell.append($('<i>').attr("class", "far fa-clock sortType type1").hide());
			cell.append($('<i>').attr("class", "fas fa-clock sortType type2").hide());
		}
		if (column.hasOwnProperty('title')) {
			cell.append(column.title);
		} else {
			cell.append(column.name);
		}
		names.append($('<td>').attr("data-name", column.name).html(cell));
	});
	return names;
}
simpleGrid.gui.filters = function(config) {
	var filters = $('<tr>').attr("id", config.Name + "-filtering");
	var appendableParam = function(column, name) {
		var attribute = '';
		if (column.hasOwnProperty(name)) {
			attribute = name + '="' + column[name] + '"';
		}
		return attribute;
	};
	config.Columns.forEach(function(column, index) {
		var cell = $('<td>').attr("data-name", column.name);
		if (column.type === "actions") {
			cell.html($('<input>').attr('type', "checkbox").click(function() {
				$('.' + config.Name + '-grid-action').prop('checked', $(this).prop('checked'));
			}));
		} else if (column.hasOwnProperty('filter')) {
			switch(column.filter) {
				case 'text':
					cell.html(
							'<input type="text" '
							+ appendableParam(column, "size")
							+ ' '
							+ appendableParam(column, "minlength")
							+ ' '
							+ appendableParam(column, "maxlength")
							+ ' />'); 
					break;
				case 'number':
					cell.html(
							'<input type="number" '
							+ appendableParam(column, "step")
							+ ' '
							+ appendableParam(column, "max")
							+ ' '
							+ appendableParam(column, "min")
							+ ' />');
					break;
				case 'select':
					var select = $('<select>');
					column.select_options.forEach(function(option, index) {
						select.append($('<option>').attr("value", option.value).text(option.text));
					});
					cell.html(select);
					break;
				case 'datetime':
					cell.html('<input type="datetime-local" />');
					break;
				default: console.log("Mising filter type " + column.filter);
			}
			cell.change(function() {
				simpleGrid.data.load(config);
			});
		} else {
			cell.text('');
		}
		filters.append(cell);
	});
	return filters;
}

simpleGrid.gui.header = function(config) {
	return $('<thead>').append(simpleGrid.gui.sorting(config)).append(simpleGrid.gui.filters(config));
}

simpleGrid.gui.body = function() {
	return $('<tbody>'); /* empty, data loaded after */ 
}

simpleGrid.gui.footer = function(config) {
	var pages = $('<div>').attr("id", config.Name + "-actualPage");
	// actions
	var actions = $('<div>');
	var select = $('<select>').append($('<option>').attr("value", '').text('---'));
	config.Actions.forEach(function (item, index) {
		select.append($('<option>').attr("value", item.link).text(item.title));
	});
	select.change(function() {
		$(this).next("a").attr("href", $(this).val());
	});
	var button = $('<a>').attr("href", "").click(function() {
		var ids = {};
		$('.' + config.Name + "-grid-action:checked").each(function() {
			// ids.push($(this).data("unique"));
			ids[$(this).data("unique")] = $(this).data("unique");
		});
		if (true) { // TODO async switch
			$.ajax({
				url: $(this).attr("href"),
				data: {
					uniques: ids
				},
			}).done(function(res, message, xhr) {
				console.log(res, message, xhr);
				$('#' + config.Name + "-gridMessages")
					.html($('<div>').css("background-color", 'blue').text("success")); // TODO
			}).fail(function(xhr, re, error) {
				console.log(xhr, re, error);
				$('#' + config.Name + "-gridMessages")
					.html($('<div>').css("background-color", 'red').text("error")); // TODO
			});
			return false;
		}
	}).text(gridLang.execute);
	actions.append(select).append(button);
	return $('<div>').append(actions).append(pages); // tfoot
};

simpleGrid.init = function(gridIdentifier, config) {
	var table = $('<table>');
	table.append(simpleGrid.gui.header(config)).append(simpleGrid.gui.body());
	var container = $('<div>').attr("class", "grid")
		.attr("id", config.Name + "-grid")
		.append($('<div>').attr("id", config.Name + "-gridMessages"))
		.append(table).append(simpleGrid.gui.footer(config));
	$(gridIdentifier).html(container);
	simpleGrid.data.load(config, true);
};
