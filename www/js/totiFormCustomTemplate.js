/* TOTI Form custom template version 1.0.0 */
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
	addInput: function(formUnique, container, name, label, isRequired, input) {
		var labelCell = document.createElement('label');
		labelCell.innerText = label;
    if (isRequired === true) {
        var required = document.createElement('code');
        required.innerText = '*';
        required.style['font-size'] = "1.2em";
        required.style.cursor = "pointer";
        required.setAttribute('title', totiTranslations.formMessages.required);
        labelCell.appendChild(required);
    }
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
		if (label !== undefined) {
			labelCell.innerText = label;
		}
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
			inputCell.bind = function(value) {
				inputCell.querySelectorAll('[value]').forEach((c)=>{
					c.style.display = "none";
				});
				var subElement = inputCell.querySelector('[value="' + value + '"]');
				if (subElement !== null) {
					subElement.style.display = "inline-block";
				}
			};
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
	},
	getDynamicContainer: function(formUnique, container, parentContainer, deep, name, title, addItem) {
		var list = container.querySelectorAll('[toti-form-dynamic-container="' + name + '"]');
		if (list.length === 0) {
			return null;
		}
		var dynamicContainer = list[list.length-1];
		var addButton = container.querySelector('[toti-form-add-button="' + name + '"]');
		var titleCell = container.querySelector('[toti-form-dynamic-name="' + name + '"]');
		if (title !== undefined  && titleCell !== null) {
			titleCell.innerText = title;
		}
        if (addItem !== null && addButton !== null) {
			addButton.addEventListener("click", function(e) {
				e.preventDefault();
				addItem();
			});
        } else if (addButton !== null) {
        	addButton.remove();
        }
        return dynamicContainer;
	},
	getDynamicRow: function(formUnique, container, dynamicContainer, name, remove, position, templateName) {
		var template = container.querySelector('[toti-form-dynamic-template="' + templateName + '"]').cloneNode(true).content;

		function setName(selector) {
			dynamic.querySelectorAll('[' + selector + ']').forEach((el)=>{
				el.setAttribute(selector, name + (position === null ? "" : "[" + position + "]") + "[" + el.getAttribute(selector) + "]");
			});
		}
		if (template.children.length !== 1) {
			throw new Error("Dynamic row template can contains only one subelement");
		}
		var dynamic = template.children[0];
		setName('toti-form-input');
		setName('toti-form-label');
		setName('toti-form-error');
		dynamicContainer.appendChild(dynamic);		
		
		var removeButton = dynamic.querySelector('[toti-form-remove-button="' + name + '"]');
		if (remove !== null && removeButton !== null) {
			removeButton.addEventListener("click", function(e) {
				e.preventDefault();
				remove();
			});
		} else if (removeButton !== null) {
			removeButton.remove();
		}
		return dynamic;
	},
	createErrorList: function(errors) {
		var ol = document.createElement("ul");
		errors.forEach(function(item) {
			var li = document.createElement("li");
			li.innerText = item;
			ol.appendChild(li);
		});
		return ol;
	},
	removeUnusedElement: function(formUnique, container, name) {
         function removeElement(id) {
              var element = container.querySelector('[toti-form-' + id + '="' + name + '"]');
              if (element !== null) {
                  element.remove();
              }
         }
         removeElement('input');
         removeElement('label');
         removeElement('error');
     }
};