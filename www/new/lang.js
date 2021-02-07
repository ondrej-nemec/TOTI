// depends on totiStorage - storage.js
var totiLang = {
	"pages": {
		"title": /* "<t:trans message='common.grid.paging.pages'/>", /*/ "Pages:", //*/
		"first": /* "<t:trans message='common.grid.paging.first' />", /*/ "First", //*/
		"previous": /* "<t:trans message='common.grid.paging.previous' />", /*/ "Previous", //*/
		"next": /* "<t:trans message='common.grid.paging.next' />", /*/ "Next", //*/
		"last": /* "<t:trans message='common.grid.paging.last' />" /*/ "Last" //*/
	},
	"actions": {
		"select": /* "<t:trans message='common.grid.action.select' />", /*/ "Select action", //*/
		"execute": /* "<t:trans message='common.grid.action.execute' />", /*/ "Execute", //*/
		"noSelectedItems": /* "<t:trans message='common.grid.action.no-selected-item' />" /*/ "No selected items" //*/
	},
	"gridMessages": {
		"noItemsFound": /* "<t:trans message='common.grid.no-item-found' />", /*/ "No Item Found", //*/
		"loadingError": /* "<t:trans message='common.grid.loading-error' />" /*/ "Problem with data loading" //*/
	},
	"formMessages": {
		"saveError": /* "<t:trans message='common.form.saving-problem' />", /*/ "Problem with form saving", //*/
		"bindError": /* "<t:trans message='common.form.binding-problem' />" /*/ "Loading data failure" //*/
	},
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