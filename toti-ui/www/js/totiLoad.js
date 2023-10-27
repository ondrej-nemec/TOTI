/* TOTI Load version 1.0.2 */
var totiLoad = {
	anonymous: function(url, method, headers = {}, urlData = {}, bodyData = {}) {
		return totiLoad._load(url, method, headers, urlData, bodyData);
	},
	load: function(url, method, headers = {}, urlData = {}, bodyData = {}) {
		return totiLoad._load(url, method, {
			...headers,
			...totiLoad.getTotiHeaders()
		}, urlData, bodyData);
	},
	_load: function(url, method, headers, urlData, bodyData) {
		return new Promise(function (resolve, reject) {
			var xhr = new XMLHttpRequest();
			/* sending body is not supported for GET and HEAD */
			if (method.toLowerCase() === "get" || method.toLowerCase() === "head") {
				if (bodyData instanceof FormData) {
					for (const[name, value] of bodyData.entries()) {
						urlData[name] = value;
					}
				} else {
					urlData = {
						...urlData,
						...bodyData
					};
					var auxData = {};
					for (const[name, value] of Object.entries(urlData)) {
						totiLoad.parseParams(auxData, name, value);
					}
					urlData = auxData;
				}
			}
			if (urlData !== null && urlData !== {}) {
				url = url + "?" + ((urlData instanceof URLSearchParams) ? urlData : new URLSearchParams(urlData)).toString();
			}
			xhr.open(method, url); /* method*/

			for (const[name, value] of Object.entries(headers)) {
				xhr.setRequestHeader(name, value);
			}
			xhr.onload = function () {
				if (xhr.status >= 200 && xhr.status < 300) {
					var response = xhr.response;
					var type = xhr.getResponseHeader('Content-Type');
					if (type !== null && type.toLowerCase().startsWith('application/json')) {
						response = JSON.parse(response);
					}
					resolve(response);
				} else {
					reject({
						status: xhr.status,
						statusText: xhr.statusText,
						responseText: xhr.responseText
					});
				}
			};
			xhr.onerror = function () {
				reject({
					status: xhr.status,
					statusText: xhr.statusText,
					responseText: xhr.responseText
				});
			};
			if (bodyData instanceof FormData) {
				xhr.send(bodyData);
			} else if (bodyData instanceof URLSearchParams) {
				/* clear automatic header */
				if (!headers.hasOwnProperty("Content-Type")) {
					xhr.setRequestHeader("Content-Type", "");
				}
				xhr.send(bodyData.toString());
			} else if (typeof bodyData === 'string' || bodyData instanceof String) {
				xhr.send(bodyData);
			} else {
				/* clear automatic header */
				if (!headers.hasOwnProperty("Content-Type")) {
					xhr.setRequestHeader("Content-Type", "");
				}
				xhr.send(new URLSearchParams(bodyData).toString());
			}
		});
	},
	parseParams: function(result, name, value) {
		if (value === null) {
			/* ignore */
		} else if (Array.isArray(value)) {
			value.forEach((item)=>{
				totiLoad.parseParams(result, name + '[]', item);
			});
		} else if (typeof value === 'object') {
			for(const[key, item] of Object.entries(value)) {
				totiLoad.parseParams(result, name + '[' + key + ']', item);
			}
		} else {
			result[name] = value;
		}
	},
	getTotiHeaders: function() {
		var headers = {};
		var lang = totiStorage.getVariable(totiLang.variableName);
		if (lang !== null) {
			headers["Accept-Language"] = lang;
		}
		var authToken = totiStorage.getVariable(totiAuth.variableToken);
        if (authToken !== null) {
           headers["Authorization"] = "bearer " + authToken.access_token;
        }
        return headers;
	}
};