function createForm(configuration) {
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
	return new class {
		container = null;
		inputs = null;
		constructor(configuration) {
			console.log("Form", configuration, Toti);
		}
		getContainer() {}
		addInput(fieldConf) {}
		getInput(name) {}
		loadBind(loadConfig) {}
		loadPlaceholders(loadConfig) {}
		placeholers(values) {}
		bind(values) {}
		render() {}
		submit() {}
	};
}