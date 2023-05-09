/* TOTI Translations version 0.3.0 */
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
		"actionSuccess": /* "<t:trans message='toti.grid.action-success' />", /*/ "Action successfully done", //*/
		"actionFailure": /* "<t:trans message='toti.grid.action-failure' />" /*/ "Action fails" //*/
	},
	"formMessages": {
		"submitError": /* "<t:trans message='toti.form.saving-problem' />", /*/ "Problem with form submit", //*/
		"submitErrorForbidden": /* "<t:trans message='toti.form.saving-problem' />", /*/ "You are not allowed to submit form", //*/
		"bindError": /* "<t:trans message='toti.form.binding-problem' />" /*/ "Loading data failure", //*/
		"bindErrorForbidden": /* "<t:trans message='toti.form.binding-problem' />" /*/ "You are not allowed to load data", //*/
		"sendSuccess": /* "<t:trans message='toti.form.binding-problem' />" /*/ "Form saved", //*/
		"renderError": /* "<t:trans message='toti.grid.loading-error' />", /*/ "Problem form rendering", //*/
		"required": /* "<t:trans message='toti.form.required' />" /*/ "Item is required" //*/
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
