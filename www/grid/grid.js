var gridLang = gridLang || {
	"pages": {
		"title": "Pages:",
		"first": "First",
		"previous": "Previous",
		"next": "Next",
		"last": "Last"
	},
	"messages": {
		"noItemsFound": "No items founded",
		"loadingError": "Loading error",
		"noItemsSelected": "No items selected"
	},
	"execute": "Execute..."
};

var totiControl = {};
totiControl = {
	showMessage: function(uniqueName, message, clazz) {
		$('#' + uniqueName + "-flashMessages")
				.html($('<div>').attr("class", clazz).text(message));
	},
	ajax: function(url, method, data, onSuccess, onFailure) {
		var headers = {};
		if (typeof createRequestHeaders == 'function') { 
			headers = createRequestHeaders();
		}
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
	inputs: {
		createInput: function(type, attributes = {}) {
			var input = $("<input>").attr("type", type);
			for ([key, name] of Object.entries(attributes)) {
				input.attr(key, name);
			}
			return input;
		},
		createSelect: function(options, attributes = {}){
			var select = $('<select>');
			for ([key, name] of Object.entries(attributes)) {
				select.attr(key, name);
			}
			options.forEach(function(option, index) {
				var opt = $('<option>').attr("value", option.value).text(option.text);
				if (opt.hasOwnProperty("attributes")) {
					for ([key, name] of Object.entries(option.attributes)) {
						opt.attr(key, name);
					}
				}
				select.append(opt);
			});
			return select;
		},
		selectInput: function(inputConfig) {
			var addParamIfExists = function(name, object) {
				if(inputConfig.hasOwnProperty(name)) {
					object[name] = inputConfig[name];
				}
			};
			switch(inputConfig.type) {
				case 'text':
					var attrs = {};
					addParamIfExists("name", attrs);
					addParamIfExists("size", attrs);
					addParamIfExists("minlength", attrs);
					addParamIfExists("maxlength", attrs);
					return totiControl.inputs.createInput('text', attrs);
				case 'number':
					var attrs = {};
					addParamIfExists("name", attrs);
					addParamIfExists("step", attrs);
					addParamIfExists("max", attrs);
					addParamIfExists("min", attrs);
					return totiControl.inputs.createInput('number', attrs);
				case 'datetime':
					var attrs = {};
					addParamIfExists("name", attrs);
					return totiControl.inputs.createInput('datetime-local', attrs);
				case 'email':
					var attrs = {};
					addParamIfExists("name", attrs);
					return totiControl.inputs.createInput('email', attrs);
				case 'password':
					var attrs = {};
					addParamIfExists("name", attrs);
					addParamIfExists("size", attrs);
					addParamIfExists("minlength", attrs);
					addParamIfExists("maxlength", attrs);
					return totiControl.inputs.createInput('password', attrs);
				case 'checkbox':
					var attrs = {};
					addParamIfExists("name", attrs);
					return totiControl.inputs.createInput('checkbox', attrs);
				case 'select':
					var attrs = {};
					addParamIfExists("name", attrs);
					return totiControl.inputs.createSelect(inputConfig.options, attrs);
				default: console.log("Mising input type " + inputConfig.type);
			}
		}
	},
	data: {
		loadForm: function() {
			// TODO
		},
		loadGrid: function(config, initialLoad = false) {
			var firstLoad = function(search) {
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
			};
			var otherLoads = function(search) {
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
				var pageIndex = $("#" + config.Name + "-actualPage").attr('data-actualpage');

				var urlParams = {
						pageIndex: pageIndex === undefined ? 1 : pageIndex,
						pageSize: pageSize === undefined ? config.pageSizeDefault : pageSize,
						filters: filters,
						sorting: sorts
				};
				return urlParams;
			};
			var urlParams = {};
			var search = decodeURIComponent(window.location.search.substring(1));
			if (initialLoad && search !== '') {
				urlParams = firstLoad(search);
			} else {
				urlParams = otherLoads(search);
			}
			var onFailure = function(xhr,status,error) {
				console.log(xhr, status, error);
				$('#' + response.uniqueName + "-grid table tbody").html($('<div>').text(gridLang.messages.loadingError));
			};
			var onSuccess = function(response) {
				var body = $('#' + config.Name + "-control table tbody");
				body.html('');
				if (response.data.length === 0) {
					body.html($('<div>').text(gridLang.messages.noItemsFound));
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
						} else if (column.type === 'buttons') {
							column.buttons.forEach(function(button, index) {
								td.append(
									$('<a>').attr("href", button.href(row)).html(
										$('<button>').attr("class", button.class).text(button.title)
									).click(function() {
										if (!button.confirmation(row)) {
											return false;
										}
										totiControl.showMessage(config.Name, '', '');
										if (button.ajax) {
											totiControl.ajax(
												$(this).attr("href"),
												button.method,
												button.params(row),
												totiControl.showMessage(config.Name, button.onSuccess(row), 'toti-control-flash success'),
												totiControl.showMessage(config.Name, button.onFailure(row), 'toti-control-flash error')
											);
											return false;
										}
									})
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
				// reload pages
				totiControl.gui.gridPagging(config, response.pageIndex, response.itemsCount);
				// set params in url
				window.history.pushState({"html":window.location.href},"", "?" + jQuery.param(urlParams));
			};
			totiControl.ajax(config.DataUrl, config.DataMethod, urlParams, onSuccess, onFailure);
		}
	},
	gui: {
		flashMessages: function(uniqueName) {
			return $('<div>').attr("id", uniqueName + "-flashMessages");
		},
		form: function(config){}, // TODO

		gridPagging: function(config, actualPage, pageCount) {
			var onPageClick = function(newPage, pages) {
				return function() {
					pages.attr("data-actualPage", newPage);
					totiControl.data.loadGrid(config, false);
					return false;
				};
			};
			var pages = $("#" + config.Name + "-actualPage");
			pages.html('');
			pages.attr("data-actualPage", actualPage);

			pages.append($('<scan>').text(gridLang.pages.title));
			pages.append('&nbsp;');
			if (actualPage > 1) {
				pages.append($('<a>').attr("href", "").text(gridLang.pages.first).click(onPageClick(1, pages)));
				pages.append('&nbsp;');
			}
			if (actualPage > 2) {
				pages.append($('<a>').attr("href", "").text(gridLang.pages.previous).click(onPageClick(actualPage - 1, pages)));
				pages.append('&nbsp;');
			}

			var lower = actualPage - Math.floor(config.pagesButton / 2);
			if (lower < 1) {
				lower = 1;
			}
			for (i = lower; i < Math.min(lower + config.pagesButton, pageCount); i++) {
				var page = $('<a>').attr("href", "").text(i);
				if (i === actualPage) {
					page.attr("class", "actualPage");
				}
				page.click(onPageClick(i, pages));
				pages.append(page);
				pages.append('&nbsp;');
			}

			if (actualPage < pageCount) {
				pages.append($('<a>').attr("href", "").text(gridLang.pages.next).click(onPageClick(actualPage+1, pages)));
				pages.append('&nbsp;');
			}
			if ((actualPage + 1) < pageCount) {
				pages.append($('<a>').attr("href", "").text(gridLang.pages.last).click(onPageClick(pageCount, pages)));
				pages.append('&nbsp;');
			}
			pages.append('&nbsp;');
			var pageSizeSelect = $('<select>').attr("id", config.Name + "-pageSize");
			config.pageSizes.forEach(function(size, index) {
				pageSizeSelect.append($('<option>').attr("value", size).text(size));
			});
			pageSizeSelect.change(function() {
				totiControl.data.loadGrid();
			});
			pageSizeSelect.val(config.pageSizeDefault);
			pages.append(pageSizeSelect);
		},
		grid: function(config){
			var loadData = function() {
				totiControl.data.loadGrid(config, false);
			};
			var sorting = function() {
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
							loadData();
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
			};
			var filters = function() {
				var filters = $('<tr>').attr("id", config.Name + "-filtering");
				config.Columns.forEach(function(column, index) {
					var cell = $('<td>').attr("data-name", column.name);
					if (column.type === "actions") {
						cell.html(totiControl.inputs.selectInput({"type": "checkbox"}).click(function() {
							$('.' + config.Name + '-grid-action').prop('checked', $(this).prop('checked'));
						}));
					} else if (column.hasOwnProperty('filter')) {
						column.type = column.filter;
						cell.html(totiControl.inputs.selectInput(column));
						cell.change(function() {
							loadData();
						});
					} else {
						cell.text('');
					}
					filters.append(cell);
				});
				return filters;
			};
			var header = function() {
				return $('<thead>').append(sorting()).append(filters());
			};
			var body = function() {
				return $('<tbody>'); // empty, data loaded after
			};
			var pages = function() {
				return $('<div>').attr("id", config.Name + "-actualPage");
			};
			var actions = function() {
				var actions = $('<div>');
				var select = $('<select>').append($('<option>').attr("value", '').text('---'));
				config.Actions.forEach(function (item, index) {
					select.append($('<option>')
							.attr("value", item.link)
							.data("ajax", item.hasOwnProperty("ajax") ? item.sync : true)
							.text(item.title));
				});
				select.change(function() {
					$(this).next("a").attr("href", $(this).val());
				});
				var button = $('<a>').attr("href", "").click(function() {
					var ids = {};
					$('.' + config.Name + "-grid-action:checked").each(function() {
						ids[$(this).data("unique")] = $(this).data("unique");
					});
					if (Object.keys(ids).length === 0) {
						totiControl.showMessage(config.Name, gridLang.messages.noItemsSelected, "toti-control-flash error");
						return false;
					}
					
					if ($(this).parent().children('select').children('option:selected').data("ajax")) {
						totiControl.showMessage(config.Name, "", "");
						totiControl.ajax($(this).attr("href"), "GET", {
								uniques: ids
							}, function() {
								totiControl.showMessage(config.Name, "Success", "toti-control-flash success");
							}, function() {
								totiControl.showMessage(config.Name, "Failure", "toti-control-flash error");
							}
						);
						return false;
					}
				}).text(gridLang.execute);
				actions.append(select).append(button);
				return actions;
			};
			var footer = function() {
				return $('<div>').append(actions()).append(pages());
			};
			var table = $('<table>').append(header()).append(body());
			return $('<div>').append(table).append(footer());
		}
	},
	init: function(elementIdentifier, config, type) {
		var container = $('<div>').attr("id", config.Name + "-control");
		container.append(totiControl.gui.flashMessages(config.Name));
		var loadData = function(){};
		switch(type) {
			case "grid": 
				container.append(totiControl.gui.grid(config));
				loadData = function() {
					totiControl.data.loadGrid(config, true);
				};
				break;
			case "form": 
				container.append(totiControl.gui.form(config));
				loadData = function() {
					totiControl.data.loadForm();
				};
				break;
			default: console.log("Unknown control type " + type);
		}
		$(elementIdentifier).html(container);
		loadData();
	}
};
