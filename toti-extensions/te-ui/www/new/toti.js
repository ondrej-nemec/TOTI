const Toti = function () {
	class Input {
		name = null;
		type = null;
		container = null;
		onChangeCallbacks = [];
		exclude = false;
		disabled = false;
		constructor(type, name, container) {
			this.name = name;
			this.type = type;
			this.container = container;
		}
		getName() {
			return this.name;
		}
		getType() {
			return this.type;
		}
		getContainer() {
			return this.container;
		}
		onChange(callback) {
			this.onChangeCallbacks.push(callback);
		}
		_callOnChangeCallbacks() {
			var instance = this;
			this.onChangeCallbacks.forEach((func)=>{
				func(instance);
			});
		}
		isExcluded() {
			return this.excluded;
		}
		setExcluded(isExcluded) {
			this.excluded = isExcluded;
		}
		/*just api*/
		setDisabled(isDisabled) {
			console.warn('Missing implementation of "setDisabled(isDisabled)" method');
		}
		getValue() {
			console.warn('Missing implementation of "getValue" method');
		}
		setValue(value) {
			console.warn('Missing implementation of "setValue(value)" method');
		}
		clear() {
			console.warn('Missing implementation of "clear" method');
		}
		/* shared logic */
		__setAttributes(container, attributes, except = {}) {
			for (const[key, value] of Object.entries(attributes)) {
				if (key === 'exclude') {
					this.excluded = value;
				} else if (!except.hasOwnProperty(key) || except[key](this, container, value) === true) {
					container.setAttribute(key, value);
				}
			}
		}
	}
	class BaseInput extends Input {
		value = null;
		fGetValue = null;
		fSetValue = null;
		constructor(create, fGetValue, fSetValue, type, attributes) {
			var input = create();
			super(type, attributes.name, input);
			this.fGetValue = fGetValue;
			this.fSetValue = fSetValue;
			this.__setAttributes(input, attributes, {
				value: (t, c, value)=>{
					t.setValue(value);
				},
				disabled: (t, c, value)=>{
					input.disabled = value;
				}
			});

			var instance = this;
			input.addEventListener('change', ()=>{
				instance.setValue(fGetValue(input));
			});
		}
		setValue(value) {
			this.fSetValue(this.container, value);
			this.value = this.fGetValue(this.container); /* converting value to required format */
			this._callOnChangeCallbacks();
		}
		getValue() {
			return this.value;
		}
		clear() {
			this.setValue('');
		}
		setDisabled(isDisabled) {
			this.container.disabled = isDisabled;
		}
	}
	class StandartInput extends BaseInput {
		constructor(type, attributes) {
			super(
				()=>{
					var input = document.createElement("input");
					input.setAttribute("type", type);
					return input;
				}, (input)=>{
					return input.value;
				}, (input, value)=>{
					input.value = value;
				},
				type, attributes
			);
		}
	}
	class CheckBoxInput extends BaseInput {
		constructor(type, attributes) {
			super(
				()=>{
					var input = document.createElement("input");
					input.setAttribute("type", type);
					return input;
				}, (input)=>{
					return input.checked;
				}, (input, value)=>{
					input.checked = !!value;
				},
				type, attributes
			);
		}
	}
	class TextAreaInput extends BaseInput {
		constructor(type, attributes) {
			super(
				()=>{
					return document.createElement("textarea");
				}, (input)=>{
					return input.value;
				}, (input, value)=>{
					input.value = value;
				},
				type, attributes
			);
		}
	}
	class RadioListInput extends Input {
		value = null;
		inputs = {};
		constructor(type, attributes) {
			var container = document.createElement("div");
			super(type, attributes.name, container);
			this.__setAttributes(container, attributes, {
				value: (t, c, value)=>{
					t.setValue(value);
				},
				radios: ()=>{},
				name: ()=>{},
				disabled: ()=>{}
			});

			var instance = this;
			attributes.radios.forEach((radio)=>{
				var inputDiv = document.createElement('div');
				container.appendChild(inputDiv);
				var id = attributes.name + "-" + radio.id;
				inputDiv.setAttribute('id', id + "-block");
				if (radio.hasOwnProperty("title")) {
					var label = document.createElement('label');
					label.innerText = radio.title;
					label.setAttribute("for", id);
					inputDiv.setAttribute('id', id + "-label");
					inputDiv.appendChild(label);
				}
				var input = document.createElement('input');
				inputDiv.appendChild(input);
				input.setAttribute('type', 'radio');
				var settings = {
					id: id,
					name: attributes.name,
					form: attributes.formName,
					value: radio.value
				};
				if (radio.value === attributes.value) {
					settings.checked = 'checked';
				}
				if (attributes.hasOwnProperty('required')) {
					settings.required = attributes.required;
				}
				if (attributes.hasOwnProperty('disabled')) {
					settings.disabled = attributes.disabled;
				}
				this.__setAttributes(input, settings);
				input.addEventListener('change', ()=>{
					instance.setValue(input.value);
				});
				instance.inputs[radio.value] = input;
			});
		}
		setValue(value) {
			this.value = value;
			this.inupts[value].checked = 'checked';
			this._callOnChangeCallbacks();
		}
		getValue() {
			return this.value;
		}
		clear() {
			this.inputs.forEach((input)=>{
				input.checked = false;
			})
			this.value = null;
		}
		setDisabled(isDisabled) {
			this.inputs.forEach((input)=>{
				input.disabled = isDisabled;
			});
		}
	}
	class OptionalInput extends Input {
		parent = null;
		used = false;
		checkbox = null;
		constructor(parent) {
			var container = document.createElement("div");
			super(parent.getType(), parent.getName(), container);

			this.parent = parent;
			var checkbox = document.createElement("input");
			this.checkbox = checkbox;
			checkbox.setAttribute("type", "checkbox");

			checkbox.checked = false;
			parent.setDisabled(true);

			var instance = this;
			checkbox.onclick = ()=>{
				instance.used = checkbox.checked;
				if (checkbox.checked) {
					parent.setDisabled(false);
				} else {
					parent.setDisabled(true);
				}
			};
			container.appendChild(checkbox);
			container.appendChild(parent.getContainer());
		}
		setValue(value) {
			this.parent.setValue();
			this._callOnChangeCallbacks();
		}
		getValue() {
			if (this.used) {
				return this.parent.getValue();
			}
			return null;
		}
		clear() {
			this.parent.clear();
		}
		setDisabled(isDisabled) {
			this.parent.setDisabled(isDisabled);
			this.checkbox.disabled = isDisabled;
		}
	}
	class DynamicInput extends Input {}
	class InputList extends Input {}
	class LoadedInput extends Input {}
	class DatetimeInput extends Input {}

	var createInput = (attributes)=>{
		if (!attributes.hasOwnProperty('type')) {
			console.error("Missing attribute type", attributes);
			return null;
		}
		var type = attributes.type;
		delete attributes.type;
		switch(type) {
			case 'text':
			case 'email':
			case 'search':
			case 'url':
			case 'tel':
				if (attributes.hasOwnProperty('load') || attributes.hasOwnProperty('options')) {
					// return totiControl.inputs._createInput('text', attributes);
					// TODO
				}
				return new StandartInput(type, attributes);
			case 'textarea':
				return new TextAreaInput(type, attributes);
			case 'password':
			case 'range':
			case 'color':
				var input = new StandartInput(type, attributes);
				/* for password only */
				if (attributes.hasOwnProperty('optional') && attributes.optional === true) {
					return input;
				}
				if (!attributes.hasOwnProperty("required") || !attributes.required) {
					return input;
				}
				return new OptionalInput(input);
			case 'button':
				// TODO
				return new StandartInput(type, attributes);
			case 'radiolist':
				return new RadioListInput(type, attributes);
			case 'checkbox':
				return new CheckBoxInput(type, attributes);
			case 'select':
				// TODO
				return new StandartInput(type, attributes);
			case 'datetime':
			case 'datetime-local':
				// TODO
				// datetime - potreba upravit hodnotu + strict (ten udelat lepe)
				return new StandartInput(type, attributes);
			default:
				return new StandartInput(type, attributes);
		}
	};
	return {
		init(configuration) {
			// Toti.configuration = configuration;
		},
		createInput(attributes) {
			return createInput(attributes);
		},
		Form: (conf)=>{
			return new class {
				container = null;
				inputs = null;
				constructor(configuration) {
					console.log("Form", configuration, Toti);
				}
				addInput(fieldConf) {}
				getInput(name) {}
				submit() {}
				loadBind(loadConfig) {}
				bindValues(values) {}
				loadPlaceholders(loadConfig) {}
				render() {}
			}(conf)
		},
		Grid: (conf)=>{
			return new class {
				container = null;
				columns = null;
				constructor(configuration) {
					console.log("Grid", configuration, Toti);
				}
				render() {}
				sortBy(name, direction, refresh = true) {}
				nextSort(name, refresh = true) {}
				filterBy(name, value, refresh = true) {}
				setPageIndex(pageIndex, refresh = true) {}
				addNextPage(refresh = true) {}
				setPageSize(pageSize, refresh = true) {}
				startRefresh() {}
				stopRefresh() {}
				getSelectedRow() {}
				refreshData(clearPrevious = true) {}
			}(conf)
		},
		load(configuration) {
		/*	function iterateParams(params, callback) {
				function parseParams(name, value, addItem) {
					if (value === null) {
						/* ignore *//*
					} else if (Array.isArray(value)) {
						value.forEach((item)=>{
							parseParams(name + '[]', item, addItem);
						});
					} else if (typeof value === 'object') {
						for(const[key, item] of Object.entries(value)) {
							parseParams(name + '[' + key + ']', item, addItem);
						}
					} else {
						addItem(name, value);
					}
				}
				function onItem(name, value) {
					parseParams(name, value, (n, v)=>{
						callback(n, v);
					});
				}
				if (params instanceof URLSearchParams) {
					params.forEach((value, name)=>{
						onItem(name, value);
					});
				} else if (params instanceof FormData) {
					for (const[name, value] of params.entries()) {
						onItem(name, value);
					}
				} else if (typeof params === 'object') {
					for(const[name, value] of Object.entries(params)) {
						onItem(name, value);
					}
				}
			}
			function getProperty(name, def = null) {
				if (configuration.hasOwnProperty(name)) {
					return configuration[name];
				}
				if (def === null) {
					throw new Error('Toti.load: Parameter "' + name '" is required.');
				}
				return def;
			}
			var url = new URL(getProperty('url'), document.location);
			var method = getProperty('method', 'get').toLowerCase();
			
			iterateParams(getProperty('query' {}), (name, value)=>{
				url.searchParams.set(name, value);
			});

			/* sending body is not supported for GET and HEAD *//*
			if (method === "get" || method === "head") {
				iterateParams(getProperty('body', {}), (name, value)=>{
					url.searchParams.set(name, value);
				});
			}

			var xhr = new XMLHttpRequest();
			xhr.open(method, url.toString());

			for (const[name, value] of Object.entries(getProperty('headers' {}))) {
				xhr.setRequestHeader(name, value);
			}
*/
			// TODO zkusit fetch?
			/*
			pri GET a HEAD prevest body na params
			url - extrahovat pripadne query parametry
			*/
		}
	};
}();