/***** GENERAL	 *******/
var textInputs = [];
[
	{
		type: 'text'
	},
	{
		type: 'email'
	},
	{
		type: 'search'
	},
	{
		type: 'url'
	},
	{
		type: 'tel'
	}
].forEach((spec)=>{
	textInputs.push({
		name: 'Input: ' + spec.type,
		type: 'group',
		tests: [
			{
				type: 'test',
				name: 'Basic',
				conf: {
					type: spec.type,
					name: 'testInput'
				},
				verify: {
					base: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('SPAN', await callContainer('tagName'));

						var datalist = await locator.locator('datalist');
						await expect(datalist).toBeAttached();

						var input = await locator.locator('input');
						await expect(input).toBeEnabled();
						await expect(input).toBeVisible();
						await expect(input).toBeAttached();
					},
					setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
						var input = await locator.locator('input');

						await expect(input).toHaveValue('');
						assert.isNull(await callInstance('getValue'));

						await callInstance('setValue', 'some text');
						await expect(input).toHaveValue('some text');

						await callInstance('setValue', 'another text');
						await expect(input).toHaveValue('another text');

						await callInstance('clear');
						await expect(input).toHaveValue('');

					},
					fillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.isNull(await callInstance('getValue'));
						var input = await locator.locator('input');

						await input.fill('something');
						await input.blur(); // lost focus on input to fire onchange event
						assert.areSame('something', await callInstance('getValue'));
					}
				}
			},
			{
				type: 'test',
				name: 'Min length',
				conf: {
					type: spec.type,
					name: 'testInput',
					minlength: 5
				},
				verify: {
					base: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc');
						assert.isFalse(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isTrue(await callInstance('isValid'));
					},
					set: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await callInstance('setMinlength', 10);

						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isFalse(await callInstance('isValid'));

						await callInstance('setValue', 'abc123987654');
						assert.isTrue(await callInstance('isValid'));
					},
					clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await callInstance('setMinlength', null);

						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123987654');
						assert.isTrue(await callInstance('isValid'));
					}
				}
			},
			{
				type: 'test',
				name: 'Max length',
				conf: {
					type: spec.type,
					name: 'testInput',
					maxlength: 5
				},
				verify: {
					base: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isFalse(await callInstance('isValid'));
						
						await callInstance('setValue', 'abc');
						assert.isTrue(await callInstance('isValid'));
					},
					set: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await callInstance('setMaxlength', 3);

						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isFalse(await callInstance('isValid'));

						await callInstance('setValue', 'ab');
						assert.isTrue(await callInstance('isValid'));
					},
					clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await callInstance('setMaxlength', null);

						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123987654');
						assert.isTrue(await callInstance('isValid'));
					}
				}
			},
			{
				type: 'test',
				name: 'Pattern',
				conf: {
					type: spec.type,
					name: 'testInput',
					pattern: "^[a-zA-Z]{2}[0-9]*$"
				},
				verify: {
					base: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isFalse(await callInstance('isValid'));
						
						await callInstance('setValue', 'ab123');
						assert.isTrue(await callInstance('isValid'));
					},
					set: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await callInstance('setPattern', "^[0-9]*$");

						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isFalse(await callInstance('isValid'));
						
						await callInstance('setValue', '123');
						assert.isTrue(await callInstance('isValid'));
					},
					clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await callInstance('setPattern', null);

						assert.isTrue(await callInstance('isValid'));

						await callInstance('setValue', 'abc123');
						assert.isTrue(await callInstance('isValid'));
						
						await callInstance('setValue', 'ab123');
						assert.isTrue(await callInstance('isValid'));
					}
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
					var datalist = await locator.locator('datalist');
					await expect(datalist).toBeAttached();

					var input = await locator.locator('input');
					await expect(input).toBeDisabled();
					await expect(input).toBeVisible();
					await expect(input).toBeAttached();
				}
			},
			{
				type: 'test',
				name: 'Not editable',
				conf: {
					type: spec.type,
					name: 'testInput',
					editable: false,
					value: 'Something'
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('SPAN', await callContainer('tagName'));
					expect(locator).toBeAttached();
					expect(locator).toBeVisible();
					expect(locator).toHaveText('Something');
				}
			},
			{
				type: 'test',
				name: 'Options',
				conf: {
					type: spec.type,
					name: 'testInput',
					options: [
						'Option 1',
						'Option 2',
						'Option 3',
						'Another Row',
						'Something else'
					]
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var input = await locator.locator('input');
					await expect(input).toBeEnabled();
					await expect(input).toBeVisible();
					await expect(input).toBeAttached();

					var datalist = await locator.locator('datalist');
					await expect(datalist).toBeAttached();

					var options = await datalist.locator('*');
					await expect(options).toHaveCount(5);

					async function checkOption(index, value) {
						var opt = await options.nth(index);
						//await expect(opt).toHaveValue(value);
						await expect(opt).toHaveAttribute('value', value);
						await expect(opt).toHaveText('');
					}
					await checkOption(0, 'Option 1');
					await checkOption(1, 'Option 2');
					await checkOption(2, 'Option 3');
					await checkOption(3, 'Another Row');
					await checkOption(4, 'Something else');
				}
			},
			{
				type: 'test',
				name: 'Load',
				conf: {
					type: spec.type,
					name: 'testInput',
					load: {
						url: '/inputs/text',
						method: 'get',
						params: {}
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var input = await locator.locator('input');
					await expect(input).toBeEnabled();
					await expect(input).toBeVisible();
					await expect(input).toBeAttached();

					var datalist = await locator.locator('datalist');
					await expect(datalist).toBeAttached();

					var options = await datalist.locator('*');
					await expect(options).toHaveCount(0);

					// sleep
					await new Promise(resolve => setTimeout(resolve, 6000));
					await expect(options).toHaveCount(3);

					async function checkOption(index, value) {
						var opt = await options.nth(index);
						//await expect(opt).toHaveValue(value);
						await expect(opt).toHaveAttribute('value', value);
						await expect(opt).toHaveText('');
					}
					await checkOption(0, 'Loaded Option');
					await checkOption(1, 'Some data');
					await checkOption(2, 'Else');
				}
			},
			{
				type: 'test',
				name: 'Options and Load',
				conf: {
					type: spec.type,
					name: 'testInput',
					options: [
						'Option 1',
						'Option 2',
						'Option 3',
						'Another Row',
						'Something else'
					],
					load: {
						url: '/inputs/text',
						method: 'get',
						params: {}
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var input = await locator.locator('input');
					await expect(input).toBeEnabled();
					await expect(input).toBeVisible();
					await expect(input).toBeAttached();

					var datalist = await locator.locator('datalist');
					await expect(datalist).toBeAttached();

					var options = await datalist.locator('*');
					await expect(options).toHaveCount(5);
					await checkOption(0, 'Option 1');
					await checkOption(1, 'Option 2');
					await checkOption(2, 'Option 3');
					await checkOption(3, 'Another Row');
					await checkOption(4, 'Something else');

					// sleep
					await new Promise(resolve => setTimeout(resolve, 6000));

					await expect(options).toHaveCount(8);

					async function checkOption(index, value) {
						var opt = await options.nth(index);
						//await expect(opt).toHaveValue(value);
						await expect(opt).toHaveAttribute('value', value);
						await expect(opt).toHaveText('');
					}
					await checkOption(0, 'Option 1');
					await checkOption(1, 'Option 2');
					await checkOption(2, 'Option 3');
					await checkOption(3, 'Another Row');
					await checkOption(4, 'Something else');
					await checkOption(5, 'Loaded Option');
					await checkOption(6, 'Some data');
					await checkOption(7, 'Else');
				}
			}
			// TODO build-in valid vs toti valid
		]
	});
});

/***** TEXTAREA *******/

textInputs.push({
	name: 'Input: textarea',
	type: 'group',
	tests: [
		{
			type: 'test',
			name: 'Basic',
			conf: {
				type: 'textarea',
				name: 'testInput'
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('SPAN', await callContainer('tagName'));

					var info = await locator.locator('.textarea-length-info');
					await expect(info).toBeAttached();
					await expect(info).toBeHidden();
					await expect(info).toHaveText('');

					var input = await locator.locator('textarea');
					await expect(input).toBeEnabled();
					await expect(input).toBeVisible();
					await expect(input).toBeAttached();
				},
				setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var input = await locator.locator('textarea');

					await expect(input).toHaveValue('');
					assert.isNull(await callInstance('getValue'));

					await callInstance('setValue', 'some text');
					await expect(input).toHaveValue('some text');

					await callInstance('setValue', 'another text');
					await expect(input).toHaveValue('another text');

					await callInstance('clear');
					await expect(input).toHaveValue('');

				},
				fillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isNull(await callInstance('getValue'));
					var input = await locator.locator('textarea');

					await input.fill('something');
					await input.blur(); // lost focus on input to fire onchange event
					assert.areSame('something', await callInstance('getValue'));
				}
			}
		},
		{
			type: 'test',
			name: 'Min length',
			conf: {
				type: 'textarea',
				name: 'testInput',
				minlength: 5
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var info = await locator.locator('.textarea-length-info');
					var input = await locator.locator('textarea');

					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('5/0/~');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc');
					assert.isFalse(await callInstance('isValid'));

					await expect(info).toHaveText('5/3/~');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abc');

					// set valid
					await callInstance('setValue', 'abc123');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('5/6/~');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abc123');
				},
				set: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var info = await locator.locator('.textarea-length-info');
					var input = await locator.locator('textarea');

					await callInstance('setMinlength', 10);

					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('10/0/~');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc123456');
					assert.isFalse(await callInstance('isValid'));

					await expect(info).toHaveText('10/9/~');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abc123456');

					// set valid
					await callInstance('setValue', 'abcefg123456');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('10/12/~');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abcefg123456');
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var info = await locator.locator('.textarea-length-info');
					var input = await locator.locator('textarea');

					await callInstance('setMinlength', null);

					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('');
					await expect(info).toBeHidden();
					await expect(input).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc123456');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('');
					await expect(info).toBeHidden();
					await expect(input).toHaveValue('abc123456');

					// set valid
					await callInstance('setValue', 'abcefg123456');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('');
					await expect(info).toBeHidden();
					await expect(input).toHaveValue('abcefg123456');
				}
			}
		},
		{
			type: 'test',
			name: 'Max length',
			conf: {
				type: 'textarea',
				name: 'testInput',
				maxlength: 5
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var info = await locator.locator('.textarea-length-info');
					var input = await locator.locator('textarea');

					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('0/0/5');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc123');
					assert.isFalse(await callInstance('isValid'));

					await expect(info).toHaveText('0/6/5');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abc123');

					// set valid
					await callInstance('setValue', 'abc');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('0/3/5');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abc');
				},
				set: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var info = await locator.locator('.textarea-length-info');
					var input = await locator.locator('textarea');

					await callInstance('setMaxlength', 10);

					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('0/0/10');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abcefg123789');
					assert.isFalse(await callInstance('isValid'));

					await expect(info).toHaveText('0/12/10');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abcefg123789');

					// set valid
					await callInstance('setValue', 'abc');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('0/3/10');
					await expect(info).toBeVisible();
					await expect(input).toHaveValue('abc');
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var info = await locator.locator('.textarea-length-info');
					var input = await locator.locator('textarea');

					await callInstance('setMaxlength', null);

					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('');
					await expect(info).toBeHidden();
					await expect(input).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc123');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('');
					await expect(info).toBeHidden();
					await expect(input).toHaveValue('abc123');

					// set valid
					await callInstance('setValue', 'abc');
					assert.isTrue(await callInstance('isValid'));

					await expect(info).toHaveText('');
					await expect(info).toBeHidden();
					await expect(input).toHaveValue('abc');
				}
			}
		},
		{
			type: 'test',
			name: 'Disabled',
			conf: {
				type: 'textarea',
				name: 'testInput',
				disabled: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var info = await locator.locator('.textarea-length-info');
				var input = await locator.locator('textarea');

				await expect(input).toBeDisabled();
				await expect(info).toBeHidden();
			}
		},
		{
			type: 'test',
			name: 'Not editable',
			conf: {
				type: 'textarea',
				name: 'testInput',
				editable: false,
				value: 'Something'
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				expect(locator).toBeAttached();
				expect(locator).toBeVisible();
				expect(locator).toHaveText('Something');
			},
		},
		{
			type: 'test',
			name: 'Cols',
			conf: {
				type: 'textarea',
				name: 'testInput',
				cols: 50
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('textarea');
				await expect(input).toHaveAttribute('cols', '50');
			}
		},
		{
			type: 'test',
			name: 'Rows',
			conf: {
				type: 'textarea',
				name: 'testInput',
				rows: 10
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('textarea');
				await expect(input).toHaveAttribute('rows', '10');
			}
		}
	]
});

/***** PASSWORD *******/

textInputs.push({
	name: 'Input: password',
	type: 'group',
	tests: [
		{
			type: 'test',
			name: 'Basic',
			conf: {
				type: 'password',
				name: 'testInput'
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame('INPUT', await callContainer('tagName'));
					
					await expect(locator).toHaveAttribute('type', 'password');
					await expect(locator).toBeEnabled();
					await expect(locator).toBeVisible();
					await expect(locator).toBeAttached();
				},
				setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toHaveValue('');
					assert.isNull(await callInstance('getValue'));

					await callInstance('setValue', 'some text');
					await expect(locator).toHaveValue('some text');

					await callInstance('setValue', 'another text');
					await expect(locator).toHaveValue('another text');

					await callInstance('clear');
					await expect(locator).toHaveValue('');

				},
				fillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isNull(await callInstance('getValue'));

					await locator.fill('something');
					await locator.blur(); // lost focus on input to fire onchange event
					assert.areSame('something', await callInstance('getValue'));
				}
			}
		},
		{
			type: 'test',
			name: 'Default value',
			conf: {
				type: 'password',
				name: 'testInput',
				value: 'Something'
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toHaveValue('Something');
			},
		},
		{
			type: 'test',
			name: 'Min length',
			conf: {
				type: 'password',
				name: 'testInput',
				minlength: 5
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					// initial state
					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc');

					assert.isFalse(await callInstance('isValid'));
					await expect(locator).toHaveValue('abc');

					// set valid
					await callInstance('setValue', 'abc123');

					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toHaveValue('abc123');
				},
				set: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMinlength', 10);
					// initial state
					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abcefg123');

					assert.isFalse(await callInstance('isValid'));
					await expect(locator).toHaveValue('abcefg123');

					// set valid
					await callInstance('setValue', 'abcefg123456');

					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toHaveValue('abcefg123456');
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMinlength', null);
					// initial state
					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc');

					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toHaveValue('abc');

					// set valid
					await callInstance('setValue', 'abc123');

					assert.isTrue(await callInstance('isValid'));
					await expect(locator).toHaveValue('abc123');
				}
			}
		},
		{
			type: 'test',
			name: 'Max length',
			conf: {
				type: 'password',
				name: 'testInput',
				maxlength: 5
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(locator).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc123');
					assert.isFalse(await callInstance('isValid'));

					await expect(locator).toHaveValue('abc123');

					// set valid
					await callInstance('setValue', 'abc');
					assert.isTrue(await callInstance('isValid'));

					await expect(locator).toHaveValue('abc');
				},
				set: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMaxlength', 10);
					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(locator).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abcefg123987');
					assert.isFalse(await callInstance('isValid'));

					await expect(locator).toHaveValue('abcefg123987');

					// set valid
					await callInstance('setValue', 'abc45689');
					assert.isTrue(await callInstance('isValid'));

					await expect(locator).toHaveValue('abc45689');
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMaxlength', null);
					// initial state
					assert.isTrue(await callInstance('isValid'));

					await expect(locator).toHaveValue('');

					// set invalid
					await callInstance('setValue', 'abc123');
					assert.isTrue(await callInstance('isValid'));

					await expect(locator).toHaveValue('abc123');

					// set valid
					await callInstance('setValue', 'abc');
					assert.isTrue(await callInstance('isValid'));

					await expect(locator).toHaveValue('abc');
				}
			}
		},
		{
			type: 'test',
			name: 'Disabled',
			conf: {
				type: 'password',
				name: 'testInput',
				disabled: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toBeDisabled();
			}
		},
		{
			type: 'test',
			name: 'Not editable',
			conf: {
				type: 'password',
				name: 'testInput',
				editable: false,
				value: 'Something'
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				expect(locator).toBeAttached();
				expect(locator).toBeHidden();
				expect(locator).toHaveText('');
			},
		},
		{
			type: 'test',
			name: 'Optional',
			conf: {
				type: 'password',
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

					var input = await locator.locator('.optional-input-field');
					await expect(input).toHaveAttribute('type', 'password');
					await expect(input).toBeDisabled();
					await expect(input).toBeVisible();
					await expect(input).toBeAttached();
				},
				setRules: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setMinlength', 5);
					await callInstance('setMaxlength', 5);
					await callInstance('setPattern', 'abc');
				}
			}
		}
	]
});

/*****  *******/
require('../spec.js')({
	name: 'Inputs: Text inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: textInputs
});