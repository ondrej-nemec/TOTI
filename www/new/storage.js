var totiStorage = {
	saveVariable: function(name, value) {
		localStorage[name] = JSON.stringify(value);
	},
	getVariable: function(name) {
		if (!localStorage[name] || localStorage[name] === null || (localStorage[name] == 'null') || localStorage[name] === undefined) {
			return null;
		}
		return JSON.parse(localStorage[name]);
	},
	removeVariable: function(name) {
		localStorage.removeItem(name);
	}
};