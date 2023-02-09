/* TOTI Grid default template version 0.1.1 */
/* can be overriden */
var totiGridDefaultTemplate = {
	getContainer: function(parentSelector, gridUnique) {
		var table = document.createElement("table");
		table.setAttribute("style", "font-family: Arial, Helvetica, sans-serif;  margin-bottom: 0.5em;");
		document.querySelector(parentSelector).appendChild(table);
 
		/*****************/
		var head = document.createElement("thead");
		head.setAttribute("style", "background-color: #B0C4DE");
		var headSortingRow = document.createElement("tr");
		headSortingRow.setAttribute("id", "toti-grid-sorting_" + gridUnique);
		head.appendChild(headSortingRow);

		var headFilterRow = document.createElement("tr");
		headFilterRow.setAttribute("id", "toti-grid-filtering_" + gridUnique);
		head.appendChild(headFilterRow);
		table.appendChild(head);
		/*****************/
		var body = document.createElement("tbody");
		body.setAttribute("id", "toti-grid-rows");
		table.appendChild(body);
		/*****************/
		var footer = document.createElement("tfoot");
		var footerRow = document.createElement('tr');
		footer.appendChild(footerRow);
		var footerCell = document.createElement('td');
		footerCell.setAttribute("colspan", 100);
		footerRow.appendChild(footerCell);
		table.appendChild(footer);

		var groupActions = document.createElement("div");
		groupActions.setAttribute('style', 'display: inline-block');
		groupActions.setAttribute("id", "toti-grid-group-action");
		footerCell.appendChild(groupActions);

		var pageIndex = document.createElement("div");
		pageIndex.setAttribute('style', 'display: inline-block');
		pageIndex.setAttribute("id", "toti-grid-page-index");
		footerCell.appendChild(pageIndex);

		var pageSize = document.createElement("div");
		pageSize.setAttribute('style', 'display: inline-block');
		pageSize.setAttribute("id", "toti-grid-page-size");
		footerCell.appendChild(pageSize);

		var autoRefresh = document.createElement("div");
		autoRefresh.setAttribute('style', 'display: inline-block');
		autoRefresh.setAttribute("id", "toti-grid-autorefresh");
		footerCell.appendChild(autoRefresh);
		/*****************/
		var caption = document.createElement("caption");
		caption.setAttribute("id", "toti-grid-caption");
		table.appendChild(caption);

		return table;
	},
	setTitle: function(gridUnique, container, name, title) {
		var th = document.createElement('th');
		th.style.padding = "0.5em";
		th.style.cursor = "pointer";

		th.setAttribute("id", "toti-grid-title_" + gridUnique + "_" + name);
		th.innerText = title;
		container.querySelector("#toti-grid-sorting_" + gridUnique).appendChild(th);
	},
	addSorting: function(gridUnique, container, name, grid, defautlValue) {
		var th = container.querySelector("#toti-grid-title_" + gridUnique + "_" + name);

		var imgUp = document.createElement("img");
		imgUp.setAttribute("src", totiImages.arrowUp);
		imgUp.setAttribute("alt", "");
		imgUp.setAttribute("width", 15);
		imgUp.setAttribute("id", "toti-grid-sorting-asc");
		imgUp.style.position = "absolute";

		var imgDown = document.createElement("img");
		imgDown.setAttribute("src", totiImages.arrowDown);
		imgDown.setAttribute("alt", "");
		imgDown.setAttribute("width", 15);
		imgDown.setAttribute("id", "toti-grid-sorting-desc");
		imgDown.style.position = "absolute";
		imgDown.style.top = "10px";

		var div = document.createElement("div");
		div.style.display = "inline-block";
		div.style.position = "relative";
		div.style["padding-left"] = "5px";
		div.style["padding-right"] = "5px";
		div.style.width = "15px";
		div.style.top = "-1em";

		div.appendChild(imgUp);
		div.appendChild(imgDown);
		function selectArrows(sortOrder) {
			switch(sortOrder) {
				case null:
					imgDown.style.display = "inline";
					imgUp.style.display = "inline";
					break;
				case false:
					imgDown.style.display = "inline";
					imgUp.style.display = "none";
					break;
				case true:
					imgDown.style.display = "none";
					imgUp.style.display = "inline";
					break;
			}
		}
		selectArrows(defautlValue);
		th.addEventListener("click", function(event) {
			selectArrows(grid.nextSort(name));
		});
		th.appendChild(div);
	},
	addFilter: function(gridUnique, container, name, grid, input) {
		var th = document.createElement('th');
		th.style.padding = "0.5em";
		if (input !== null) {
			th.appendChild(input);
		}
		container.querySelector("#toti-grid-filtering_" + gridUnique).appendChild(th);
	},
	addCheckbox: function(gridUnique, container, checkbox) {
		var th = document.createElement('th');
		th.appendChild(checkbox);
		container.querySelector("#toti-grid-filtering_" + gridUnique).appendChild(th);
	},
	addButtons: function(gridUnique, container, name, buttons) {
		var th = document.createElement('th');
		th.append(...buttons);
		container.querySelector("#toti-grid-filtering_" + gridUnique).appendChild(th);
	},
	addPageSize: function(gridUnique, container, sizes, selectedSize, grid) {
		var options = [];
		sizes.forEach(function(size) {
			options.push({
				value: size,
				title: size
			});
		});
		var select = totiControl.input({
			"type": "select",
			options: options,
			value: selectedSize
		});
		container.querySelector('#toti-grid-page-size').appendChild(select);
		select.addEventListener("change", function() {
			grid.setPageSize(select.value);
		});
		return (value)=>{
			select.value = value;
		};
	},
	setCaption: function(gridUnique, container, displayed, total, text) {
		var caption = container.querySelector('#toti-grid-caption');
		caption.innerText = text;
	},
	clearBody: function(gridUnique, container) {
		container.querySelector("#toti-grid-rows").innerHTML = "";
	},
	addRow: function(gridUnique, container) {
		var row = document.createElement('tr');
		row.setAttribute("class", 'toti-grid-row');
		container.querySelector("#toti-grid-rows").appendChild(row);
		return row;
	},
	setRowSelected: function(gridUnique, container, row, values) {
		var color = "lightgreen";
		if (row.classList.contains('toti-grid-selected')) {
			row.style['background-color'] = "white";
			row.classList.remove('toti-grid-selected');
			return null;
		} else {
			container.querySelectorAll('.toti-grid-selected').forEach(function(old) {
				old.classList.remove('toti-grid-selected');
				old.style['background-color'] = "white";
			});
			row.classList.add('toti-grid-selected');
			row.style['background-color'] = "lightgreen";
			return values;
		}
	},
	/**
	* Mode:
	*  0: value
	*  1: checkbox
	*  2: list of buttons
	*/
	addCell: function(gridUnique, container, rowContainer, name, value, mode) {
		var cell = document.createElement('td');
		cell.setAttribute("style", "padding: 0.5em;");
		switch(mode) {
			case 0:
				cell.innerText = value;
				break;
			case 1:
				cell.appendChild(value);
				break;
			case 2:
				cell.append(...value);
				break;
		}
		rowContainer.appendChild(cell);
	},
	addExpand: function(gridUnique, container, rowContainer, name, isExpanded, level, showElements, hideELements) {
		var cell = document.createElement('td');
		rowContainer.appendChild(cell);
		
		var div = document.createElement("div");
		div.style['margin-left'] = (level * 0.9) + "em";
		cell.appendChild(div);
		
		if (isExpanded === null) {
			if (level > 0) {
				var img = document.createElement("img");
				img.src = totiImages.dot;
				img.width = "10";
				div.appendChild(img);
			}
			return;
		}
		cell.setAttribute("toti-expanded", !isExpanded);
		
		function show() {
			if (cell.getAttribute("toti-expanded") === "true") {
				cell.setAttribute("toti-expanded", "false");
				hideELements();
				cell.querySelector("#toti-expand").style.display = "none";
				cell.querySelector("#toti-collapse").style.display = "inline";
			} else {
				cell.setAttribute("toti-expanded", "true");
				showElements();
				cell.querySelector("#toti-expand").style.display = "inline";
				cell.querySelector("#toti-collapse").style.display = "none";
			}
		}
		function createImage(src, id) {
			var img = document.createElement("img");
			img.src = src;
			img.width = "25";
			img.setAttribute("id", id);
			div.appendChild(img);
			img.onclick = show;
			return img;
		}
		createImage(totiImages.arrowDown, "toti-expand");
		createImage(totiImages.arrowRight, "toti-collapse");
		show();
	},
	addActions: function(gridUnique, container, actions, onSelect) {
		var options = [];
		options.push({
			title: totiTranslations.actions.select,
			value: null
		});
		actions.forEach(function(action, index) {
			action.value = index;
			options.push(action);
		});
		var select = totiControl.input({
			options: options,
			type: "select"
		});
		var execute = totiControl.button({
			value: totiTranslations.actions.execute,
			style: "font-weight: bold; border-radius: 4px; border: 3px solid #f44336; background-color: #f8d7da;"
		});
		execute.addEventListener("click", function(event) {
			event.preventDefault();
			if (select.value === '') {
				return false;
			}
			return onSelect(event, actions[select.value]);
		});
		container.querySelector('#toti-grid-group-action').appendChild(select);
		container.querySelector('#toti-grid-group-action').appendChild(execute);
	},
	addPageButton: function(gridUnique, container, title, isActual, onClick) {
		var button = document.createElement('button');
		button.innerText = title;
		button.addEventListener("click", onClick);
		button.classList.add("toti-grid-page-buttons");

		button.style["font-weight"] = "bold";
	    button.style['border-radius'] = "4px";
	    button.style.border = "3px solid #B0C4DE";
	    button.style['background-color'] = "#ffffff";
		if (isActual) {
			button.style.padding = "0.5em";
   			button.style['background-color'] = "#E0FFFF";
		}
		button.addEventListener("mouseenter", function() {
			button.style['background-color'] = "#E0FFFF";
		});
		button.addEventListener("mouseleave", function() {
		    button.style['background-color'] = "#ffffff";
		});
		container.querySelector('#toti-grid-page-index').appendChild(button);
	},
	clearPageButtons: function(gridUnique, container) {
		container.querySelector('#toti-grid-page-index').innerHTML = "";
	}
};