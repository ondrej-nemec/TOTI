/* TOTI Load version 2.0.0 */
var totiLoad = {
	/*
	- headers: object
	- queryParams: object|URLSearchParams
	- bodyData: object|URLSearchParams|string|FormData
	*/
	async: function(url, method, headers = {}, queryParams = {}, bodyData = {}) {
		return totiLoad.asyncAnonymous(url, method, {
			...headers,
			...totiLoad.getTotiHeaders()
		}, queryParams, bodyData);
	},
	/*
	- headers: object
	- queryParams: object|URLSearchParams
	- bodyData: object|URLSearchParams|string|FormData
	*/
	asyncAnonymous: function(url, method, headers = {}, queryParams = {}, bodyData = {}) {
		return new Promise(function (resolve, reject) {
			var xhr = new XMLHttpRequest();
			
			url = totiLoad.createLink(url, queryParams);
			/* sending body is not supported for GET and HEAD */
			if (method.toLowerCase() === "get" || method.toLowerCase() === "head") {
				url = totiLoad.createLink(url, bodyData);
			}
			xhr.open(method, url);

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
	/*
	- body data: FormData|object|URLSearchParams
	- queryParams: object|URLSearchParams
	- formAttributes: object
	*/
	sync: function(url, method, formAttributes = {}, queryParams = {}, bodyData = {}) {
		var formTosend = document.createElement("form");
		formTosend.style.display = "none";
		document.body.appendChild(formTosend);

		formTosend.setAttribute("action", totiLoad.createLink(url, queryParams));
		formTosend.setAttribute("method", method);
		for(const[name, value] of Object.entries(formAttributes)) {
			formTosend.setAttribute(name, value);
		}
		totiLoad.iterateParams(bodyData, (name, value)=>{
			var hidden = document.createElement("input");
			hidden.name = name;
			hidden.value = value;
			formTosend.appendChild(hidden);
		});
		formTosend.submit();
		formTosend.remove();
	},
	/*************/
	createLink: function(link, params) {
		var url = new URL(link, document.location);
		totiLoad.iterateParams(params, (name, value)=>{
			url.searchParams.set(name, value);
		});
		return url.toString();
	},
	iterateParams: function(params, callback) {
		function onItem(name, value) {
			totiLoad.parseParams(name, value, (n, v)=>{
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
	},
	parseParams: function(name, value, addItem) {
		if (value === null) {
			/* ignore */
		} else if (Array.isArray(value)) {
			value.forEach((item)=>{
				totiLoad.parseParams(name + '[]', item, addItem);
			});
		} else if (typeof value === 'object') {
			for(const[key, item] of Object.entries(value)) {
				totiLoad.parseParams(name + '[' + key + ']', item, addItem);
			}
		} else {
			addItem(name, value);
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