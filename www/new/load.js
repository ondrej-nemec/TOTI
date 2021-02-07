var totiLoad = {
	async: function(url, method, data, headers, onSuccess, onFailure) {
		totiLoad.request(true, url, method, data, headers, onSuccess, onFailure);
	},
	sync: function(url, method, data, headers, onSuccess, onFailure) {
		totiLoad.request(false, url, method, data, headers, onSuccess, onFailure);
	},
	request: function(async, url, method, data, headers, onSuccess, onFailure) {
		var xhr = new XMLHttpRequest();
		xhr.open(method, url, async);
		for (const[name, value] of Object.entries(headers)) {
			xhr.setRequestHeader(name, value);
		}
		xhr.onload = function() {
			try {
        		onSuccess(JSON.parse(xhr.response), xhr);
		    } catch (e) {
		        onSuccess(xhr.response, xhr);
		    }
		};
		xhr.onerror = function () {
		    onFailure(xhr);
		};
		xhr.send(new URLSearchParams(data).toString());
	},
	link: function(url, method, data, headers) {
		var xhr = new XMLHttpRequest()
		xhr.open(method, url, true);
		for (const[name, value] of Object.entries(headers)) {
			xhr.setRequestHeader(name, value);
		}
		var onFinish = function() {
			document.documentElement.innerHTML = xhr.response;
			window.history.pushState({},"", xhr.responseURL);
			totiDisplay.printStoredFlash();
		/* location.reload();
		 window.onload();
		 console.log("onload");*/
		};
		xhr.onload = onFinish();
		//xhr.onerror = onFinish();
		xhr.send(new URLSearchParams(data).toString());
	},
	// TODO wll be here
	getHeaders: function() {
		return {
			...totiAuth.getAuthHeader(),
			...totiLang.getLangHeader()
		};
	}
};