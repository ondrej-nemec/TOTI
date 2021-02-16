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
		var footer = document.createElement("td");
		footer.setAttribute("colspan", 100);
		if (this.config.actions.length > 0) {
			// TODO footer.appendChild(this.createActions(uniqueName, this.config.actions));
		}
		footer.appendChild(this.createPages(uniqueName, this.config.pages.pagesButtonCount, 1));
		footer.appendChild(this.createPagesSize(uniqueName, this.config.pages.pagesSizes, this.config.pages.defaultSize));

		var table = document.createElement("table");
		table.setAttribute("class", "toti-table");

		table.appendChild(head);
		table.appendChild(body);
		var footerTr = document.createElement("tr");
		footerTr.appendChild(footer);
		var tFooter = document.createElement("tfooter");
		tFooter.appendChild(footerTr);

		document.createElement("tfooter");
		table.appendChild(tFooter);
		// TODO caption s x from y

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
				cell.setAttribute("no-folters", "");
			} else if (column.hasOwnProperty('filter')) {
				if (column.filter.type === "select") {
					/*var options = [];
					column.filter.options.forEach(function(option) {
						var params = {};
						if (option.hasOwnProperty('params')) {
							params = option.params;
						}
						options.push(totiControl.input(option.value, option.title, params));
					});
					delete column.filter.options;*/
					cell.appendChild(
						totiControl.input(column.filter)
					);
				} else {
					cell.appendChild(
						totiControl.input(column.filter)
					);
				}
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
		// TODO
	}

	createPages(uniqueName, pagesButtonCount, actualPage) {
		var pagging = document.createElement("div");
		pagging.setAttribute("id", uniqueName + "-pages");
		var span = document.createElement("span");
		span.innerText = totiTranslations.pages.title;
		pagging.appendChild(span);
		pagging.innerText = '&nbsp;';
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
			/*
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
		},*/
	}

	load(uniqueName, initialLoad = false) {
		var object = this;
		var loadDataSuccess = function(body, uniqueName, response, columns, headers, identifier) {
			if (response.data.length === 0) {
				var td = document.createElement("td");
				td.setAttribute("colspan", 100);
				td.innerText = totiTranslations.gridMessages.noItemsFound;
				body.appendChild(document.createElement("tr").appendChild(td));
				return;
			}
			object.pagesOnLoad(uniqueName, response.pageIndex, response.itemsCount / totiGrid.pagesSize.get(uniqueName));
			response.data.forEach(function(row, rowIndex) {
				var tableRow = document.createElement("tr");
				tableRow.setAttribute("index", rowIndex);
				tableRow.setAttribute("class", "toti-row" + (rowIndex %2) + " toti-row-" + uniqueName);
				tableRow.onclick = function(event) {
					// TODO 
					/*if (jQuery(e.target).is('input') ||jQuery(e.target).is('button')) {
						return;
					}*/
					var actualClass = tableRow.getAttribute("class");
					document.getElementsByClassName("row-selected").forEach(function(element) {
						var clazz = element.getAttribute("class");
						element.setAttribute("class", clazz.replace("row-selected", ""));
					});
					var clazz = tableRow.getAttribute("class");
					if (!actualClass.includes("row-selected")) {
						element.setAttribute("class", actualClass + " row-selected");
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
							var buttonElement = totiControl.button(button);
							buttonElement.onclick(function(event) {
								totiControl.getAction(settings)(event);
								setTimeout(function(){
									object.load(uniqueName);
								}, 500);
							});
							td.appendChild(buttonElement);
						});
					} else if (column.hasOwnProperty("renderer")) {
						// TODO RENDERER TODO
						td.appendChild(window[column.renderer](row[column.name], row));
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
		totiLoad.async(
			this.config.dataLoadUrl,
			this.config.dataLoadMethod,
			urlParams,
			totiLoad.getHeaders(),
			function(response) {
				window.history.pushState({"html":window.location.href},"", "?" + new URLSearchParams(urlParams).toString());
				loadDataSuccess(
					body,
					uniqueName,
					response, 
					object.config.columns,
					object.config.headers,
					object.config.identifier
				);
			},
			function(xhr) {
				var tr = document.createElement("tr");
				tr.setAttribute("colspan", 100);
				tr.innerText = totiTranslations.gridMessages.loadingError;
				body.appendChild(tr);
			}
		);
	}
	
	filtersOnLoad(uniqueName, urlParams) {
		var data = {};
		if (urlParams.filters !== undefined) {
			data = JSON.parse(urlParams.filters);
		}
		document.getElementById(uniqueName + "-filtering").querySelectorAll("td").forEach(function(element) {
			var name = element.getAttribute('data-name');
			element.children.value = data[name];
		});
	}

	sortingOnLoad(uniqueName, urlParams) {
		var data = {};
		if (urlParams.sorting != undefined) {
			data = JSON.parse(urlParams.sorting);
		}
		document.getElementById( uniqueName + "-sorting").querySelectorAll('td').forEach(function(sort) {
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
				title: title
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
		for (i = lower; i < Math.min(lower + pagesList.getAttribute("data-pagesbuttoncount"), pagesCount); i++) {
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
				sorts[element.querySelector("data-name")] = (sort === 1) ? 'ASC' : 'DESC';
			}
		});
		return JSON.stringify(sorts);
	}
	
	filtersGet(uniqueName) {
		var filters = {};
		document.getElementById(uniqueName + "-filtering").querySelectorAll('td').forEach(function(element, index) {
			if (element.getAttribute('no-filters') !== null) {
				return;
			}
			var value = element.children.value;
			if (element.getAttribute('data-name') != '' && value !== null && value !== '') {
				filters[element.getAttribute('data-name')] = value;
			}
		});
		return JSON.stringify(filters);
	}
	

}

/*

var totiGrid = {
	actions: {
		//actions: [ {link, title, ajax, method} ]
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
						// TODO Improvement use link? now no params sended 
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
			// empty
		},
		get: function(uniqueName) {
			// empty 
		}
	}
};*/