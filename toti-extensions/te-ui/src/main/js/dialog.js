function dialog(configuration) {
	var dialog = {};
	totiUtils.optionalConf(configuration, 'prompt', (message, defValue = "")=>{
		return new Promise((resolve)=>{
			resolve(prompt(message, defValue));
		});
	}, dialog);
	totiUtils.optionalConf(configuration, 'confirm', (message)=>{
		return new Promise((resolve)=>{
			resolve(confirm(message));
		});
	}, dialog);
	totiUtils.optionalConf(configuration, 'alert', (message)=>{
		return new Promise((resolve)=>{
			resolve(alert(message));
		});
	}, dialog);
	return dialog;
}