/* TOTI ExtendedSelect version 0.0.1 */
class ExtendedSelect {

	selectedGroup = null;
	selectedValue = null;
	elements = new SortedMap();
	selfRef = {};

	container = null;
	input = null;
	hints = null;

	onChange = [];

	constructor() {
		this.hints = this.createHintElement('div', false);
		this.hints.classList.add('toti-hints-group');
		this.input = this.createInputElement('input');
		this.input.variation = 'search-select';

		this.container = document.createElement('div');
		this.container.classList.add('toti-hint-select');
		this.container.type = 'search-select';

		this.container.appendChild(this.input);
		this.container.appendChild(this.hints);
	}

	clear() {
		this.hints.innerHTML = "";
		this.input.value = "";
		this.selectedGroup = null;
		this.elements = new SortedMap();
		this.selfRef = {};
		this.selectedValue = null;
	}

	getContainer() {
		return this.container;
	}

	getInput() {
		return this.input;
	}

	setSelectedGroup(value) {
		this.selectedGroup = value;
	}

	addOption(value, title, optGroupValue, optGroupTitle, disabled, level) {
		if (optGroupValue !== null && this.selectedGroup !== null && optGroupValue != this.selectedGroup) {;
			return false;
		}
		var parent = null;
		if (optGroupValue !== null && this.selectedGroup === null) {
			if (level === -1) {
				var key = 'g-' + optGroupValue;
				if (!this.elements.exists(key)) {
					var childContainer = this.createHintElement('div');
					childContainer.classList.add("toti-hints-option-group-options");

					var headerTitle = this.createHintElement('span');
					headerTitle.innerText = optGroupTitle === null ? optGroupValue : optGroupTitle;
					headerTitle.group = true;

					var headerExpand = this.createHintElement('span');
					headerExpand.innerText = '+';
					headerExpand.classList.add('toti-hints-option-group-header-expand');
					headerExpand.group = true;

					var headerCollapse = this.createHintElement('span');
					headerCollapse.innerText = '-';
					headerCollapse.classList.add('toti-hints-option-group-header-collapse');
					headerCollapse.group = true;

					var header = this.createHintElement('div');
					this.overrideProperty(header, 'expand', 'toti-hints-expand');
					header.expand = true;
					header.group = true;
					header.classList.add('toti-hints-option-group-header');
					header.appendChild(headerTitle);
					header.appendChild(headerExpand);
					header.appendChild(headerCollapse);

					var group = this.createHintElement("div");
					group.classList.add("toti-hints-option-group");
					group.appendChild(header);
					group.appendChild(childContainer);
					this.hints.appendChild(group);


					this.elements.put(key, {
						container: group,
						childContainer: childContainer,
						value: optGroupValue,
						title: optGroupTitle,
						selectable: false,
						elements: new SortedMap()
					});
					header.addEventListener('click', ()=>{
						childContainer.show = !childContainer.show;
						header.expand = !header.expand;
					});
				}
				parent = this.elements.get(key);
			} else {
				var key = 'v-' + optGroupValue;
				var element = this.elements.get(key);
				if (element === null && this.selfRef.hasOwnProperty(key)) {
					element = this.selfRef[key];
				}
				if (element !== null) {
					if (!element.hasOwnProperty('elements')) {
						var childContainer = this.createHintElement('div');
						childContainer.classList.add("toti-hints-option-childs");
						childContainer.classList.add("toti-hints-option-childs-" + level);
						
						var headerExpand = this.createHintElement('div');
						headerExpand.innerText = '+';
						headerExpand.classList.add('toti-hints-option-control-expand');
						headerExpand.group = true;

						var headerCollapse = this.createHintElement('div');
						headerCollapse.innerText = '-';
						headerCollapse.classList.add('toti-hints-option-control-collapse');
						headerCollapse.group = true;

						var header = this.createHintElement('div');
						this.overrideProperty(header, 'expand', 'toti-hints-expand');
						header.expand = true;
						header.group = true;
						header.classList.add('toti-hints-option-control');
						header.appendChild(headerExpand);
						header.appendChild(headerCollapse);

						headerExpand.addEventListener('click', ()=>{
							childContainer.show = true;
							header.expand = true;
						});
						headerCollapse.addEventListener('click', ()=>{
							childContainer.show = false;
							header.expand = false;
						});

						var group = this.createHintElement("div");

						
						var parentContainer = element.container.parentElement;
						parentContainer.appendChild(group);
						element.container.remove();

						header.appendChild(element.container);
						group.appendChild(header);
						group.appendChild(childContainer);

						element.container = group;
						element.childContainer = childContainer;
						element.elements = new SortedMap();
					}
					parent = element;
				}
			}
		}
		var container = parent === null ? this.hints : parent.childContainer;
		var elements = parent === null ? this.elements : parent.elements;

		var element = this.createValueItem(title, value, disabled);
		var key = 'v-' + value;
		elements.put(key, {
			container: element,
			value: value,
			title: title,
			selectable: true
		});
		if (parent !== null) {
			this.selfRef[key] = elements.get(key);
		}
		container.appendChild(element);
		return true;
	}

	selectValue(value) {
		if (value === null || value === undefined) {
			this.selectedValue = null;
			return '';
		}
		var key = 'v-' + value;
		var element = this.elements.get(key);
		if (element === null && this.selfRef.hasOwnProperty(key)) {
			element = this.selfRef[key];
		}
		if (element === null) {
			this.selectedValue = null;
			return '';
		}
		this.selectedValue = value;
		return element.title;
	}

	hideHints() {
		this.hints.show = false;
		this.hints.removeAttribute('style');
	}

	showHints(phrase = null) {
		this.hints.show = true;
		var object = this;
		var displayedCount = 0;
		this.elements.forEach((key, element)=>{
			displayedCount += object.processElement(phrase, element);
		});
		function resize() {
			var bonds = this.input.getBoundingClientRect();
	
			var spaceDown = window.innerHeight - bonds.bottom;
			var spaceUp = bonds.top;
	
			var heigth = Math.min(370, 19 * displayedCount);
			var position = bonds.bottom;
			if (spaceDown >= heigth) {
					/* nothing */
			} else if (spaceUp >= heigth || spaceUp > spaceDown) {
				heigth = Math.min(spaceUp, heigth);
				position = bonds.top - heigth;
			} else {
				heigth = spaceDown;
			}
	
			this.hints.style.position = "fixed";
			this.hints.style.top = position + "px";
			this.hints.style.left = bonds.left + "px";
			this.hints.style.width = bonds.width + "px";
			this.hints.style['max-height'] = heigth + "px!important";
		}
		resize();
		new ResizeObserver(resize).observe(this.input);
	}

	processElement(phrase, element) {
		var index = 0;
		if (element.hasOwnProperty('elements')) {
			var result = 0;
			var object = this;
			element.elements.forEach((key, el)=>{
				index += object.processElement(phrase, el);
			});
		}
		var match = element.selectable && totiUtils.unaccent(element.title).includes(totiUtils.unaccent(phrase));
		if (element.value === this.selectedValue) {
			element.container.selected = true;
		} else {
			element.container.selected = false;
		}
		
		if (phrase === null || match || index > 0) {
			element.container.show = true;
			index++;
		} else {
			element.container.show = false;
		}
		return index;
	}

	overrideProperty(element, property, attribute, trueOnly = false) {
		var name = 'toti_' + property;
		Object.defineProperty(element, property, {
		  set(val) {
		  	if (trueOnly && !val) {
				element.removeAttribute(attribute);
		  	} else {
		  		element.setAttribute(attribute, val);
		  	}
		  	this[name] = val;
		  },
		  get() {
		  	return this[name];
		  }
		});
	}

	createValueItem(title, value, disabled = false) {
		var item = this.createHintElement("div", true);
		this.overrideProperty(item, 'value', 'value');
		this.overrideProperty(item, 'disabled', 'disabled', true);
		this.overrideProperty(item, 'selected', 'selected', true);
		this.overrideProperty(item, 'hover', 'toti-hover', true);
		item.disabled = disabled;
		item.value = value;
		item.selected = false;
		item.classList.add("toti-hints-value");
		item.innerText = title;

		item.addEventListener('mouseenter', ()=>{
			if (!item.disabled) {
				item.hover = true;
			}
		});
		item.addEventListener('mouseleave', ()=>{
			if (!item.disabled) {
				item.hover = false;
			}
		});
		var object = this;
		item.addEventListener('click', ()=>{
			if (!item.disabled) {
				object.hideHints();
				var event = new Event('change');
				event.value = item.value;
				object.input.dispatchEvent(event);
			}
		});
		return item;
	}

	createHintElement(name, show = true) {
        var element = document.createElement(name);
		element.classList.add("toti-hints");
		this.overrideProperty(element, 'show', 'toti-show');
		element.show = show;
		return element;
	}

	createInputElement() {
		var object = this;

		var input = document.createElement('input');
		input.classList.add("toti-hints-input");
		input.classList.add("toti-hints");
		var id = "hints_" + Math.floor(Math.random() * 1000000);
		input.hints = id;

		/* override onchange */
		input.parentListener = input.addEventListener;
		input.addEventListener = (name, fn)=>{
			if (name === 'change') {
				object.onChange.push(fn);
				return;
			}
			return input.parentListener(name, fn);
		};
		/* override input.value */
		const nativeValueDesc = Object.getOwnPropertyDescriptor(input, 'value') || Object.getOwnPropertyDescriptor(input.constructor.prototype, 'value');
		Object.defineProperty(input,'value',{
		  set(val) {
		  	var value = object.selectValue(val);
		    /* Continue with native behavior */
		    return nativeValueDesc.set.call(this, value);
		  },
		  get() {
		  	if (object.hints.show) {
		  		return nativeValueDesc.get.call(this);
		  	} else {
		  		return object.selectedValue;
		  	}
		  }
		});
		input.onchange = (e)=>{
			var isFilled = input.value !== null;
			/* click to hints */
			if (!object.hints.show) {
				if (e.hasOwnProperty('value')) {
					input.value = e.value;
				} else {
					input.value = '';
				}
				object.onChange.forEach((fn)=>{
					fn(e);
				});
			} else {
				input.value = '';
				if (!isFilled) {
					object.onChange.forEach((fn)=>{
						fn(e);
					});
				}
			}
		};
		input.addEventListener('click', ()=>{
			/* show hints */
			if (object.hints.show) {
				object.hideHints();
			} else {
				object.showHints();
			}
		});
		input.addEventListener('keyup', totiUtils.delay(()=>{
			if (object.hints.show) {
				object.showHints(input.value);
			}
		}, 500));
		function isOutOfContainer(e) {
			if (!object.hints.show) {
				return;
			}
			if (e.srcElement.classList.contains('toti-hints') && e.srcElement.hints === id) {
				return;
			}
			if (e.srcElement.classList.contains('toti-hints') && e.srcElement.group === true) {
				return;
			}
			/*if (e.srcElement.classList.contains('toti-hints-option-group-header')) {
				return;
			}*/
			object.hideHints();
		}
		document.addEventListener("click", isOutOfContainer);
		document.addEventListener("scroll", (e)=>{
			object.hideHints();
		});
		return input;
	}

}