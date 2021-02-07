// depends on totiStorage storage.js
var totiDisplay = {
	prompt: function(message, defValue = "") {
		return prompt(message, defValue);
	},
	confirm: function(message, params = {}) {
		return confirm(totiControl.utils.parametrizedString(message, params));
	},
	alert: function(message) {
		alert(message);
	},
	flash: function(severity, message) {
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

		if (totiSettings.flashTimeout > 0) {
			setTimeout(function() {
				div.style.display = "none";
			}, totiSettings.flashTimeout);
		}
		document.getElementById('flash').appendChild(div);
		console.log("Flash " + severity + ":");
		console.log(message);
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
	}
};
document.addEventListener("DOMContentLoaded", function(event) { 
	totiDisplay.printStoredFlash();
});