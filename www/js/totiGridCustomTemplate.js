/* TOTI Grid custom template version 0.0.0 */
var totiGridCustomTemplate = {
	getContainer: function(parentSelector, gridUnique) {
		return document.querySelector(parentSelector);;
	},
	setTitle: function(gridUnique, container, name, title) {
		var titleCell = container.querySelector('#toti-grid-title_' + name);
		if (titleCell !== null) {
			titleCell.innerText = title;
		}
	},
	addSorting: function(gridUnique, container, name, grid, defautlValue = null) {
		// TODO sorting je volitelný - nezobrazit, pokud neni nastaven
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
		container.app
	},
	addButtons: function(gridUnique, container, buttons) {},
	addPageSize: function(gridUnique, container, sizes, selectedSize, grid) {},	
	setCaption: function(gridUnique, container, displayed, total, text) {},
	addRow: function(gridUnique, container) {},
	setRowSelected: function(gridUnique, container, row) {},
	addCell: function(gridUnique, container, rowContainer, value, mode) {},
	clearBody: function(gridUnique, container) {},
	addActions: function(gridUnique, container, actions, onSelect) {},
	addPageButton: function(gridUnique, container, title, isActual, onClick) {},
	clearPageButtons: function(gridUnique, container) {}
};