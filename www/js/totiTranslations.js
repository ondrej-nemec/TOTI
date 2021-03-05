/* TOTI Translations version 0.1.0 */
var totiTranslations = {
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
	"formButtons": {
		"add": /* "<t:trans message='common.form.add-variant' />" /*/ "Add Variant" //*/,
		"remove": /* "<t:trans message='common.form.remove-variant' />" /*/ "Remove Variant" //*/
	},
	/* TODO use translations with JSON.parse() ??? */
	"timestamp": {
		dateString: {
			"date": {"year": "numeric", "month": "long", "day": "numeric"},
			"datetime-local": {
				"year": "numeric", "month": "long", "day": "numeric",
				"hour": "numeric", "minute": "numeric", "second": "numeric"
			},
			"month": {"year": "numeric", "month": "long"},
		},
		timeString: {
			"time": {"hour": "numeric", "minute": "numeric", "second": "numeric"},
		},
		"week": {"year": "numeric", "week": "numeric"},
	}
};
