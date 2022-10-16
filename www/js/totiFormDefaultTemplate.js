/* TOTI Form default template version 0.0.0 */
/* can be overriden */
var totiFormDefaultTemplate = {
	getContainer: function(selector, formUnique, editable) {
		var form = document.createElement(editable ? "form" : "div");
		document.querySelector(selector).appendChild(form);

		if (editable) {
			var errors = document.createElement("div");
			errors.setAttribute("id", "toti-form-errors");
			errors.setAttribute("class", "toti-form-errors");
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
	addInput: function(formUnique, container, name, label, input) {
		var row = document.createElement("tr");

		var labelCell = document.createElement('td');
		var labelContainer = document.createElement('label');
		labelContainer.innerText = label;
		labelCell.appendChild(labelContainer);

		var inputCell = document.createElement('td');
		inputCell.appendChild(input);

		var errorCell = document.createElement('td');
		errorCell.setAttribute("id", name);
		errorCell.setAttribute("class", "toti-form-errors");

		row.appendChild(labelCell);
		row.appendChild(inputCell);
		row.appendChild(errorCell);

		container.querySelector('#toti-form-inputs-' + formUnique).appendChild(row);
	},
	addHidden: function(formUnique, container, input) {
		container.querySelector('#toti-form-hiddens').appendChild(input);
	},
	addControl: function(formUnique, container, name, button) {
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

		container.querySelector('#toti-form-inputs-' + formUnique).appendChild(row);
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

		container.querySelector('#toti-form-inputs-' + formUnique).appendChild(row);
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

		container.querySelector('#toti-form-inputs-' + formUnique).appendChild(row);
	}
	
};