
require('/home/coder/project/playwright.spec.js')({
	name: 'Functions',
	create: (conf)=>{
		return null;
	},
	tests: [
		{
			name: "Dialog",
			type: "group",
			tests: [
				{
					name: "Prompt",
					type: "function",
					function: ()=>{
						var val = Toti.dialog.prompt("Prompt message", "Def value");
						val.then((x)=>{
							console.log("Prompt", x);
						});
						return val;
						return Toti.dialog.prompt("Prompt message", "Def value");
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{}
				},
				{
					name: "Confirm",
					type: "function",
					function: ()=>{
						return Toti.dialog.confirm("Confirm message");
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{}
				},
				{
					name: "Alert",
					type: "function",
					function: ()=>{
						return Toti.dialog.alert("Alert message");
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{}
				}
			]
		},
		{
			name: "Animation",
			type: "group",
			tests: [
				{
					name: "Input loading",
					type: "group",
					tests: [
						{
							name: "Loading: Start stop",
							type: "function",
							element: ()=>{
								var input = document.createElement('input');
								var container = document.createElement('div');
								container.appendChild(input);
								return container;
							},
							function: (element)=>{
								var loading = new LoadingAnimation(element);
								return totiUtils.sleep(1)
								.then(()=>{
									loading.start();
								}).then(()=>{
									return totiUtils.sleep(5000);
								})
								.then(()=>{
									loading.stop();
								});
							},
							verify: async (expect, assert, page, callInstance, callContainer, locator)=>{}
						},
						{
							name: "Loading: Start Failure",
							type: "function",
							element: ()=>{
								var input = document.createElement('input');
								var container = document.createElement('div');
								container.appendChild(input);
								return container;
							},
							function: (element)=>{
								var loading = new LoadingAnimation(element);
								return totiUtils.sleep(1)
								.then(()=>{
									loading.start();
								}).then(()=>{
									return totiUtils.sleep(5000)
								})
								.then(()=>{
									loading.failure();
								});
							},
							verify: async (expect, assert, page, callInstance, callContainer, locator)=>{}
						},
						{
							name: "Loading: Start stop remove",
							type: "function",
							element: ()=>{
								var input = document.createElement('input');
								var container = document.createElement('div');
								container.appendChild(input);
								return container;
							},
							function: (element)=>{
								var loading = new LoadingAnimation(element);
								return totiUtils.sleep(1)
								.then(()=>{
									loading.start();
								}).then(()=>{
									return totiUtils.sleep(5000);
								})
								.then(()=>{
									loading.stop();
								}).then(()=>{
									return totiUtils.sleep(5000);
								})
								.then(()=>{
									loading.remove();
								});
							},
							verify: async (expect, assert, page, callInstance, callContainer, locator)=>{}
						},
						{
							name: "Loading: Run, failure, restart remove",
							type: "function",
							element: ()=>{
								var input = document.createElement('input');
								var container = document.createElement('div');
								container.appendChild(input);
								return container;
							},
							function: (element)=>{
								var loading = new LoadingAnimation(element);
								return totiUtils.sleep(1)
								.then(()=>{
									loading.start();
								}).then(()=>{
									return totiUtils.sleep(5000);
								}).then(()=>{
									loading.failure();
								}).then(()=>{
									return totiUtils.sleep(5000);
								}).then(()=>{
									loading.start();
								}).then(()=>{
									return totiUtils.sleep(5000);
								}).then(()=>{
									loading.remove();
								});
							},
							verify: async (expect, assert, page, callInstance, callContainer, locator)=>{}
						}
					]
				}
			]
		}
	]
});