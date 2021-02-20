var totiUtils = {
	// TODO is used?
	parseUrlToObject: function (data) {
		return JSON.parse('{"' + data.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
	},
	parametrizedString: function(string, params) {
		for(const[name, value] of Object.entries(params)) {
			string = string.replaceAll("\{" + name + "\}", value);
		}
		return string;
	},
	forEach: function(array, callback) {
		if (typeof array === 'object') {
			for (const[key, item] of Object.entries(array)) {
				callback(key, item);
			}
		} else {
			array.forEach(function(item, index) {
				callback(index, item);
			});
		}
	}
};