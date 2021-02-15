// depends on totiStorage - storage.js
var totiLang = {
	variableName: "language",
	changeLanguage: function (language) {
		totiStorage.saveVariable(totiLang.variableName, language);
		document.cookie = "Language=" + language + ";Path=/";
	},
	getLang: function() {
		var lang = totiStorage.getVariable(totiLang.variableName);
		if (lang === null) {
			return navigator.language.toLowerCase().replace("-", "_");
		}
		return lang;
	},
	getLangHeader: function() {
		var lang = totiStorage.getVariable(totiLang.variableName);
		if (lang === null) {
			return {};
		}
		return {
			"Accept-Language": lang
		};
	}
};