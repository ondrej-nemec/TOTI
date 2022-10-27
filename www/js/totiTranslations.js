/* TOTI Translations version 0.2.1 */
var totiTranslations = {
	"pages": {
//		"title": /* "<t:trans message='toti.grid.paging.pages'/>", /*/ "Pages:", //*/
		"first": /* "<t:trans message='toti.grid.paging.first' />", /*/ "First", //*/
		"previous": /* "<t:trans message='toti.grid.paging.previous' />", /*/ "Previous", //*/
		"next": /* "<t:trans message='toti.grid.paging.next' />", /*/ "Next", //*/
		"last": /* "<t:trans message='toti.grid.paging.last' />" /*/ "Last", //*/
		"loadNext": /* "<t:trans message='toti.grid.paging.last' />" /*/ "Load next" //*/
	},
	"actions": {
		"select": /* "<t:trans message='toti.grid.action.select' />", /*/ "Select action", //*/
		"execute": /* "<t:trans message='toti.grid.action.execute' />", /*/ "Execute", //*/
		"noSelectedItems": /* "<t:trans message='toti.grid.action.no-selected-item' />" /*/ "No selected items" //*/
	},
	"gridMessages": {
		"noItemsFound": /* "<t:trans message='toti.grid.no-item-found' />", /*/ "No Item Found", //*/
		"loadingError": /* "<t:trans message='toti.grid.loading-error' />", /*/ "Problem with data loading", //*/
		"tableCaption": /* "<t:trans message='toti.grid.table-caption' />" /*/ "Showed {onPage}/{total} on page {pageIndex}" //*/
	},
	"buttons": {
		"actionSuccess": "Action successfully done",
		"actionFailure": "Action fails"
	},
	"formMessages": {
		"submitError": /* "<t:trans message='toti.form.saving-problem' />", /*/ "Problem with form submit", //*/
		"submitErrorForbidden": /* "<t:trans message='toti.form.saving-problem' />", /*/ "You are not allowed to submit form", //*/
		"bindError": /* "<t:trans message='toti.form.binding-problem' />" /*/ "Loading data failure", //*/
		"sendSuccess": /* "<t:trans message='toti.form.binding-problem' />" /*/ "Form saved", //*/
		"renderError": /* "<t:trans message='toti.grid.loading-error' />", /*/ "Problem form rendering" //*/
	},
	"dynamicList": {
		"add": /* "<t:trans message='toti.form.add-variant' />", /*/ "Add Variant", //*/
		"remove": /* "<t:trans message='toti.form.remove-variant' />" /*/ "Remove Variant" //*/
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
