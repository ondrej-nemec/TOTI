/* TOTI Grid version 0.0.21 */
class TotiGrid {

	constructor(config) {
		this.config = config;
	}

	init(elementIdentifier, uniqueName) {
		var object = this;
		document.addEventListener("DOMContentLoaded", function(event) {
            document.querySelector(elementIdentifier).innerHTML = "";
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
		if (this.config.hasOwnProperty("pagesButtonCount")) {
			footer.appendChild(this.createPages(uniqueName, this.config.pagesButtonCount, 1));
			footer.appendChild(space);
			footer.appendChild(space);
		}
		if (this.config.hasOwnProperty("pages")) {
			footer.appendChild(this.createPagesSize(uniqueName, this.config.pages.pagesSizes, this.config.pages.defaultSize));
		}
		/***********/

		var table = document.createElement("table");
		table.setAttribute("class", "toti-table");
		
		var caption = document.createElement("caption");
		caption.setAttribute("id", uniqueName + "-table-caption");
		caption.innerText = "";
		table.appendChild(caption);

		table.appendChild(head);
		table.appendChild(body);
		var footerTr = document.createElement("tr");
		footerTr.appendChild(footer);
		var tFooter = document.createElement("tfoot");
		tFooter.appendChild(footerTr);

		document.createElement("tfooter");
		table.appendChild(tFooter);

		var grid = document.createElement("div");
		grid.setAttribute("id", uniqueName + "-control");
		var tableContainer = document.createElement("form");
		tableContainer.appendChild(table);
		grid.appendChild(tableContainer);
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
						sortType = 0;
					}
					cell.setAttribute('data-sort', sortType);
					cell.querySelectorAll(".sortType").forEach(function(element) {
						element.style.display = "none";
					});
					cell.querySelectorAll(".type" + sortType).forEach(function(element) {
						element.style.display = "inline";
					});
					object.load(uniqueName);
				};

				var imgUp = document.createElement("img");
				imgUp.setAttribute("src", totiImages.arrowUp);
				imgUp.setAttribute("alt", "");
				imgUp.setAttribute("width", 15);
				imgUp.setAttribute("class", "sortType type0 type1");

				var imgDown = document.createElement("img");
				imgDown.setAttribute("src", totiImages.arrowDown);
				imgDown.setAttribute("alt", "");
				imgDown.setAttribute("width", 15);
				imgDown.setAttribute("class", "sortType type0 type2");

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
						chBoxs.forEach(function(el) { el.checked = checkbox.checked; });
					}
				};
				cell.appendChild(checkbox);
				cell.setAttribute("no-filters", "");
			} else if (column.type == "buttons") {
				if (column.reset) {
					var reset = totiControl.input({
						type: "reset"
					});
					reset.onclick = function() {
						/*set timeout is fix - onclick is fired before crearing*/
						setTimeout(function() {
							object.load(uniqueName);
						}, 10);
					};
					cell.appendChild(reset);
				}
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
			"async": true,
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
			var async = option.getAttribute("async");
			var submitConfirmation = option.getAttribute("submitConfirmation");
			
			var ids = [];
			document.querySelectorAll('.' + uniqueName + "-grid-action:checked").forEach(function(checkbox) {
				ids.push(checkbox.getAttribute("data-unique"));
			});
			if (ids.length === 0) {
				totiDisplay.flash("error", totiTranslations.actions.noSelectedItems);
				return false;
			}
			var params = {"ids": JSON.stringify(ids)};
			if (async === 'true') {
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
		var loadDataSuccess = function(body, uniqueName, response, columns) {
			if (response.data.length === 0) {
				body.innerHTML = "";
				var td = document.createElement("td");
				td.setAttribute("colspan", 100);
				td.innerText = totiTranslations.gridMessages.noItemsFound;
				body.appendChild(document.createElement("tr").appendChild(td));
				return;
			}
			var pageSize = object.pagesSizeGet(uniqueName);
			object.pagesOnLoad(uniqueName, response.pageIndex, pageSize === null ? null : response.itemsCount / pageSize);
			document.getElementById(uniqueName + "-table-caption").innerText = 
				totiTranslations.gridMessages.tableCaption
				.replace("{total}", response.itemsCount)
				.replace("{onPage}", response.data.length)
				.replace("{pageIndex}", response.pageIndex);
			response.data.forEach(function(row, rowIndex) {
				var tableRow = document.createElement("tr");
				tableRow.setAttribute("index", rowIndex);
				tableRow.setAttribute("class", "toti-row-" + (rowIndex %2) + " toti-row-" + uniqueName);
				if (object.config.useRowSelection) {
					tableRow.onclick = function(event) {
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
				}

				columns.forEach(function(column, colIndex) {
					var td = document.createElement("td");
					td.setAttribute('index', colIndex);
					if (column.type === 'actions') {
						td.appendChild(totiControl.input({
							type: "checkbox",
							"class": uniqueName + "-grid-action",
							"data-unique": row[column.identifier]
						}));
					} else if (column.type === 'buttons') {
						var showButton = function(rowData, condition, evaluate) {
							if (evaluate) {
								condition = totiUtils.parametrizedString(condition, rowData);
							}
							return totiUtils.execute(condition, [rowData], evaluate);
						};
						column.buttons.forEach(function(button, index) {
							if (button.hasOwnProperty("condition") && !showButton(row, button.condition, button.evaluate)) {
								return;
							}
							var href = totiUtils.parametrizedString(button.href, row);
							var settings = {
								href: href,
								method: button.method,
								async: button.async,
								params: button.requestParams,
								submitConfirmation: function() {
									if (button.hasOwnProperty('confirmation')) {
										var message = totiUtils.parametrizedString(button.confirmation, row);
										return totiDisplay.confirm(message);
									}
									return true;
								},
								type: button.hasOwnProperty('style') ? button.style : 'basic'
							};
                            if (button.hasOwnProperty("onSuccess")) {
                               settings.onSuccess = button.onSuccess;
                            }
                            if (button.hasOwnProperty("onError")) {
                               settings.onError = button.onError;
                            }

							var buttonClone = totiUtils.clone(button);
							buttonClone.id += "_" + rowIndex;
							buttonClone.href = href;

							var buttonElement = totiControl.button(buttonClone, button.async);
							buttonElement.onclick = function(event) {
								totiControl.getAction(settings)(event);
								setTimeout(function(){
									object.load(uniqueName);
								}, 500);
							};
							td.appendChild(buttonElement);
						});
					} else if (column.hasOwnProperty("renderer")) {
						var renderer = totiUtils.execute(column.renderer, [row[column.name], row]);
						if (typeof renderer === "object") {
							td.appendChild(renderer);
						} else {
							td.innerText = renderer;
						}
					} else if (column.hasOwnProperty("filter") && column.filter.hasOwnProperty("renderOptions")) {
						var value = row[column.name];
                        if (value !== null) {
                            if (column.filter.renderOptions.hasOwnProperty(value)) {
                                td.innerText = column.filter.renderOptions[value];
                            } /* else {
                                // for yes/no renderer
                                var find = column.filter.renderOptions.find(element => element.value === value + "");
                                td.innerText = find ? find.title : value;
                            } */
                        }
					} else if (column.hasOwnProperty("filter")  && column.filter.hasOwnProperty("originType")) {
						td.innerText = totiControl.parseValue(column.filter.originType, row[column.name]);
					} else {
						td.innerText = row[column.name];
					}
					tableRow.append(td);
				});
				if (object.config.hasOwnProperty("onRowRenderer")) {
					body.append(totiUtils.execute(object.config.onRowRenderer, [tableRow, row]));
				} else {
					body.append(tableRow);
				}
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
						object.config.columns
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
			if (element.children.length > 0 && data[name] !== undefined) {
				element.children[0].value = data[name];
				if (element.children[0].type === "fieldset") {
					element.children[0].set();
				}
			}
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
				var a = sort.querySelector("a");
				a.setAttribute("data-sort", sortType);
				a.querySelector(".sortType").style.display = "none";
				a.querySelector(".type" + sortType).style.display = "inline";
			}
		});
	}

	pagesSizeOnLoad(uniqueName, pageSize) {
		document.getElementById(uniqueName + "-pageSize").value = pageSize;
	}

	pagesOnLoad(uniqueName, actualPage, pagesCount) {
		var object = this;
		var pagesList = document.getElementById(uniqueName + "-pages-list");
		if (pagesList === null) {
			return;
		}
		pagesList.setAttribute("data-actualpage", actualPage);
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
		var lower = actualPage - parseInt(Math.floor(pagesList.getAttribute("data-pagesbuttoncount")) / 2);
		if (lower < 1) {
			lower = 1;
		}
		var append = pagesCount%actualPage === 0 ? 1 : 0;
		for (var i = lower; i < Math.min(lower + parseInt(pagesList.getAttribute("data-pagesbuttoncount")), pagesCount) + append; i++) {
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
		var element = document.getElementById(uniqueName + "-pages-list");
		if (element === null) {
			return null;
		}
		return element.getAttribute("data-actualpage");
	}

	pagesSizeGet(uniqueName) {
		var element = document.getElementById(uniqueName + "-pageSize");
		if (element === null) {
			return null;
		}
		return element.value;
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