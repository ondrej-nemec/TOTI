/* TOTI Form version 0.0.28 */
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

			var errors = document.createElement("div");
            errors.setAttribute("id", uniqueName + "-errors-form");
            errors.appendChild(document.createElement("span"));
            var errorContainer = container.querySelector('[name="form-error-form"]');
            if (errorContainer !== null) {
				errorContainer.appendChild(errors);
            }
            
		}
		form.appendChild(container);
		
		this.iterateFields(uniqueName, this.config.fields, form, container, editable, useTemplate);
		return form;
	}

	iterateFields(uniqueName, fields, form, container, defaultEditable, useTemplate, parent = null, listPosition = null) {
		for (const[index, field] of Object.entries(fields)) {
			var editable = field.hasOwnProperty("editable") ? field.editable : defaultEditable;
			field.editable = editable;
			if (parent !== null) {
				field.id = (parent.id === null ? "" : parent.id + "-") + field.id;
				if (!field.hasOwnProperty("name") || field.name === "") {
					field.id += "_" + index;
				}
				var fieldName = parent.name;
                if (Object.keys(fields).length !== 1) {
                	fieldName += "[" + (field.hasOwnProperty("name") ? field.name : "") + "]";
                }
                field.name = fieldName;
			}
			if (listPosition !== null && field.hasOwnProperty("title")) {
				field.title = field.title.replace('{i}', listPosition);
			}
            if (parent !== null && parent.hasOwnProperty("depends")) {
				field.depends = parent.depends;
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
						addButton.setAttribute("input", field.id);
						addButton.onclick = function(addButtonEvent) {
							var count = parseInt(addButtonEvent.srcElement.getAttribute(dynamicCount));
							object.dynamic[field.name](count, field.name, addButtonEvent.srcElement);
						};
						addButton.style.cursor = "pointer";
					} else {
						addButton.style.display = "none";
					}
				}
				this.dynamic[field.name] = function(position, inputName, sourceInput = null) {
					if (sourceInput === null) {
						sourceInput = document.querySelector("[name='add'][input='" + uniqueName + "-id-" + inputName + "']");
					}
					if (sourceInput !== null) {
						sourceInput.setAttribute(dynamicCount, parseInt(sourceInput.getAttribute(dynamicCount))+ 1);
					}
					var removeFunc = function (removeButtonEvent) {
						var addButton = sourceInput;
						if (addButton === null) {
							addButton = document.querySelector('[name="add"][input="' + removeButtonEvent.srcElement.getAttribute("input") + '"]');
						}
						if (addButton === null) {
							return;
						}
						addButton.setAttribute(
                            dynamicCount,
							parseInt(addButton.getAttribute(dynamicCount)) - 1
                        );
					};
					var itemTemplate = field.template;
					if (useTemplate) {
						var pattern = itemTemplate.querySelector('template[name="pattern"]').content.cloneNode(true);
						if (pattern.childElementCount === 1) {
							itemTemplate = pattern.firstElementChild;
						} else {
							itemTemplate =  document.createElement("div");
							itemTemplate.append(...pattern.children);
						}
						itemTemplate.setAttribute("name", "toti-list-item-" + field.name);

						Array.prototype.forEach.call(
							itemTemplate.getElementsByClassName("dynamic-container-part"),
							function(item, index) {
								item.setAttribute(
									"name",
									item.getAttribute("name").replace("%p", inputName).replace("%s", position)
								);
							}
						);

						field.template.appendChild(itemTemplate);
						var removeButton = itemTemplate.querySelector("[name='remove']");
						if (removeButton !== null) {
							removeButton.style.cursor = "pointer";
							removeButton.setAttribute("input", field.id);
							removeButton.setAttribute(dynamicCount, position);
							if (editable) {
								removeButton.onclick = function(removeButtonEvent) {
									itemTemplate.parentNode.removeChild(itemTemplate);
									removeFunc(removeButtonEvent);
								};
							} else {
								removeButton.style.display = "none";
							}
						}
					} else if (editable && field.removeButton) {
						var spanIdent = document.createElement("span");
						spanIdent.setAttribute("name", "toti-list-item-" + field.name);

						var removeButton = document.createElement("div");
						removeButton.style.cursor = "pointer";
						removeButton.setAttribute("name", "remove");
						removeButton.innerText = totiTranslations.formButtons.remove;
						removeButton.setAttribute("input", field.id);
						removeButton.setAttribute(dynamicCount, position);

						removeButton.onclick = function(removeButtonEvent) {
							Array.prototype.forEach.call(
								document.querySelectorAll("[name^='" + inputName + "[" + position + "']"),
								function(item) {
									itemTemplate.removeChild(item.parentNode.parentNode);
								}
							);
							itemTemplate.removeChild(removeButton);
							itemTemplate.removeChild(spanIdent);
							removeFunc(removeButtonEvent);
						};

						itemTemplate.appendChild(spanIdent);
						itemTemplate.appendChild(removeButton);
					}
					var fields = totiUtils.clone(field.fields);
					object.iterateFields(
						uniqueName,
						fields,
						form,
						itemTemplate,
						editable, useTemplate,
						{
							name: field.name + "[" + position + "]",
							id: field.id + "_" + position,
							depends: depends.value
						},
						sdepends.hasOwnProperty("title") ? depends.title : (sourceInput === null ? null : sourceInput.getAttribute(dynamicCount))
					);
				};
                if (field.hasOwnProperty("load")) {
                    totiLoad.async(field.load.url, field.load.method, field.load.params, totiLoad.getHeaders(), function(loaded) {
                        loaded.forEach(function(layer, index) {
                            object.dynamic[field.name](layer.value, index, null, layer);
                        });
                    }, function(xhr) {console.log(xhr);}, false);
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
		error.style.display = "inline-block";
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
            if (elements.length > 0 && child !== null) {
                var element = elements[0];
                if (element !== null) {
                    for (var i = 0; i < element.attributes.length; i++) {
                        var attrib = element.attributes[i];
                        if (attrib.name === "p-id") {
                            console.warn("Custom id is not allowed.");
                            continue;
                        } else if (attrib.name === 'p-class' || attrib.name === 'class') {
                            attrib.value.split(" ").forEach(function(clazz) {
                                child.classList.add(clazz);
                            });
                        } else if (attrib.name.startsWith("p-")) {
                            child.setAttribute(attrib.name.substring(2), attrib.value);
                        }
                    }
                    element.parentNode.insertBefore(child, element);
                    element.remove();
                    /*element.appendChild(child);*/
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
				input = this.printSelectFunc(field, 'renderOptions');
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
				var config = {
					href: field.href,
					method: field.method,
					async: field.async,
					params: field.requestParams,
					submitConfirmation: function() {
						if (field.hasOwnProperty('confirmation')) {
							return totiDisplay.confirm(field.confirmation, {});
						}
						return true;
					}
				};
				if (field.hasOwnProperty("onError")) {
					config.onFailure = field.onError;
				}
				if (field.hasOwnProperty("onSuccess")) {
					config.onSuccess = field.onSuccess;
				}
				input.onclick = totiControl.getAction(config);
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
            var val;
            if (typeof option === 'object') {
                val = option.value;
                span.setAttribute("value", option.value);
                span.innerText = option.title;
            } else { /* for renderOptions*/
                span.setAttribute("value", i);
                span.innerText = option;
                val = i;
            }
            if (!(field.hasOwnProperty("value") && field.value === val)) {
				span.style.display = "none";
			}
			input.appendChild(span);
           /*if (field.hasOwnProperty("value") && field.value !== val) {
				span.style.display = "none";
			}
			input.appendChild(span);*/
		};
		totiUtils.forEach(options, withOption);
		return input;
	}

	getSubmit(uniqueName, submit) {
		return function(event) {
			try {
				var errorLists = document.getElementsByClassName('error-list');
				while(errorLists[0]) {
					errorLists[0].remove();
				}

				var form = document.getElementById(uniqueName);
				if (!form.reportValidity()) {
					return false;
				}
				var data = new FormData(); /* if parameter is form, all not disabled params are added */
				Array.prototype.forEach.call(form.elements, function(input) {
					var type = input.getAttribute("type");
					var name = input.getAttribute("name");
	                if (name === null) {
	                    return;
	                }
	                if (input.getAttribute("exclude") !== null && input.getAttribute("exclude") === "true") {
	                    return;
	                }
					if (input.type === "fieldset" && input.getAttribute("origintype") !== "datetime-local") {
						/* ignored*/
						return;
					}
	                if (type === "submit" || type === "button" || type === "reset" || type === "image") {
						/* ignored*/
						return;
	                }
					/******/
					if (input.getAttribute("origintype") === "datetime-local") {
						if (input.value !== undefined) {
							data.append(name, input.value);
						}
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
					if (submit.getAttribute("confirmation") !== null) {
						return totiDisplay.confirm(submit.getAttribute("confirmation"));
					}
					return true;
				};
				if (!submitConfirmation(data)) {
					event.preventDefault();
					return false;
				}
				if (submit.getAttribute("async") === 'true') {
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
							var getMessage = function(r) {
								if (typeof r === 'object') {
									return r.message;
								} else {
									return r;
								}
							};
							if (submit.getAttribute("onSuccess") != null) {
								window[submit.getAttribute("onSuccess")](response, submit, form);
							} else if (submit.getAttribute("redirect") === null) {
								totiDisplay.flash('success', getMessage(response));
							} else {
								totiDisplay.storedFlash('success', getMessage(response));
							}
							if (submit.getAttribute("redirect") != null) {
								var id = typeof response === 'object' ? response.id : "";
								window.location = submit.getAttribute("redirect").replace("{id}", id);
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
										var inputErrors = document
											.querySelectorAll('#' + uniqueName + '-errors-' + elementId + '');
										if (inputErrors.length > 0) {
											inputErrors[elementIdentifiers[0]].appendChild(ol);
										}
									} else {
										var inputError = document.querySelector('#' + uniqueName + '-errors-' + elementId + '');
										if (inputError !== null) {
											inputError.appendChild(ol);
										}
										
									}
								}
							} else if (submit.getAttribute("onFailure") != null) {
								window[submit.getAttribute("onFailure")](xhr, submit, form);
							} else {
								/* TODO xhr.response - objekt or message and 403*/
								totiDisplay.flash('error', totiTranslations.formMessages.saveError);
							}
						}
					);
					return false;
				}
				/* TODO send form data */
				/*event.preventDefault();
				var formTosend = document.createElement("form");
				formTosend.setAttribute("action", form.getAttribute("action"));
				formTosend.setAttribute("method", form.getAttribute("method"));
				if (form.getAttribute("enctype") !== null) {
					formTosend.setAttribute("enctype", form.getAttribute("enctype"));
				}
				for (const[name, value] of data.entries()) {
					var hidden = document.createElement("input");
					hidden.name = name;
					hidden.value = value;
					formTosend.appendChild(hidden);
				}
				formTosend.submit();*/
				return true;
			} catch (ex) {
				console.log(ex);
				totiDisplay.flash('error', totiTranslations.formMessages.saveError);
				return false;
			}
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
				if (bind.hasOwnProperty('onFailure') && bind.onFailure !== null) {
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
                if (typeof element.onchange === "function") {
                	element.onchange();
                }
            }
		}
	}
}