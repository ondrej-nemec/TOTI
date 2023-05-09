/* TOTI Translations version 0.3.0 */
var totiTranslations = {
	"pages": {
		"first": "<t:trans message='toti.grid.paging.first' />",
		"previous": "<t:trans message='toti.grid.paging.previous' />",
		"next": "<t:trans message='toti.grid.paging.next' />",
		"last": "<t:trans message='toti.grid.paging.last' />",
		"loadNext": "<t:trans message='toti.grid.paging.last' />"
	},
	"actions": {
		"select": "<t:trans message='toti.grid.action.select' />",
		"execute": "<t:trans message='toti.grid.action.execute' />",
		"noSelectedItems": "<t:trans message='toti.grid.action.no-selected-item' />"
	},
	"gridMessages": {
		"noItemsFound": "<t:trans message='toti.grid.no-item-found' />",
		"loadingError": "<t:trans message='toti.grid.loading-error' />",
		"tableCaption": "<t:trans message='toti.grid.table-caption' />"
	},
	"buttons": {
		"actionSuccess": "<t:trans message='toti.grid.action-success' />",
		"actionFailure": "<t:trans message='toti.grid.action-failure' />"
	},
	"formMessages": {
		"submitError": "<t:trans message='toti.form.saving-problem' />",
		"submitErrorForbidden": "<t:trans message='toti.form.saving-forbidden' />",
		"bindError": "<t:trans message='toti.form.binding-problem' />",
		"bindErrorForbidden": "<t:trans message='toti.form.binding-forbidden' />",
		"sendSuccess": "<t:trans message='toti.form.submit-success' />",
		"renderError": "<t:trans message='toti.form.render-error' />",
		"required": "<t:trans message='toti.form.required' />"
	},
	"dynamicList": {
		"add": "<t:trans message='toti.form.add-variant' />",
		"remove": "<t:trans message='toti.form.remove-variant' />"
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
