class TotiGrid {

	constructor(config) {
		this.config = config;
	}
}



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
			totiGrid.config[uniqueName].actions
		);
		document.querySelector(elementIdentifier).appendChild(grid);
		totiGrid.load(uniqueName, true)	
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
		var body = document.querySelector('#' + uniqueName + "-control table tbody");
		body.innerHTML = '';
		totiLoad.async(
			totiGrid.config[uniqueName].dataLoadUrl,
			totiGrid.config[uniqueName].dataLoadMethod,
			urlParams,
			totiLoad.getHeaders(),
			function(response) {
				window.history.pushState({"html":window.location.href},"", "?" + new URLSearchParams(urlParams).toString();
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
			}
		);
	},
	_loadDataSuccess: function(body, uniqueName, response, columns, headers, identifier) {
		if (response.data.length === 0) {
			var td = document.createElement("td");
			td.setAttribute("colspan", 100);
			td.innerText = totiTranslations.gridMessages.noItemsFound;
			body.appendChild(document.createElement("tr").appendChild(td));
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
					td.html(window[column.renderer](row[column.name]));
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