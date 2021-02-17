class TotiForm {

	constructor(config) {
		this.config = config;
	}

	init(elementIdentifier, uniqueName) {
		var object = this;
		document.addEventListener("DOMContentLoaded", function(event) { 
			document.querySelector(elementIdentifier).appendChild(
				object.create(uniqueName, document.querySelector(elementIdentifier))
			);
			if (object.config.hasOwnProperty('bind')) {
				object.bind(uniqueName, object.config.bind);
			}
		});
	}

	create(uniqueName, element) {
		var printSelectFunc = function (field, optionsName) {
			var input = document.createElement('div');
			var options = field[optionsName];
			delete field[optionsName];
			for ([key, name] of Object.entries(field)) {
				input.setAttribute(key, name);
			}
			options.forEach(function(option) {
				var span = document.createElement('span');
				span.setAttribute("value", option.value);
				span.innerText = option.title;
				if (field.value !== option.value) {
					span.style.display = "none";
				}
				input.appendChild(span);
			});
			return input;
		}

		var form;
		if (this.config.editable) {
			var errors = document.createElement("div");
			errors.setAttribute("id", uniqueName + "-errors-form");
			errors.appendChild(document.createElement("span"));
			form = document.createElement("form");
			form.setAttribute("id", uniqueName);
			form.setAttribute("action", this.config.action);
			form.setAttribute("method", this.config.method);
			form.appendChild(errors);
		} else {
			form = document.createElement("div");
			form.setAttribute("id", uniqueName);
		}
		var table;
		var useTemplate = false;
		if (element.innerHTML.length === 0) {
			table = document.createElement("table");
		} else {
			useTemplate = true;
			table = document.createElement("div");
			table.innerHTML = element.innerHTML;
			element.innerHTML = "";
		}
		form.appendChild(table);
		
		var config = this.config;
		var object = this;
		this.config.fields.forEach(function(field, index) {
			field.id = uniqueName + "-" + field.id;
			field.form = uniqueName;
			var label = null;
			if (field.hasOwnProperty('title')) {
				label = totiControl.label(field.id, field.title, {
					id: uniqueName +  "-" + field.id + "-label"
				});
			}
			var input;
			if (!config.editable && field.type !== 'button') {
				if (field.type === 'select') {
					input = printSelectFunc(field, 'options');
				} else if (field.type === 'radiolist') {
					input = printSelectFunc(field, 'radios');
				} else if (field.type === 'checkbox') {
					input = printSelectFunc(field, 'values');
				} else if (field.type !== 'submit' && field.type !== 'hidden') {
					input = document.createElement("div");
					for ([key, name] of Object.entries(field)) {
						if (key === "value") {
							input.innerText = name;
						} else {
							input.setAttribute(key, name);
						}
					}				
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
					input.onclick = object.getSubmit(uniqueName, input);
				} else if (type === 'button') {
					var onClick = totiControl.getAction({
						href: field.href,
						method: field.method,
						async: field.ajax,
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
			var error = document.createElement("div");
			error.setAttribute("id", uniqueName + '-errors-' + field.name);
			if (useTemplate) {
				var labelElement = form.querySelector("#form-label-" + field.name);
				if (labelElement !== null && label !== null) {
					labelElement.appendChild(label);
				}
				var inputElement = form.querySelector("#form-input-" + field.name);
				if (inputElement !== null) {
					inputElement.appendChild(input);
				}
				var errorElement = form.querySelector("#form-error-" + field.name);
				if (config.editable && errorElement !== null) {
					errorElement.appendChild(error);
				}
			} else {
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
				var tableContent = document.createElement("div");
				table.appendChild(row);
			}
		});
		return form;
	}

	getSubmit(uniqueName, submit) {
		return function(event) {
			Array.prototype.forEach.call(document.getElementsByClassName('error-list'), function(el) {
			    el.remove();
			});

			var form = document.getElementById(uniqueName);
			if (!form.reportValidity()) {
				return false;
			}
			var data = new FormData(form); 
			Array.prototype.forEach.call(form.elements, function(input) {
				var type = input.getAttribute("type");
				var name = input.getAttribute("name");
				// TODO check value
				if (type === "datetime-local") {
					value = input.value;
					value = value.replace("T", " ");
					data.append(name, value);
				} else if (type === "textarea") {
					data.append(name, input.innerText);
				} else if (input.tagName === "SELECT") {
					var option = input.querySelector("[selected='true']");
					data.append(name, input.value);
				} else if (type === "submit" || type === "button") {
					// ignored
				} else if (type === "radio") {
					if (input.checked) {
						data.append(name, input.value);
					}
				} else if (type === "file") {
					if (input.files.length > 0) {
						data.append(name, input.value);
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
				// header['Content-type'] = 'application/x-www-form-urlencoded';
				totiLoad.async(
					form.getAttribute("action"), 
					form.getAttribute("method"), 
					data, 
					header, 
					function(response) {
						if (submit.getAttribute("onSuccess") != null) {
							window[submit.getAttribute("onSuccess")](response);
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
								document.querySelector('#' + uniqueName + '-errors-' + key + '').innerHTML = ol;
							}
						} else if (element.getAttribute("onFailure") != null) {
							window[element.getAttribute("onFailure")](xhr);
						} else {
							totiDisplay.flash('error', totiTranslations.formMessages.saveError);
						}
					}
				);
			}
		};
	}

	/**
	bind: url, method, jsonObject params, String beforeBind(optional), String afterBind(optional), String onFailure(optional)
	*/
	bind(formId, bind) {
		totiLoad.async(
			bind.url, 
			bind.method, 
			bind.params,
			totiLoad.getHeaders(), 
			function(values) {
				if (bind.hasOwnProperty("beforeBind")) {
					window[bind.beforeBind]();
				}
				var form  = document.getElementById(formId);
				for (const[key, val] of Object.entries(values)) {
					var value = val;
					var element = form.querySelector('[name="' + key + '"]');
					if (element === null) {
						continue;
					}
					if (element.type === "datetime-local" && value !== null) {
						value = value.replace(" ", "T");
					}
					if (element.querySelector("span") != null) {
						element.querySelectorAll("span").forEach(function(el) {
							el.style.display = "none";
						});
						element.querySelector("[value=" + value + "]").style.display = "block";
					} else {
						element.value = value;
						/*
						if (element.type === "checkbox") {
							element.checked = value ? "checked" : false;
						} else if (element.type === "radio") {
							form.querySelector('[name="' + key + '"][value="' + value + '"]').setAttribute("checked", true);
						} else if (element.type === "textarea") {
							element.innerText = value;
						} else if (element.type === "select-one") {
							var option = element.querySelector("[value='" + value + "']");
							option.setAttribute("selected", true);
						} else {
							element.setAttribute("value", value);
						}
						*/
					}
				}
				if (bind.hasOwnProperty("afterBind")) {
					window[bind.afterBind]();
				}
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
}
