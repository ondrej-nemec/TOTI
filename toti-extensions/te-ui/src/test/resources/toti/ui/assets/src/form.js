/*
	nejaka metoda, co udela render, placeholders a bind
- render
	- before a after callbacks
	- template|functions callbacks
	- action a method, pripadně enctype
	- iterovat existujici prvky a pridavat do containeru - pres metodu

- bind
	- nacist hodnoty a postupne dodat

- submit - TODO nejak navazat na submity
	- submit uz v sobe ma confirmation
	- projit vsechny inputy a vytvorit object dat
		- exclude, editable, ....
		- isValid, getErrors
		- submit - policy
	- beforesubmit callbacky (customhandler - muze zrusit odeslani) 
	- submit - sync|async
	- onSuccess
		- afterSubmit
		- submit ma v sobe onSuccess a redirect - skloubit
	- onError
		- pokud 400 a je to json - vykreslit chyby
		- submit ma na sobe onFailure
		- chybovou hlasku podle response 400, 403, ostatni

*/
class Form {
	container = null;
	inputs = null;
	callbacks = null;
	editable = true;
	constructor(attributes, configuration) {
		// TODO callbacks - gui - default, template, js set
		if (!attributes.hasOwnProperty('inputs')) {
			throw new Error("Missing attribute 'inputs'");
		}
		if (attributes.hasOwnProperty('editable')) {
			this.editable = attributes.editable;
		}

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
			return;
		}
		var row = null;
		if (element instanceof DynamicInput || element instanceof InputList || element instanceof LoadedList) {
			row = this.callbacks.createSpecialInputBundle(
				this.container, element.getTitle(), element.getContainer(), element
			);
		} else if (element instanceof Button || element instanceof SubmitInput) {
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
	/*
	// deprecated
	loadPlaceholders(loadConfig) {}
	// deprecated
	placeholers(values) {}
	*/
	loadBind(loadConfig) {
		// TODO
	}
	bind(values) {
		for(const[name, value] of Object.entries(values)) {
			if (this.inputs.hasOwnProperty(key)) {
				this.inputs.get(key).element.setValue(value);
			} else {
				console.warning("Unknown input '" + key + "'");
			}
		}
	}
	submit() {
		// TODO
	}
	_getValues() {
		var values = {};
		this.inputs.forEach((key, input)=>{
			values[key] = input.getValue();
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
}
function createForm(configuration, settings) {
	return new Form(configuration, settings);
}