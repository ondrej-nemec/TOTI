/* TOTI Lang version 0.0.2 */
var totiLang = {
	variableName: "language",
	changeLanguage: function (language) {
		totiStorage.saveVariable(totiLang.variableName, language);
		document.cookie = "Language=" + language + ";Path=/";
	},
	getLang: function() {
		var lang = totiStorage.getVariable(totiLang.variableName);
		/* TODO problem in firefox - lang is only "cs"*/
		if (lang === null) {
			return navigator.language.toLowerCase();
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
