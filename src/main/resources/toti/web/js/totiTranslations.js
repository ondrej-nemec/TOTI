/* TOTI Translations version 0.1.0 */
var totiTranslations = {
	"pages": {
		"title": "<t:trans message='common.grid.paging.pages'/>",
		"first": "<t:trans message='common.grid.paging.first' />",
		"previous": "<t:trans message='common.grid.paging.previous' />",
		"next": "<t:trans message='common.grid.paging.next' />",
		"last": "<t:trans message='common.grid.paging.last' />"
	},
	"actions": {
		"select": "<t:trans message='common.grid.action.select' />",
		"execute": "<t:trans message='common.grid.action.execute' />",
		"noSelectedItems": "<t:trans message='common.grid.action.no-selected-item' />"
	},
	"gridMessages": {
		"noItemsFound": "<t:trans message='common.grid.no-item-found' />",
		"loadingError": "<t:trans message='common.grid.loading-error' />"
	},
	"formMessages": {
		"saveError":  "<t:trans message='common.form.saving-problem', />",
		"bindError":  "<t:trans message='common.form.binding-problem' />"
	},
	"formButtons": {
		"add": "<t:trans message='common.form.add-variant' />",
		"remove": "<t:trans message='common.form.remove-variant' />"
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
