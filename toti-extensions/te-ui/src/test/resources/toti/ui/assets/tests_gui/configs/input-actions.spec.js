var actionInputTests = [];
[
	'button', 'submit', 'reset', 'image'
].forEach((input)=>{
	actionInputTests.push({
		name: 'Input ' + input,
		type: 'group',
		tests: [
			{
				name: 'TODO',
				type: 'test',
				conf: {
					type: input
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			}
		]
	});
});

require('../spec.js')({
	name: 'Inputs: Actions inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: actionInputTests
});