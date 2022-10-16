/* TOTI Display version 1.0.0 */
var totiDisplay = {
	prompt: function(message, defValue = "") {
		return prompt(message, defValue);
	},
	confirm: function(message) {
		return confirm(message);
	},
	alert: function(message) {
		alert(message);
	},
	/**********/
	flashTimeout: 0,
	flash: function(severity, message, error = null) {
		var div = document.createElement("div");

		var img = document.createElement("img");
		img.setAttribute("src", totiImages.cross);
		img.setAttribute("alt", "");
		img.setAttribute("width", 15);
		img.onclick = function() {
			div.style.display = "none";
		};

		var span = document.createElement("span");
		span.innerHTML = '&nbsp;&nbsp;' + message;

		div.setAttribute("class", 'flash flash-' + severity);
		div.appendChild(img);
		div.appendChild(span);

		if (totiDisplay.flashTimeout > 0) {
			setTimeout(function() {
				div.style.display = "none";
			}, totiSettings.flashTimeout);
		}
		var flash = document.getElementById('flash');
		if (flash !== null) {
			flash.appendChild(div);
		}
		switch(severity.toLowerCase()) {
			case "error":
				console.error("Flash " + severity + ": " + message);
				if (error !== null) {
					console.error(error);
				}
				break;
			case "warn":
			case "warning":
				console.warn("Flash " + severity + ": " + message);
				if (error !== null) {
					console.warn(error);
				}
				break;
			default:
				console.log("Flash " + severity + ": " + message);
				if (error !== null) {
					console.log(error);
				}
		}
	},
	storedFlash: function(severity, message) {
		var name = 'flash';
		var actual = totiStorage.getVariable(name);
		if (actual === null) {
			actual = {};
		}
		if (actual[severity] === undefined) {
			actual[severity] = [];
		}
		actual[severity].push(message);
		totiStorage.saveVariable(name, actual);
	},
	/**********/
	isFade: false,
	fadeIn: function() {
		if (totiDisplay.isFade) {
			return;
		}
		var fade = document.createElement('div');
		fade.style.position = "absolute";
		fade.style.top = 0;
		fade.style.left = 0;
		fade.style.width = "100%";
		fade.style.height = "100%";
		fade.style['z-index'] = 2000;
		fade.style['background-color'] = "rgba(255,255,255,0.5)";

		fade.setAttribute("id", "toti-fade-in");
		fade.onclick = function() {}; /* prevent click */

		document.body.appendChild(fade);
// TODO loading picture
		totiDisplay.isFade = true;
	},
	fadeOut: function() {
		if (!totiDisplay.isFade) {
			return;
		}
		document.querySelector("#toti-fade-in").remove();
		totiDisplay.isFade = false;
	},
	/*********************/
	gridTemplate: totiGridDefaultTemplate,
	formTemplate: totiFormDefaultTemplate
};