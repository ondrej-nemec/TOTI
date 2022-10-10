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
		// TODO beforePrint/Render
		this.formUnique = formUnique;
		var template = this.template = this.getTemplate(selector, customTemplate);
		var container = this.container = template.getContainer(selector, formUnique, this.config.editable);
		container.setAttribute("action", this.config.action);
		container.setAttribute("method", this.config.method);

		var form = this;
		this.config.fields.forEach(function(field) {
			form.addInput(field);
		});
		// TOOD afterPrint/Render
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
					input.addEventListener("click", function() {
						form.submit(input);
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

	submit(srcElement) {}

}