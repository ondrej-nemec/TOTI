const baseUrl = 'https://example.com';
function _parseUrl(urlString, callback) {
	var url = new URL(urlString, new URL(baseUrl));
	var diff = url.toString().replace(urlString, '');
	callback(url);
	return url.toString().replace(diff, '');
}

function _parseParams(params, fAdd) {
	for (const[name, param] of Object.entries(params)) {
		_parseParam(name, param, fAdd);
	}
}
function _parseParam(name, param, fAdd) {
		if (param === null) {
				// TODO nothing? or empty string?
		} else if (Array.isArray(param)) {
			param.forEach((p)=>{
				_parseParam(name + '[]', p, fAdd);
			});
		} else if (typeof param === 'object') {
			for (const[n, p] of Object.entries(param)) {
				_parseParam(name + '[' + n + ']', p, fAdd);
			}
		} else {
			fAdd(name, param);
		}
}


function parsePathParams(urlString, configuration) {
	if (!configuration.hasOwnProperty('path') || !Array.isArray(configuration.path)) {
		return urlString;
	}
	return _parseUrl(urlString, (url)=>{
		configuration.path.forEach((param)=>{
			if (url.pathname.includes('[param]')) {
				url.pathname = url.pathname.replace('[param]', param);
			} else {
				var path = url.pathname;
				if (!path.endsWith('/')) {
					path += '/';
				}
				url.pathname = path + param;
			}
		});
	});
}
function parseQueryParams(urlString, configuration, name) {
	if (configuration.hasOwnProperty(name)) {
		return _parseUrl(urlString, (url)=>{
			_parseParams(configuration[name], (n, p)=>{
				url.searchParams.append(n, p);
			});
		});
	}
	return urlString;
}
function getMethod(configuration) {
	if (configuration.hasOwnProperty('method')) {
		return configuration.method;
	}
	return 'GET';
}
function parseBodyParams(configuration) {
	if (configuration.hasOwnProperty('body')) {
		if (configuration.body.constructor.name !== 'Object') {
			/*
				// a string
				// ArrayBuffer
				// TypedArray
				// DataView
				// Blob
				// File
				// URLSearchParams
				// FormData
				// ReadableStream
			*/
			return body;
		}
		if (configuration.hasOwnProperty('withFile') && configuration.withFile) {
			var data = new FormData();
			_parseParams(configuration.body, (n, p)=>{
				data.append(n, p);
			});
			return data;
		} else {
			return JSON.stringify(configuration.body);
		}
	}
	return null;
}
function parseHeaders(configuration) {
	var headers = new Headers();
	if (configuration.hasOwnProperty('headers')) {
		return new Headers(configuration.headers);
			/* following implementation is not required */
			/*for (const[name, value] of Object.entries(configuration.headers)) {
				if (Array.isArray(value)) {
					value.forEach((val)=>{
						headers.append(name, val);
					});
				} else {
					headers.append(name, value);
				}
			}*/
	}
	return headers;
}
function load(configurations) {
	if (configurations.length === 0) {
		throw new Error('Toti.load expects at least one configuration');
	}
	var requests = [];
	configurations.forEach((configuration)=>{
		/*
{
		url: string
		method: string (opt)
		body: object|formData (opt)
		query: object (opt)
		path: list|object ? (opt)
		headers: object (opt)
		withFile: bool (opt)
	}
		*/
		if (!configuration.hasOwnProperty('url')) {
			throw new Error('Url parameter is required');
		}
		var method = getMethod(configuration);
		var url = parseQueryParams(
			parsePathParams(configuration.url, configuration),
			configuration,
			'query'
			);
		var body = null;
		if (method.toLowerCase() === 'get' || method.toLowerCase() === 'head') {
			url = parseQueryParams(url, configuration, 'body');
		} else {
			body = parseBodyParams(configuration);
		}

		var headers = parseHeaders(configuration);
		requests.push(fetch(new Request(url, {
			method: method,
			headers: headers,
			body: body
		})));
	});
	if (requests.length === 1) {
		return requests[0];
	}
	return Promise.all(requests);
/*
const response = await fetch(request);
console.log('Status', response.status);
console.log('Is ok', response.ok);
console.log('Headers', response.headers);

// Response.arrayBuffer()
// Response.blob()
// Response.formData()
// Response.json()
// Response.text()
*/
}


/* for jest unit test */
if (typeof module !== 'undefined') {
	module.exports = {
		parsePathParams: parsePathParams,
		parseQueryParams: parseQueryParams,
		getMethod: getMethod,
		parseBodyParams: parseBodyParams,
		parseHeaders: parseHeaders
	};
}