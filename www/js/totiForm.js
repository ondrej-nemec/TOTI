/* TOTI Form version 0.0.9 */
class TotiForm {

	constructor(config) {
		this.dynamic = {};
		this.config = config;
	}

	init(elementIdentifier, uniqueName) {
		var object = this;
		document.addEventListener("DOMContentLoaded", function(event) { 
			document.querySelector(elementIdentifier).appendChild(
				object.create(uniqueName, document.querySelector(elementIdentifier), object.config.editable)
			);
			if (object.config.hasOwnProperty("afterPrint")) {
				totiUtils.execute(object.config.afterPrint);
			}
			if (object.config.hasOwnProperty('bind')) {
				object.bindUrl(uniqueName, object.config.bind);
			}
		});
	}

	create(uniqueName, element, editable) {
		var form;
		if (editable) {
			form = document.createElement("form");
			form.setAttribute("id", uniqueName);
			form.setAttribute("action", this.config.action);
			form.setAttribute("method", this.config.method);
		} else {
			form = document.createElement("div");
			form.setAttribute("id", uniqueName);
		}
		var useTemplate = false;
		var container;
		if (element.innerHTML.length === 0) {
			container = document.createElement("table");

			var errors = document.createElement("div");
			errors.setAttribute("id", uniqueName + "-errors-form");
			errors.appendChild(document.createElement("span"));
			form.appendChild(errors);
		} else {
			useTemplate = true;
			container = document.createElement("div");
			container.innerHTML = element.innerHTML;
			element.innerHTML = "";
		}
		form.appendChild(container);
		
		this.iterateFields(uniqueName, this.config.fields, form, container, editable, useTemplate);
		return form;
	}

	iterateFields(uniqueName, fields, form, container, editable, useTemplate, parent = null, listPosition = null) {
		for (const[index, field] of Object.entries(fields)) {
			if (parent !== null) {
				field.id = (parent.id === null ? "" : parent.id + "-") + field.id;
				if (!field.hasOwnProperty("name") || field.name === "") {
					field.id += "_" + index;
				}
				field.name = parent.name + "[" + (field.hasOwnProperty("name") ? field.name : "") + "]";
			}
			if (listPosition !== null && field.hasOwnProperty("title")) {
				field.title = field.title.replace("{i}", listPosition);
			}
			if (field.type === "dynamic") {
				var template = this.createInputArea(uniqueName, field, index, editable, useTemplate, form, container);
				field.template = template;
				var object = this;
				var dynamicCount = "dynamiccount";
				var addButton = template.querySelector("[name='add']");
				if (addButton !== null) {
					if (editable) {
						addButton.setAttribute(
							dynamicCount, 
							template.querySelectorAll('[name="toti-list-item-' + field.name + '"]').length
						);
						addButton.onclick = function() {
							var count = parseInt(addButton.getAttribute(dynamicCount));
							object.dynamic[field.name](count, field.name)
						};
					} else {
						addButton.style.display = "none";
					}
				}
				this.dynamic[field.name] = function(position, inputName) {
					addButton.setAttribute(dynamicCount, parseInt(addButton.getAttribute(dynamicCount))+ 1);
					var itemTemplate = field.template;
					var removeFunc = null;
					if (useTemplate) {
						itemTemplate = document.createElement("div");
						itemTemplate.setAttribute("name", "toti-list-item-" + field.name);
						var pattern = template.querySelector('template[name="pattern"]');
						itemTemplate.innerHTML = pattern.innerHTML;

						Array.prototype.forEach.call(
							itemTemplate.getElementsByClassName("dynamic-container-part"),
							function(item, index) {
								item.setAttribute("name", item.getAttribute("name").replace("%s", position));
							}
						);

						template.appendChild(itemTemplate);
						removeFunc = function () {
							itemTemplate.parentNode.removeChild(itemTemplate);
						};
						var removeButton = itemTemplate.querySelector("[name='remove']");
						if (removeButton !== null) {
							if (editable) {
								removeButton.onclick = function() {
									itemTemplate.parentNode.removeChild(itemTemplate);
								};
							} else {
								removeButton.style.display = "none";
							}
						}
					} else if (editable && field.removeButton) {
						var spanIdent = document.createElement("span");
						spanIdent.setAttribute("name", "toti-list-item-" + field.name);

						var removeButton = document.createElement("div");
						removeButton.setAttribute("name", "remove");
						removeButton.innerText = totiTranslations.formButtons.remove;

						removeButton.onclick = function() {
							Array.prototype.forEach.call(
								document.querySelectorAll("[name^='" + inputName + "[" + position + "']"),
								function(item) {
									itemTemplate.removeChild(item.parentNode.parentNode);
								}
							);
							itemTemplate.removeChild(removeButton);
							itemTemplate.removeChild(spanIdent);
						};

						itemTemplate.appendChild(spanIdent);
						itemTemplate.appendChild(removeButton);
					}
					object.iterateFields(
						uniqueName,
						totiUtils.clone(field.fields),
						form,
						itemTemplate,
						editable, useTemplate,
						{
							name: field.name + "[" + position + "]",
							id: field.id + "_" + position
						},
						position
					);
				};
			} else if (field.type === "list") {
				this.iterateFields(uniqueName, field.fields, form, container, editable, useTemplate, {
					name: field.name,
					id: field.id
				});
			} else {
				this.createInputArea(uniqueName, field, index, editable, useTemplate, form, container);
			}
		}
	}

	createInputArea(uniqueName, field, index, editable, useTemplate, form, container) {
		field.id = uniqueName + "-" + field.id;
		field.form = uniqueName;
		var label = this.createLabel(field);
		var input = this.createInput(uniqueName, field, editable, form);

		var error = document.createElement("div");
		error.setAttribute("id", uniqueName + '-errors-' + field.name);
		if (useTemplate) {
			return this.getInputTemplate(field, container, label, input, error, index);
		} else {
			return this.createInputTemplate(field, container, label, input, error);
		}	
	}

	getInputTemplate(field, container, label, input, error, index) {
		var fieldName = field.name;
		fieldName = fieldName.replaceAll("[", "\\[").replaceAll("]", "\\]");
		if (field.originType === "dynamic") {
			var defaultDiv = document.createElement("div");
			var section = container.querySelector('[name="' + fieldName + '-container"]');
			if (section === null) {
				return defaultDiv;
			}
			return section;
		}
		
		var selectAndSetElement = function(querySelector, child) {
			var elements = container.querySelectorAll(querySelector);
			if (elements.length > 0) {
				var element = (elements.length === 1) ? elements[0] : elements[index];
				if (element !== null && child !== null) {
					element.appendChild(child);
				}
			}
		};
		selectAndSetElement("[name='form-label-" + fieldName + "']", label);
		selectAndSetElement("[name='form-input-" + fieldName + "']", input);
		selectAndSetElement("[name='form-error-" + fieldName + "']", error);
		return container;
	}

	createInputTemplate(field, container, label, input, error) {
		if (field.originType === "dynamic") {
			var tr = document.createElement("tr");
			var td = document.createElement("td");
			tr.appendChild(td);
			container.appendChild(tr);
			td.setAttribute("colspan", 3);
			var table = document.createElement("table");
			table.setAttribute("name", "toti-list-item-" + field.name);
			
			var fieldset = document.createElement("fieldset");
			if (field.hasOwnProperty("title")) {
				var legend = document.createElement("legend");
				legend.innerText = field.title;
				fieldset.appendChild(legend);
			}
			fieldset.appendChild(table);

			td.appendChild(fieldset);
			
			var addTr = document.createElement("tr");
			var addTd1 = document.createElement("td");
			var addTd2 = document.createElement("td");
			addTr.appendChild(addTd1);			
			addTr.appendChild(addTd2);
			table.appendChild(addTr);

			if (field.addButton) {
				var addButton = document.createElement("div");
				addButton.setAttribute("name", "add");
				addButton.innerText = totiTranslations.formButtons.add;
				addTd2.appendChild(addButton);
			}
			
			return table;
		}
		var first = document.createElement("td");
		first.setAttribute("class", 'toti-form-label');
		if (label !== null) {
			first.appendChild(label);
		}
		var second = document.createElement("td");
		second.setAttribute("class", 'toti-form-input');
		second.appendChild(input);

		var third = document.createElement("td");
		third.setAttribute("class", 'toti-form-error');
		third.appendChild(error);

		var row = document.createElement("tr");
		row.appendChild(first);
		row.appendChild(second);
		row.appendChild(third);
		container.appendChild(row);
		return row;
	}

	createLabel(field) {
		if (field.hasOwnProperty('title')) {
			return totiControl.label(field.id, field.title, {
				id: field.id + "-label"
			});
		}
		return null;
	}

	createInput(uniqueName, field, editable, form) {
		var input;
		if (!editable && field.type !== 'button') {
			field.originType = field.type;
			if (field.type === 'select') {
				totiControl.inputs.select(field); /* load select options if are*/
				input = this.printSelectFunc(field, 'options');
			} else if (field.type === 'radiolist') {
				input = this.printSelectFunc(field, 'radios');
			} else if (field.type === 'checkbox') {
				input = this.printSelectFunc(field, 'values');
			} else if (field.type !== 'submit' && field.type !== 'hidden' && field.type !== "reset" && field.type !== "image") {
				input = document.createElement("div");
				totiUtils.forEach(field, function(key, name) {
					if (key === "value") {
						input.innerText = name;
					} else {
						input.setAttribute(key, name);
					}
				});				
			} else {
				input = document.createElement('span');
			}
		} else {
			if (field.type === 'file') {
				form.setAttribute("enctype", "multipart/form-data");
			} else if (field.type === 'radiolist') {
				field.formName = uniqueName;
			}
			var type = field.type;
			input = totiControl.input(field);

			if (type === 'submit' || type === 'image') {
				input.onclick = this.getSubmit(uniqueName, input);
			} else if (type === 'button') {
				var onClick = totiControl.getAction({
					href: field.href,
					method: field.method,
					async: field.ajax,
					params: field.requestParams,
					submitConfirmation: function() {
						if (field.hasOwnProperty('confirmation')) {
							return totiDisplay.confirm(field.confirmation, row);
						}
						return true;
					}/*,
					onSuccess: "",
					onFailure: ""*/
				});
				input.onclick = onClick;
			}
		}
		return input;
	}

	printSelectFunc(field, optionsName) {
		var input = document.createElement('div');
		var options = field[optionsName];
		delete field[optionsName];
		totiUtils.forEach(field, function(key, name) {
			input.setAttribute(key, name);
		});
		var withOption = function(i, option) {
			var span = document.createElement('span');
			span.setAttribute("value", option.value);
			span.innerText = option.title;
			if (field.value !== option.value) {
				span.style.display = "none";
			}
			input.appendChild(span);
		};
		totiUtils.forEach(options, withOption);
		return input;
	}

	getSubmit(uniqueName, submit) {
		return function(event) {
			event.preventDefault();
			Array.prototype.forEach.call(document.getElementsByClassName('error-list'), function(el) {
			    el.remove();
			});

			var form = document.getElementById(uniqueName);
			if (!form.reportValidity()) {
				return false;
			}
			var data = new FormData(); /* if parameter is form, all not disabled params are added */
			Array.prototype.forEach.call(form.elements, function(input) {
				var type = input.getAttribute("type");
				var name = input.getAttribute("name");
				/* TODO need configuration - can be disabled, but send, can be enabled but not send */
				if (input.getAttribute("disabled") !== null) {
					return;
				}
				if (name === null || input.getAttribute("exclude") !== null) {
					return;
				}
				/******/
				if (type === "datetime-local") {
					value = input.value;
					value = value.replace("T", " ");
					data.append(name, value);
				} else if (type === "submit" || type === "button" || type === "reset" || type === "image") {
					/* ignored*/
				} else if (type === "radio") {
					if (input.checked) {
						data.append(name, input.value);
					}
				} else if (type === "checkbox") {
					data.append(name, input.checked);
				} else if (type === "file") {
					if (input.files.length > 0) {
						data.append(name, input.files[0]);
					}
				} else {
					data.append(name, input.value);
				}
			});		
			var submitConfirmation = function() {
				if (submit.hasOwnProperty('confirmation')) {
					return totiDisplay.confirm(submit.confirmation);
				}
				return true;
			};
			if (submitConfirmation !== null && !submitConfirmation(data)) {
				event.preventDefault();
				return false;
			}
			if (submit.getAttribute("ajax")) {
				event.preventDefault();
				var header = totiLoad.getHeaders();
				if (form.getAttribute("enctype") !== null) {
					header.enctype = form.getAttribute("enctype");
				}
				totiLoad.async(
					form.getAttribute("action"), 
					form.getAttribute("method"), 
					data, 
					header, 
					function(response) {
						if (submit.getAttribute("onSuccess") != null) {
							window[submit.getAttribute("onSuccess")](response, submit, form);
						} else {
							totiDisplay.flash('success', response.message);
						}
						if (submit.getAttribute("redirect") != null) {
							totiDisplay.storedFlash('success', response.message);
							window.location = submit.getAttribute("redirect").replace("{id}", response.id);
						}
					}, 
					function(xhr) {
						if (xhr.status === 400) {
							for (const[key, list] of Object.entries(JSON.parse(xhr.responseText))) {
								var ol = document.createElement("ul");
								ol.setAttribute("class", "error-list");
								list.forEach(function(item) {
									var li = document.createElement("li");
									li.innerText = item;
									ol.appendChild(li);
								});
								var elementId = key.replaceAll("[", "\\[").replaceAll("]", "\\]");
								var elementIdentifiers = elementId.split(":");
								
								if (elementIdentifiers.length > 1) {
									elementId = elementId.replace(elementIdentifiers[0] + ":", "");
									document
										.querySelectorAll('#' + uniqueName + '-errors-' + elementId + '')[elementIdentifiers[0]]
										.appendChild(ol);
								} else {
									document.querySelector('#' + uniqueName + '-errors-' + elementId + '').appendChild(ol);
								}
							}
						} else if (submit.getAttribute("onFailure") != null) {
							window[submit.getAttribute("onFailure")](xhr, submit, form);
						} else {
							totiDisplay.flash('error', totiTranslations.formMessages.saveError);
						}
					}
				);
				return false;
			}
			return true;
		};
	}

	/**
	bind: url, method, jsonObject params, String beforeBind(optional), String afterBind(optional), String onFailure(optional)
	*/
	bindUrl(formId, bind) {
		var object = this;
		totiLoad.async(
			bind.url, 
			bind.method, 
			bind.params,
			totiLoad.getHeaders(), 
			function(values) {
				var beforeBind = null;
				var afterBind = null;
				if (bind.hasOwnProperty("beforeBind")) {
					beforeBind = bind.beforeBind;
				}
				if (bind.hasOwnProperty("afterBind")) {
					afterBind = bind.afterBind;
				};
				object.bind(formId, values, beforeBind, afterBind);
			}, 
			function(xhr) {
				if (bind.hasOwnProperty('onFailure')) {
					window[bind.onFailure](xhr);
				} else {
					totiDisplay.flash('error', totiTranslations.formMessages.bindError);
				}
			}
		);
	}
	bind(formId, values, beforeBind, afterBind) {
		totiUtils.execute(beforeBind, [values]);
		var form  = document.getElementById(formId);
		this.bindValues(values, form);
		totiUtils.execute(afterBind, [values]);
	}

	bindValues(values, form, createKey = null, originKey = null) {
		for (const[k, val] of Object.entries(values)) {
			var addedNewDynamic = false;
			if (originKey !== null && this.dynamic[originKey] !== undefined) {
				this.dynamic[originKey](k, originKey);
				addedNewDynamic = true;
			}
			var key = createKey === null ? k : createKey(k, addedNewDynamic);
			var value = val;
			if (value !== null && typeof value === "object") {
				this.bindValues(value, form, function(newKey, addedNewInput) {
					if (Array.isArray(value) && !addedNewInput) {
						return key + "[]";
					} else {
						return key + "[" + newKey + "]";
					}
				}, key);
				continue;
			}
			
			var elements = form.querySelectorAll('[name="' + key + '"]');
            if (elements.length === 0) {
                key = key + "[]";
                elements = form.querySelectorAll('[name="' + key + '"]');
            }

			var element;
			if (elements.length === 1) {
				element = elements[0];
			} else if (elements.length > 1) {
				/* more inputs with same key - list or radio */
				var radioCandidate = form.querySelector('[name="' + key + '"][type="radio"][value="' + value + '"]');
				var subListCandidate = elements[k];

				if (radioCandidate !== null) {
					element = radioCandidate;
				} else if (subListCandidate === undefined) {
					/* selecting logic for dynamic list */
					element = elements[elements.length - 1];
				} else {
					element = subListCandidate;
				}
			} else if (elements.length === 0) {
				continue;
			}
			
			if (element.type === undefined) { /* detail*/
				value = totiControl.parseValue(element.getAttribute("originType"), value);
				if (element.querySelector("span") != null) { /* select or radio list*/
					element.querySelectorAll("span").forEach(function(el) {
						el.style.display = "none";
					});
					if (value === null) {
						value = "";
					}
					var finalValueElement = element.querySelector("[value='" + value + "']");
					if (finalValueElement !== null) {
						finalValueElement.style.display = "block";
					}
				} else {
					element.innerText = value;
				}
			} else { /*form*/
                if (element.type === "datetime-local" && value !== null) {
                    value = value.replace(" ", "T");
                } else if (element.getAttribute("origintype") === "datetime-local" && value !== null) {
                    /* datetime-local renderer */
                    value = value.replace(" ", "T");
                }
                if (element.type === "checkbox") {
                    element.checked = value ? "checked" : false;
                } else if (element.type === "radio") {
                    form.querySelector('[name="' + key + '"][value="' + value + '"]').setAttribute("checked", true);
                } else {
                    element.value = value;
                }
                if (element.getAttribute("origintype") === "datetime-local" && element.type === "fieldset") {
                    element.onbind();
                }
            }
		}
	}
}