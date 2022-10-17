/* TOTI Form custom template version 0.0.0 */
var totiFormCustomTemplate = {
	getContainer: function(selector, formUnique, editable) {
		var form = document.createElement(editable ? "form" : "div");
		var container = document.querySelector(selector);
		while (container.childNodes.length > 0) {
		    form.appendChild(container.childNodes[0]);
		}
		container.appendChild(form);
		return form;
	},
	setFormAttribute: function(formUnique, container, name, value) {
		container.setAttribute(name, value);
	},
	addInput: function(formUnique, container, name, label, input) {
		var labelCell = document.createElement('label');
		labelCell.innerText = label;
		totiUtils.replaceElement(container, '[toti-form-label="' + name + '"]', labelCell, ['toti-form-label']);

		totiUtils.replaceElement(container, '[toti-form-input="' + name + '"]', input, ['toti-form-input']);
	},
	addHidden: function(formUnique, container, input) {
		container.appendChild(input);
	},
	addControl: function(formUnique, container, name, button) {
		totiUtils.replaceElement(container, '[toti-form-input="' + name + '"]', button, ['toti-form-input']);
	},
	addRow: function(formUnique, container, name, originType, label, value) {
		var labelCell = document.createElement('label');
		labelCell.innerText = label;
		totiUtils.replaceElement(container, '[toti-form-label="' + name + '"]', labelCell, ['toti-form-label']);

		var inputCell = container.querySelector('[toti-form-input="' + name + '"]');
		if (inputCell !== null) {
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
		}
	},
	addOptionRow: function(formUnique, container, name, originType, label, options, val) {
		var labelCell = document.createElement('label');
		labelCell.innerText = label;
		totiUtils.replaceElement(container, '[toti-form-label="' + name + '"]', labelCell, ['toti-form-label']);

		var inputCell = container.querySelector('[toti-form-input="' + name + '"]');
		if (inputCell !== null) {
			Object.values(options).forEach(function(option) {
				var optionCell = document.createElement("div");
				optionCell.setAttribute("value", option.value);
				optionCell.innerText = option.title;
				if (val === null || val !== option.value) {
					optionCell.style.display = "none";
				}
				inputCell.appendChild(optionCell);
			});
			inputCell.setAttribute('name', name);
			inputCell.setAttribute('originType', originType);
			inputCell.bind = function(val) {
			var subElement = inputCell.querySelector('[value="' + val + '"]');
				if (subElement !== null) {
					subElement.style.display = "inline-block";
				}
			};
			if (value !== null && value !== undefined) {
				inputCell.bind(value);
			}
		}
	},
	addPromisedRow: function(formUnique, container, name, originType, label, promise) {
		var labelCell = document.createElement('label');
		labelCell.innerText = label;
		totiUtils.replaceElement(container, '[toti-form-label="' + name + '"]', labelCell, ['toti-form-label']);

		var inputCell = container.querySelector('[toti-form-input="' + name + '"]');
		if (inputCell !== null) {
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
			inputCell.setAttribute('name', name);
			inputCell.setAttribute('originType', originType);
			inputCell.bind = function(value) {
				afterLoadOptions.then(function(options) {
					if (options.hasOwnProperty(value)) {
						options[value].style.display = "inline-block";
					}
				});
			};
			if (value !== null && value !== undefined) {
				inputCell.bind(value);
			}
		}
	}
	
};