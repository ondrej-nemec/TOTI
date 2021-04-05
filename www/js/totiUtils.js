/* TOTI Utils version 0.1.2 */
var totiUtils = {
	/* TODO is used? */
	parseUrlToObject: function (data) {
		return JSON.parse('{"' + data.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
	},
	parametrizedString: function(string, params) {
		for(const[name, value] of Object.entries(params)) {
			string = string.replaceAll("\{" + name + "\}", value);
		}
		return string;
	},
	forEach: function(array, callback) {
		/*Array.prototype.forEach.call()*/
		/*if (typeof array === 'object') {*/
			for (const[key, item] of Object.entries(array)) {
				callback(key, item);
			}
	/*	} else {
			array.forEach(function(item, index) {
				callback(index, item);
			});
		}*/
	},
	browser: function() {
		/* https://stackoverflow.com/a/9851769 */
		/* Opera 8.0+ */
		if ((!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0) {
			return "opera";
		}
		/* Firefox 1.0+ */
		if (typeof InstallTrigger !== 'undefined') {
			return "firefox";
		}
		/* Safari 3.0+ "[object HTMLElementConstructor]" */
		var isSafari = /constructor/i.test(window.HTMLElement) || (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] || (typeof safari !== 'undefined' && window['safari'].pushNotification));
		if (isSafari) {
			return "safari";
		}
		/* Internet Explorer 6-11 */
		var isIE = /*@cc_on!@*/false || !!document.documentMode;
		if (isIE) {
			return "IE";
		}
		/* Edge 20+ */
		var isEdge = !isIE && !!window.StyleMedia;
		if (isEdge) {
			return "edge";
		}
		/* Chrome 1 - 79 */
		var isChrome = !!window.chrome; /* && (!!window.chrome.webstore || !!window.chrome.runtime);*/
		/* Edge (based on chromium) detection */
		var isEdgeChromium = isChrome && (navigator.userAgent.indexOf("Edg") != -1);
		if (isEdgeChromium) {
			return "edge-chromium";
		}
		if (isChrome) {
			return "chrome";
		}
		/* Blink engine detection
		var isBlink = (isChrome || isOpera) && !!window.CSS; */
		return "";
	},
	execute: function(callback, args = []) {
		if (callback === null) {
			return;
		}
		if (typeof callback === 'function') {
		    return callback(...args);
		} else {
			return window[callback](...args);
		}
	},
	clone: function(object) {
		return JSON.parse(JSON.stringify(object));
	}
};
