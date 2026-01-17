var totiUtils = {
	clone: (data)=>{
		var functions = {};
		var json = JSON.stringify(data, (key, value)=>{
			if (typeof value === 'function') {
				var unique = 'function_' + totiUtils.random();
				functions[unique] = value;
				return unique;
			}
			return value;
		});
		return JSON.parse(json, (key, value, context)=>{
			if (functions.hasOwnProperty(value)) {
				return functions[value];
			}
			return value;
		});
	},
	random: (size = 10000000)=>{
		return Math.floor(Math.random() * size);
	},
	sleep: function(ms) {
		return new Promise(resolve => setTimeout(resolve, ms));
	},
	unaccent: function(str) {
		if (str === null) {
			return null;
		}
		/* https://stackoverflow.com/…463 */
		return str.toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
	},
	parametrizedString: function(string, params) {
		for(const[name, value] of Object.entries(params)) {
			string = string.replaceAll("\{" + name + "\}", value);
		}
		return string;
	},
	/* https://stackoverflow.com/a/1026087/8240462 */
	capitalizeFirstLetter: function (val) {
	    return String(val).charAt(0).toUpperCase() + String(val).slice(1);
	},
	execute: function(callback, args = [], evaluate = false) {
		if (callback === null) {
			console.warn('No callback given');
			return;
		}
		if (typeof callback === 'function') {
		    return callback(...args);
		}
		var func = Toti.getCallback(callback);
		if (func !== null) {
			return func(...args);
		}
		if (window.hasOwnProperty(callback)) {
			return window[callback](...args);
		}
		if (evaluate === true) {
			return eval(totiUtils.parametrizedString(callback, args));
		}
		console.warn('Unknown callback', callback);
	},
};
/* for jest unit test */
if (typeof module !== 'undefined') {
	module.exports = totiUtils;
}