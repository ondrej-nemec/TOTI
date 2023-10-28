/* TOTI StandartSelect version 0.0.0 */
class StandartSelect {

	selectedGroup = null;
	container = null;
	groups = {};

	constructor() {
		this.container = document.createElement('select');
	}

	clear() {
		this.container.innerHTML = "";
		this.groups = {};
		this.selectedGroup = null;
	}

	setSelectedGroup(value) {
		this.selectedGroup = value;
	}

	addOption(value, title, optGroupValue, optGroupTitle, disabled, level) {
		if (optGroupValue !== null && this.selectedGroup !== null && optGroupValue != this.selectedGroup) {;
			return false;
		}
		var container = this.container;
		/* Native HTML optgroup not for self ref */
		if (optGroupValue !== null && this.selectedGroup === null && level === -1) {
			if (!this.groups.hasOwnProperty(optGroupValue)) {
				var group = document.createElement("optgroup");
				group.setAttribute("label", optGroupTitle === null ? optGroupValue : optGroupTitle);
				this.container.appendChild(group);
				this.groups[optGroupValue] = group;
			}
			container = this.groups[optGroupValue];
		}
		var option = document.createElement('option');
		if (value !== null) {
			option.value = value;
		}
		if (disabled) {
			option.setAttribute("disabled", true);
		}

		if (level > 0) {
			var pre = document.createElement('span');
			for (var i = 0; i < level; i++) {
				pre.innerHTML += '&nbsp;&nbsp;';
			}
			var titlePart = document.createElement('span');
			titlePart.innerText = title;
			option.appendChild(pre);
			option.appendChild(titlePart);
		} else {
			option.innerText = title;
		}
		/*if (isPrompt) {
			option.setAttribute("selected", true);
		}*/
		container.appendChild(option);
		return true;
	}

	getContainer() {
		return this.container;
	}

	getInput() {
		return this.container;
	}

	/*createElement() {
		var select = document.createElement('select');
		this.container = select;
		return select;
	}*/

	_createOption(params) {
		var option = document.createElement('option');
		for ([key, value] of Object.entries(params)) {
			if (key === "title") {
				option.innerText = value;
			} else if (key === "disabled") {
				if (value === true) {
					option.setAttribute("disabled", true);
				}
			} else {
				option.setAttribute(key, value);
			}

		}
		return option;
	}

}