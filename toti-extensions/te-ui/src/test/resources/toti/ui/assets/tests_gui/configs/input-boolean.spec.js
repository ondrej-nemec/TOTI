var booleanInputs = [];
[
	{
		type: 'checkbox'
	},
	{
		type: 'radio'
	}
].forEach((spec)=>{
	var index = 0;
	var tests = [
		{
			type: 'test',
			name: 'Basic',
			conf: {
				type: spec.type,
				name: 'testInput_' + index++
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('INPUT', await callContainer('tagName'));
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();
				},
				setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setValue', true);
					assert.isTrue(await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toBeChecked();

					await callInstance('setValue', false);
					assert.isFalse(await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					// TODO will not be working with radio
					await expect(locator).not.toBeChecked();

					await callInstance('setValue', null);
					assert.isFalse(await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await expect(locator).not.toBeChecked();
				},
				fillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setValue', false);
					
					await locator.check();
					assert.isTrue(await callInstance('getValue'));
				}
			}
		},
		{
			type: 'test',
			name: 'Required',
			conf: {
				type: spec.type,
				name: 'testInput_' + index++,
				required: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('INPUT', await callContainer('tagName'));
				await expect(locator).toBeEnabled();
				await expect(locator).toBeVisible();
				await expect(locator).toBeAttached();

				assert.isFalse(await callInstance('getValue'));
				assert.isFalse(await callInstance('isValid'));

				await callInstance('setValue', true);
				assert.isTrue(await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));
			}
		},
		{
			type: 'test',
			name: 'Disabled',
			conf: {
				type: spec.type,
				name: 'testInput_' + index++,
				disabled: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('INPUT', await callContainer('tagName'));
				await expect(locator).toBeDisabled();
				await expect(locator).toBeVisible();
				await expect(locator).toBeAttached();
			}
		},
		{
			type: 'test',
			name: 'Not editable, no value',
			conf: {
				type: spec.type,
				name: 'testInput_' + index++,
				editable: false
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				expect(locator).toBeAttached();
				expect(locator).toBeVisible();
				expect(locator).toHaveText('false');
			},
		},
		{
			type: 'test',
			name: 'Not editable with value',
			conf: {
				type: spec.type,
				name: 'testInput_' + index++,
				editable: false,
				value: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				expect(locator).toBeAttached();
				expect(locator).toBeVisible();
				expect(locator).toHaveText('true');
			},
		},
		{
			type: 'test',
			name: 'Not editable options',
			conf: {
				type: spec.type,
				name: 'testInput_' + index++,
				editable: false,
				value: true,
				options: {
					true: 'Yes',
					false: 'No'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				expect(locator).toBeAttached();
				expect(locator).toBeVisible();
				expect(locator).toHaveText('Yes');
			},
		}
	];
	booleanInputs.push({
		name: 'Input: ' + spec.type,
		type: 'group',
		tests: tests
	});
});

require('../spec.js')({
	name: 'Inputs: Boolean inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: booleanInputs
});