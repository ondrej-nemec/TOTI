/* TOTI Grid custom template version 0.0.0 */
var totiGridCustomTemplate = {
	getContainer: function(parentSelector, gridUnique) {
		return document.querySelector(parentSelector);
	},
	setTitle: function(gridUnique, container, name, title) {
		var titleCell = container.querySelector('[toti-grid-title="' + name + '"]');
		if (titleCell !== null) {
			titleCell.innerText = title;
		}
	},
	addSorting: function(gridUnique, container, name, grid, defautlValue = null) {
		container.querySelectorAll('[toti-grid-sort][name="' + name + '"]').forEach(function(element) {
			element.addEventListener("click", function() {
				var sortOrder = grid.nextSort(element.getAttribute("name"));
				if (element.getAttribute("toti-grid-sort") !== "") {
					totiUtils.execute(element.getAttribute("toti-grid-sort"), [element, sortOrder]);
				}
			});
		});
	},
	addFilter: function(gridUnique, container, name, grid, input) {
		var placeholder = container.querySelector('[toti-grid-filter="' + name + '"]');
		if (placeholder !== null && input !== null) {
			var atts = placeholder.attributes;
			for (var i = 0; i < atts.length; i++){
				if (atts[i].nodeName !== 'filter') {
					input.setAttribute(atts[i].nodeName, atts[i].nodeValue);
				}
			}
			placeholder.parentNode.replaceChild(input, placeholder);
		}
	},
	addCheckbox: function(gridUnique, container, checkbox) {
		var placeholders = container.querySelectorAll('[toti-grid-checkbox]');
		placeholders.forEach(function(placeholder) {
			var atts = placeholder.attributes;
			for (var i = 0; i < atts.length; i++){
				if (atts[i].nodeName !== 'checkbox') {
					checkbox.setAttribute(atts[i].nodeName, atts[i].nodeValue);
				}
			}
			placeholder.parentNode.replaceChild(checkbox, placeholder);
		});
	},
	addButtons: function(gridUnique, container, name, buttons) {
		var placeholder = container.querySelector('[toti-grid-buttons="' + name + '"]');
		if (placeholder !== null) {
			placeholder.parentNode.append(...buttons);
			placeholder.remove();
		}
	},
	addPageSize: function(gridUnique, container, sizes, selectedSize, grid) {
		var element = container.querySelector('[toti-grid="page-size"]');
		/* TODO z nejakeho duvodu jeste neexistuje */
		if (element !== null) {
			element.addEventListener("change", function() {
				grid.setPageSize(element.value);
			});
			return (value)=>{
				element.value = value;
			};
		}
		return (value)=>{};
	},	
	setCaption: function(gridUnique, container, displayed, total, text) {
		var element = container.querySelector('[toti-grid=caption]');
		if (element !== null) {
			element.innerText = text;
		}
	},
	addRow: function(gridUnique, container) {
		var rowContainer = container.querySelector('[toti-grid="rows"]');
		var row = rowContainer.querySelector('template').content.firstElementChild.cloneNode(true);
		row.setAttribute("toti-grid", "row");
		rowContainer.appendChild(row);
		return row;
	},
	setRowSelected: function(gridUnique, container, row, values) {
		var color = container.getAttribute("toti-selected-row-color");
		if (color === null) {
			return null;
		}
		console.log(container);
		if (row.classList.contains('toti-grid-selected')) {
			row.style['background-color'] = null;
			row.classList.remove('toti-grid-selected');
			return null;
		} else {
			container.querySelectorAll('.toti-grid-selected').forEach(function(old) {
				old.classList.remove('toti-grid-selected');
				old.style['background-color'] = null;
			});
			row.classList.add('toti-grid-selected');
			row.style['background-color'] = color;
			return values;
		}
	},
	addCell: function(gridUnique, container, rowContainer, name, value, mode) {
		var content = rowContainer.querySelector('[toti-grid-cell="' + name + '"]');
		if (content === null) {
			return;
		}
		switch(mode) {
			case 0:
				content.innerText = value;
				break;
			case 1:
				content.appendChild(value);
				break;
			case 2:
				content.append(...value);
				break;
		}
	},
	clearBody: function(gridUnique, container) {
		var body = container.querySelector('[toti-grid="rows"]');
		body.querySelectorAll('[toti-grid=row]').forEach(function(row){
			row.remove();
		});
	},
	addActions: function(gridUnique, container, actions, onSelect) {
		/* TODO improve*/
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
		var placeholder = container.querySelector('#toti-grid-group-action');
		if (placeholder !== null) {
			placeholder.appendChild(select);
			placeholder.appendChild(execute);
		}
	},
	addPageButton: function(gridUnique, container, title, isActual, onClick) {
		var buttonContainer = container.querySelector('[toti-grid="page-buttons"]');
		var button = buttonContainer.querySelector('template').content.firstElementChild.cloneNode(true);

		var textHolder = button.querySelector('[toti-grid-page-button="title"]');
		if (textHolder !== null) {
			textHolder.innerText = title;
		} else if (button.getAttribute('toti-grid-page-button') === 'title') {
			button.innerText = title;
		}
		button.setAttribute("toti-grid", "page-button");
		button.addEventListener("click", onClick);
		if (isActual) {
			button.classList.add("active"); /*TODO improvement: configurable */
		}
		buttonContainer.appendChild(button);
	},
	clearPageButtons: function(gridUnique, container) {
		var element = container.querySelector('[toti-grid="page-buttons"]');
		element.querySelectorAll('[toti-grid="page-button"]').forEach(function(row){
			row.remove();
		});
	}
};