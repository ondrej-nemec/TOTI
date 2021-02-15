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
	}
};