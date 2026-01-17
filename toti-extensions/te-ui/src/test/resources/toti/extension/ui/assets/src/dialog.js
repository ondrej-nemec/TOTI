function dialog(configuration) {
	var dialog = {};
	function create(name, func) {
		if (configuration.hasOwnProperty(name)) {
			dialog[name] = configuration[name];
		} else {
			dialog[name] = func;
		}
	}
	create('promt', (message, defValue = "")=>{
		return new Promise((resolve)=>{
			resolve(prompt(message, defValue));
		});
	});
	create('confirm', (message)=>{
		return new Promise((resolve)=>{
			resolve(confirm(message));
		});
	});
	create('alert', (message)=>{
		return new Promise((resolve)=>{
			resolve(alert(message));
		});
	});
	return dialog;
}