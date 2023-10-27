/* TOTI Display version 1.1.0 */
var totiDisplay = {
	prompt: function(message, defValue = "") {
		return new Promise((resolve)=>{
			resolve(prompt(message, defValue));
		});
	},
	confirm: function(message) {
		return new Promise((resolve)=>{
			resolve(confirm(message));
		});
	},
	alert: function(message) {
		return new Promise((resolve)=>{
			resolve(alert(message));
		});
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
	isFade: 0,
	showFade: function(fade) {},
	removeFade: function(fade) {},
	fadeIn: function() {
		var fadeElement = document.querySelector("#toti-fade-in");
		if (fadeElement === null) {
			var fade = document.createElement('div');
			fade.style.position = "fixed";
			fade.style.top = 0;
			fade.style.left = 0;
			fade.style.width = "100%";
			fade.style.height = "100%";
			fade.style['z-index'] = 2000;
			fade.style['background-color'] = "rgba(255,255,255,0.5)";

			fade.setAttribute("id", "toti-fade-in");
			fade.onclick = function() {}; /* prevent click */

			document.body.appendChild(fade);
			totiDisplay.showFade(fade);
		}
		totiDisplay.isFade++;
	},
	fadeOut: function() {
		totiDisplay.isFade--;
		if (totiDisplay.isFade < 1) {
			var fadeElement = document.querySelector("#toti-fade-in");
			totiDisplay.removeFade(fadeElement);
			if (fadeElement !== null) {
				fadeElement.remove();
			}
		}
		if (totiDisplay.isFade < 0) {
			totiDisplay.isFade = 0;
		}
	},
	/*********************/
	gridTemplate: totiGridDefaultTemplate,
	formTemplate: totiFormDefaultTemplate
};