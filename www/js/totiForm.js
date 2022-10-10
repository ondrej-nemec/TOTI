/* TOTI Form version 1.0.0 */
class TotiForm {
	/*
	vykreslit
	bind
	editable vs not editable
	API pro dynamic
	callbacks:
		before print
		after print
		before bind
		after bind
		? before submit ?
		? after submit ?
	inputs:
		itemlist
		dynamic list + using load
		submit, hidden, reset, image ignorovat v detailu
		v detailu spravně vykreslit select, checkbox, radolist, file
		možnost poslat submit s hodnotami + přidat hodnotu
	submit:
		spravna reakce na failure - ne 400, nejak zobrazit validacni errory
		spravna reakce na success + redirect -> nastavitelny parametr 'id' a message
		v submitu : exclude, disabled, pamatovat na datetime-local (firefox) - fieldset
		submit confirmation
		after submit redirect - u submitu

	možnost odeslat post synchroně: ??? 
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
				formTosend.submit();

	*/

	constructor(config) {
		this.config = config;
		this.template = null;
		this.container = null;
		this.formUnique = null;
	}

	render(selector, formUnique, customTemplate = null) {
		if (this.config.hasOwnProperty("beforeRender") && this.config.beforeRender !== null) {
			totiUtils.execute(this.config.beforeRender);
		}
		this.formUnique = formUnique;
		var template = this.template = this.getTemplate(selector, customTemplate);
		var container = this.container = template.getContainer(selector, formUnique, this.config.editable);
		container.setAttribute("action", this.config.action);
		container.setAttribute("method", this.config.method);

		var form = this;
		this.config.fields.forEach(function(field) {
			form.addInput(field);
		});
		if (this.config.hasOwnProperty("afterRender") && this.config.afterRender !== null) {
			totiUtils.execute(this.config.afterRender);
		}
		if (this.config.hasOwnProperty("bind")) {
			this.bind(this.config.bind, formUnique, container);
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

	addInput(field) {
		var editable = field.hasOwnProperty("editable") ? field.editable : this.config.editable;
		// TODO list position title
		// TODO upravit jmeno, pokud je predek: predek[jmeno]
		if (field.type === "dynamic") {
			// TODO implement
		} else if (field.type === "list") {
			// TODO implement
		} else if (!editable) {
			switch(field.type) {
					// TODO realValue u selectu
				case "submit":
				case "image":
				case "hidden":
				case "reset":
				case "password":
					/* ignored */
					break;
				case "button":
					var input = totiControl.button(field);
					this.template.addControl(this.formUnique, this.container, input);
					break;
				case "checkbox":
					this.template.addOptionRow(this.formUnique, this.container, field.name, field.type, field.title, field.values);
					break;
				case "radiolist":
					this.template.addOptionRow(this.formUnique, this.container, field.name, field.type, field.title, field.radios);
					break;
				case "select":
					var select = totiControl.input(field);
					this.template.addPromisedRow(this.formUnique, this.container, field.name, field.type, field.title, select.setOptions);
					break;
				default:
					this.template.addRow(this.formUnique, this.container, field.name, field.type, field.title);
					break;
			}
		} else {
			// TODO file - set form.setAttribute("enctype", "multipart/form-data");

			var input = totiControl.input(field);
			switch(field.originType) {
				case "hidden":
					this.template.addHidden(this.formUnique, this.container, input);
					break;
				case "submit":
				case "image":
					var form = this;
					input.addEventListener("click", function(e) {
						e.preventDefault();
						return form.submit(input);
					});
					this.template.addControl(this.formUnique, this.container, input);
					break;
				case "button":
					this.template.addControl(this.formUnique, this.container, input);
					break;
				default:
					this.template.addInput(this.formUnique, this.container, field.name, field.title, input);
					break;
			}
		}
	}

	addDynamicField(name) {}

	removeDynamicField(name, index) {}

	submit(srcElement) {
		var form = this.container;
		if (this.container.tagName !== 'FORM') {
			form = this.container.querySelector('form');
		}

		if (!form.reportValidity()) {
			return false;
		}
		var data = new FormData(); /* if parameter is form, all not disabled params are added */
		form.querySelectorAll("input, select, fieldset").forEach((input)=>{
			var type = input.getAttribute("type");
			var name = input.getAttribute("name");
			if (name === null) {
				return;
			}
			if (input.getAttribute("exclude") !== null && input.getAttribute("exclude") === "true") {
				return;
			}
			// TODO optional fields - color, range
			if (input.type === "fieldset") { /* no more required: && input.getAttribute("origintype") !== "datetime-local"*/
				/* ignored*/
				return;
			}
			if (type === "button" || type === "reset") { /*type === "submit" ||  || type === "image"*/
				/* ignored*/
				return;
			}
			
			/* no more required: if (input.getAttribute("origintype") === "datetime-local") {
				if (input.value !== undefined) {
					data.append(name, input.value);
				}
			} else */ if (type === "submit" || type === "image") {
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
		var submitConfirmation = function() {
			if (srcElement.getAttribute("confirmation") !== null) {
				return totiDisplay.confirm(srcElement.getAttribute("confirmation"));
			}
			return true;
		};
		if (!submitConfirmation(data)) {
			return false;
		}
		if (srcElement.getAttribute("async") === 'true') {
			var headers = {};
			if (form.getAttribute("enctype") !== null) {
				headers.enctype = form.getAttribute("enctype");
			}
			// load: function(url, method, headers = {}, urlData = {}, bodyData = {}) {
			totiLoad.load(
				form.getAttribute("action"), 
				form.getAttribute("method"), 
				headers,
				{},
				data
			).then(function(result) {
				// TODO
				console.log("success", result);
				/*
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
				*/
			}).catch(function(error) {
				// TODO
				console.log("error", error);
				/*
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
						ii TODO xhr.response - objekt or message and 403
						totiDisplay.flash('error', totiTranslations.formMessages.saveError);
					}
				}
				*/
			});
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
	}

	bind(bindConfig, formUnique, container) {
		if (bindConfig.hasOwnProperty("beforeBind") && bindConfig.beforeBind !== null) {
			totiUtils.execute(bindConfig.beforeBind);
		}
		totiLoad.load(bindConfig.url, bindConfig.method).then(function(values) {
			console.log("TODO bind", values);
			for (const[k, value] of Object.entries(values)) {
				var element = container.querySelector("[name='" + k + "']");
				if (element === null) {
					continue;
				}
				// TODO dynamic list, input list
				// TODO not editable
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
						var radiolist = container.querySelector("[name='" + k + "'][value='" + value + "']");
						if (radiolist !== null) {
                   			radiolist.checked = value ? "checked" : false;
						}
						break;
					// TODO realValue u selectu
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
			if (bindConfig.hasOwnProperty("afterBind") && bindConfig.afterBind !== null) {
				totiUtils.execute(bindConfig.afterBind);
			}
		}).catch(function(error) {
			if (bindConfig.onFailure === null) {
				totiDisplay.flash("error", totiTranslations.formMessages.bindError);
			} else {
				totiUtils.execute(bindConfig.onFailure, [error]);
			}
		});
	}

}