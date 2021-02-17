var totiLoad = {
	async: function(url, method, data, headers, onSuccess, onFailure) {
		var params = new URLSearchParams(data).toString();
		var xhr = new XMLHttpRequest();
		if (method.toLowerCase() === "get") {
			url += "?" + new URLSearchParams(data).toString();
		}
		xhr.open(method, url, true);
		for (const[name, value] of Object.entries(headers)) {
			xhr.setRequestHeader(name, value);
		}
		xhr.onload = function() {
			if (xhr.status >= 400) {
				onFailure(xhr);
			} else {
				var response = xhr.response;
				try {
	        		response = JSON.parse(xhr.response);
			    } catch (e) {
			    	response = xhr.response;
			    }
			    onSuccess(response, xhr);
			}
			// TODO rs 300 ???
		};
		xhr.send(params);
	},
	link: function(url, method, data, headers) {
		window.location = url + "?ids=" + new URLSearchParams(data).toString();
/*
		var xhr = new XMLHttpRequest()
		xhr.open(method, url, true);
		for (const[name, value] of Object.entries(headers)) {
			xhr.setRequestHeader(name, value);
		}
		var onFinish = function() {
			document.documentElement.innerHTML = xhr.response;
			window.history.pushState({},"", xhr.responseURL);
			totiDisplay.printStoredFlash();
		};
		xhr.onload = onFinish();
		//xhr.onerror = onFinish();
		xhr.send(new URLSearchParams(data).toString());*/
		/* location.reload();
		 window.onload();
		 console.log("onload");*/
	},
	// TODO wll be here ?
	getHeaders: function() {
		return {
			...totiAuth.getAuthHeader(),
			...totiLang.getLangHeader()
		};
	}
};