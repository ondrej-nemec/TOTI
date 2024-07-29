const Toti = function () {
	class Input {
		value = null;
		container = null;
		setValue(value) {
			this.value = value;
			console.log("TODO onChange " + value);
			// TODO onChange
		}
		getValue() {
			return this.value;
		}
		getContainer() {
			return this.container;
		}
		onPrint(callback) {
			// TODO
		}
		onChange(callback) {
			// TODO
		}
		getType() {}
		addListener(method, callback) {}
	}
	class StandartInput extends Input {}
	class OptionalInput extends Input {}
	class DynamicInput extends Input {}
	class InputList extends Input {}
	class LoadedInput extends Input {}
	class DatetimeInput extends Input {}
	return {
		init(configuration) {
			// Toti.configuration = configuration;
		},
		Form: (conf)=>{
			return new class {
				constructor(configuration) {
					console.log("Form", configuration, Toti);
				}
				addInput(fieldConf) {}
				submit() {}
				loadBind(loadConfig) {}
				bindValues(values) {}
				loadPlaceholders(loadConfig) {}
			}(conf)
		},
		Grid: (conf)=>{
			return new class {
				constructor(configuration) {
					console.log("Grid", configuration, Toti);
				}
				render() {}
				sortBy(name, direction, refresh = true) {}
				nextSort(name, refresh = true) {}
				filterBy(name, value, refresh = true) {}
				setPageIndex(pageIndex, refresh = true) {}
				addNextPage(refresh = true) {}
				setPageSize(pageSize, refresh = true) {}
				startRefresh() {}
				stopRefresh() {}
				getSelectedRow() {}
				refreshData(clearPrevious = true) {}
			}(conf)
		},
		load(configuration) {
		/*	function iterateParams(params, callback) {
				function parseParams(name, value, addItem) {
					if (value === null) {
						/* ignore *//*
					} else if (Array.isArray(value)) {
						value.forEach((item)=>{
							parseParams(name + '[]', item, addItem);
						});
					} else if (typeof value === 'object') {
						for(const[key, item] of Object.entries(value)) {
							parseParams(name + '[' + key + ']', item, addItem);
						}
					} else {
						addItem(name, value);
					}
				}
				function onItem(name, value) {
					parseParams(name, value, (n, v)=>{
						callback(n, v);
					});
				}
				if (params instanceof URLSearchParams) {
					params.forEach((value, name)=>{
						onItem(name, value);
					});
				} else if (params instanceof FormData) {
					for (const[name, value] of params.entries()) {
						onItem(name, value);
					}
				} else if (typeof params === 'object') {
					for(const[name, value] of Object.entries(params)) {
						onItem(name, value);
					}
				}
			}
			function getProperty(name, def = null) {
				if (configuration.hasOwnProperty(name)) {
					return configuration[name];
				}
				if (def === null) {
					throw new Error('Toti.load: Parameter "' + name '" is required.');
				}
				return def;
			}
			var url = new URL(getProperty('url'), document.location);
			var method = getProperty('method', 'get').toLowerCase();
			
			iterateParams(getProperty('query' {}), (name, value)=>{
				url.searchParams.set(name, value);
			});

			/* sending body is not supported for GET and HEAD *//*
			if (method === "get" || method === "head") {
				iterateParams(getProperty('body', {}), (name, value)=>{
					url.searchParams.set(name, value);
				});
			}

			var xhr = new XMLHttpRequest();
			xhr.open(method, url.toString());

			for (const[name, value] of Object.entries(getProperty('headers' {}))) {
				xhr.setRequestHeader(name, value);
			}
*/
			// TODO zkusit fetch?
			/*
			pri GET a HEAD prevest body na params
			url - extrahovat pripadne query parametry
			*/
		}
	};
}();