/* TOTI Grid version 1.0.0 */
class TotiGrid {

	cookieName = "grid-cache";
	cookieAge = 60*5; /* 5 min */
	
	constructor(config) {
		this.config = config;
		this.sorting = new SortedMap();
		this.filtering = new SortedMap();
		this.pageIndex = 1;
		this.pageSize = 0;
		this.pageSizeCallback = ()=>{};
		this.selectedRow = null;
	}

	render(selector, gridUnique, customTemplate = null) {
		totiDisplay.fadeIn();
		var template = this.template = this.getTemplate(selector, customTemplate);
		var container = this.container = template.getContainer(selector, gridUnique);
		this.gridUnique = gridUnique;

		/* get grid selection from url and cookie */
		var urlCache = decodeURIComponent(window.location.search.substring(1));
		var cookieCache = totiUtils.getCookie(this.cookieName);
		var search = {};
		if (urlCache !== '') {
			search = totiUtils.parseUrlToObject(urlCache);
		} else if (cookieCache !== null) {
			search = JSON.parse(cookieCache);
		}
		var sorting = new SortedMap();
		if (search.hasOwnProperty('sorting')) {
			sorting = SortedMap.deserialize(search.sorting);
		}
		var filters = new SortedMap();
		if (search.hasOwnProperty('filters')) {
			filters = SortedMap.deserialize(search.filters);
		}

		var grid = this;
        var promises = [];
		this.config.columns.forEach(function(column) {
			template.setTitle(gridUnique, container, column.name, column.hasOwnProperty('title') ? column.title : column.name);
			if (column.useSorting) {
				var defSort = null;
				if (column.hasOwnProperty('defSort')) {
					grid.sortBy(column.name, column.defSort, false);
					defSort = column.defSort;
				}
				if (sorting.exists(column.name)) {
					grid.sortBy(column.name, sorting.get(column.name), false);
					defSort = sorting.get(column.name);
				}
				template.addSorting(gridUnique, container, column.name, grid, defSort);
			}
			switch(column.type) {
				case "actions":
					var checkbox = totiControl.input({
						type: "checkbox"
					});
					checkbox.addEventListener("click", function() {
						container.querySelectorAll('[unique="' + gridUnique + '"]').forEach(function(chbx) {
							chbx.checked = checkbox.checked;
						});
					});
					template.addCheckbox(gridUnique, container, checkbox);
					break;
				case "buttons":
					var buttons = [];
					column.globalButtons.forEach(function(buttonConf) {
						/*if (buttonConf.type === 'reset') {
							var reset = totiControl.input(buttonConf);
							reset.setAttribute("grid", grid.gridUnique);
							buttons.push(reset);
							reset.addEventListener('click', function() {
								container.querySelectorAll(".toti-grid-filtering").forEach(function(input) {
									input.value = '';
									grid.filterBy(input.getAttribute("name"), null, false);
									console.log(input.getAttribute("name"));
								});
								grid.refreshData();
							});

						} else */if (buttonConf.type === 'button' && buttonConf.hasOwnProperty('is_reset') && buttonConf.is_reset) {
							/* IPRROVEMENT refresh button */
							var button = totiControl.button(buttonConf);
                            button.setAttribute("grid", grid.gridUnique);
                            button.onclick = function(e) {
                                e.preventDefault();
                                container.querySelectorAll(".toti-grid-filtering").forEach(function(input) {
                                    input.value = '';
                                    grid.filterBy(input.getAttribute("name"), null, false);
                                });
                                grid.refreshData();
                             };
                             buttons.push(button);
						} else if (buttonConf.type === 'button') {
							var button = totiControl.button(buttonConf);
							button.setAttribute("grid", grid.gridUnique);
							button.addEventListener("click", function() {
								setTimeout(function(){
									grid.refreshData(clearPrevious);
								}, 500);
							});
							buttons.push(button);
						}
					});
					template.addButtons(gridUnique, container, column.name, buttons);
					break;
				case "value":
					var filter = null;
					if (column.hasOwnProperty("filter")) {
						var defValue = null;
						if (column.filter.hasOwnProperty('value')) {
							defValue = column.filter.value;
						}
						if (filters.exists(column.name)) {
							defValue = filters.get(column.name);
						}
						if (defValue !== null) {
							column.filter.value = defValue;
							grid.filterBy(column.name, defValue, false);
						}
						column.filter.name = column.name;

						filter = totiControl.input(column.filter);
						if (column.filter.originType === 'select') {
                            promises.push(filter.setOptions);
                        }
						filter.setAttribute("grid", grid.gridUnique);
						filter.classList.add("toti-grid-filtering");

						filter.addEventListener('change', function() {
							grid.filterBy(column.name, filter.value);
						});
					}
					template.addFilter(gridUnique, container, column.name, grid, filter);
					break;
				default: 
					break;
			}
		});
		if (this.config.actions.length > 0) {
			template.addActions(gridUnique, container, this.config.actions, function(event, settings) {
				totiDisplay.fadeIn();
				var params = new URLSearchParams();
				container.querySelectorAll('[unique="' + gridUnique + '"]:checked').forEach(function(checkbox) {
					params.append("ids[]", checkbox.getAttribute("data-value"));
				});
				if (params.toString() === '') {
					totiDisplay.fadeOut();
					totiDisplay.flash("error", totiTranslations.actions.noSelectedItems);
					return false;
				}
				var onClickSettings = totiUtils.clone(settings);
				onClickSettings['params'] = params;
				return totiControl.getAction(onClickSettings)(event);
			});
		}
		if (this.config.hasOwnProperty('pagesSizes')) {
			this.pageSizeCallback = template.addPageSize(gridUnique, container, this.config.pagesSizes, this.config.pageSize, this);
		}
		this.setPageSize(this.config.pageSize, false);
		if (search.hasOwnProperty('pageSize')) {
			this.setPageSize(search.pageSize, false);
		}
		if (search.hasOwnProperty('pageIndex')) {
			this.setPageIndex(search.pageIndex, false);
		}

        Promise.all(promises).then((values)=>{
			totiDisplay.fadeOut();
			this.refreshData();
			if (this.config.refresh > 0) {
				this.startRefresh();
			}
        }).catch(function(e) {
			totiDisplay.fadeOut();
        	console.error(e);
        });
	}

	getTemplate(parentSelector, template) {
		var parent = document.querySelector(parentSelector);
		if (parent === null) {
			console.error("Selector '" + parentSelector + "' is not pointing to existing element");
			return null;
		}
		var totiTemplate = totiDisplay.gridTemplate;
		if (template === false) { /* parent.children.length > 0 */
			totiTemplate = totiGridCustomTemplate;
		}
		if (template === null || template === false) {
			template = {};
		}
		for (const[name, value] of Object.entries(totiTemplate)) {
			if (!template.hasOwnProperty(name)) {
				template[name] = value;
			}
		}
		return template;
	}

	/*
	* String name: column name
	* boolean direction: null - no sort, false - asc, true - desc 
	*/
	sortBy(name, direction, refresh = true) {
		if (direction === null) {
			this.sorting.remove(name);
		} else {
			this.sorting.put(name, direction);
		}
		if (refresh) {
			this.refreshData();
		}
	}

	/**
	* Save next sorting state in order: no sorting (null), ASC (false), DESC (true)
	* @param name mixed
	* @return new sorting value
	*/
	nextSort(name, refresh = true) {
		var sort = this.sorting.get(name);
		var result;
		switch(sort) {
			case null:
				this.sorting.put(name, false);
				result = false;
				break;
			case false:
				this.sorting.put(name, true);
				result = true;
				break;
			case true:
				this.sorting.remove(name);
				result = null;
				break;
		}
		if (refresh) {
			this.refreshData();
		}
		return result;
	}

	/*
	* String name: column name
	* object value: looking for, null means not filter
	*/
	filterBy(name, value, refresh = true) {
		if (value === null || value === '') {
			this.filtering.remove(name);
		} else {
			this.filtering.put(name, value);
		}
		if (refresh) {
			this.refreshData();
		}
	}

	/*
	* display page on index
	* int pageIndex
	*/
	setPageIndex(pageIndex, refresh = true) {
		this.pageIndex = pageIndex;
		if (refresh) {
			this.refreshData();
		}
	}

	/*
	* add next page without removing previous data
	*/
	addNextPage(refresh = true) {
		this.pageIndex++;
		if (refresh) {
			this.refreshData(false);
		}
	}

	/*
	* int pageSize: how many items will be on one page. Negative numbers means no limit
	*/
	setPageSize(pageSize, refresh = true) {
		this.pageSize = pageSize;
		this.pageIndex = 1;
		this.pageSizeCallback(pageSize);
		if (refresh) {
			this.refreshData();
		}
	}

	startRefresh() {
		var grid = this;
		this.refreshInterval = setInterval(function() {
			grid.refreshData();
		}, this.config.refresh);
	}

	stopRefresh() {
		if (this.refreshInterval !== null && this.refreshInterval !== undefined) {
			clearInterval(this.refreshInterval);
		}
	}

	getSelectedRow() {
		return this.selectedRow;
	}

	/*
	* load data again
	*/
	refreshData(clearPrevious = true) {
		totiDisplay.fadeIn();
		var selectionParameters = {
			sorting: this.sorting.serialize(),
			filters: this.filtering.serialize(),
			pageIndex: this.pageIndex,
			pageSize: this.pageSize
		};
		var grid = this;
		totiLoad.load(this.config.dataLoadUrl, this.config.dataLoadMethod, {}, {}, selectionParameters)
		.then(function(response) {
			if (grid.pageIndex != response.pageIndex) {
				totiDisplay.fadeOut();
				return;
			}
			/* save state */
			window.history.pushState({"html":window.location.href},"", "?" + new URLSearchParams(selectionParameters).toString());
			totiUtils.setCookie(grid.cookieName, JSON.stringify(selectionParameters), grid.cookieAge, null);
			/* print */
			grid.template.setCaption(
				grid.gridUnique, grid.container, response.data.length, response.itemsCount,
				totiTranslations.gridMessages.tableCaption
					.replace("{total}", response.itemsCount)
					.replace("{onPage}", response.data.length)
					.replace("{pageIndex}", response.pageIndex)
			);
			if (clearPrevious) {
				grid.template.clearBody(grid.gridUnique, grid.container);
			}
			response.data.forEach(function(rowData) {
				var row = grid.template.addRow(grid.gridUnique, grid.container);
                if (grid.config.hasOwnProperty("rowRenderer")) {
                    totiUtils.execute(grid.config.rowRenderer, [row, rowData]);
                }
				row.addEventListener("click", function() {
					if (grid.config.useRowSelection) {
						grid.selectedRow = grid.template.setRowSelected(grid.gridUnique, grid.container, row);
					}
				});
				if (response.data.length === 0) {
                     totiDisplay.flash("warn", totiTranslations.gridMessages.noItemsFound);
                }
				grid.config.columns.forEach(function(column) {
					var cellData = null;
					var isElement = false;

					if (column.type === 'actions') {
						var checkbox = totiControl.input({
							type: "checkbox",
							unique: grid.gridUnique,
							"data-value": rowData[column.identifier]
						});
						grid.template.addCell(grid.gridUnique, grid.container, row, column.name, checkbox, 1);
					} else if (column.type === 'buttons') {
						var buttons = [];
						function showButton(condition, evaluate) {
							if (evaluate) {
								condition = totiUtils.parametrizedString(condition, rowData);
							}
							return totiUtils.execute(condition, [rowData], evaluate);
						};
						column.buttons.forEach(function(buttonConf) {
							if (buttonConf.hasOwnProperty("condition") && !showButton(buttonConf.condition, buttonConf.evaluate)) {
								return;
							}
							var btnConf = totiUtils.clone(buttonConf);
							btnConf.action.href = totiUtils.parametrizedString(buttonConf.action.href, rowData);
							if (btnConf.action.hasOwnProperty('submitConfirmation')) {
								btnConf.action.submitConfirmation = totiUtils.parametrizedString(btnConf.action.submitConfirmation, rowData);
							}
							var button = totiControl.button(btnConf);
							buttons.push(button);
							button.addEventListener("click", function() {
								setTimeout(function(){
									grid.refreshData(clearPrevious);
								}, 500);
							});
						});
						grid.template.addCell(grid.gridUnique, grid.container, row, column.name, buttons, 2);
					} else if (column.hasOwnProperty("renderer")) {
						var renderer = totiUtils.execute(column.renderer, [rowData[column.name], rowData]);
						grid.template.addCell(grid.gridUnique, grid.container, row, column.name, renderer, 0);
					} else if (!rowData.hasOwnProperty(column.name)) {
						grid.template.addCell(grid.gridUnique, grid.container, row, column.name, "", 0);
					} else if (column.hasOwnProperty("filter") && column.filter.hasOwnProperty("renderOptions")) {
						var value = rowData[column.name];
						if (value !== null) {
                            if (column.filter.renderOptions.hasOwnProperty(value)) {
                                value = column.filter.renderOptions[value];
                            }
						}
						grid.template.addCell(grid.gridUnique, grid.container, row, column.name, value, 0);
					} else if (column.hasOwnProperty("filter")  && column.filter.hasOwnProperty("originType")) {
						grid.template.addCell(
							grid.gridUnique, grid.container, row, column.name,
							totiControl.parseValue(column.filter.originType, rowData[column.name]), 
							0
						);
					} else {
						grid.template.addCell(grid.gridUnique, grid.container, row, column.name, rowData[column.name], 0);
					}
				});
			});
		    
		    grid.template.clearPageButtons(grid.gridUnique, grid.container);

		    if (grid.config.paggingButtonsCount > 0 && response.data.length > 0)  {
		    	var onClick = function(newIndex) {
		    		return function() {
		    			grid.setPageIndex(newIndex);
		    		};
		    	};
			    if (response.itemsCount > grid.pageSize && response.pageIndex > 1) {
			    	grid.template.addPageButton(grid.gridUnique, grid.container, totiTranslations.pages.first, false, onClick(1));
			    }
			    if (response.itemsCount > grid.pageSize && response.pageIndex > 2) {
			    	grid.template.addPageButton(
			    		grid.gridUnique, grid.container, totiTranslations.pages.previous, false, onClick(response.pageIndex - 1)
			    	);
			    }
			    var lastPageIndex = Math.ceil(response.itemsCount/grid.pageSize);
			    var lowerPage = response.pageIndex - Math.floor(grid.config.paggingButtonsCount / 2);
				if (lowerPage > lastPageIndex - grid.config.paggingButtonsCount) {
					lowerPage = lastPageIndex - grid.config.paggingButtonsCount;
				}
			    if (lowerPage < 1) {
					lowerPage = 1;
				}

			    var upperPage = lowerPage + grid.config.paggingButtonsCount;
			    if (lastPageIndex === upperPage) {
			    	upperPage = lastPageIndex + 1;
			    } else if (upperPage > lastPageIndex) {
			    	upperPage = lastPageIndex + 1;
			    }

				for (var i = lowerPage; i < upperPage; i++) {
					grid.template.addPageButton(grid.gridUnique, grid.container, i, i === response.pageIndex, onClick(i));
				}

				if (response.pageIndex + 1 < lastPageIndex) {
			    	grid.template.addPageButton(
			    		grid.gridUnique, grid.container, totiTranslations.pages.next, false, onClick(response.pageIndex + 1)
			    	);
			    }
				if (response.pageIndex < lastPageIndex) {
			    	grid.template.addPageButton(grid.gridUnique, grid.container, totiTranslations.pages.last, false, onClick(lastPageIndex));
				}
		    }
		    if (grid.config.useLoadButton) {
		    	grid.template.addPageButton(grid.gridUnique, grid.container, totiTranslations.pages.loadNext, false, function() {
		    		grid.addNextPage();
		    	});
		    }
			totiDisplay.fadeOut();
		})
		.catch(function(xhr) {
			console.error(xhr);
			totiDisplay.fadeOut();
			totiDisplay.flash("error", totiTranslations.gridMessages.loadingError, xhr);
		});
	}

}