
var formNotEditableTests = [];
var formEditableTests = [];
[
	{
		name: "Empty",
		conf: {
			inputs: []
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Basic input",
		conf: {
			inputs: [
				{
					type: "text",
					placeholder: "Input 1 - no title"
				},
				{
					type: "text",
					title: "Input 2 - with title"
				},
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Editable",
		conf: {
			inputs: [
				{
					type: "text",
					title: "No editable",
					value: "Some text"
				},
				{
					type: "text",
					title: "Editable: true",
					editable: true,
					value: "Some text"
				},
				{
					type: "text",
					title: "Editable: false",
					editable: false,
					value: "Some text"
				},
			],
			editable: true
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Input validation",
		conf: {
			inputs: [
				{
					type: "text",
					title: "Min length",
					value: "Some text",
					name: "min-length",
					minlength: 5
				},
				{
					type: "text",
					title: "Max length",
					value: "Some text",
					name: "max-length",
					maxlength: 7
				},
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Bind",
		conf: {
			inputs: [
				{
					type: "text",
					title: "A",
					name: "a"
				},
				{
					type: "text",
					title: "B",
					name: "b"
				},
			],
			bind: {
				url: '/form/bind',
				method: 'POST'
			}
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Submit",
		conf: {
			inputs: [
				{
					type: "text",
					title: "A",
					name: "a"
				},
				{
					type: "text",
					title: "B",
					name: "b"
				},
				{
					type: "submit"
				},
			],
			bind: {
				url: '/form/submit',
				method: 'POST'
			}
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	// TODO custom callbacks?
	// TODO onX callbacks

	{
		name: "Inputs: Hidden",
		conf: {
			inputs: [
				{
					type: "hidden",
					name: "hidden-input"
				},
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Inputs: submit",
		conf: {
			inputs: [
				{
					type: "submit",
					title: "Send"
				},
				{
					type: "image",
					src: 'icon.png',
					title: "Send"
				}
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Inputs: Button",
		conf: {
			inputs: [
				{
					type: "button",
					title: "Some button"
				},
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Inputs: Input list",
		conf: {
			inputs: [
				{
					type: "inputList",
					title: "Input list",
					fields: [
						{
							type: 'test-standart',
							name: "text1"
						},
						{
							type: 'test-standart',
							name: "text2"
						}
					]
				},
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Inputs: Dynamic input",
		conf: {
			inputs: [
				{
					type: "dynamicInput",
					title: "Dynamic Input",
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	},
	{
		name: "Inputs: Loaded list",
		conf: {
			inputs: [
				{
					type: "loadedList",
					title: "Loaded List",
					load: {
						url: '/inputs/loadedList',
						method: 'get',
						params: {}
					},
					field: {
						type: 'test-standart',
						name: "Input {i}"
					}
				},
			]
		},
		eVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
		nVerify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error("TODO");
		},
	}
].forEach((spec)=>{
	var nConf = Toti.utils.clone(spec.conf);
	nConf.editable = false;
	var eConf = Toti.utils.clone(spec.conf);
	eConf.editable = true;

	formEditableTests.push({
		type: "test",
		name: spec.name,
		conf: eConf,
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			spec.eVerify(expect, assert, page, callInstance, callContainer, locator);
		},
	});
	formNotEditableTests.push({
		type: "test",
		name: spec.name,
		conf: nConf,
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			spec.nVerify(expect, assert, page, callInstance, callContainer, locator);
		},
	});
})

require('../spec.js')({
	name: 'Form',
	create: (conf)=>{
		return Toti.Form(conf);
	},
	tests: [
		{
			type: "group",
			name: "Editable",
			tests: formEditableTests
		},
		{
			type: "group",
			name: "Not Editable",
			tests: formNotEditableTests
		}
	]
});