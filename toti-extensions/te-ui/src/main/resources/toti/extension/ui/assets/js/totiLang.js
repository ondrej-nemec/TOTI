/* TOTI Lang version 0.0.3 */
var totiLang = {
	variableName: "language",
	changeLanguage: function (language) {
		totiStorage.saveVariable(totiLang.variableName, language);
		document.cookie = "Language=" + language + ";Path=/; SameSite=Strict";
	},
	getLang: function() {
		var lang = totiStorage.getVariable(totiLang.variableName);
		if (lang === null) {
			return navigator.language.toLowerCase();
		}
		return lang;
	}
};
