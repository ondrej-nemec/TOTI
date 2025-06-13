var actionInputTests = [];
[
	{
		type: 'submit',
		src: false
	},
	{
		type: 'image',
		src: true
	}
].forEach((input)=>{
	function conf(base) {
		if (input.src) {
			base.src = 'icon.png';
		}
		return base;
	}
	actionInputTests.push({
		name: 'Input ' + input.type,
		type: 'group',
		tests: [
			{
				name: 'Basic',
				type: 'test',
				conf: conf({
					type: input.type
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Not editable',
				type: 'test',
				conf: conf({
					type: input.type,
					editable: false
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Disabled',
				type: 'test',
				conf: conf({
					type: input.type,
					disabled: true
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Confirmation',
				type: 'test',
				conf: conf({
					type: input.type,
					confirmation: 'Confirmation message'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Title',
				type: 'test',
				conf: conf({
					type: input.type,
					title: 'Send to clouds'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Value',
				type: 'test',
				conf: conf({
					type: input.type,
					value: 'OK'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			// submit policy - no tests
			{
				name: 'Submit Sync TODO',
				type: 'test',
				conf: conf({
					type: input.type,
					submitConfiguration: {
						url: '/inputs/submit'
					}
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Submit Async Success',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit'
					},
					onSuccess: ['onSubmitSuccess']
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Submit Async Success Redirect',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit'
					},
					onSuccess: ['onSubmitSuccess'],
					redirect: 'index.html#{b}'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Submit Async Failure',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit-not-existing'
					},
					onFailure: ['onSubmitFailure']
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Submit Async Failure Redirect',
				type: 'test',
				conf: conf({
					type: input.type,
					async: true,
					submitConfiguration: {
						url: '/inputs/submit-not-existing'
					},
					onFailure: ['onSubmitFailure'],
					redirect: 'index.html'
				}),
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			}
		]
	});
});

var buttonTests = [];
[
	{
		name: 'Basic',
		conf: {
			type: 'button'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		}
	},
	{
		name: 'Title',
		conf: {
			type: 'button',
			title: 'Magic button'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		}
	},
	// not editable is not displayed
	/*{
		name: 'Not editable',
		conf: {
			type: 'button',
			editable: false
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		}
	},*/
	{
		name: 'Disabled',
		conf: {
			type: 'button',
			title: 'Magic button',
			disabled: true
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		}
	},
	{
		name: 'Confirmation',
		conf: {
			type: 'button',
			title: 'Magic button',
			confirmation: 'Confirmation message'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		}
	},
	{
		name: 'Icon',
		conf: {
			type: 'button',
			icon: 'bi bi-pen'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		}
	},
	{
		name: 'Tooltip',
		conf: {
			type: 'button',
			title: 'Magic button',
			tooltip: 'Magic button Tooltip'
		},
		verifyAsync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		},
		verifySync: async (expect, assert, page, callInstance, callContainer, locator)=>{
			throw new Error('Not implemented');
		}
	}
].forEach((test)=>{
	buttonTests.push({
		name: test.name + " Async",
		type: 'test',
		conf: test.conf,
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			test.verifyAsync(expect, assert, page, callInstance, callContainer, locator);
		}
	});
	var conf = JSON.parse(JSON.stringify(test.conf));
	conf.link = 'test.html';
	buttonTests.push({
		name: test.name + " Sync",
		type: 'test',
		conf: conf,
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			test.verifySync(expect, assert, page, callInstance, callContainer, locator);
		}
	});
});
buttonTests.push({
	name: 'Submit Async Success',
	type: 'test',
	conf: {
		type: 'button',
		title: 'Magic button',
		requestConfiguration: {
			url: '/inputs/button'
		},
		onSuccess: ['onButtonSuccess']
	},
	verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
		throw new Error('Not implemented');
	}
});
buttonTests.push({
	name: 'Submit Async Failure',
	type: 'test',
	conf: {
		type: 'button',
		title: 'Magic button',
		requestConfiguration: {
			url: '/inputs/button-not-existing'
		},
		onFailure: ['onButtonFailure']
	},
	verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
		throw new Error('Not implemented');
	}
});
actionInputTests.push({
	name: 'Input button',
	type: 'group',
	tests: buttonTests
});


actionInputTests.push({
	name: 'Input reset',
	type: 'group',
	tests: [
		{
			name: 'Basic',
			type: 'test',
			conf: {
				type: 'reset'
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				throw new Error('Not implemented');
			}
		},
		{
			name: 'Not editable',
			type: 'test',
			conf: {
				type: 'reset',
				editable: false
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				throw new Error('Not implemented');
			}
		},
		{
			name: 'Disabled',
			type: 'test',
			conf: {
				type: 'reset',
				disabled: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				throw new Error('Not implemented');
			}
		}
	]
});

require('../spec.js')({
	name: 'Inputs: Actions inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: actionInputTests
});