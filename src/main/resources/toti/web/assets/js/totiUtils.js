/* TOTI Utils version 1.0.0 */
var totiUtils = {
	/* TODO is used? */
	sleep: function(ms) {
		return new Promise(resolve => setTimeout(resolve, ms));
	},
	parseUrlToObject: function (data) {
		return JSON.parse('{"' + data.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g,'":"') + '"}');
	},
	parametrizedString: function(string, params) {
		for(const[name, value] of Object.entries(params)) {
			string = string.replaceAll("\{" + name + "\}", value);
		}
		return string;
	},
	browser: function() {
		console.warning("totiUtils.browser() is deprecated");
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
	execute: function(callback, args = [], evaluate = false) {
		if (callback === null) {
			return;
		}
		if (typeof callback === 'function') {
		    return callback(...args);
		} else if (window.hasOwnProperty(callback)) {
			return window[callback](...args);
		} else if (evaluate === true) {
			/* return eval(totiUtils.parametrizedString(callback, args));*/
			return eval(callback);
		}
	},
	clone: function(object) {
		return JSON.parse(JSON.stringify(object));
	},
	getCookie: function(name) {
	  name += "=";
	  var ca = document.cookie.split(';');
	  for(var i = 0; i < ca.length; i++) {
	    var c = ca[i];
	    while (c.charAt(0) == ' ') {
	      c = c.substring(1);
	    }
	    if (c.indexOf(name) == 0) {
	      return c.substring(name.length, c.length);
	    }
	  }
	  return null;
	},
	setCookie: function(name, value, maxAge = null, path = '/') {
		if (path === null) {
			path = window.location.pathname;
		}
		var cookieString = name + "=" + value + ";SameSite=Strict;path=" + path;
		if (maxAge !== null) {
			cookieString += ";Max-Age=" + maxAge;
		}
		document.cookie = cookieString;
	},
	printStoredFlash: function() {
		var name = 'flash';
		var actual = totiStorage.getVariable(name);
		if (actual !== null) {
			for (const[severity, messages] of Object.entries(actual)) {
				messages.forEach(function(message) {
					totiDisplay.flash(severity, message);
				});
			}
			totiStorage.removeVariable(name);
		}
	},
	replaceElement: function(container, selector, newElement, excludeAttributes = []) {
		var placeholder = container.querySelector(selector);
		if (placeholder !== null && newElement !== null) {
			var atts = placeholder.attributes;
			for (var i = 0; i < atts.length; i++){
				if (!excludeAttributes.includes(atts[i].nodeName)) {
					newElement.setAttribute(atts[i].nodeName, atts[i].nodeValue);
				}
			}
			placeholder.parentNode.replaceChild(newElement, placeholder);
		}
	}
};
document.addEventListener("DOMContentLoaded", function(event) { 
	totiUtils.printStoredFlash();
});
