/* TOTI Form default template version 0.0.0 */
/* can be overriden */
var totiFormDefaultTemplate = {
	getContainer: function(selector, formUnique, editable) {
		var form = document.createElement(editable ? "form" : "div");
		document.querySelector(selector).appendChild(form);

		if (editable) {
			var errors = document.createElement("div");
			errors.setAttribute("toti-form-error", "form");
			form.appendChild(errors);

			var hiddens = document.createElement("div");
			hiddens.setAttribute("id", "toti-form-hiddens");
			form.appendChild(hiddens);
		}
		
		var table = document.createElement("table");
		var body = document.createElement("tbody");
		body.setAttribute("id", "toti-form-inputs-" + formUnique);
		table.appendChild(body);

		var buttons1 = document.createElement('tr');
		var buttons2 = document.createElement('td');
		buttons2.setAttribute("id", "toti-form-buttons-" + formUnique);
		buttons2.setAttribute("colspan", 3);
		buttons1.appendChild(buttons2);

		form.appendChild(table);
		form.appendChild(buttons1);
		return form;
	},
	setFormAttribute: function(formUnique, container, name, value) {
		container.setAttribute(name, value);
	},
	addInput: function(formUnique, container, name, label, input, removeFunc) {
		var row = document.createElement("tr");

		var labelCell = document.createElement('td');
		var labelContainer = document.createElement('label');
		if (label !== undefined) {
			labelContainer.innerText = label;
		}
		labelCell.appendChild(labelContainer);

		var inputCell = document.createElement('td');
		inputCell.appendChild(input);

		var errorCell = document.createElement('td');
		errorCell.setAttribute("toti-form-error", name);

		row.appendChild(labelCell);
		row.appendChild(inputCell);
		if (removeFunc !== null) {
			var removeCell = document.createElement("td");
			var removeButton = document.createElement("button");
			removeButton.addEventListener("click", function(e) {
				e.preventDefault();
				removeFunc(row);
			});
			removeButton.innerText = totiTranslations.dynamicList.remove;
			removeCell.appendChild(removeButton);
			row.appendChild(removeCell);
		}
		row.appendChild(errorCell);


		totiFormDefaultTemplate.addToContainer(formUnique, container, row);
	},
	addHidden: function(formUnique, container, input, removeFunc) {
		container.querySelector('#toti-form-hiddens').appendChild(input);
	},
	addControl: function(formUnique, container, name, button, removeFunc) {
		container.querySelector("#toti-form-buttons-" + formUnique).appendChild(button);
	},
	addRow: function(formUnique, container, name, originType, label, value) {
		var row = document.createElement("tr");

		var labelCell = document.createElement('td');
		labelCell.innerText = label;

		var inputCell = document.createElement('td');
		inputCell.setAttribute('name', name);
		inputCell.setAttribute('originType', originType);

		inputCell.bind = function(val) {
			inputCell.innerText = val;
			if (originType === 'color') {
				inputCell.style['background-color'] = val;
			}
		};
		if (value !== null && value !== undefined) {
			inputCell.bind(value);
		}

		row.appendChild(labelCell);
		row.appendChild(inputCell);

		totiFormDefaultTemplate.addToContainer(formUnique, container, row);
	},
	addOptionRow: function(formUnique, container, name, originType, label, options, val) {
		var row = document.createElement("tr");

		var labelCell = document.createElement('td');
		labelCell.innerText = label;

		var inputCell = document.createElement('td');
		inputCell.setAttribute('name', name);
		inputCell.setAttribute('originType', originType);

		Object.values(options).forEach(function(option) {
			var optionCell = document.createElement("div");
			optionCell.setAttribute("value", option.value);
			optionCell.innerText = option.title;
			if (val === null || val !== option.value) {
				optionCell.style.display = "none";
			}
			inputCell.appendChild(optionCell);
		});
		inputCell.bind = function(value) {
			var subElement = inputCell.querySelector('[value="' + value + '"]');
			if (subElement !== null) {
				subElement.style.display = "inline-block";
			}
		};

		row.appendChild(labelCell);
		row.appendChild(inputCell);

		totiFormDefaultTemplate.addToContainer(formUnique, container, row);
	},
	addPromisedRow: function(formUnique, container, name, originType, label, promise) {
		var row = document.createElement("tr");

		var labelCell = document.createElement('td');
		labelCell.innerText = label;

		var inputCell = document.createElement('td');
		inputCell.setAttribute('name', name);
		inputCell.setAttribute('originType', originType);

		var afterLoadOptions = promise.then(function(options) {
			var result = {};
			Object.values(options).forEach(function(option) {
				var optionCell = document.createElement("div");
				optionCell.setAttribute("value", option.value);
				optionCell.innerText = option.title;
				optionCell.style.display = "none";

				result[option.value] = optionCell;

				inputCell.appendChild(optionCell);
			});
			return result;
		});

		inputCell.bind = function(value) {
			afterLoadOptions.then(function(options) {
				if (options.hasOwnProperty(value)) {
					options[value].style.display = "inline-block";
				}
			});
		};

		row.appendChild(labelCell);
		row.appendChild(inputCell);

		totiFormDefaultTemplate.addToContainer(formUnique, container, row);
	},
	addToContainer: function(formUnique, container, element) {
		var target = container.querySelector('#toti-form-inputs-' + formUnique);
		if (target === null) {
			container.appendChild(element);
		} else {
			target.appendChild(element);
		}
	},
	getDynamicContainer: function(formUnique, container, name, title, addItem) {
		var dynamic = document.createElement("tr");
		totiFormDefaultTemplate.addToContainer(formUnique, container, dynamic);
		
		var cell = document.createElement('td');
		cell.setAttribute("colspan", 4);
		dynamic.appendChild(cell);

		var table = document.createElement("fieldset");
		cell.appendChild(table);

		var legend = document.createElement("legend");
		table.appendChild(legend);

		if (title !== undefined) {
			var titleCell = document.createElement('span');
			titleCell.innerText = title;
			legend.appendChild(titleCell);
		}
        if (addItem !== null) {
			var addButton = document.createElement("button");
			addButton.addEventListener("click", function(e) {
				e.preventDefault();
				addItem();
			});
			addButton.innerText = totiTranslations.dynamicList.add;
			legend.appendChild(addButton);
        }
		
		return table;
	},
	getDynamicRow: function(formUnique, container, dynamicContainer, name, remove) {
		var container = document.createElement('table');
		dynamicContainer.appendChild(container);

        if (remove) {
			var removeButton = document.createElement("button");
			removeButton.addEventListener("click", function(e) {
				e.preventDefault();
				remove();
			});
			removeButton.innerText = totiTranslations.dynamicList.remove;

			var cell1 = document.createElement('td');
			cell1.setAttribute('colspan', 3);
			var cell2 = document.createElement('td');
			cell2.appendChild(removeButton);

			var row = document.createElement('tr');
			container.appendChild(cell1);
			container.appendChild(cell2);
			container.appendChild(row);
        }
		return container;
	},
	createErrorList: function(errors) {
		var ol = document.createElement("ul");
		errors.forEach(function(item) {
			var li = document.createElement("li");
			li.innerText = item;
			ol.appendChild(li);
		});
		return ol;
	}
};