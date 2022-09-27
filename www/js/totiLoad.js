/* TOTI Load version 1.0.0 */
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
				urlData = {
					...urlData,
					...bodyData
				};
			}
			if (urlData !== null && urlData !== {}) {
				url = url + "?" + new URLSearchParams(urlData).toString();
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
						statusText: xhr.statusText
					});
				}
			};
			xhr.onerror = function () {
				reject({
					status: xhr.status,
					statusText: xhr.statusText
				});
			};
			if (bodyData instanceof FormData) {
				xhr.send(bodyData);
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
        if (typeof totiProfiler !== 'undefined' && totiProfiler.pageId !== null) {
        	headers["PageId"] = totiProfiler.pageId;
        }
        return headers;
	}
};