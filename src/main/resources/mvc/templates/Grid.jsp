<div id="${gridName}"></div>
	
<script>
var totiControlLang = totiControlLang || {
	"pages": {
		"title": "Pages:",
		"first": "First",
		"previous": "Previous",
		"next": "Next",
		"last": "Last"
	},
	"messages": {
		"noItemsFound": "No items founded",
		"loadingError": "Loading error",
		"noItemsSelected": "No items selected"
	},
	"execute": "Execute..."
};
var ${gridName}_controlConfig = {
	"Name": "${gridName}",
	"Unique": "${rowUnique}",
	"DataUrl": "${dataUrl}", // "grid-content.json",
	"DataMethod": "${dataHttpMethod}",
	"pagesButton": ${pagesButtonCount},
	"pageSizes": ${pageSizes},
	"pageSizeDefault": ${defaultPageSize},
	"Columns": [
		<t:foreach item="mvc.control.Column column" collection="${fields}">
		
		</t:foreach>
		{
			"name": "",
			"title": "Actions",
			"type": "actions",
			"sorting": false
		},
		{
			"name": "id",
			"sorting": true,
			"type": "value",
			"size": 4,
			"max": 1,
			"min": 5
		},{
			"name": "name",
			"title": "Name",
			"filter": "text", // missing means no filter; text | select | datetime ( | number | checkbox ) ??
			"sorting": true,
			"type": "value"
		},{
			"name": "age",
			"title": "Age",
			"filter": "number",
			"sorting": true,
			"type": "value"
		},{
			"name": "datetime",
			"title": "Datetime",
			"filter": "number",
			"filter": "datetime",
			"sorting": true,
			"type": "value"
		},{
			"name": "maried",
			"title": "Is Married",
			"sorting": true,
			"type": "value",
			"filter": "select",
			"options": [
				{
					"value": "",
					"text": '----'
				},
				{
					"value": "0",
					"text": "NO"
				},
				{
					"value": "1",
					"text": "YES"
				}
			],
			"renderer": function(value) {
				if (value === 'false') {
					return "NO";
				} else if (value === 'true') {
					return "YES";
				}
			}
		},
		{
			"name": "",
			"title": "Buttons",
			"sorting": false,
			"type": "buttons",
			"buttons": [
				{
					"class": "",
					"title": "Button",
					"href": function(row) {
						return ""
					},
					"method": "DELETE",// only if ajax == true
					"params": function(row) {
						// only if ajax == true
					},
					"onSuccess": function(row) {
						// only if ajax == true
					},
					"onFailure": function(row) {
						// only if ajax == true
					},
					"confirmation": function(row) {
						console.log("confirmation", row);
						return true;
					},
					"ajax": true
				},
				{
					"class": "",
					"title": "Button2",
					"href": function(row) {
						return "/1.png";
					},
					"confirmation": function(row) {
						console.log("confirmation", row);
						return true;
					},
					"ajax": false
				}
			]
		}
	],
	"Actions": [
		{"title": "Action 1", "link": "/base/grid/action"},
		{"title": "Action 2", "link": "/base/grid/action"},
		{"title": "Not existing Action", "link": "not-existing.json"},
		{"title": "Synchro Action", "link": "/grid-old/jsgrid-1.5.3.zip", "ajax": false}
	]
};
totiControl.init('#${gridName}', ${gridName}_controlConfig, "grid");
</script>
