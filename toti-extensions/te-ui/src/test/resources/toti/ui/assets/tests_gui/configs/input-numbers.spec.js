var numberInputs = [];
[
	{
		type: 'number',
		useOptional: false,
		verify: {
			base: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('INPUT', await callContainer('tagName'));
				await expect(locator).toBeEnabled();
				await expect(locator).toBeVisible();
				await expect(locator).toBeAttached();
			},
			disabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('INPUT', await callContainer('tagName'));
				await expect(locator).toBeDisabled();
				await expect(locator).toBeVisible();
				await expect(locator).toBeAttached();
			},
			value: async (expect, assert, page, callInstance, callContainer, locator, value, min = null, max = null)=>{
				if (value === null || value.toString().startsWith('_')) {
					await expect(locator).toHaveValue('');
				} else {
					await expect(locator).toHaveValue(value + '');
				}
				if (min !== null) {
					await expect(locator).toHaveAttribute('min', min + '');
				}
				if (max !== null) {
					await expect(locator).toHaveAttribute('max', max + '');
				}
			},
			fill: async(locator, value)=>{
				await locator.fill(value + '');
				await locator.blur(); // lost focus on input to fire onchange event
			}
		}
	},
	{
		type: 'range',
		useOptional: true,
		verify: {
			base: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				var input = await locator.locator('input');
				await expect(input).toBeEnabled();
				await expect(input).toBeVisible();
				await expect(input).toBeAttached();

				var maxPanel = locator.locator('.toti-range-max');
				await expect(maxPanel).toBeVisible();
				await expect(maxPanel).toBeAttached();

				var minPanel = locator.locator('.toti-range-min');
				await expect(minPanel).toBeVisible();
				await expect(minPanel).toBeAttached();

				var currentPanel = locator.locator('.toti-range-current');
				await expect(currentPanel).toBeVisible();
				await expect(currentPanel).toBeAttached();
			},
			disabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				var input = await locator.locator('input');
				await expect(input).toBeDisabled();
				await expect(input).toBeVisible();
				await expect(input).toBeAttached();

				var maxPanel = locator.locator('.toti-range-max');
				await expect(maxPanel).toBeVisible();
				await expect(maxPanel).toBeAttached();
				await expect(maxPanel).toHaveText('100');

				var minPanel = locator.locator('.toti-range-min');
				await expect(minPanel).toBeVisible();
				await expect(minPanel).toBeAttached();
				await expect(minPanel).toHaveText('0');
				
				var currentPanel = locator.locator('.toti-range-current');
				await expect(currentPanel).toBeVisible();
				await expect(currentPanel).toBeAttached();
				await expect(currentPanel).toHaveText('50');
			},
			value: async (expect, assert, page, callInstance, callContainer, locator, value, min = null, max = null)=>{
				if (max === null) {
					max = 100; // range default value
				}
				if (min === null) {
					min = 0; // range default value
				}

				if (value === null) {
					value = Math.round((max - min)/2) + min; // range default value
				} else if (value.toString().startsWith('_')) {
					value = value.slice(1);
				}
				var inputValue = value;

				if (min > inputValue) {
					inputValue = min; // input value cannot be under min
				} else if (max < inputValue) {
					inputValue = max; // input value cannot be higher max
				}
				inputValue = Math.round(inputValue);

				var input = await locator.locator('input');
				await expect(input).toHaveValue(inputValue + '');


				var maxPanel = await locator.locator('.toti-range-max');
				await expect(maxPanel).toBeVisible();
				await expect(maxPanel).toBeAttached();
				await expect(input).toHaveAttribute('max', max + '');
				await expect(maxPanel).toHaveText(max + '');
				
				var minPanel = await locator.locator('.toti-range-min');
				await expect(minPanel).toBeVisible();
				await expect(minPanel).toBeAttached();
				await expect(input).toHaveAttribute('min', min + '');
				await expect(minPanel).toHaveText(min + '');
				
				var currentPanel = await locator.locator('.toti-range-current');
				await expect(currentPanel).toBeAttached();
				await expect(currentPanel).toHaveText(value + '');
				await expect(currentPanel).toBeVisible();
			},
			fill: async(locator, value)=>{
				var input = await locator.locator('input');
				await input.fill(value + '');
				await input.blur(); // lost focus on input to fire onchange event
			}
		}
	}
].forEach((spec)=>{
	var tests = [
		{
			type: 'test',
			name: 'Basic',
			conf: {
				type: spec.type,
				name: 'testInput'
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await spec.verify.base(expect, assert, page, callInstance, callContainer, locator);
				},
				setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isNull(await callInstance('getValue'));

					await callInstance('setValue', 6);
					assert.areSame(6, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 6);

					await callInstance('setValue', 0.123);
					assert.areSame(0.123, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 0.123);
				},
				fillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isNull(await callInstance('getValue'));

					await spec.verify.fill(locator, 42);
					assert.areSame(42, await callInstance('getValue'));
				}
			}
		},
		{
			type: 'test',
			name: 'Min',
			conf: {
				type: spec.type,
				name: 'testInput',
				min: 5
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isTrue(await callInstance('isValid'));
					assert.isNull(await callInstance('getValue'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, '_53', 5, null);

					await callInstance('setValue', 4);
					assert.areSame(4, await callInstance('getValue'));
					assert.isFalse(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 4, 5, null);

					await callInstance('setValue', 5);
					assert.areSame(5, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 5, 5, null);

					await callInstance('setValue', 6);
					assert.areSame(6, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 6, 5, null);
				},
				set: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMin', 10);

					assert.isTrue(await callInstance('isValid'));
					assert.isNull(await callInstance('getValue'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, '_53', 10, null);

					await callInstance('setValue', 9);
					assert.areSame(9, await callInstance('getValue'));
					assert.isFalse(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 9, 10, null);

					await callInstance('setValue', 10);
					assert.areSame(10, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 10, 10, null);

					await callInstance('setValue', 11);
					assert.areSame(11, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 11, 10, null);
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMin', null);

					assert.isTrue(await callInstance('isValid'));
					assert.isNull(await callInstance('getValue'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, '_53', null, null);

					await callInstance('setValue', 4);
					assert.areSame(4, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 4, null, null);

					await callInstance('setValue', 5);
					assert.areSame(5, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 5, null, null);

					await callInstance('setValue', 6);
					assert.areSame(6, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 6, null, null);
				}
			}
		},
		{
			type: 'test',
			name: 'Max',
			conf: {
				type: spec.type,
				name: 'testInput',
				max: 5
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, '_3', null, 5);

					await callInstance('setValue', 6);
					assert.areSame(6, await callInstance('getValue'));
					assert.isFalse(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 6, null, 5);

					await callInstance('setValue', 5);
					assert.areSame(5, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 5, null, 5);

					await callInstance('setValue', 4);
					assert.areSame(4, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 4, null, 5);
				},
				set: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMax', 10);

					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, '_3', null, 10);

					await callInstance('setValue', 11);
					assert.areSame(11, await callInstance('getValue'));
					assert.isFalse(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 11, null, 10);

					await callInstance('setValue', 10);
					assert.areSame(10, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 10, null, 10);

					await callInstance('setValue', 9);
					assert.areSame(9, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 9, null, 10);
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMax', null);

					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, '_3', null, null);

					await callInstance('setValue', 6);
					assert.areSame(6, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 6, null, null);

					await callInstance('setValue', 5);
					assert.areSame(5, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 5, null, null);

					await callInstance('setValue', 4);
					assert.areSame(4, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					await spec.verify.value(expect, assert, page, callInstance, callContainer, locator, 4, null, null);
				}
			}
		},
		{
			type: 'test',
			name: 'Step full, positive',
			conf: {
				type: spec.type,
				name: 'testInput',
				step: 5
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setValue', 6);
					assert.isFalse(await callInstance('isValid'));

					await callInstance('setValue', 0.123);
					assert.isFalse(await callInstance('isValid'));

					await callInstance('setValue', 35);
					assert.isTrue(await callInstance('isValid'));
				},
				set: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setStep', 2);

					await callInstance('setValue', 6);
					assert.isTrue(await callInstance('isValid'));

					await callInstance('setValue', 0.123);
					assert.isFalse(await callInstance('isValid'));

					await callInstance('setValue', 35);
					assert.isFalse(await callInstance('isValid'));
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setStep', null);

					await callInstance('setValue', 6);
					assert.isTrue(await callInstance('isValid'));

					await callInstance('setValue', 0.123);
					assert.isTrue(await callInstance('isValid'));

					await callInstance('setValue', 35);
					assert.isTrue(await callInstance('isValid'));
				}
			}
		},
		{
			type: 'test',
			name: 'Step Not full, positive',
			conf: {
				type: spec.type,
				name: 'testInput',
				step: 1.01
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await callInstance('setValue', 0.001);
				assert.isFalse(await callInstance('isValid'));

				await callInstance('setValue', 1);
				assert.isFalse(await callInstance('isValid'));

				await callInstance('setValue', 2.02);
				assert.isTrue(await callInstance('isValid'));
			}
		},
		{
			type: 'test',
			name: 'Step Not full, positive, less 0',
			conf: {
				type: spec.type,
				name: 'testInput',
				step: 0.01
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await callInstance('setValue', 0.001);
				assert.isFalse(await callInstance('isValid'));

				await callInstance('setValue', 0.123);
				assert.isFalse(await callInstance('isValid'));

				await callInstance('setValue', 0.8);
				assert.isTrue(await callInstance('isValid'));

				await callInstance('setValue', 35);
				assert.isTrue(await callInstance('isValid'));
			}
		},
		{
			type: 'test',
			name: 'Step full, negative',
			conf: {
				type: spec.type,
				name: 'testInput',
				step: 5
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await callInstance('setValue', 6);
				assert.isFalse(await callInstance('isValid'));

				await callInstance('setValue', 0.123);
				assert.isFalse(await callInstance('isValid'));

				await callInstance('setValue', 35);
				assert.isTrue(await callInstance('isValid'));
			}
		},
		{
			type: 'test',
			name: 'Step with wrong def value',
			conf: {
				type: spec.type,
				name: 'testInput',
				step: 5,
				value: 6
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isFalse(await callInstance('isValid'));
			}
		},
		{
			type: 'test',
			name: 'Disabled',
			conf: {
				type: spec.type,
				name: 'testInput',
				disabled: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await spec.verify.disabled(expect, assert, page, callInstance, callContainer, locator);
			}
		},
		{
			type: 'test',
			name: 'Not editable',
			conf: {
				type: spec.type,
				name: 'testInput',
				editable: false,
				value: 42
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				expect(locator).toBeAttached();
				expect(locator).toBeVisible();
				expect(locator).toHaveText('42');
			},
		}
	];
	if (spec.useOptional) {
		tests.push({
			type: 'test',
			name: 'Optional',
			conf: {
				type: spec.type,
				name: 'testInput',
				optional: true
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('DIV', await callContainer('tagName'));

					var checkbox = await locator.locator('.optional-input-switch');
					await expect(checkbox).toBeEnabled();
					await expect(checkbox).toBeVisible();
					await expect(checkbox).toBeAttached();
					assert.isFalse(await checkbox.isChecked());

					var container = await locator.locator('.optional-input-field');
					var input = await container.locator('input');
					await expect(input).toHaveAttribute('type', spec.type);
					await expect(input).toBeDisabled();
					await expect(input).toBeVisible();
					await expect(input).toBeAttached();
				},
				setRules: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMin', 5);
					await callInstance('setMax', 35);
					await callInstance('setStep', 5);

					var container = await locator.locator('.optional-input-field');
					await spec.verify.value(expect, assert, page, callInstance, callContainer, container, '_35', 5, 35);
				}
			},
		});
	}
	numberInputs.push({
		name: 'Input: ' + spec.type,
		type: 'group',
		tests: tests
	});
});

require('../spec.js')({
	name: 'Inputs: Number inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: numberInputs
});