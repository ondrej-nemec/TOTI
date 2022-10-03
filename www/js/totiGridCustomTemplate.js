/* TOTI Grid custom template version 0.0.0 */
var totiGridCustomTemplate = {
	getContainer: function(parentSelector, gridUnique) {
		return document.querySelector(parentSelector);
	},
	setTitle: function(gridUnique, container, name, title) {
		var titleCell = container.querySelector('#toti-grid-title_' + name);
		if (titleCell !== null) {
			titleCell.innerText = title;
		}
	},
	addSorting: function(gridUnique, container, name, grid, defautlValue = null) {
		container.querySelectorAll('[totiSort][name="' + name + '"]').forEach(function(element) {
			element.addEventListener("click", function() {
				var sortOrder = grid.nextSort(element.getAttribute("name"));
				if (element.getAttribute("totiSort") !== "") {
					totiUtils.execute(element.getAttribute("totiSort"), [element, sortOrder]);
				}
			});
		});
	},
	addFilter: function(gridUnique, container, name, grid, input) {
		var placeholder = container.querySelector('toti[filter="' + name + '"]');
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
		var placeholders = container.querySelectorAll('toti[checkbox]');
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
		var placeholder = container.querySelector('toti[buttons="' + name + '"]');
		if (placeholder !== null) {
			placeholder.parentNode.append(...buttons);
			placeholder.remove();
		}
	},
	addPageSize: function(gridUnique, container, sizes, selectedSize, grid) {},	
	setCaption: function(gridUnique, container, displayed, total, text) {},
	addRow: function(gridUnique, container) {
		var rowContainer = container.querySelector('template#toti-grid-row');
		var row = rowContainer.content.firstElementChild.cloneNode(true);
		rowContainer.parentNode.appendChild(row);
		return row;
	},
	setRowSelected: function(gridUnique, container, row) {},
	addCell: function(gridUnique, container, rowContainer, name, value, mode) {
		var content = rowContainer.querySelector('#toti-grid-cell-' + name);
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
	clearBody: function(gridUnique, container) {},
	addActions: function(gridUnique, container, actions, onSelect) {},
	addPageButton: function(gridUnique, container, title, isActual, onClick) {},
	clearPageButtons: function(gridUnique, container) {}
};