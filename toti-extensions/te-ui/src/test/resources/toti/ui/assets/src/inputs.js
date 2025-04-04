class Input {
	name = null;
	type = null;
	title = null;
	/* to be different from null so setValue is executed */
	value = undefined;

	editable = true;
	excluded = false;
	disabled = false;
	required = false;

	container = null;

	onChangeCallbacks = {};
	errors = {};
	validations = {};
	validationValues = {};

	callbacks = null;

	data = {};
	/*
	* type
	* attributes
	* callbacks {
		// varse raw value. Value is from Input.setValue()
		parseValue(value)
		// create container
		create(instance, editable, createAttributes)
		// set value to container. Value is already parsed
		setValue(instance, container, value, editable): value === null => clear
		// disable container
		setDisabled(instance, container, isDisabled, editable)
	}
	* expected: {
		attributeName: (instance, attributeValue)=>{
			true -> add to container
			otherwise -> ignore attribute
		}
	}
	*/
	constructor(type, attributes, callbacks, except = {}) {
		this.name = attributes.hasOwnProperty('name') ? attributes.name : null;
		this.type = type;
		this.callbacks = callbacks;

		var inputValue = null;
		var createAttributes = {};
		for (const[key, value] of Object.entries(attributes)) {
			switch (key) {
				case 'title':
					this.title = value;
					break;
				case 'tooltip':
					createAttributes['title'] = value;
					break;
				case 'exclude':
					this.excluded = value;
					break;
				case 'editable':
					this.editable = value;
					break;
				case 'disabled':
					this.disabled = value;
					break;
				case 'value':
					inputValue = value;
					break;
				case 'required':
					this.required = value;
					break;
				case 'optional':
				case 'type':
					/* ignore here*/
					break;
				case 'onChange':
					if (Array.isArray(value)) {
						var object = this;
						value.forEach((callback)=>{
							object.onChange(callback);
						});
					} else {
						this.onChange(value);
					}
					break;
				default:
					if (!except.hasOwnProperty(key) || except[key](value) === true) {
						createAttributes[key] = value;
					}
			}
		}
		this.container = callbacks.create(this, this.editable, createAttributes);

		/* parse value (some inputs works with NULL value), then set and call onChange */
		this.setValue(inputValue);
		/* if no defalut value set: parse NULL value state - some inputs not suport null value or null can be valid select option */
		//this.value = callbacks.parseValue(inputValue);
		/* set default value to input - no onchange calling */
		//callbacks.setValue(this, this.container, this.value, this.editable);

		if (this.disabled) {
			callbacks.setDisabled(this, this.container, this.disabled, this.editable);
		}
	}
	setData(name, value) {
		this.data[name] = value;
	}
	getData(name) {
		return this.data[name];
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
	getValue() {
		return this.value;
	}
	getTitle() {
		return this.title;
	}
	isExcluded() {
		return !this.editable || this.excluded;
	}
	isRequired() {
		return this.required;
	}
	isDisabled() {
		return this.disabled;
	}

	setExcluded(isExcluded) {
		this.excluded = isExcluded;
	}
	setDisabled(isDisabled) {
		this.disabled = isDisabled;
		this.callbacks.setDisabled(this, this.container, isDisabled, this.editable);
	}
	setRequired(isRequired) {
		this.required = isRequired;
	}
	setValue(value, source = null) {
		var newValue = this.parseValue(value);
		if (this.value === newValue) {
			return;
		}
		var oldValue = this.value;
		this.value = newValue;
		this.clearErrors();
		this.callbacks.setValue(this, this.container, this.value, this.editable);
		this._callOnChangeCallbacks(oldValue, source);
	}
	parseValue(rawValue) {
		return this.callbacks.parseValue(rawValue);
	}

	clear(source = null) {
		this.setValue(null, source);
	}
	onChange(callback, source = null) {
		var key = source === null ? '' : source;
		if (!this.onChangeCallbacks.hasOwnProperty(key)) {
			this.onChangeCallbacks[key] = [];
		}
		this.onChangeCallbacks[key].push((instance, oldValue)=>{
			Toti.utils.execute(callback, [instance, oldValue]);
		});
	}
	_callOnChangeCallbacks(oldValue, source) {
		if (source === undefined) {
			throw new Error('_callOnChangeCallbacks requires source');
		}
		var instance = this;
		for (const[index, callbacks] of Object.entries(this.onChangeCallbacks)) {
			if (index !== source) {
				callbacks.forEach((func)=>{
					func(instance, oldValue);
				});
			}
		}
	}
	addError(error, params = {}) {
		this.errors[error] = params;
	}
	getErrors() {
		return this.errors;
	}
	clearErrors() {
		this.errors = {};
	}
	addValidation(name, callback) {
		this.validations[name] = callback;
		return this;
	}
	isValid() {
		this.clearErrors();
		if (!this.editable) {
			return true;
		}
		if (this.required && this._isValueMissing()) {
			this.addError('input.required', {});
			return false;
		}
		var result = true;
		var instance = this;
		for (const[name, callback] of Object.entries(this.validations)) {
			var valid = callback(this, this.getValue(), result); /* TODO maybe send addError callback */
			if (valid === undefined) {
				throw new Error('Validation result is invalid: ' + name);
			}
			if (valid !== true) {
				result = false;
			}
		}
		return result;
	}
	_isValueMissing() {
		return this.value === null;
	}

	/**********************/
	addValidationMinLength(attributes) {
		return this._addValidation('minlength', attributes, (instance, currentValue, minlength)=>{
			if (currentValue === null) {
				return true;
			}
			var res = currentValue.length >= minlength;
			if (!res) {
				this.addError('input.tooShort', {l: minlength, c:currentValue.length});
			}
			return res;
		});
	}
	addValidationMaxLength(attributes) {
		return this._addValidation('maxlength', attributes, (instance, currentValue, maxlength)=>{
			if (currentValue === null) {
				return true;
			}
			var res = currentValue.length <= maxlength;
			if (!res) {
				this.addError('input.tooLong', {l: maxlength, c:currentValue.length});
			}
			return res;
		});
	}
	addValidationPattern(attributes) {
		return this._addValidation('pattern', attributes, (instance, currentValue, pattern)=>{
			if (currentValue === null) {
				return true;
			}
			var res = currentValue.match(pattern);
			if (res === null) {
				this.addError('input.pattern', {});
				return false;
			}
			return true;
		});
	}
	addValidationMinValue(attributes) {
		return this._addValidation('min', attributes, (instance, currentValue, min)=>{
			if (currentValue === null) {
				return true;
			}
			var res = currentValue >= min;
			if (!res) {
				this.addError('input.tooSmall', {m: min});
			}
			return res;
		});
	}
	addValidationMaxValue(attributes) {
		return this._addValidation('max', attributes, (instance, currentValue, max)=>{
			if (currentValue === null) {
				return true;
			}
			var res = currentValue <= max;
			if (!res) {
				this.addError('input.tooBig', {m: max});
			}
			return res;
		});
	}
	addValidationStep(attributes) {
		return this._addValidation('step', attributes, (instance, currentValue, step)=>{
			/*if (currentValue === null) {
				return true;
			}*/
			var res = true;
			if (currentValue === null) {
				/* ignore */
			} else if (Number.isInteger(step)) {
				/*full number*/
				res = currentValue%step === 0;
			} else {
				var tens = 1;
				var multipleStep = step;
				do {
					tens *= 10;
					multipleStep = step * tens;
				} while(!Number.isInteger(multipleStep) && tens < 1000000000);
				if (Number.isInteger(multipleStep)) {
					res = (currentValue * tens)%multipleStep === 0;
				} else {
					res = false;
				}
			}
			if (!res) {
				this.addError('input.stepIsWrong', {s: step});
			}
			return res;
		});
	}

	/*
	* propertyName
	* attributes
	* validate(currentValue, attributeValue)=>{
		this.addError('message', {});
		return true/false;
	}
	*/
	_addValidation(propertyName, attributes, validate) {
		var value = null;
		if (attributes.hasOwnProperty(propertyName)) {
			value = attributes[propertyName];
		}
		return this.createValidation(propertyName, value, validate);
	}

	/*
	* propertyName
	* attributes
	* validate(currentValue, attributeValue)=>{
		this.addError('message', {});
		return true/false;
	}
	*/
	createValidation(propertyName, defRule, validate) {
		var methodName = 'set' + Toti.utils.capitalizeFirstLetter(propertyName);
		if (!this.editable) {
			/* empty method to prevent unnessessary errors */
			this[methodName] = (value)=>{};
			return this;
		}
		this.validationValues[propertyName] = {
			method: methodName,
			value: defRule
		};
		var object = this;
		this[methodName] = (value)=>{
			object.validationValues[propertyName].value = value;
			object.callbacks.setRule(object, object.container, propertyName, value);
		};
		return this.addValidation(propertyName, (instance, value)=>{
			var rule = object.validationValues[propertyName].value;
			if (rule === null) {
				return true;
			}
			return validate(instance, value, rule);
		});
	}


	popupHT(screenInnerH, screenOuterH, inputTop, inputHeight, requiredHeight) {
		var screen = Math.min(screenInnerH, screenOuterH);

		var available = screen - inputTop - inputHeight;
		if (available >= requiredHeight) {
			return {
				top: inputTop + inputHeight,
				height: requiredHeight
			};
		}
		if (inputTop >= requiredHeight) {
			return {
				top: inputTop - requiredHeight,
				height: requiredHeight
			};
		}
		if (inputTop > available) {
			return {
				top: 0,
				height: inputTop
			};
		}
		return {
			top: inputTop + inputHeight,
			height: available
		};
	}

	popupWL(screenInnerW, screenOuterW, inputLeft, inputWidth, popupRequireddWidth) {
		var screen = Math.min(screenInnerW, screenOuterW);
		var requiredWidth = Math.max(inputWidth, popupRequireddWidth);
		if (screen < requiredWidth ) {
			return {
				width: screen,
				left: 0
			};
		}
		if (screen - inputLeft >= requiredWidth) {
			return {
				width: requiredWidth,
				left: inputLeft
			};
		}
		var available = screen - requiredWidth;
		if (available > 0) {
			return {
				width: requiredWidth,
				left: available
			};
		}
		return {
			width: screen,
			left: 0
		};
	}
}
/* for jest unit test */
if (typeof module !== 'undefined') {
	module.exports.createInput = (type, attributes, callbacks, except = {})=>{
		return new Input(type, attributes, callbacks, except);
	};
}
class OptionalInput extends Input {
	parent = null;
	used = true;
	checkbox = null;
	constructor(type, attributes, createParent) {
		var checkbox = null;
		var parent = null;
		var attributeClone = totiUtils.clone(attributes);
		super(type, attributes, {
			parseValue: (value)=>{
				return parent.parseValue(value);
			},
			create: (instance, editable, createAttributes)=>{
				if (editable) {
					var containerAttributes = {};
					var parentAttributes = {};
					for(const[name, value] of Object.entries(createAttributes)) {
						switch(name) {
							case 'placeholder':
							case 'name':
								parentAttributes[name] = value;
								break;
							default:
								containerAttributes[name] = value;
						}
					}

					parent = createParent(parentAttributes);
					parent.getContainer().classList.add('optional-input-field');


					checkbox = document.createElement('input');
					checkbox.setAttribute('type', 'checkbox');
					checkbox.classList.add('optional-input-switch');
					checkbox.addEventListener('change', ()=>{
						instance.setUsed(checkbox.checked);
					});

					var container = document.createElement("div");
					for(const[name, value] of Object.entries(containerAttributes)) {
						container.setAttribute(name, value);
					}
					container.classList.add('optional-input-container');
					container.setAttribute('data-name', parent.getName());
					container.appendChild(checkbox);
					container.appendChild(parent.getContainer());
					return container;
				} else {
					parent = createParent(attributeClone);
					checkbox = {};
					return parent.getContainer();
				}
			},
			setValue: (instance, container, value, editable)=>{
				/*
				* need to be here
				* must be after parser and change check, but before callbacks
				*/
				instance.setUsed(value !== null);

				parent.setValue(value);
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				checkbox.disabled = isDisabled;
				if (isDisabled) {
					parent.setDisabled(isDisabled);
				} else {
					parent.setDisabled(!checkbox.checked);
				}
			},
			setRule: (instance, container, name, rule)=>{
				// TODO
				// TODO receive all validate set methods
			}
		});
		if (this.required) {
			throw new Error('Cannot set optional input to required');
		}
		this.checkbox = checkbox;
		this.parent = parent;
		/* for default value, cannot be called in callbacks */
		this.setUsed(this.value !== null);
		for (const[name, definition] of Object.entries(parent.validationValues)) {
			this[definition.method] = parent[definition.method];
		}
		/* mainly for index.html - to see set buttons */
		this.validationValues = parent.validationValues;
	}
	setUsed(isUsed) {
		if (!this.editable) {
			return;
		}
		if (this.checkbox === undefined) {
			/* fix during initialization checkbox is not available, set at the end of constructor */
			return;
		}
		this.used = isUsed;
		this.checkbox.checked = isUsed;
		if (!this.isDisabled()) {
			this.parent.setDisabled(!isUsed);
		}
	}
	isUsed() {
		return this.used;
	}
	getValue() {
		if (this.used) {
			return this.parent.getValue();
		}
		return null;
	}
	setRequired(isRequired) {
		throw new Error('Cannot set optional input to required');
	}
}
/******************************/

class StandartInput extends Input {
	constructor(type, attributes, fParseValue = null) {
		super(type, attributes, {
			parseValue: (value)=>{
				if (fParseValue === null) {
					return value;
				}
				return fParseValue(value);
			},
			create: (instance, editable, createAttributes)=>{
				var container = null;
				if (editable) {
					container = document.createElement("input");
					container.setAttribute("type", type);
					container.addEventListener('change', ()=>{
						instance.setValue(container.value);
					});
				} else {
					container = document.createElement("span");
				}
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					container.value = value;
				} else {
					container.innerText = value;
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				if (rule === null) {
					container.removeAttribute(name);
				} else {
					container.setAttribute(name, rule);
				}
			}
		});
	}
}

class CheckInput extends Input {
	options = null;
	constructor(type, attributes) {
		var options = null;
		super(type, attributes, {
			parseValue: (value)=>{
				return !!value;
			},
			create: (instance, editable, createAttributes)=>{
				var container = null;
				if (editable) {
					container = document.createElement("input");
					container.setAttribute("type", type);
					container.addEventListener('change', ()=>{
						instance.setValue(container.checked);
					});
				} else {
					container = document.createElement("span");
				}
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					container.checked = value;
				} else if (options === null) {
					container.innerText = value;
				} else {
					container.innerText = options[value];
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.disabled = isDisabled;
					if (isDisabled) {
						container.setAttribute('disabled', true);
					} else {
						container.removeAttribute('disabled');
					}
				}
			},
			setRule: (instance, container, name, rule)=>{
				if (rule === null) {
					container.removeAttribute(name);
				} else {
					container.setAttribute(name, rule);
				}
			}
		}, {
			options: (values)=>{
				options = values;
			}
		});
		this.options = options;
	}
	_isValueMissing() {
		return !this.value;
	}
}

class OptionInput {

	value = null;
	title = null;
	disabled = null;
	originDisabled = null;
	selectable = null;
	/* @deprecated - no reason here ? */
	level = null;
	parent = null;

	childs = new SortedMap();
	container = null;

	callbacks = null;

	isGroup = false;
	input = null;

	/*
	* value
	* title
	* disabled
	* selectable
	* level
	* callbacks {
		createInput(value, title, disabled)
		createGroup(this, input|null)
		addChild(this, childContainer)
		setDisabled(this, isDisabled)
		setValue(this, isSelected)
	}
	*/
	constructor(value, title, disabled, selectable, level, callbacks) {
		this.value = value;
		this.title = title;
		this.disabled = disabled;
		this.originDisabled = disabled;
		this.selectable = selectable;
		this.level = level;
		this.callbacks = callbacks;

		this.container = document.createElement("div");
		if (selectable) {
			this.isGroup = false;
			this.input = callbacks.createInput(value, title, disabled);
			this.container.appendChild(this.input);
		} else {
			this.isGroup = true;
			this.container.appendChild(callbacks.createGroup(this, null));
		}
	}
	addChild(child) {
		if (!this.isGroup) {
			this.input.remove();
			this.input = this.callbacks.createGroup(this, this.input);
			this.container.appendChild(this.input);
		}
		this.isGroup = true;
		if (this.childs.exists(child.getKey())) {
			return;
		}
		this.callbacks.addChild(this, child.getContainer());
		this.childs.put(child.getKey(), child);
		child.parent = this;
		child.setDisabled(this.disabled, true);
	}
	getParent() {
		return this.parent;
	}
	isDisabled() {
		return this.disabled;
	}
	isSelectable() {
		return this.selectable;
	}
	isValid() {
		return !this.isDisabled() && this.isSelectable();
	}
	setDisabled(isDisabled, internal = false) {
		/* parent change child to enabled, but origin configuration define disabled */
		if (internal && !isDisabled && this.originDisabled) {
			isDisabled = this.originDisabled;
		} else {
			this.callbacks.setDisabled(this, isDisabled);
		}
		if (this.parent !== null && this.parent.isDisabled() && !isDisabled) {
			isDisabled = true;
		}
		this.disabled = isDisabled;
		this.childs.forEach((key, child)=>{
			child.setDisabled(isDisabled, true);
		});
	}
	setValue(value) {
		this.callbacks.setValue(this, value === this.value);
		this.childs.forEach((key, child)=>{
			child.setValue(value);
		});
	}
	getValue() {
		return this.value;
	}
	getTitle() {
		return this.title;
	}
	getContainer() {
		return this.container;
	}
	getInput() {
		return this.input;
	}
	getKey() {
		return this.value; //  + "_" + this.title;
	}
	getChild(key) {
		if (this.childs.exists(key)) {
			return this.childs.get(key);
		}
		return null;
	}
}
class OptionsInput extends Input {
	selectedOption = null;
	promise = null;
	element = null;
	/*
	* type
	* attributes
	* callbacks {
		// create main container
		createContainer(instance, elements, isSearch, prompt)
		// set value to container, not to options, called with parsed value
		setValue(instance, container, selectedOption)
		// disable container, not options
		setDisabled(instance, container, isDisabled)
		// create OptionInput
		createOption(instance, value, title, disabled, selectable, level)
	}
	* except
	*/
	constructor(type, attributes, callbacks, except = {}) {
		var elements = new SortedMap();
		var allowedValues = {};
		var selectedOption = {
			selected: null
		};
		var addOptions = (instance, options, parentElement, level = 0)=>{
			var parent = null;
			if (parentElement instanceof OptionInput) {
				parent = parentElement;
			} else {
				parent = {
					addChild: (child)=>{
						if (parentElement !== null) {
							parentElement.add(child.getContainer());
						}
						elements.put(child.getKey(), child);
					},
					getChild: (key)=>{
						if (elements.exists(key)) {
							return elements.get(key);
						}
						return null;
					}
				};
			}
			options.forEach((option)=>{
				var disabled = option.hasOwnProperty('disabled') ? option.disabled : false;
				var selectable = option.hasOwnProperty('selectable') ? option.selectable : true;

				var input = callbacks.createOption(instance, option.value, option.title, disabled, selectable, level);
				var origin = parent.getChild(input.getKey())
				if (origin === null) {
					parent.addChild(input);
				} else {
					input = origin;
				}
				if (!allowedValues.hasOwnProperty(input.getValue())) {
					allowedValues[input.getValue()] = input;
				}

				if (option.hasOwnProperty('options')) {
					addOptions(instance, option.options, input, level + 1);
				}
			});
		}

		var options = null;
		var load = null;
		var search = false;
		var prompt = null;
		var promise = null;
		except.options = (value)=>{
			options = value;
		};
		except.load = (value)=>{
			load = value;
		};
		except.prompt = (value)=>{
			prompt = value;
		};
		except.search = (value)=>{
			search = value;
		};
		super(type, attributes, {
			parseValue: (value)=>{
				if (!allowedValues.hasOwnProperty(value)) {
					selectedOption.selected = null;
					return null;
				}
				var option = allowedValues[value];
				if (!option.isSelectable()) {
					selectedOption.selected = null;
					return null;
				}
				selectedOption.selected = option;
				return value;
			},
			create: (instance, editable, createAttributes)=>{
				if (options === null) {
					throw new Error("Undefined options");
				}
				function initOptions(parentElement, useContainer) {
					var container = useContainer ? parentElement : null;
					addOptions(instance, options, container);
					if (load === null) {
						return new Promise(resolve => resolve());
					}
					var animation = Toti.animations.inputLoading(parentElement);
					return Toti.load({
						url: load.url,
						method: load.method,
						params: load.params,
						async: true,
						dataType: 'json'
					}).then((data)=>{
						addOptions(instance, data, container);
					}).then(()=>{
						if (attributes.hasOwnProperty('value') && instance.getSelected() === null) {
							instance.setValue(attributes.value);
						}
						animation.remove();
					}).catch((error)=>{
						animation.failure();
						console.error(error);
					});
				}
				var container = null;
				if (editable) {
					container = callbacks.createContainer(instance, elements, search, prompt);
					promise = initOptions(container, true);
				} else {
					container = document.createElement("span");
					promise = initOptions(container, false);
				}
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					elements.forEach((key, el)=>{
						el.setValue(value);
					});
					callbacks.setValue(instance, container, selectedOption.selected);
				} else {
					container.innerText = allowedValues[value].getTitle();
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					callbacks.setDisabled(instance, container, isDisabled);
					elements.forEach((key, el)=>{
						el.setDisabled(isDisabled);
					});
				}
			},
			setRule: (instance, container, name, rule)=>{
				callbacks.setRule(instance, container, name, rule);
			}
		}, except);
		this.promise = promise;
		this.selectedOption = selectedOption;
		this.elements = elements;
		this.addValidation('selected', (instance, value)=>{
			if (instance.getSelected() === null) {
				return true;
			}
			var option = instance.getSelected();
			var res = option.isValid();
			if (!res) {
				instance.addError('input.valueNotAllowed', {v: value, t: option.getTitle()});
			}
			return res;
		});
	}
	getSelected() {
		return this.selectedOption.selected;
	}
	afterOptionLoad(then) {
		return this.promise.then(then);
	}
	getOptions() {
		return this.elements;
	}
}

/**********************/

class TextAreaInput extends Input {
	input = null;
	info = null;
	constructor(attributes) {
		var input = null;
		var info = null;
		var setInfoCount = (instance)=>{
			var maxLength = null; // ;
			if (instance.validationValues.hasOwnProperty('maxlength')) {
				maxLength = instance.validationValues.maxlength.value;
			}
			var minLength = null; //0;
			if (instance.validationValues.hasOwnProperty('minlength')) {
				minLength = instance.validationValues.minlength.value;
			}
			if (maxLength === null && minLength === null) {
				info.innerText = '';
				return;
			}
			if (maxLength === null) {
				maxLength = '~';
			}
			if (minLength === null) {
				minLength = 0;
			}
			info.innerText = minLength + '/' + input.value.length + '/' + maxLength;
		};
		super('textarea', attributes, {
			parseValue: (value)=>{
				if (value === '') {
					return null;
				}
				return value;
			},
			create: (instance, editable, createAttributes)=>{
				var container = document.createElement("span");
				if (editable) {
					input = document.createElement("textarea");
					input.addEventListener('change', ()=>{
						instance.setValue(input.value);
					});
					input.addEventListener('keyup', ()=>{
						setInfoCount(instance);
					});

					info = document.createElement('div');
					info.classList.add('textarea-length-info');

					for(const[name, value] of Object.entries(createAttributes)) {
						input.setAttribute(name, value);
					}

					container.appendChild(input);
					container.appendChild(info);
				} else {
					for(const[name, value] of Object.entries(createAttributes)) {
						container.setAttribute(name, value);
					}
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					input.value = value;
					setInfoCount(instance);
				} else {
					container.innerText = value;
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					input.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				var input = container.querySelector('textarea');
				if (rule === null) {
					input.removeAttribute(name);
				} else {
					input.setAttribute(name, rule);
				}
				setInfoCount(instance);
			}
		});
		this.input = input;
		this.info = info;
		this.addValidationMinLength(attributes);
		this.addValidationMaxLength(attributes);
		if (this.value === null) {
			setInfoCount(this);
		}
	}
}


class RangeInput extends Input {
	constructor(attributes) {
		var input = null;
		var maxPanel = null;
		var minPanel = null;
		var currentPanel = null;
		var setInfoCount = (instance, current)=>{
			if (current !== null) {
				currentPanel.innerText = current;
			} else {
				currentPanel.innerText = input.value;
			}
			if (instance.validationValues.hasOwnProperty('max') && instance.validationValues.max.value !== null) {
				maxPanel.innerText = instance.validationValues.max.value;
			} else {
				maxPanel.innerText = 100;
			}
			if (instance.validationValues.hasOwnProperty('min') && instance.validationValues.min.value !== null) {
				minPanel.innerText = instance.validationValues.min.value;
			} else {
				minPanel.innerText = 0;
			}
			input.setAttribute('max', maxPanel.innerText);
			input.setAttribute('min', minPanel.innerText);
		};
		super('range', attributes, {
			parseValue: (value)=>{
				// TODO shared with number - one place
				if (isNaN(value) || value === null) {
					return null;
				}
				return parseFloat(value);
			},
			create: (instance, editable, createAttributes)=>{
				var container = document.createElement("span");
				if (editable) {
					input = document.createElement("input");
					input.setAttribute('type', 'range');
					input.addEventListener('change', ()=>{
						instance.setValue(input.value);
					});

					maxPanel = document.createElement('span');
					maxPanel.classList.add('toti-range-max');
					minPanel = document.createElement('span');
					minPanel.classList.add('toti-range-min');
					currentPanel = document.createElement('span');
					currentPanel.classList.add('toti-range-current');

					for(const[name, value] of Object.entries(createAttributes)) {
						input.setAttribute(name, value);
					}

					var upperPart = document.createElement('div');
					upperPart.classList.add('toti-range-upper');
					upperPart.appendChild(minPanel);
					upperPart.appendChild(input);
					upperPart.appendChild(maxPanel);

					var lowerPart = document.createElement('div');
					lowerPart.classList.add('toti-range-lower');
					lowerPart.appendChild(currentPanel);
					currentPanel.style.margin = '0 auto';
					currentPanel.style.display = 'table';

					container.appendChild(upperPart);
					container.appendChild(lowerPart);
				} else {
					for(const[name, value] of Object.entries(createAttributes)) {
						container.setAttribute(name, value);
					}
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					input.value = value;
					setInfoCount(instance, value);
				} else {
					container.innerText = value;
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					input.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				var input = container.querySelector('input');
				if (rule === null) {
					input.removeAttribute(name);
				} else {
					input.setAttribute(name, rule);
				}
				setInfoCount(instance, instance.getValue());
			}
		});
		this.addValidationMaxValue(attributes);
		this.addValidationMinValue(attributes);
		this.addValidationStep(attributes);
		if (this.value === null) {
			setInfoCount(this, null);
		}
	}
}
class TextInput extends Input {
	options = null;
	input = null;
	datalist = null;

	constructor(type, attributes) {
		var load = null;
		var defaultOptions = null;

		var options = {};
		var input = null;
		var datalist = null;

		var addOptions = (data, datalist, options)=>{
			data.forEach((option)=>{
				if (options.hasOwnProperty(option)) {
					return;
				}
				var container = document.createElement('option');
				container.value = option;
				datalist.appendChild(container);
				options[option] = container;
			});
		}
		super(type, attributes, {
			parseValue: (value)=>{
				if (value === '' || value === null) {
					return null;
				}
				return value.toString();
			},
			create: (instance, editable, createAttributes)=>{
				var container = document.createElement("span");
				if (editable) {
					input = document.createElement("input");
					input.setAttribute("type", type);
					input.addEventListener('change', ()=>{
						instance.setValue(input.value);
					});

					datalist = document.createElement('datalist');
					if (defaultOptions !== null) {
						addOptions(defaultOptions, datalist, options);
					}
					if (load !== null) {
						var animation = Toti.animations.inputLoading(container);
						Toti.load({
							url: load.url,
							method: load.method,
							params: load.params,
							async: true,
							dataType: 'json'
						}).then((data)=>{
							addOptions(data, datalist, options);
							animation.remove();
						}).catch((error)=>{
							animation.failure();
							console.error(error);
						});
					}

					for(const[name, value] of Object.entries(createAttributes)) {
						input.setAttribute(name, value);
					}

					var id = 'datalist-' + instance.name + '_' + totiUtils.random(1000000);
					datalist.setAttribute("id", id);
					input.setAttribute('list', id);

					container.appendChild(input);
					container.appendChild(datalist);
				} else {
					for(const[name, value] of Object.entries(createAttributes)) {
						container.setAttribute(name, value);
					}
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					input.value = value;
				} else {
					container.innerText = value;
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					input.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				var input = container.querySelector('input');
				if (rule === null) {
					input.removeAttribute(name);
				} else {
					input.setAttribute(name, rule);
				}
			}
		}, {
			options: (value)=>{
				defaultOptions = value;
			},
			load: (value)=>{
				load = value;
			}
		});
		this.options = options;
		this.input = input;
		this.datalist = datalist;
		this.addValidationPattern(attributes);
		this.addValidationMinLength(attributes);
		this.addValidationMaxLength(attributes);
		/* TODO */
		/*this.addValidation('build-in', (instance, value)=>{
			console.log(value);
			console.log(input.checkValidity());
			console.log();
			return true;
		});*/
	}
	getOptions() {
		return this.options;
	}
	/* TODO improvement */
	/*addOption() {
		// TODO if not null
		this.fAddOption();
	}
	removeOption() {
		// TODO if not null
		this.fRemoveOption();
	}*/
}

class FileInput extends Input {
	// TODO jak je zpracovano na serveru
	constructor(attributes) {
		var multiple = false;
		super('file', attributes, {
			parseValue: (value)=>{
				if (value === null) {
					return null;
				}
				if (!Array.isArray(value)) {
					return null;
				}
				var files = [];
				value.forEach((data)=>{
					files.push(new File(
						[], data.name, {
							type: data.type,
							size: data.size
						},
						'utf-8'
					));
				});
				/*
				let fileName = 'hasFilename.jpg'
			    let file = new File([imgBlob], fileName,{type:"image/jpeg", lastModified:new Date().getTime()}, 'utf-8');
			    let container = new DataTransfer(); 
			    container.items.add(file);
			    document.querySelector('#file_input').files = container.files;
				*/
				return files;
			},
			create: (instance, editable, createAttributes)=>{
				var container = null;
				if (editable) {
					container = document.createElement("input");
					container.setAttribute("type", 'file');
					if (multiple) {
						container.setAttribute('multiple', 'multiple');
					}
					container.addEventListener('change', ()=>{
						instance.setValue(container.value);
					});
				} else {
					container = document.createElement("div");
				}
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					/* https://stackoverflow.com/a/70485949/8240462 */
			   		let dataTransfer = new DataTransfer();
			   		if (value !== null) {
			   			value.forEach((val)=>{
			   				dataTransfer.items.add(val);
			   			});
			   		}
					container.files = dataTransfer.files;
				} else if (value === null) {
					container.innerHTML = '';
				} else {
					value.forEach((file)=>{
						var row = document.createElement('div');
						row.classList.add('toti-file-detail');
						row.innerText = file.name + ' (' + file.size + ')';
						container.appendChild(row);
					});
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.disabled = isDisabled;
				}
			}
		}, {
			multiple: (value)=>{
				multiple = true;
				return true;
			}
		});
		/* TODO improve validate min/max file count, type, content length,...*/
	}
}

class HiddenInput extends Input {
	constructor(type, attributes) {
		super(type, attributes, {
			parseValue: (value)=>{
				if (value === '') {
					return null;
				}
				return value;
			},
			create: (instance, editable, createAttributes)=>{
				var container = null;
				if (editable) {
					container = document.createElement("input");
					container.setAttribute("type", type);
					container.addEventListener('change', ()=>{
						instance.setValue(container.value);
					});
				} else {
					container = document.createElement("span");
				}
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					container.value = value;
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				/* makes no sense */
			},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			}
		});
	}
}

class ColorInput extends Input {
	constructor(attributes) {
		super('color', attributes, {
			parseValue: (value)=>{
				if (value === null || !value.match('^#(?:[0-9a-fA-F]{3}){1,2}$')) {
					/* input default value */
					//return '#000000';
					return null;
				}
				return value;
			},
			create: (instance, editable, createAttributes)=>{
				var container = null;
				if (editable) {
					container = document.createElement("input");
					container.setAttribute("type", 'color');
					container.addEventListener('change', ()=>{
						instance.setValue(container.value);
					});
				} else {
					container = document.createElement("span");
				}
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					container.value = value;
				} else {
					container.innerText = value;
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				if (rule === null) {
					container.removeAttribute(name);
				} else {
					container.setAttribute(name, rule);
				}
			}
		});
	}
}

class Timestamp {
	type = null;
	year = null;
	month = null;
	day = null;
	hour = null;
	minute = null;
	second = null;
	nano = null;
	week = null;
	constructor(type) {
		this.type = type;
	}
	compare(other) {
		var res = this._compare(other, 'year');
		if (res !== 0) {
			return res;
		}
		res = this._compare(other, 'month');
		if (res !== 0) {
			return res;
		}
		res = this._compare(other, 'day');
		if (res !== 0) {
			return res;
		}
		res = this._compare(other, 'week');
		if (res !== 0) {
			return res;
		}
		res = this._compare(other, 'hour');
		if (res !== 0) {
			return res;
		}
		res = this._compare(other, 'minute');
		if (res !== 0) {
			return res;
		}
		res = this._compare(other, 'second');
		if (res !== 0) {
			return res;
		}
		return this._compare(other, 'nano');
	}
	_compare(other, name) {
		if (other[name] === null && this[name] === null) {
			return 0;
		}
		if (this[name] === null) {
			throw new Error('Uncomparable Timestamps: ' + this.toString() + " vs " + other.toString());
		}
		if (isNaN(this[name])) {
			return isNaN(other[name]) || other[name] === 0 ? 0 : 1;
		}
		if (isNaN(other[name])) {
			return isNaN(this[name]) || this[name] === 0 ? 0 : -1;
		}
		if (other[name] === this[name]) {
			return 0;
		}
		return other[name] > this[name] ? 1 : -1;
	}
	toNumber() {
		var zero = new Date('1970-01-01');
		switch(this.type) {
			case 'week':
				var firstWeekDay = this.firstDayOfWeek(this.year, this.week);
				return Math.round((firstWeekDay - zero) / (7 * 24 * 60 * 60 * 1000));
			case 'month':
				var z = zero.getFullYear() * 12 + 1; /* january this year is start point */
				var thiz = this.year * 12 + this.month;
				return Math.abs(z - thiz);
			case 'date':
				return Math.round((new Date(this.year, this.month -1, this.day) - zero) / (24 * 60 * 60 * 1000));
			case 'datetime':
				/* same as time for now */
			case 'time':
				return 3600 * this.hour + 60 * this.minute
				 + (isNaN(this.second) ? 0 : this.second)
				  + 0.001 * (isNaN(this.nano) ? 0 : this.nano);
		}
	}
	/* https://stackoverflow.com/a/8803300/8240462 */
	firstDayOfWeek(year, week) {
		var d = new Date(year + "-01-01 00:00:00");
		var w = d.getTime() + 604800000 * (week - 1);
		return new Date(w);
	}
	format() {
		switch(this.type) {
			case 'week':
				return this.formatNumber(this.year) + this.formatNumber(this.week, '-W');
			case 'month':
				return this.formatNumber(this.year) + this.formatNumber(this.month, '-');
			case 'date':
				return this.formatNumber(this.year)
				 + this.formatNumber(this.month, '-')
				  + this.formatNumber(this.day, '-');
			case 'time':
				return this.formatNumber(this.hour)
				 + this.formatNumber(this.minute, ':')
				 + this.formatNumber(this.second, ':')
				 + this.formatNumber(this.nano, '.', 3);
			case 'datetime':
				return this.formatNumber(this.year)
				 + this.formatNumber(this.month, '-')
				 + this.formatNumber(this.day, '-')
				 + this.formatNumber(this.hour, ' ')
				 + this.formatNumber(this.minute, ':')
				 + this.formatNumber(this.second, ':')
				 + this.formatNumber(this.nano, '.', 3);
		}
	}
	formatNumber(number, prefix = '', digits = 2) {
		if (number === null || isNaN(number)) {
			return '';
		}
		var res = prefix;
		if (digits > 1 && number < 10) {
			res += '0';
		}
		if (digits > 2 && number < 100) {
			res += '0';
		}
		return res + number;
	}
	toString() {
		return this.type + ': Y=' + this.year + ', M=' + this.month + ', D=' + this.day
		+ ', H=' + this.hour + ', M=' + this.minute + ', S=' + this.second + ', N=' + this.nano
		+ ', W=' + this.week;
	}
}

function parseDatetime(type, value) {
	function matches(r, indexes) {
		const matches = r.exec(value);
		var res = new Timestamp(type);
		for (const[name, index] of Object.entries(indexes)) {
			if (matches[index] !== undefined) {
				res[name] = parseInt(matches[index]);
			} else {
				res[name] = NaN;
			}
		}
		return res;
	}
	const date = /^(([0-9]{4})-([0-9]{2})-([0-9]{2}))$/;
	const time = /^(([0-9]{2}):([0-9]{2})(:([0-9]{2})(\.([0-9]{3}))?)?)$/;
	const datetime = /^(([0-9]{4})-([0-9]{2})-([0-9]{2}))[ T](([0-9]{2}):([0-9]{2})(:([0-9]{2})(\.([0-9]{3}))?)?)$/;
	const week = /^(([0-9]{4})-W([0-9]{2}))$/;
	const month = /^(([0-9]{4})-([0-9]{2}))$/;
	if (week.test(value) && type === 'week') {
		return matches(week, {
			year: 2,
			week: 3
		});
	}
	if (month.test(value) && type === 'month') {
		return matches(month, {
			year: 2,
			month: 3
		});
	}
	if (date.test(value) && type === 'date') {
		return matches(date, {
			year: 2,
			month: 3,
			day: 4
		});
	}
	if (time.test(value) && type === 'time') {
		return matches(time, {
			hour: 2,
			minute: 3,
			second: 5,
			nano: 7
		});
	}
	if (datetime.test(value) && type === 'datetime') {
		return matches(datetime, {
			year: 2,
			month: 3,
			day: 4,
			hour: 6,
			minute: 7,
			second: 9,
			nano: 11
		});
	}
	return null;
}
/* for jest unit test */
if (typeof module !== 'undefined') {
	module.exports.createDatetime = parseDatetime;
}
class DatetimeInput extends Input {
	constructor(type, attributes) {
		super(type, attributes, {
			parseValue: (value)=>{
				return parseDatetime(type, value);
			},
			create: (instance, editable, createAttributes)=>{
				var container = null;
				if (editable) {
					container = document.createElement("input");
					container.setAttribute("type", type === 'datetime' ? 'datetime-local' : type);
					container.addEventListener('change', ()=>{
						instance.setValue(container.value);
					});
				} else {
					container = document.createElement("span");
				}
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{
				if (editable) {
					container.value = value === null ? '' : value.format();
				} else {
					container.innerText = value === null ? '' : value.format(); // TODO parse?
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				if (rule === null) {
					container.removeAttribute(name);
				} else {
					container.setAttribute(name, rule);
				}
			}
		});
		this._addValidation('step', attributes, (instance, currentValue, step)=>{
			if (currentValue === null) {
				return true;
			}
			var s = step;
			var n = currentValue.toNumber();
			while (s < 1) {
				s = s * 10;
				n = n * 10;
			}
			var res = n%s === 0;
			if (!res) {
				this.addError('input.stepIsWrong', {s: step});
			}
			return res;
		});
		this._addValidation('min', attributes, (instance, currentValue, minString)=>{
			if (currentValue === null) {
				return true;
			}
			var min = parseDatetime(type, minString);
			if (min === null) {
				return true;
			}
			var res = min.compare(currentValue) > 0;
			if (!res) {
				this.addError('input.toSmall', {m: min});
			}
			return res;
		});
		this._addValidation('max', attributes, (instance, currentValue, maxString)=>{
			if (currentValue === null) {
				return true;
			}
			var max = parseDatetime(type, maxString);
			if (max === null) {
				return true;
			}
			var res = max.compare(currentValue) < 0;
			if (!res) {
				this.addError('input.tooBig', {m: max});
			}
			return res;
		});
	}
}

class RadioListInput extends OptionsInput {
	constructor(attributes) {
		super('radiolist', attributes, {
			createContainer: (instance, elements, isSearch, prompt)=>{
				var container = document.createElement("span");
				container.add = (child)=>{
					container.appendChild(child);
				};
				return container;
			},
			setValue: (instance, container, selectedOption)=>{
				/* setValue - no implementation needed */
			},
			setDisabled: (instance, container, isDisabled)=>{
				/* setDisabled - no implementation needed */
			},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			},
			createOption: (instance, value, title, disabled, selectable, level)=>{
				return new OptionInput(
					value, title, disabled, selectable, level, {
						createInput: (value, title, disabled)=>{
							var id = 'radiolist_' + instance.name + '_' + totiUtils.random(1000000);
							var input = document.createElement('input');
							input.setAttribute('type', 'radio');
							input.setAttribute('value', value);
							input.setAttribute('name', instance.name);
							input.setAttribute('id', id);
							input.setAttribute('level', level);
							if (disabled) {
								input.setAttribute('disabled', true);
							}
							input.addEventListener('change', ()=>{
								instance.setValue(input.value);
							});

							var label = document.createElement('label');
							label.innerText = title;
							label.setAttribute('for', id);

							var container = document.createElement('div');
							container.appendChild(input);
							container.appendChild(label);
							return container;
						},
						createGroup: (optionInstance, input = null)=>{
							var label = document.createElement('legend');
							if (input === null) {
								label.innerText = optionInstance.getTitle();
							} else {
								label.appendChild(input);
							}
							var container = document.createElement('fieldset');
							container.appendChild(label);
							return container;
						},
						addChild: (optionInstance, childContainer)=>{
							optionInstance.getContainer().querySelector('fieldset').appendChild(childContainer);
						},
						setDisabled: (optionInstance, isDisabled)=>{
							var input = optionInstance.getContainer().querySelector('[level="' + level + '"]');
							if (input === null) {
								return;
							}
							if (isDisabled) {
								input.setAttribute('disabled', true);
							} else {
								input.removeAttribute('disabled');
							}
						},
						setValue: (optionInstance, isSelected)=>{
							var input = optionInstance.getContainer().querySelector('[level="' + level + '"]');
							if (input === null) {
								/* not selectable group is null */
								return;
							}
							input.checked = isSelected;
						}
					}
				);
			}
		});
	}
}
/* undefined during jest unit tests */
if (typeof document !== 'undefined') {
	document.addEventListener("DOMContentLoaded", ()=>{
		document.body.addEventListener('click', (e)=>{
			var src = e.srcElement;
			while (src !== null && !src.classList.contains('toti-select')) {
				src = src.parentElement;
			}
			document.querySelectorAll('.toti-select').forEach((select)=>{
				if (select !== src) {
					select.querySelector('.toti-select-options').hide(true);
				}
			});
		});
		document.addEventListener("scroll", (e)=>{
			document.querySelectorAll('.toti-select').forEach((select)=>{
				select.querySelector('.toti-select-options').hide(true);
			});
		});
	});
}
class SelectInput extends OptionsInput {
	/* TODO improvement listen on arrow up/down */
	selfModify = false;
	constructor(attributes) {
		var depends = null;
		super('select', attributes, {
			createContainer: (instance, elements, isSearch, prompt)=>{
				var input = document.createElement('input');
				input.setAttribute("name", instance.getName());
				if (!isSearch) {
					input.setAttribute('readonly', 'true');
				}
				input.classList.add('toti-select-input');
				if (prompt !== null) {
					input.setAttribute('placeholder', prompt);
				}
				var options = document.createElement('div');
				options.classList.add('toti-select-options');

				function select_creteResult() {
					var result = {
						count: 0, /* displayed items count */
						maxLength: 0 /* text length */
					};
					result.getCount = ()=>{
						return result.count;
					};
					result.getMaxLength = ()=>{
						return result.maxLength;
					};
					result.addCount = (count)=>{
						result.count += count;
					};
					result.setMaxLength = (maxLength)=>{
						result.maxLength = Math.max(result.maxLength, maxLength);
					};
					result.merge = (other)=>{
						result.addCount(other.getCount());
						result.setMaxLength(other.getMaxLength());
					};
					return result;
				}

				function processElements(elements, phrase/*, isParentHideByDepends = null*/) {
					var groupResult = select_creteResult();
					elements.forEach((key, element)=>{
						if (element.isHiddenByDepends) {
							element.hide();
							return;
						}
						var itemResult = select_creteResult();
						if (Toti.utils.unaccent(element.getTitle()).includes(Toti.utils.unaccent(phrase))) {
							itemResult.addCount(1);
						}
						if (element.isGroupVisible) {
							itemResult.merge(processElements(element.childs, phrase));
						}
						if (itemResult.count > 0) {
							itemResult.setMaxLength(element.getTitle().length);
							element.show();
						} else {
							element.hide();
						}
						groupResult.merge(itemResult);
					});
					return groupResult;
				}
				function resize() {
					var phrase = '';
					if (instance.getSelected() === null) {
						phrase = input.value;
					} else if (input.value !== instance.getSelected().getTitle()) {
						phrase = input.value
					}
					var displayed = processElements(elements, phrase);
					var bonds = input.getBoundingClientRect();
					var rowHeight = 24.4; /* TODO from font size */
					var ht = instance.popupHT(
						window.innerHeight, window.outerHeight, bonds.top, bonds.height,
						Math.min(rowHeight * 15, rowHeight * displayed.count)
					);
					var wl = instance.popupWL(window.innerWidth, window.outerWidth, bonds.left, bonds.width, displayed.maxLength*17);

					options.style.position = "fixed";
					options.style.top = ht.top + "px";
					options.style.height = ht.height + "px";
					options.style.left = wl.left + "px";
					options.style.width = wl.width + "px";
				}
				
				new ResizeObserver(()=>{
					resize();
				}).observe(input);

				instance.show = ()=>{
					options.setAttribute('visible', true);
					resize();
				};
				/* TODO improve use only instance */
				instance.hide = options.hide = ()=>{
					options.setAttribute('visible', false);
				};
				instance.isShowed = ()=>{
					return options.getAttribute('visible') === 'true';
				};
				instance.resize = resize;

				input.keyup = ()=>{
					options.setAttribute('visible', true);
					resize();
				};
				input.click = ()=>{
					if (instance.isShowed()) {
						instance.hide();
					} else {
						instance.show();
					}
				};
				input.onclick = input.click;
				input.onkeyup = input.keyup;

				var container = document.createElement("div");
				container.appendChild(input);
				container.appendChild(options);
				container.classList.add('toti-select');
				// needed? container.setAttribute('id', 'totiSelect-' + Toti.utils.random(100000000));
				/* TODO improve use try use instance instead */
				container.add = (child)=>{
					options.appendChild(child);
				};
				return container;
			},
			setValue: (instance, container, selectedOption)=>{
				var input = container.querySelector('.toti-select-input');
				if (selectedOption === null) {
					input.value = '';
				} else {
					input.value = selectedOption.getTitle();
					/* this is old place where input was set/remove disabled */
				}
			},
			setDisabled: (instance, container, isDisabled)=>{
				var input = container.querySelector('.toti-select-input');
				if (isDisabled) {
					input.onclick = ()=>{};
					input.onkeyup = ()=>{};
					input.setAttribute('disabled', true);
				} else {
					input.onclick = input.click;
					input.onkeyup = input.keyup;
					input.removeAttribute('disabled');
				}
			},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			},
			createOption: (instance, value, title, disabled, selectable, level)=>{
				var result = new OptionInput(
					value, title, disabled, selectable, level, {
						createInput: (value, title, disabled)=>{
							var container = document.createElement('div');
							container.classList.add('toti-select-option');
							container.innerText = title;
							container.setAttribute('level', level);
							container.setAttribute('data-selected', false);
							container.click = ()=>{
								instance.setValue(value);
								instance.hide();
							};

							container.setAttribute('data-selectable', true);
							container.setAttribute('data-disabled', disabled);
							container.setAttribute('data-selected', false);
							if (!disabled) {
								container.onclick = container.click;
							}
							return container;
						},
						createGroup: (optionInstance, input = null)=>{
							optionInstance.isGroupVisible = true;
							var label = null;
							if (input === null) {
								label = document.createElement('div');
								label.innerText = optionInstance.getTitle();
								label.classList.add('toti-select-option');

								label.setAttribute('data-selectable', selectable);
								label.setAttribute('data-selected', false);
								label.setAttribute('data-disabled', disabled);
							} else {
								label = input;
							}
							var childs = document.createElement('div');
							childs.classList.add('toti-select-childs');
							childs.setAttribute('level', level);
							childs.setAttribute('visible', true);

							var showGroup = document.createElement('img');
							showGroup.src = Toti.images.arrowDown;
							showGroup.setAttribute('visible', false);
							showGroup.onclick = ()=>{
								optionInstance.isGroupVisible = true;
								showGroup.setAttribute('visible', false);
								hideGroup.setAttribute('visible', true);
								childs.setAttribute('visible', true);
								instance.resize();
							};

							var hideGroup = document.createElement('img');
							hideGroup.src = Toti.images.arrowUp;
							hideGroup.setAttribute('visible', true);
							hideGroup.onclick = ()=>{
								optionInstance.isGroupVisible = false;
								showGroup.setAttribute('visible', true);
								hideGroup.setAttribute('visible', false);
								childs.setAttribute('visible', false);
								instance.resize();
							};

							var groupControl = document.createElement('div');
							groupControl.classList.add('toti-select-group-control');
							groupControl.appendChild(showGroup);
							groupControl.appendChild(hideGroup);

							var legend = document.createElement('div');
							legend.classList.add('toti-select-group-legend');
							legend.appendChild(label);
							legend.appendChild(groupControl);

							var container = document.createElement('div');
							container.classList.add('toti-select-group');
							container.appendChild(legend);
							container.appendChild(childs);
							return container;
						},
						addChild: (optionInstance, childContainer)=>{
							childContainer.classList.add('toti-select-child');
							childContainer.setAttribute('visible', true);
							optionInstance.getContainer().querySelector('.toti-select-childs[level="' + level + '"]').appendChild(childContainer);
						},
						setDisabled: (optionInstance, isDisabled)=>{
							var container = optionInstance.getContainer();
							if (isDisabled) {
								container.onclick = ()=>{};
							} else {
								container.onclick = container.click;
							}
							var input = optionInstance.getInput();
							if (input === null) {
								return;
							}
							if (optionInstance.isGroup) {
								input = input.querySelector('.toti-select-option[level="' + level + '"]');
							}
							input.setAttribute('data-disabled', isDisabled);
						},
						setValue: (optionInstance, isSelected)=>{
							var current = optionInstance.getContainer().querySelector('.toti-select-option[level="' + level + '"]');
							if (current !== null) {
								current.setAttribute('data-selected', isSelected);
							}
						}
					}
				);
				result.isHiddenByDepends = false;
				result.show = ()=>{
					result.getContainer().setAttribute('visible', true);
				};
				result.hide = ()=>{
					result.getContainer().setAttribute('visible', false);
				};
				result.isHidden = ()=>{
					return result.hidden;
				};
				return result;
			}
		}, {
			depends: (value)=>{
				depends = value;
			}
		});
		/* not editable is missing this method */
		if (this.hasOwnProperty('hide')) {
			/* need to be here to select default value */
			this.hide(); 
		}
		var object = this;
		this.afterOptionLoad(()=>{
			/* not editable is missing this method */
			if (object.hasOwnProperty('resize')) {
				object.resize();
			}
		});
		/* works only for first group level*/
		if (this.editable && depends !== null) {
			var master = inputs[depends];
			var setMasterValue = ()=>{
				if (object.selfModify) {
					object.selfModify = false;
					return;
				}
				var selected = object.findParent(object.getSelected());
				/* onchange callback is called only if value change */
				if (selected === null) {
					master.clear();
				} else {
					master.setValue(selected.getValue());
				}
			};
			
			if (master instanceof OptionsInput) {
				Promise.all([
					this.afterOptionLoad(()=>{}),
					master.afterOptionLoad(()=>{})
				]).then(()=>{
					if (object.getSelected() !== null) {
						setMasterValue();
					}
					if (master.getSelected() !== null) {
						object.parentChanged(master.getValue());
					}
					/* set after init */
					master.onChange(()=>{
						object.parentChanged(master.getValue());
					});
				});
			} else {
				master.onChange(()=>{
					object.parentChanged(master.getValue());
				});

				this.parentChanged(master.getValue());
				this.afterOptionLoad(setMasterValue);
			}
			this.onChange(setMasterValue);
		}
		/* need to be after depends callbacks */
		this.onChange((instance, oldValue)=>{
			var input = instance.getContainer().querySelector('.toti-select-input');
			var selectedOption = instance.getSelected();
			if (selectedOption !== null) {
				if (selectedOption.isValid()) {
					input.classList.remove('toti-select-disabled');
				} else {
					input.classList.add('toti-select-disabled');
				}
			}
		});
	}
	findParent(selected) {
		if (selected === null) {
			return null;
		}
		var parent = selected.getParent();
		if (parent === null) {
			return selected;
		}
		return this.findParent(parent);
	}
	parentChanged(masterValue) {
		var markedOption = this.getOptions().get(masterValue);
		this.getOptions().forEach((value, option)=>{
			if (markedOption === null) {
				option.isHiddenByDepends = false;
				option.setDisabled(false, true);
			} else if (masterValue === value) {
				option.isHiddenByDepends = false;
				option.setDisabled(false, true);
			} else {
				option.isHiddenByDepends = true;
				option.setDisabled(true, true);
			}
		});
		var selected = this.findParent(this.getSelected());
		if (selected === null && masterValue === null) {
			return;
		}
		if (selected !== null && masterValue === selected.getValue()) {
			return;
		}
		if (selected !== null || this.getSelected() !== null) {
			this.selfModify = true;
		}
		if (selected !== null) {
			this.clear();
		}
	}
}

class DynamicInput extends Input {
	inputs = null;
	fAddField = null;
	fRemoveField = null;
	fGetValue = null;
	selfSet = false;
	constructor(attributes, configuration) {
		const source = 'dynamic';

		var functions = {};
		if (configuration.hasOwnProperty('dynamicList')) {
			functions = configuration.dynamicList;
		}
		if (!functions.hasOwnProperty('createContainer')) {
			functions.createContainer = (instance, useAddButton, fAdd)=>{
				var control = document.createElement('div');
				if (instance.getTitle() !== null) {
					var title = document.createElement('span');
					title.innerText = instance.getTitle();
					control.appendChild(title);
				}
				if (useAddButton && instance.editable) {
					var add = document.createElement('button');
					add.classList.add('toti-dynamic-add');
					add.setAttribute('data-name', instance.getName());
					add.innerText = '+';
					add.onclick = ()=>{
						fAdd();
						return false;
					};
					control.appendChild(add);
				}
				var container = document.createElement('div');
				container.appendChild(control);
				return container;
			};
		}
		if (!functions.hasOwnProperty('createInputRow')) {
			functions.createInputRow = (instance, container, input, useRemoveButton, fRemove)=>{
				var control = document.createElement('div');
				if (input.getTitle() !== null && !(input instanceof DynamicInput) && !(input instanceof InputList)) {
					var title = document.createElement('span');
					title.classList.add('toti-dynamic-title');
					title.innerText = input.getTitle();
					control.appendChild(title);
				}
				if (useRemoveButton && instance.editable) {
					var remove = document.createElement('button');
					remove.classList.add('toti-dynamic-remove');
					remove.setAttribute('data-name', instance.getName());
					remove.innerText = '-';
					remove.onclick = ()=>{
						fRemove();
						return false;
					};
					control.appendChild(remove);
				}
				control.appendChild(input.getContainer());
				container.appendChild(control);
				return control;
			};
		}
		if (!functions.hasOwnProperty('setDisabled')) {
			functions.setDisabled = (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.querySelectorAll('[data-name="' + instance.getName() + '"]').forEach((button)=>{
						if (isDisabled) {
							button.setAttribute('disabled', true);
						} else {
							button.removeAttribute('disabled');
						}
					});
				}
			};
		}
		if (!functions.hasOwnProperty('rename')) {
			functions.rename = (instance, container, inputContainer, title)=>{
				inputContainer.querySelector('.toti-dynamic-title').innerText = title;
			};
		}

		var inputs = new SortedMap();
		var fieldDefinition = null;
		var useAddButton = true;
		var useRemoveButton = true;
		var addFirstBlank = true;

		var fSetValue = (instance, element, unique)=>{
			var value = Toti.utils.clone(instance.getValue());
			var index = inputs.get(unique).index;
			
			value[index] = element.getValue();
			instance.setValue(value);
		};
		var fRemoveValue = (instance, field)=>{
			var value = Toti.utils.clone(instance.getValue());
			//delete value[field.index];
			value.splice(field.index, 1)
			instance.setValue(value);
		};
		/* null index remove last item */
		var fRemoveField = (instance, changeMasterValue, unique = null)=>{
			// TODO onRemove callback?
			if (unique === null) {
				unique = inputs.getLastKey();
			}
			var field = inputs.remove(unique);
			if (field === null) {
				return;
			}

			field.container.remove();
			for (var i = field.index; i < inputs.size(); i++) {
				var item = inputs.getByIndex(i);
				item.index = i;
				functions.rename(instance, instance.container, item.container, item.getTitle(i));
			}
			if (changeMasterValue) {
				fRemoveValue(instance, field);
			}
		}
		var fAddField = (instance, addBlanc)=>{
			// TODO onAdd callback?
			var unique = Toti.utils.random();
			var conf = Toti.utils.clone(fieldDefinition);
			if (!conf.hasOwnProperty('onChange')) {
				conf.onChange = [];
			}
			conf['data-dynamic'] = unique;
			conf.name = instance.getName() + "[" + unique + "][" + conf.name + "]";

			const title = conf.hasOwnProperty('title') ? conf.title : null;
			var getTitle = (index)=>{
				if (title !== null) {
					return title.replace('{i}', index);
				}
				return null;
			};
			if (conf.hasOwnProperty('title')) {
				conf.title = getTitle(inputs.size());
			}

			var input = Toti.createInput(conf, configuration);
			input.onChange((element, oldValue)=>{
				fSetValue(instance, element, unique);
			}, source);
			var container = functions.createInputRow(instance, instance.container, input, useRemoveButton, ()=>{
				if (instance.isDisabled()) {
					return;
				}
				fRemoveField(instance, true, unique);
			});
			var field = {
				input: input,
				container: container,
				getTitle: getTitle,
				index: inputs.size(),
				unique: unique
			};
			inputs.put(unique, field);
			/* executed only if called by addButton */
			if (addBlanc) {
				fSetValue(instance, input, unique);
			}
			return field;
		};

		// TODO automaticky pridat min a nedovolit pridat max - krome setValue
		var max = null;
		var min = null;
		super('DynamicList', attributes, {
			parseValue: (value)=>{
				if (value === null) {
					return [];
				}
				if (typeof value === 'string' || value instanceof String) {
					try {
				        value = JSON.parse(value);
				    } catch (e) { /*nothing*/ }
				}
				if (Array.isArray(value)) {
					return value;
				}
				return [];
			},
			create: (instance, editable, createAttributes)=>{
				if (fieldDefinition === null) {
					throw new Error('DynamicList must contains field definition');
				}
				var container = functions.createContainer(instance, useAddButton, ()=>{
					if (instance.isDisabled()) {
						return;
					}
					fAddField(instance, true);
				});
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, values, editable)=>{
				var lastIndex = -1;
				values.forEach((value, index)=>{
					var field = inputs.getByIndex(index);
					if (field === null) {
						field = fAddField(instance, false);
					}
					field.input.setValue(value, source);
					lastIndex = index;
				});
				if (lastIndex < inputs.size() - 1) {
					var size = inputs.size();
					for (var i = lastIndex + 1; i < size; i++) {
						/* remove last */
						fRemoveField(instance, false);
					}
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{
				functions.setDisabled(instance, container, isDisabled, editable);
			},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			}
		}, {
			field: (value)=>{
				fieldDefinition = value;
			},
			useAddButton: (value)=>{
				useAddButton = value;
			},
			useRemoveButton: (value)=>{
				useRemoveButton = value;
			},
			addFirstBlank: (value)=>{
				addFirstBlank = value;
			},
			max: (value)=>{
				max = value;
			},
			min: (value)=>{
				min = value;
			}
		});
		this.fAddField = fAddField;
		this.fRemoveField = fRemoveField;
		this.inputs = inputs;
		if (addFirstBlank && !attributes.hasOwnProperty('value') && this.editable && !this.isDisabled()) {
			fAddField(this, true);
		}
		this.createValidation('min', min, (instance, value, ruleMin)=>{
			var res = value.length >= ruleMin;
			if (!res) {
				instance.addError('input.dynamicMinLenght', {m: ruleMin, l: value.length});
			}
			return res;
		});
		this.createValidation('max', max, (instance, value, ruleMax)=>{
			var res = value.length <= ruleMax;
			if (!res) {
				instance.addError('input.dynamicMaxLenght', {m: ruleMax, l: value.length});
			}
			return res;
		});
	}
	_isValueMissing() {
		return this.value.length === 0;
	}
	addField() {
		return this.fAddField(this, true);
	}
	/* null index remove last item */
	removeField(index = null) {
		this.fRemoveField(this, true, index === null ? null : this.inputs.getKeyByIndex(index));
	}
	isValid() {
		var isValid = super.isValid();
		this.inputs.forEach((unique, field)=>{
			if (isValid) {
				isValid = field.input.isValid();
			}
		});
		return isValid;
	}
	/*setDisabled(isDisabled) {
		super.setDisabled(isDisabled);
		this.inputs.forEach((unique, field)=>{
			field.input.setDisabled(isDisabled);
		});
	}*/
}
class InputList extends Input {
	fields = {};
	constructor(attributes, configuration) {
		var functions = {};
		if (configuration.hasOwnProperty('inputList')) {
			functions = configuration.inputList;
		}
		if (!functions.hasOwnProperty('createContainer')) {
			functions.createContainer = (instance)=>{
				var container = document.createElement('div');
				if (instance.getTitle() !== null) {
					var title = document.createElement('span');
					title.innerText = instance.getTitle();
					container.appendChild(title);
				}
				return container;
			};
		}
		if (!functions.hasOwnProperty('createInputRow')) {
			functions.createInputRow = (instance, container, input)=>{
				var control = document.createElement('div');
				if (input.getTitle() !== null && !(input instanceof DynamicInput) && !(input instanceof InputList)) {
					var title = document.createElement('span');
					title.innerText = input.getTitle();
					control.appendChild(title);
				}
				control.appendChild(input.getContainer());
				container.appendChild(control);
				return control;
			};
		}
		var source = 'inputList';
		var inputs = null;
		var fields = {};
		var inputsValue = {};
		var initial = true;
		super('InputList', attributes, {
			parseValue: (value)=>{
				function parseObject(object) {
					Object.keys(fields).forEach((key)=>{
						if (object.hasOwnProperty(key)) {
							return;
						}
						if (initial && inputsValue.hasOwnProperty(key)) {
							object[key] = inputsValue[key];
						} else {
							object[key] = null;
						}
					});
					initial = false;
					return object;
				}
				if (value === null) {
					return parseObject({});
				}
				if (typeof value === 'string' || value instanceof String) {
					try {
				        value = JSON.parse(value);
				    } catch (e) { /*nothing*/ }
				}
				if (Array.isArray(value)) {
					return parseObject(value);
				}
				if (typeof value === 'object') {
					return parseObject(value);
				}
				return parseObject({});
			},
			create: (instance, editable, createAttributes)=>{
				if (inputs === null) {
					throw new Error('InputList must contains subinputs');
				}
				var container = functions.createContainer(instance);
				inputs.forEach((inputAttributes, index)=>{
					var subAttributes = Toti.utils.clone(inputAttributes);
					var fieldName = inputAttributes.name;
					if (fieldName === null || fieldName === undefined || fieldName === '') {
						fieldName = index;
					}

					subAttributes.name = instance.getName() + "[" + fieldName + "]";
					var input = Toti.createInput(subAttributes, configuration);

					fields[fieldName] = input;

					functions.createInputRow(instance, container, input);
					input.onChange((element, oldValue)=>{
						var value = Toti.utils.clone(instance.getValue());
						value[fieldName] = input.getValue();
						instance.setValue(value);
					}, source);
					inputsValue[fieldName] = input.getValue();
				});
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, values, editable)=>{
				var setNames = Object.keys(fields);
				for (const[name, value] of Object.entries(values)) {
					if (fields.hasOwnProperty(name)) {
						fields[name].setValue(value, source);
						/* https://stackoverflow.com/a/5767357/8240462 */
						const index = setNames.indexOf(name);
  						setNames.splice(index, 1);
					}
				}
				if (setNames.length > 0) {
					setNames.forEach((name)=>{
						fields[name].setValue(null, source);
					});
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			}
		}, {
			fields: (value)=>{
				inputs = value;
			}
		});
		this.fields = fields;
	}
	_isValueMissing() {
		return Object.keys(this.fields).length === 0;
	}
	isValid() {
		var isValid = super.isValid();
		for(const[unique, field] of Object.entries(this.fields)) {
			if (isValid) {
				isValid = field.isValid();
			}
		}
		return isValid;
	}
	/*setDisabled(isDisabled) {
		super.setDisabled(isDisabled);
		for(const[unique, field] of Object.entries(this.fields)) {
			field.setDisabled(isDisabled);
		}
	}*/
}
class LoadedList extends Input {
	loaded = false;
	constructor(attributes, configuration) {
		var functions = {};
		if (configuration.hasOwnProperty('loadedList')) {
			functions = configuration.inputList;
		}
		if (!functions.hasOwnProperty('createContainer')) {
			functions.createContainer = (instance)=>{
				var container = document.createElement('div');
				if (instance.getTitle() !== null) {
					var title = document.createElement('span');
					title.innerText = instance.getTitle();
					container.appendChild(title);
				}
				return container;
			};
		}
		if (!functions.hasOwnProperty('createInputRow')) {
			functions.createInputRow = (instance, container, input)=>{
				var control = document.createElement('div');
				if (input.getTitle() !== null && !(input instanceof DynamicInput) && !(input instanceof InputList)) {
					var title = document.createElement('span');
					title.innerText = input.getTitle();
					control.appendChild(title);
				}
				control.appendChild(input.getContainer());
				container.appendChild(control);
				return control;
			};
		}

		var fieldDefinition = null;
		var load = null;
		var fields = {};
		super('LoadedList', attributes, {
			parseValue: (value)=>{
				if (value === null) {
					return {};
				}
				if (typeof value === 'string' || value instanceof String) {
					try {
				        value = JSON.parse(value);
				    } catch (e) { /*nothing*/ }
				}
				if (Array.isArray(value)) {
					return {}; /* array is not supported */
				}
				if (typeof value === 'object') {
					return value;
				}
				return {};
			},
			create: (instance, editable, createAttributes)=>{
				if (fieldDefinition === null) {
					throw new Error('LoadedList must contains field definition');
				}
				if (load === null) {
					throw new Error('LoadedList must contains load attribute');
				}
				var container = functions.createContainer(instance);
				var animation = Toti.animations.inputLoading(container);
				Toti.load({
					url: load.url,
					method: load.method,
					params: load.params,
					async: true,
					dataType: 'json'
				}).then((data)=>{
					instance.loaded = true;
					data.forEach((row)=>{
						var rowUnique = row.value;
						var subAttributes = Toti.utils.clone(fieldDefinition);
						subAttributes.name = instance.getName() + "[" + rowUnique + "]";
						if (subAttributes.hasOwnProperty('title') && subAttributes.title !== null) {
							subAttributes.title = subAttributes.title.replace('{i}', row.title);
						}
						if (instance.value.hasOwnProperty(rowUnique)) {
							subAttributes.value = instance.value[rowUnique];
						}

						var input = Toti.createInput(subAttributes, configuration);
						if (typeof input['parentChanged'] === 'function') {
							input.parentChanged(rowUnique);
						}
						fields[rowUnique] = input;

						functions.createInputRow(instance, container, input);
					});
				}).then(()=>{
					if (attributes.hasOwnProperty('value')) {
						instance.setValue(attributes.value);
					}
					animation.remove();
				}).catch((error)=>{
					animation.failure();
					console.error(error);
				});
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, values, editable)=>{
				var setNames = Object.keys(fields);
				for (const[name, value] of Object.entries(values)) {
					if (fields.hasOwnProperty(name)) {
						fields[name].setValue(value);
						/* https://stackoverflow.com/a/5767357/8240462 */
						const index = setNames.indexOf(name);
  						setNames.splice(index, 1);
					}
				}
				if (setNames.length > 0) {
					setNames.forEach((name)=>{
						fields[name].setValue(null);
					});
				}
			},
			setDisabled: (instance, container, isDisabled, editable)=>{},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			}
		}, {
			field: (value)=>{
				fieldDefinition = value;
			},
			load: (value)=>{
				load = value;
			}
		});
		this.fields = fields;
	}
	_isValueMissing() {
		return Object.keys(this.fields).length === 0;
	}
	isValid() {
		var isValid = this.loaded && super.isValid();
		for(const[unique, field] of Object.entries(this.fields)) {
			if (isValid) {
				isValid = field.isValid();
			}
		}
		return isValid;
	}
	/*setDisabled(isDisabled) {
		super.setDisabled(isDisabled);
		for(const[unique, field] of Object.entries(this.fields)) {
			field.setDisabled(isDisabled);
		}
	}*/
}
class SubmitInput extends Input {
	constructor(type, attributes) {
		var confirmation = null;
		var redirect = null;
		var onSuccess = null;
		var onFailure = null;
		super(type, attributes, {
			parseValue: (value)=>{
				return value;
			},
			create: (instance, editable, createAttributes)=>{
				if (!editable) {
					return null;
				}
				var container = document.createElement("input");
				container.setAttribute("type", type);
				if (instance.getTitle() !== null) {
					container.setAttribute('value', instance.getTitle());
				}
				container.addEventListener('click', ()=>{
					// TODO
					console.log("TODO submit");
					console.log('confirmation', configuration);
				});
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			}
		}, {
			confirmation: (value)=>{
				confirmation = value;
			},
			redirect: (value)=>{
				confirmation = value;
			},
			onSuccess: (value)=>{
				confirmation = value;
			},
			onFailure: (value)=>{
				confirmation = value;
			}
		});
/*
submit
	private String redirect;
	private String confirmation;
	private boolean async = true;
	private String onFailure = null;
	private String onSuccess = null;
	private Object value = null;
	private SubmitPolicy submitPolicy = SubmitPolicy.EXCLUDE;

	private boolean disabled = false;
	private final String type;
	private final String name;
	private final String title;
	private final Map<String, String> params = new HashMap<>();

image
	src
	
*/
	}
}
class Button extends Input {
	constructor(type, attributes) {
		var url = null;
		var method = null;
		var params = null;
		var onFailure = null;
		var onSuccess = null;
		var async = null;
		var condition = null;
		var icon = null;
		var confirmation = null;
		super(type, attributes, {
			parseValue: (value)=>{
				return value;
			},
			create: (instance, editable, createAttributes)=>{
				if (!editable) {
					return null;
				}
				var container = document.createElement("input");
				container.setAttribute("type", type);
				if (instance.getTitle() !== null) {
					container.setAttribute('value', instance.getTitle());
				}
				container.addEventListener('click', ()=>{
					// TODO
					console.log("TODO submit");
					console.log('confirmation', configuration);
				});
				for(const[name, value] of Object.entries(createAttributes)) {
					container.setAttribute(name, value);
				}
				return container;
			},
			setValue: (instance, container, value, editable)=>{},
			setDisabled: (instance, container, isDisabled, editable)=>{
				if (editable) {
					container.disabled = isDisabled;
				}
			},
			setRule: (instance, container, name, rule)=>{
				/* not required */
			}
		}, {
			url: (value)=>{
				url = value;
			},
			method: (value)=>{
				method = value;
			},
			params: (value)=>{
				params = value;
			},
			onFailure: (value)=>{
				onFailure = value;
			},
			onSuccess: (value)=>{
				onSuccess = value;
			},
			async: (value)=>{
				async = value;
			},
			condition: (value)=>{
				condition = value;
			},
			icon: (value)=>{
				icon = value;
			},
			confirmation: (value)=>{
				confirmation = value;
			}
		});
		// TODO use? private boolean evaluate = false;
		// TODO private List<String> classes = new LinkedList<>();
	}
/* // TODO grid only
	private boolean isReset = false;
	private boolean isRefresh = false;
	private boolean addFilters = false;
*/
}


function _createInput(attributes, configuration) {
	if (!attributes.hasOwnProperty('type')) {
		console.error("Missing attribute type", attributes);
		throw new Error("Missing attribute type");
	}
	function getOptional(type, create) {
		if (attributes.hasOwnProperty('optional')) {
			return new OptionalInput(type, attributes, create);
		}
		return create(attributes);
	}
	var parserEmptyStringIsNull = (value)=>{
		if (value === '') {
			return null;
		}
		return value;
	};
	var type = attributes.type;
	delete attributes.type;
	if (configuration.hasOwnProperty(type) && !['test-standart', 'test-check', 'test-optional', 'dynamicList', 'inputList', 'loadedList'].includes(type)) {
		return configuration[type](attributes, (callbacks, except = {})=>{
			return new Input(type, attributes, callbacks, except);
		});
	}
	switch(type) {
		case 'test-standart':
			return new StandartInput('text', attributes);
		case 'test-check':
			return new CheckInput('checkbox', attributes);
		case 'test-optional':
			return new OptionalInput('optional-text', attributes, (attrs)=>new StandartInput('range', attrs));
		/*************************/
		case 'text':
		case 'email': /* automatic email pattern check */
		case 'search': /* clear value button */
		case 'url': /* automatic protocol check */
		case 'tel': /* nothing special */
			return new TextInput(type, attributes);
		case 'number':
			return new StandartInput(type, attributes, (value)=>{
				if (isNaN(value) || value === null) {
					return null;
				}
				return parseFloat(value);
			})
			.addValidationMaxValue(attributes)
			.addValidationMinValue(attributes)
			.addValidationStep(attributes);
		case 'textarea':
			return new TextAreaInput(attributes);
		case 'radio':
		case 'checkbox':
			return new CheckInput(type, attributes);
		case 'password':
			return getOptional(type, (attr)=>{
				return new StandartInput(type, attr, (value)=>{
					if (attr.hasOwnProperty('editable') && !attr.editable) {
						return null;
					}
					if (value === '' || value === null) {
						return null;
					}
					return value.toString();
				})
				.addValidationPattern(attributes)
				.addValidationMinLength(attributes)
				.addValidationMaxLength(attributes)
			});
		case 'range':
			return getOptional(type, (attr)=>{
				return new RangeInput(attr);
			});
		case 'color':
			return getOptional(type, (attr)=>{
				return new ColorInput(attr, parserEmptyStringIsNull);
			}, []);
		case 'radiolist':
			return new RadioListInput(attributes);
		case 'file':
			return getOptional(type, (attr)=>{
				return new FileInput(attr);
			});
		case 'hidden':
			return new HiddenInput(type, attributes);
		case 'select':
			return new SelectInput(attributes);
		case 'reset':
			console.warn('Reset in fact not working');
		case 'button':
			return new Button(type, attributes);
		case 'submit':
		case 'image':
			return new SubmitInput(type, attributes);
		case 'datetime-local':
			type = 'datetime';
		case 'date':
		case 'time':
		case 'month':
		case 'week':
		case 'datetime':
			return new DatetimeInput(type, attributes);
		case 'dynamicInput':
			return new DynamicInput(attributes, configuration);
		case 'inputList':
			return new InputList(attributes, configuration);
		case 'loadedList':
			return new LoadedList(attributes, configuration);
		default:
			throw new Error('Unsupported input type: "' + type + '"');
	}
}
var inputs = {};
function createInput(attributes, configuration) {
	if (!attributes.hasOwnProperty('unique')) {
		attributes.unique = 'toti-' + Toti.utils.random(100000000);
	}

	var input = _createInput(attributes, configuration);
	inputs[attributes.unique] = input;
	return input;
}