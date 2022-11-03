/* TOTI Form version 1.0.0 */
class TotiForm {

	constructor(config) {
		this.config = config;
		this.template = null;
		this.container = null;
		this.formUnique = null;
		this.dynamic = {};
	}

	render(selector, formUnique, customTemplate = null) {
		totiDisplay.fadeIn();
		try {
			if (this.config.hasOwnProperty("beforeRender") && this.config.beforeRender !== null) {
				totiUtils.execute(this.config.beforeRender, [this]);
			}
			this.formUnique = formUnique;
			var template = this.template = this.getTemplate(selector, customTemplate);
			var container = this.container = template.getContainer(selector, formUnique, this.config.editable);
			template.setFormAttribute(formUnique, container, "action", this.config.action);
			template.setFormAttribute(formUnique, container, "method", this.config.method);

			var form = this;
			this.config.fields.forEach(function(field) {
				form.addInput(field);
			});
			if (this.config.hasOwnProperty("afterRender") && this.config.afterRender !== null) {
				totiUtils.execute(this.config.afterRender, [this]);
			}
			if (this.config.hasOwnProperty("bind")) {
				this.bind(this.config.bind, formUnique, container);
			}
			totiDisplay.fadeOut();
		} catch(exception) {
			totiDisplay.fadeOut();
			totiDisplay.flash('error', totiTranslations.formMessages.renderError, exception);
		}
	}

	getTemplate(parentSelector, template) {
		var parent = document.querySelector(parentSelector);
		if (parent === null) {
			console.error("Selector '" + parentSelector + "' is not pointing to existing element");
			return null;
		}
		var totiTemplate = totiDisplay.formTemplate;
		if (template === false) { /* parent.children.length > 0 */
			totiTemplate = totiFormCustomTemplate;
		}
		if (template === null || template === false) {
			template = {};
		}
		for (const[name, value] of Object.entries(totiTemplate)) {
			if (!template.hasOwnProperty(name)) {
				template[name] = value;
			}
		}
		return template;
	}

	addInput(field, parent = null) {
		var editable = field.hasOwnProperty("editable") ? field.editable : this.config.editable;
		var form = this;
		var container = this.container;
		var removeFunc = null;
		var originName = field.name;
		if (parent !== null) {
			var fieldName = parent.name;
            if (parent.position !== false) {
               	fieldName += "[" + field.name + "]";
            }
            if (parent.group !== null) {
                fieldName = fieldName.replace('{i}', parent.group);
            }
            field.name = fieldName;

			if (parent.position !== false && field.hasOwnProperty("title")) {
				field.title = field.title.replace('{i}', parent.position);
			}
			container = parent.container;
		}

		
		if (field.type === "dynamic" && field.hasOwnProperty('load')) {
			var dynamicContainer = form.template.getDynamicContainer(form.formUnique, form.container, field.name, field.title, null);
			totiLoad.load(field.load.url, field.load.method, {}, {}, field.load.params)
			.then((loaded)=>{
				loaded.forEach((group)=>{
					var parentName = field.name;
					if (field.fields.length > 1 || (field.fields.lenght == 1 && (field.fields[0].name.length > 1 || !field.fields[0].name.lenght === '{i}') )) {
						parentName += "[" + group.value + "]";
					}
					field.fields.forEach(function(subField, index) {
						var f = totiUtils.clone(subField);
						f.optionGroup = group.value;
						form.addInput(f, {
							name: parentName,
							group: group.value,
							position: group.title,
							container: dynamicContainer
						});
					});
				});
			}).catch((xhr)=>{
				totiDisplay.flash('error', totiTranslations.formMessages.renderError, xhr);
            });
		} else if (field.type === "dynamic") {
			var dynamicCache = this.dynamic;
			if (parent !== null) {
				if (parent.hasOwnProperty('dynamic')) {
					parent.dynamic['dynamic'] = {};
					dynamicCache = parent.dynamic['dynamic'];
				}
			}
			var removeItem = function(position) {
				if (dynamicCache[originName].elements.hasOwnProperty(position)) {
					dynamicCache[originName].elements[position].remove();
					delete dynamicCache[originName].elements[position];
				}
			};
			var addItem = function() {
				var position = ++dynamicCache[originName].position;
				var pos = null;
				var parentName = field.name;
				if (field.fields.length > 1 || (field.fields.lenght == 1 && (field.fields[0].name.length > 1 || !field.fields[0].name.lenght === '{i}') )) {
					parentName += "[{i}]";
					pos = position;
				}
				var removeFunc = field.removeButton && editable ? ()=>{
					removeItem(position);
				} : null;
				var rowContainer = form.template.getDynamicRow(form.formUnique, form.container, dynamicCache[originName].container, field.name, removeFunc, pos, originName);
				if (rowContainer === null) {
					return;
				}
				dynamicCache[originName].elements[position] = rowContainer;
				field.fields.forEach(function(f, index) {
					form.addInput(totiUtils.clone(f), {
						name: parentName,
						position: position+1,
						container: rowContainer,
                        group: position,
                        dynamic: dynamicCache[field.name]
					});
				});
				return position;
			};
			var dynamicContainer = form.template.getDynamicContainer(form.formUnique, form.container, originName, field.title, field.addButton && editable ? addItem : null);
			if (dynamicContainer === null) {
				return;
			}
			dynamicCache[originName] = {
				add: addItem,
				remove: removeItem,
				position: -1,
				container: dynamicContainer,
				elements: {}
			};
			addItem();
		} else if (field.type === "list") {
			field.fields.forEach(function(subField, index) {
				form.addInput(subField, {
					name: field.name,
					position: index,
					container: container,
                    group: null
				});
			});
		} else if (!editable) {
			switch(field.type) {
				case "submit":
					/* IMPROVE: display real value*/
				case "image":
				case "hidden":
				case "reset":
				case "password":
					/* ignored */
					break;
				case "file":
					/* IMPROVE: display value */
					break;
				case "button":
					var input = totiControl.button(field, field.action.async);
					input.setAttribute("form", this.formUnique);
					this.template.addControl(this.formUnique, container, field.name, input);
					break;
				case "checkbox":
					this.template.addOptionRow(this.formUnique, container, field.name, field.type, field.title, field.values, field.value);
					break;
				case "radiolist":
					this.template.addOptionRow(this.formUnique, container, field.name, field.type, field.title, field.radios, field.value);
					break;
				case "select":
					var select = totiControl.input(field);
					this.template.addPromisedRow(this.formUnique, container, field.name, field.type, field.title, select.setOptions);
					break;
				default:
					this.template.addRow(this.formUnique, container, field.name, field.type, field.title, field.value);
					break;
			}
		} else {
			var input = totiControl.input(field);
			input.setAttribute("form", this.formUnique);
			switch(field.originType) {
				case "hidden":
					this.template.addHidden(this.formUnique, container, input, removeFunc);
					break;
				case "submit":
				case "image":
					var form = this;
					input.addEventListener("click", function(e) {
						e.preventDefault();
						return form.submit(input);
					});
					this.template.addControl(this.formUnique, container, field.name, input);
					break;
				case "button":
					this.template.addControl(this.formUnique, container, field.name, input, removeFunc);
					break;
				case "file":
					this.template.setFormAttribute(this.formUnique, container, "enctype", "multipart/form-data");
					/* no break */
				default:
					this.template.addInput(this.formUnique, container, field.name, field.title, input, removeFunc);
					break;
			}
		}
	}

	addDynamicField(name) {
		if (this.dynamic.hasOwnProperty(name)) {
			return this.dynamic[name].add();
		}
		return null;
	}

	removeDynamicField(name, index) {
		if (this.dynamic.hasOwnProperty(name)) {
			this.dynamic[name].remove(index);
		}
		
	}

	submit(srcElement) {
		/* IMPROVE: before and after submit callbacks ? */
		totiDisplay.fadeIn();
		var form = this.container;
		var template = this.template;
		if (this.container.tagName !== 'FORM') {
			form = this.container.querySelector('form');
		}

		if (!form.reportValidity()) {
			totiDisplay.fadeOut();
			return false;
		}
		var data = new FormData(); /* if parameter is form, all not disabled params are added */
		form.querySelectorAll("input, select, fieldset, textarea").forEach((input)=>{
			var type = input.getAttribute("type");
			var name = input.getAttribute("name");
			if (name === null) {
				return;
			}
			if (input.getAttribute("exclude") !== null && input.getAttribute("exclude") === "true") {
				return;
			}
			/* for not strict datetime */
			if (input.type === "fieldset" && input.getAttribute("origintype") !== "datetime-local") {
				/* ignored*/
				return;
			}
			if (type === "button" || type === "reset") { /*type === "submit" ||  || type === "image"*/
				/* ignored*/
				return;
			}
			
			if (input.getAttribute("origintype") === "datetime-local") {
				if (input.value !== undefined) {
					data.append(name, input.value);
				}
			} else if (type === "submit" || type === "image") {
				switch(input.getAttribute("submitpolicy")) {
					case "EXCLUDE": return;
					case "INCLUDE_ON_CLICK":
						if (input !== srcElement) {
							return;
						}
					case "INCLUDE":
						data.append(name, input.getAttribute("realvalue"));
						break;
				}
			} else  if (type === "radio") {
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
		function submitConfirmation() {
			if (srcElement.getAttribute("confirmation") !== null) {
				return totiDisplay.confirm(srcElement.getAttribute("confirmation"));
			}
			return new Promise((resolve)=>{
				resolve(true);
			});
		};
		submitConfirmation().then(function(submitAllowed) {
			if (!submitAllowed) {
				return false;
			}
			if (srcElement.getAttribute("async") === 'true') {
				var headers = {};
				if (form.getAttribute("enctype") !== null) {
					headers.enctype = form.getAttribute("enctype");
				}
				totiLoad.load(
					form.getAttribute("action"), 
					form.getAttribute("method"), 
					headers,
					{},
					data
				).then(function(result) {
					if (srcElement.getAttribute("onSuccess") != null) {
						window[srcElement.getAttribute("onSuccess")](result, srcElement, form);
					} else if (srcElement.getAttribute("redirect") === null) {
						totiDisplay.flash('success', totiTranslations.formMessages.sendSuccess);
					} else {
						totiDisplay.storedFlash('success', totiTranslations.formMessages.sendSuccess);
					}
					if (srcElement.getAttribute("redirect") !== null) {
						var redirect = srcElement.getAttribute("redirect");
						if (typeof result === 'object') {
							redirect = totiUtils.parametrizedString(redirect, result);
						}
						window.location = redirect;
					}
				}).catch(function(error) {
					if (error.hasOwnProperty('status') && error.status === 400) {
						form.querySelectorAll('[toti-form-error]').forEach(function(errContainer) {
							errContainer.innerHTML = "";
						});
						for (const[key, list] of Object.entries(JSON.parse(error.responseText))) {
							var errorsInElement = template.createErrorList(list);
							var elementId = key.replaceAll("[", "\\[").replaceAll("]", "\\]");
							var elementIdentifiers = elementId.split(":");

							if (elementIdentifiers.length > 1) {
								elementId = elementId.replace(elementIdentifiers[0] + ":", "");
								var inputErrors = form.querySelectorAll('[toti-form-error="' + elementId + '"]');
								if (inputErrors.length > 0) {
									inputErrors[elementIdentifiers[0]].appendChild(errorsInElement);
								}
							} else {
								var inputError = form.querySelector('[toti-form-error="' + elementId + '"]');
								if (inputError !== null) {
									inputError.appendChild(errorsInElement);
								}
							}
						}
					} else if (srcElement.getAttribute("onFailure") != null) {
						window[srcElement.getAttribute("onFailure")](err, srcElement, form);
					} else if (error.hasOwnProperty('status') && error.status === 403) {
						totiDisplay.flash('error', totiTranslations.formMessages.submitErrorForbidden, error);
					} else {
						totiDisplay.flash('error', totiTranslations.formMessages.submitError, error);
					}
				});
				return;
			}
			var formTosend = document.createElement("form");
			formTosend.style.display = "none";
			document.body.appendChild(formTosend);

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
			formTosend.submit();
			formTosend.remove();
		});
		totiDisplay.fadeOut();
		return false;
	}

	bind(bindConfig, formUnique, container) {
		var form = this;
		totiLoad.load(bindConfig.url, bindConfig.method).then(function(values) {
			if (bindConfig.hasOwnProperty("beforeBind") && bindConfig.beforeBind !== null) {
				totiUtils.execute(bindConfig.beforeBind, [values, form]);
			}
			form._bind(values);
			if (bindConfig.hasOwnProperty("afterBind") && bindConfig.afterBind !== null) {
				totiUtils.execute(bindConfig.afterBind, [values, form]);
			}
		}).catch(function(error) {
			if (bindConfig.onFailure === null) {
				totiDisplay.flash("error", totiTranslations.formMessages.bindError, error);
			} else {
				totiUtils.execute(bindConfig.onFailure, [error]);
			}
		});
	}

	_bind(values) {
		var form = this;
        var container = this.container;
		function bindElement(dynamicCache, originName, name, value, position = 0) {
			var dynamic = dynamicCache[originName];
			if (value === null) {
				/* ignore */
            } else if (Array.isArray(value)) {
				value.forEach(function(val, index) {
					var key = name + "[]";
					if (dynamic) {
						var existing = container.querySelectorAll("[name^='" + key + "']");
						if (existing.length <= index) {
							dynamic.add();
						}
					}
					bindElement(dynamicCache, "", key, val, index);
				});
				return;
			} else if (typeof value === 'object') {
				for (const[k, v] of Object.entries(value)) {
					var key = name + "[" + k + "]";
					if (dynamic) {
						var existOne = container.querySelector("[name^='" + key + "']");
						if (existOne === null) {
							dynamic.add();
						}
					}
					if (dynamic && dynamic.hasOwnProperty('dynamic')) {
						dynamicCache = dynamic.dynamic;
					}
					bindElement(dynamicCache, k, key, v);
				}
				return;
			}
			function getElement() {
				var elements = container.querySelectorAll("[name='" + name + "']");
				if (elements.length === 0) {
					return;
				}
				return elements[position];
			}
			var element = getElement();
			
			if (element === undefined) {
				return;
			}
			switch(element.type) {
				case undefined:
					/* not editable */
					if (element.bind !== undefined) {
						element.bind(totiControl.parseValue(element.getAttribute('origintype'), value));
					}
					break;
				case "checkbox":
					element.checked = value ? "checked" : false;
					break;
				case "radio":
					var radiolist = container.querySelector("[name='" + name + "'][value='" + value + "']");
					if (radiolist !== null) {
						radiolist.checked = value ? "checked" : false;
					}
					break;
				/* IMPROVE: realValue u selectu */
				case "select-one":
					var val = value;
					var select = element;
					element.setOptions.then(function() {
						select.value = val;
					});
					break;
				default:
					element.value = value;
			}
		}
		for (const[name, value] of Object.entries(values)) {
			bindElement(this.dynamic, name, name, value);
		}
	}

}