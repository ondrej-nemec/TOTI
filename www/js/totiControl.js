/* TOTI Control version 1.0.0 */
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
	button: function(attributes, async = true) {
		var button;
		if (async) {
			button = document.createElement('button');
		} else {
			button = document.createElement('a');
			/*button.classList.add("toti-button");*/
		}
		return totiControl.inputs.button(button, attributes);
	},
	input: function (attributes) {
		if (!attributes.hasOwnProperty('type')) {
			console.error("Missing attribute type", attributes);
			return null;
		}
		var type = attributes.type;
		attributes.originType = type;
		delete attributes.type;

		if (type === "checkbox" && attributes.value) {
			attributes.checked = "checked";
		}

		if (type === 'datetime-local') {
			if (attributes.hasOwnProperty("value")) {
				attributes.value = attributes.value.replace(" ", "T");
			}
			if (!attributes.strict) {
				return totiControl.inputs.datetime(attributes);
			}
			var browser = totiUtils.browser();
			if (browser !== "opera" && !browser.includes("chrom")) {
				return totiControl.inputs.datetime(attributes);
				/*console.error("Your browser probably not support datetime-local");*/
			}
			return totiControl.inputs._createInput("datetime-local", attributes);
		} else if (type === 'textarea') {
			return totiControl.inputs.textarea(attributes);
		} else if (type === 'select') {
			return totiControl.inputs.select(attributes);
		} else if (type === 'option') {
			return totiControl.inputs.option(attributes);
		} else if (type === 'radiolist') {
			return totiControl.inputs.radiolist(attributes);
		} else if (type === 'button') {
			var button = document.createElement("input");
			button.type = "button";
			return totiControl.inputs.button(button, attributes);
		} else if (type === 'range') {
			return totiControl.inputs._createOptionalInput("range", attributes);
		} else if (type === 'color') {
			return totiControl.inputs._createOptionalInput("color", attributes);
		} else {
			return totiControl.inputs._createInput(type, attributes);
		}
	},
	inputs: {
		_selectCache: {},
		_createInput: function (type, attributes) {
			var input = document.createElement("input");
			input.setAttribute("type", type);
			for ([key, name] of Object.entries(attributes)) {
				input.setAttribute(key, name);
			}
			return input;
		},
		_createOptionalInput: function(type, attributes) {
			if (attributes.hasOwnProperty("required") && attributes.required) {
				return totiControl.inputs._createInput(type, attributes);
			}
			var container = document.createElement("fieldset");
		    for ([key, name] of Object.entries(attributes)) {
		    	if (key !== "value") {
					container.setAttribute(key, name);
		    	}
		    }
		    var checkbox = document.createElement("input");
			checkbox.setAttribute("type", "checkbox");

		    var input = totiControl.inputs._createInput(type, attributes);

			checkbox.checked = false;
			input.setAttribute("disabled", true);
			input.setAttribute("exclude", true);

			container.set = function() {
				checkbox.checked = true;
				input.removeAttribute("disabled");
				input.value = container.value;
			};
			input.onchange = function() {
				container.value = input.value;
			};
			checkbox.onclick = function() {
				if (checkbox.checked) {
					input.removeAttribute("disabled");
					input.removeAttribute("exclude");
					container.value = input.value;
				} else {
					container.value = '';
					input.setAttribute("disabled", true);
					input.setAttribute("exclude", true);
				}
			};

		    container.appendChild(checkbox);
		    container.appendChild(input);

		    return container;
		},
		button: function(button, attributes) {
			for ([key, name] of Object.entries(attributes)) {
				switch (key) {
					case "value":
					case "title":
						/*var span = document.createElement("span");
		              	span.innerText = name;
		              	button.appendChild(span);*/
		              	button.innerText = name;
		              	button.value = name;
						break;
					case "tooltip":
						button.setAttribute("title", name);
						break;
					case "action":
						if (typeof attributes[key] === "object") {
							button.onclick = totiControl.getAction(attributes[key]);
						} else if (typeof attributes[key] === 'function') {
							button.addEventListener('click', attributes[key]);
						} else {
							button.addEventListener("click", function(event) {
								window[attributes.action](event); /*TODO use totiUtils execute instead?*/
							});
						}
						break;
					case "class":
						var clazzes = name;
						if (!Array.isArray(name)) {
							clazzes = name.split(" ");
						}
						clazzes.forEach(function(clazz) {
							if (clazz !== '') {
								button.classList.add(clazz);
							}
						});
						break;
					case "icon":
			            var i = document.createElement("i");
			            i.setAttribute("class", name);
			            button.append(i);
						break;
					default:
						button.setAttribute(key, name);
						break;
				}
			}
			return button;
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
					textarea.innerText = name;
				} else {
					textarea.setAttribute(key, name);
				}
			}
			return textarea;
		},
		select: function (params) {
			function addOption(container, option, selectedGroup, title) {
				if (option.hasOwnProperty('optgroup') && selectedGroup !== null && selectedGroup !== option.optgroup) {
					return;
				}
				if (option.hasOwnProperty('optgroup') && selectedGroup === null) {
					var group = title !== null ? title : option.optgroup;
					var optGroup = container.querySelector('[label="' + group + '"]');
				    if (optGroup === null) {
					    optGroup = document.createElement("optgroup");
					    optGroup.setAttribute("label", group);
					    container.appendChild(optGroup);
					}
					container = optGroup;
				}
				container.appendChild(totiControl.input({
					type: 'option',
					...option
				}));
			}
			function addOptions(select, params, depends) {
				var oldValue = select.value;
				select.value = null;
				/*params.renderOptions = {};*/

				var selectedGroup = null;
				if (depends !== null) {
					selectedGroup = depends.value;
				} else if (params.hasOwnProperty('optionGroup')) {
					selectedGroup = params.optionGroup;
				}

				var onOptions = function(options) {
					select.innerHTML = '';
					var groupSubstitution = null;
					options.forEach(function(option) {
						if (depends !== null) {
							var optGroup = depends.querySelector('[value="' + option.optgroup + '"]');
							if (optGroup !== null) {
								groupSubstitution = optGroup.innerText;
							}
						}
						addOption(select, option, selectedGroup === '' ? null : selectedGroup, groupSubstitution);
					/*	params.renderOptions[option.value] = option.title;*/
					});
					return options;
				};

				return new Promise(function (resolve, reject) {
					resolve(params.options);
				})
				.then(onOptions)
				.then(function (res) {
					if (params.hasOwnProperty("load")) {
						var cacheKey = JSON.stringify({
							"url": params.load.url,
							"method": params.load.method,
							"params": params.load.params
						});
						if (!totiControl.inputs._selectCache.hasOwnProperty(cacheKey)) {
							totiControl.inputs._selectCache[cacheKey] = totiLoad.load(params.load.url, params.load.method, {}, params.load.params);
						}
						return totiControl.inputs._selectCache[cacheKey].then(onOptions);
					}
					return res;
				})
				.then(function(options) {
					/* previous code sometimes returns null, sometimes promise*/
					select.value = params.value;
					/* render options for grid */
					var renderOptions = {};
					options.forEach(function(option) {
						renderOptions[option.value] = option.title;
					});
					params.renderOptions = renderOptions;
					return options;
				});
			}
			

			var select = document.createElement('select');
			for ([key, value] of Object.entries(params)) {
				if (key === "options" || key === "load" || key === "value" || key === "depends") {
					/* ignored now, done soon */
				} else {
					select.setAttribute(key, value);
				}
			}
			/*params.originOptions = totiUtils.clone(params.options);
            params.renderOptions = {}; /* really displayed values */

			select.setOptions = new Promise(function (resolve, reject) {
	            /* first wait until depends is loaded */
	            /* depends element must be first */
	            if (params.hasOwnProperty("depends")) {
	            	var dependElement = document.querySelector(params.depends);
	            	/* IMPROVEMENT: Observer to wait depends added to DOM */
	            	/* IMPROVEMENT: suport for radiolist */
	            	if (dependElement !== null) {
	            		if (dependElement.setOptions) { /* select */
	            			resolve(dependElement.setOptions.then(function() {
								return dependElement;
							}));
	            		} else {
	            			resolve(dependElement);
	            		}
	            	}
	            } else {
	            	resolve(null); /* no element depends */
	            }
			}).then(function(depends) {
				if (depends !== null) {
					depends.addEventListener('change', function() {
						addOptions(select, params, depends);
					});
				}
				return addOptions(select, params, depends);
			});
			return select;
		},
		option: function(params) {
			var option = document.createElement('option');
			for ([key, value] of Object.entries(params)) {
				if (key === "title") {
					option.innerText = value;
				} else {
					option.setAttribute(key, value);
				}
				
			}
			return option;
		},
		datetime: function(attributes) {
			var dateAttr = {};
			if (attributes.id.endsWith("-")) {
				attributes.id += Math.floor(Math.random() * 1000);
			}
			dateAttr.id = attributes.id + "-date";
		    if (attributes.hasOwnProperty('required')) {
		        dateAttr.required = attributes.required;
		    }
		    var date = totiControl.inputs._createInput("date", dateAttr);

		    var timeAttr = {};
		    timeAttr.id = attributes.id + "-time";
		    if (attributes.hasOwnProperty("step")) {
		        timeAttr.step = attributes.step;
		    }
		    if (attributes.hasOwnProperty('required')) {
		        timeAttr.required = attributes.required;
		    }
		    var time = totiControl.inputs._createInput("time", timeAttr);

		    var datetime = document.createElement("fieldset");
		    for ([key, name] of Object.entries(attributes)) {
		    	if (key !== "value") {
					datetime.setAttribute(key, name);
		    	}
		    }

		    datetime.appendChild(date);
		    datetime.appendChild(time);

		    var setValue = function(value) {
		    	if (value) {
			        var values = value.split("T");
			        if (values.length === 2) {
				        date.value = values[0];
				        time.value = values[1];
			        } else if (values.length === 1 && value.length === 10) {
				        date.value = value;
			        } else if (values.length === 1 && value.length >=5) {
				        time.value = value;
			        }
		    	}
		    };

		    if (attributes.hasOwnProperty("value")) {
		        setValue(attributes.value);
		    }
		    /*TODO improve with observer */
		    var formWaiting = function() {
		        if (datetime.form === null) {
		            setTimeout(formWaiting, 50);
		        } else {
		    		setValue(datetime.value);
		            datetime.form.onreset = function() {
		                datetime.value = '';
		            };
		        }
		    };

			/* for default value */
		    formWaiting();
	
     		/* form bind */
		    datetime.onbind = function() {
		        setValue(datetime.value);
		    };

    		/* if sub date or sub time change */
		    datetime.onchange = function(event) {
		        if (attributes.strict) {
		             if (date.value === '' || time.value === '') {
		                /*event.preventDefault(); // not working in firefox */
               			return false;
		             } else {
		                 datetime.value = date.value + "T" + time.value;
		             }
		        } else {
		             if (date.value === '' && time.value === '') {
		                /*event.preventDefault(); // not working in firefox */
		                datetime.value = '';
               			return false;
		             } else if (date.value === '') {
		                 datetime.value = time.value;
		             } else if (time.value === '') {
		                  datetime.value = date.value;
		            } else {
		                 datetime.value = date.value + "T" + time.value;
		             }
		        }
		    };
		    return datetime;
		}
	},
	parseValue: function(type, value) {
		/* TODO week*/
		if (totiTranslations.timestamp.dateString.hasOwnProperty(type) && value !== '' && value !== null) {
			return new Date(value).toLocaleDateString(
            	totiLang.getLang().replace("_", "-"),
				totiTranslations.timestamp.dateString[type]
			);
		}
		if (totiTranslations.timestamp.timeString.hasOwnProperty(type) && value !== '' && value !== null) {
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
			if (clickSettings.submitConfirmation !== null && clickSettings.submitConfirmation !== undefined ) {
				var confirmMessage = clickSettings.submitConfirmation;
				if (typeof confirmMessage === 'string') {
					confirmMessage = function() {
						return totiDisplay.confirm(clickSettings.submitConfirmation);
					};
				}
				if (!confirmMessage()) {
					return false;
				}
			}
			if (clickSettings.async) {
				totiDisplay.fadeIn();
				totiLoad.load(
					clickSettings.href,
					clickSettings.method,
					{},
					{},
					clickSettings.params
				).then(function(res) {
					totiDisplay.fadeOut();
					if (clickSettings.hasOwnProperty('onSuccess')) {
						totiUtils.execute(clickSettings.onSuccess, [res]);
					} else {
						totiDisplay.flash("success", totiTranslations.buttons.actionSuccess);
					}
				}).catch(function(xhr) {
					totiDisplay.fadeOut();
					if (clickSettings.hasOwnProperty('onFailure')) {
						totiUtils.execute(clickSettings.onFailure, [xhr]);
					} else {
						totiDisplay.flash("error", totiTranslations.buttons.actionFailure);
					}
				});
			} else {
				totiDisplay.fadeOut();
				/* totiControl.load.link(href, method, {}, totiControl.getHeaders()); */
				window.location = clickSettings.href;
			}
		};
	}
};