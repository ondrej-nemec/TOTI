var totiControl = {
	label: function (forInput, title, params = {}) {
		var label = document.createElement("label");
		label.innerText = title;
		label.setAttribute("for", forInput);
		for ([key, name] of Object.entries(params)) {
			label.setAttribute(key, name);
		}
		return label;
	},
	button: function(attributes) {
		var button = document.createElement('button');
		for ([key, name] of Object.entries(attributes)) {
			if (key === "value") {
				button.innerHTML = name;
			} else {
				button.setAttribute(key, name);
			}
		}
		return button;
	},
	input: function (attributes) {
		if (!attributes.hasOwnProperty('type')) {
			console.error("Missing attribute type", attributes);
			return;
		}
		var type = attributes.type;
		attributes.originType = type;
		delete attributes.type;

		if (type === "checkbox" && attributes.value) {
			attributes.checked = "checked";
		}

		// IMP datetime 
		/*if (type === 'datetime') {
			return totiControl.inputs._createInput("datetime-local", attributes);
		} else*/if (type === 'textarea') {
			return totiControl.inputs.textarea(attributes);
		} else if (type === 'select') {
			return totiControl.inputs.select(attributes);
		} else if (type === 'option') {
			return totiControl.inputs.option(attributes);
		} else if (type === 'radiolist') {
			return totiControl.inputs.radiolist(attributes);
		} else {
			return totiControl.inputs._createInput(type, attributes);
		}
	},
	inputs: {
		_createInput: function (type, attributes) {
			var input = document.createElement("input");
			input.setAttribute("type", type);
			for ([key, name] of Object.entries(attributes)) {
				input.setAttribute(key, name);
			}
			return input;
		},
		radiolist: function(params) {
			var input = document.createElement("div");
			params.radios.forEach(function(radio) {
				var item = document.createElement("div");
				var id = params.name + "-" + radio.id;
				item.setAttribute('id', id + "-block");
				if (radio.hasOwnProperty('title')) {
					item.appendChild(totiControl.label(params.id, radio.title, {
						id: id + "-label"
					}));
				}
				var settings = {
					id: id,
					name: params.name,
					form: params.formName,
					value: radio.value,
					type: "radio"
				};
				if (radio.value === params.value) {
					settings.checked = "checked";
				}
				if (params.hasOwnProperty('required')) {
					settings.required = params.required;
				}
				if (params.hasOwnProperty('disabled')) {
					settings.disabled = params.disabled;
				}
				item.appendChild(totiControl.input(settings));
				input.appendChild(item);
			});
			return input;
		},
		textarea: function(params) {
			var textarea = document.createElement('textarea');
			for ([key, name] of Object.entries(params)) {
				if (key === "value") {
					textarea.innerHTML = name;
				} else {
					textarea.setAttribute(key, name);
				}
			}
			return textarea;
		},
		select: function (params) {
			var select = document.createElement('select');
			for ([key, value] of Object.entries(params)) {
				if (key === "options" || key === "load") {
					// ignored now, done soon
				} else {
					select.setAttribute(key, value);
				}
			}
			var addOption = function(option) {
				if (typeof option === 'object') {
					option.type = "option";
					select.appendChild(totiControl.input(option));
				} else {
					select.appendChild(option);
				}
			};
			totiUtils.forEach(params.options, function(v, option) {
				addOption(option);
			});
			if (params.hasOwnProperty("load")) {
				totiLoad.async(params.load.url, params.load.method, params.load.params, totiLoad.getHeaders(), function(loaded) {
					totiUtils.forEach(loaded, function(value, opt) {
						var option = { "value": value };
						if (typeof opt === "object") {
							option.title = opt.title;
							if (opt.disabled) {
								option.disabled = "disabled";
							}
						} else {
							option.title = opt;
						}
						params.options[value] = option; // for value renderer
						addOption(option);
					});
				}, function(xhr) {
					console.log(xhr);
				}, false);
			}
			
			return select;
		},
		option: function(params) {
			var option = document.createElement('option');
			for ([key, value] of Object.entries(params)) {
				if (key === "title") {
					option.innerHTML = value;
				} else {
					option.setAttribute(key, value);
				}
				
			}
			return option;
		}
	},
	parseValue: function(type, value) {
		// TODO week
		if (totiTranslations.timestamp.dateString.hasOwnProperty(type)) {
			return new Date(value).toLocaleDateString(
            	totiLang.getLang().replace("_", "-"),
				totiTranslations.timestamp.dateString[type]
			);
		}
		if (totiTranslations.timestamp.timeString.hasOwnProperty(type)) {
			value = "1970-01-01 " + value;
			return new Date(value).toLocaleTimeString(
            	totiLang.getLang().replace("_", "-"),
				totiTranslations.timestamp.timeString[type]
			);
		}
		return value;
	},
	getAction: function(clickSettings) {
		return function(event) {
			event.preventDefault();
			if (clickSettings.submitConfirmation !== null
				 && clickSettings.submitConfirmation !== undefined 
				 && !clickSettings.submitConfirmation()) {
				return false;
			}
			if (clickSettings.async) {
				totiLoad.async(clickSettings.href, clickSettings.method, {}, totiLoad.getHeaders(), function(res) {
					if (clickSettings.hasOwnProperty('onSuccess')) {
						window[clickSettings.onSuccess](res);
					} else {
						totiDisplay.flash("success", res);
					}
				}, function(xhr) {
					if (clickSettings.hasOwnProperty('onError')) {
						window[clickSettings.onError](xhr);
					} else {
						totiDisplay.flash("error", xhr);
					}
				});
			} else {
				/* totiControl.load.link(href, method, {}, totiControl.getHeaders()); */
				window.location = clickSettings.href;
			}
		};
	}
};