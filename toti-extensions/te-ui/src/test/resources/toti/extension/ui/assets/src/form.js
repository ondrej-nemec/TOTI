class Form {
	container = null;
	inputs = null;
	callbacks = null;
	editable = true;
	errors = null; /* errors not related to any input */
	submitConfiguration = null;
	bindConfiguration = null;
	constructor(attributes, configuration) {
		if (!attributes.hasOwnProperty('inputs')) {
			throw new Error("Missing attribute 'inputs'");
		}
		if (attributes.hasOwnProperty('editable')) {
			this.editable = attributes.editable;
		}
		if (attributes.hasOwnProperty('submit')) {
			this.submitConfiguration = attributes.submit;
		} else {
			console.warn("Form is missing submit configuration");
		}
		if (attributes.hasOwnProperty('bind')) {
			this.bindConfiguration = attributes.bind;
		}
/*
	
	private String onBindFailure;
	private String beforeBind;
	private String afterBind;
	private String beforeRender;
	private String afterRender;
	private String beforeSubmit;
	private String afterSubmit;

*/
		this.inputs = new SortedMap();
		this.callbacks = configuration;
		if (!this.callbacks.hasOwnProperty('createContainer')) {
			this.callbacks.createContainer = ()=>{
				var errors = document.createElement('div');
				errors.classList.add('toti-form-error');

				var body = document.createElement('div');
				body.classList.add('toti-form-body');

				var form = document.createElement('form');
				form.appendChild(errors);
				form.appendChild(body);
				return form;
			};
		}
		if (!this.callbacks.hasOwnProperty('createStandartInputBundle')) {
			this.callbacks.createStandartInputBundle = (container, title, inputContainer, input)=>{
				function createInline(clazz) {
					var div = document.createElement('div');
					div.style.display = 'inline-block';
					div.classList.add(clazz);
					return div;
				}
				var titleDiv = createInline('toti-form-input-title');
				if (title !== null) {
					titleDiv.innerText = title;
				}
				var inputDiv = createInline('toti-form-input-element');
				inputDiv.appendChild(inputContainer);

				var errorsDiv = createInline('toti-form-input-errors');

				var row = document.createElement('div');
				row.classList.add('toti-form-input-container');
				row.appendChild(titleDiv);
				row.appendChild(inputDiv);
				row.appendChild(errorsDiv);

				container.querySelector('.toti-form-body').appendChild(row);
				return row;
			};
		}
		if (!this.callbacks.hasOwnProperty('createActionInputBundle')) {
			this.callbacks.createActionInputBundle = (container, title, inputContainer, input)=>{
				function createInline(clazz) {
					var div = document.createElement('div');
					div.classList.add(clazz);
					return div;
				}
				var inputDiv = createInline('toti-form-input-element');
				inputDiv.appendChild(inputContainer);

				var row = document.createElement('div');
				row.classList.add('toti-form-input-container');
				row.appendChild(inputDiv);

				container.querySelector('.toti-form-body').appendChild(row);
				return row;
			};
		}
		if (!this.callbacks.hasOwnProperty('createSpecialInputBundle')) {
			this.callbacks.createSpecialInputBundle = (container, title, inputContainer, input)=>{
				function createInline(clazz) {
					var div = document.createElement('div');
					div.classList.add(clazz);
					return div;
				}
				var inputDiv = createInline('toti-form-input-element');
				inputDiv.appendChild(inputContainer);

				var errorsDiv = createInline('toti-form-input-errors');

				var row = document.createElement('div');
				row.classList.add('toti-form-input-container');
				row.appendChild(inputDiv);
				row.appendChild(errorsDiv);

				container.querySelector('.toti-form-body').appendChild(row);
				return row;
			};
		}
		if (!this.callbacks.hasOwnProperty('addHidden')) {
			this.callbacks.addHidden = (container, inputContainer, input)=>{
				container.querySelector('.toti-form-body').appendChild(inputContainer);
			};
		}
		if (!this.callbacks.hasOwnProperty('inputErrors')) {
			this.callbacks.inputErrors = (row, isValid, errors, input)=>{
				var container = row.querySelector('.toti-form-input-errors');
				if (container === null) {
					if (!isValid) {
						console.warn('Missing error container', row, errors);
					}
					return;
				}
				container.innerHTML = "";
				if (isValid) {
					return;
				}
				var detail = document.createElement('div');
				for (const[message, params] of Object.entries(errors)) {
					// TODO translations
					var row = document.createElement('div');
					row.innerText = message + " " + JSON.stringify(params);
					detail.appendChild(row);
				}
				container.appendChild(detail);
			};
		}
		this.container = this.callbacks.createContainer();
		for(const inputAttribute of attributes.inputs) {
			this._addInput(inputAttribute);
		}
		if (this.bindConfiguration !== null) {
			this.loadBind(this.bindConfiguration);
		}
	}
	getContainer() {
		return this.container;
	}
	/* @Deprecated */
	render(parentElement) {
		parentElement.appendChild(this.container);
	}
	_addInput(fieldConf) {
		var object = this;
		if (!fieldConf.hasOwnProperty('editable')) {
			fieldConf.editable = this.editable;
		}
		var element = Toti.createInput(fieldConf);
		if (element instanceof HiddenInput) {
			if (this.editable) {
				this.callbacks.addHidden(this.container, element.getContainer(), element);
			} else {
				return;
			}
		}
		var row = null;
		if (element instanceof DynamicInput || element instanceof InputList || element instanceof LoadedList) {
			row = this.callbacks.createSpecialInputBundle(
				this.container, element.getTitle(), element.getContainer(), element
			);
		} else if (element instanceof SubmitInput) {
			// TODO corrent submit
			if (this.submitConfiguration !== null) {
				element.setSubmit(this.submitConfiguration, this.container);
			}
			row = this.callbacks.createActionInputBundle(
				this.container, element.getTitle(), element.getContainer(), element
			);
		} else if (element instanceof Button) {
			row = this.callbacks.createActionInputBundle(
				this.container, element.getTitle(), element.getContainer(), element
			);
		} else {
			row = this.callbacks.createStandartInputBundle(
				this.container, element.getTitle(), element.getContainer(), element
			);
		}

		var checkInputValidity = (input)=>{
			object.callbacks.inputErrors(row, input.isValid(), input.getErrors(), input);
		};

		element.onChange(checkInputValidity);
		checkInputValidity(element);
		this.inputs.put(element.getName(), {
			element: element,
			row: row
		});
	}
	getInput(name) {
		if (this.inputs.exists(name)) {
			return this.inputs.get(name).element;
		}
	}
	loadBind(loadConfig) {
		var object = this;
		Toti.load(loadConfig)
		.then((data)=>{
			object.bind(data);
		});
	}
	bind(values) {
		for(const[name, value] of Object.entries(values)) {
			if (this.inputs.exists(name)) {
				this.inputs.get(name).element.setValue(value);
			} else {
				console.warn("Unknown input '" + name + "'");
			}
		}
	}
	submit() {
		this.errors = null;
		// TODO
		// isValid
		// submitConfiguration + getValues
	}
/*
TODO pro nasleduji metody
		- exclude, editable, ....
		- isValid, getErrors
		- submit - policy
	*/
	_getValues() {
		var values = {};
		this.inputs.forEach((key, input)=>{
			values[key] = input.element.getValue();
		});
		return values;
	}
	_isValid() {
		var isValid = true;
		this.inputs.forEach((key, input)=>{
			isValid = isValid && input.element.isValid();
		});
		return isValid;
	}
	_getErrors() {
		var errors = {};
		if (this.errors !== null) {
			errors['_form'] = this.errors;
		}
		this.inputs.forEach((key, input)=>{
			errors[key] = input.element.getErrors();
		});
		return errors;
	}
}
function createForm(configuration, settings) {
	return new Form(configuration, settings);
}