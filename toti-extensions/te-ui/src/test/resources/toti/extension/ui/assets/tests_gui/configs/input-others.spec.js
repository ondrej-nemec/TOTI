require('../spec.js')({
	name: 'Inputs: Other inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: [
		{
			name: 'Input file',
			type: 'group',
			tests: [
				{
					name: 'Basic',
					type: 'test',
					conf: {
						type: 'file'
					},
					verify: {
						base: async (expect, assert, page, callInstance, callContainer, locator)=>{
							throw new Error('Not implemented');
						},
						setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
							throw new Error('Not implemented');
						}
					}
				},
				{
					name: 'Multiple',
					type: 'test',
					conf: {
						type: 'file',
						multiple: true
					},
					verify: {
						base: async (expect, assert, page, callInstance, callContainer, locator)=>{
							throw new Error('Not implemented');
						},
						setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
							throw new Error('Not implemented');
						}
					}
				},
				{
					name: 'Default value',
					type: 'test',
					conf: {
						type: 'file',
						value: [
							{
								content: 'file content',
								name: 'file-name.txt',
								type: 'plain/txt',
								size: 10
							}
						]
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						throw new Error('Not implemented');
					}
				},
				{
					name: 'Default values',
					type: 'test',
					conf: {
						type: 'file',
						value: [
							{
								content: 'file content',
								name: 'file-name.txt',
								type: 'plain/txt',
								size: 10
							},
							{
								content: 'file content 2',
								name: 'file-name-2.txt',
								type: 'plain/txt',
								size: 10
							}
						]
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						throw new Error('Not implemented');
					}
				},
				{
					name: 'Disabled',
					type: 'test',
					conf: {
						type: 'file',
						disabled: true
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						throw new Error('Not implemented');
					}
				},
				{
					name: 'Not editable',
					type: 'test',
					conf: {
						type: 'file',
						editable: false,
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						throw new Error('Not implemented');
					}
				},
				{
					name: 'Not editable with default value',
					type: 'test',
					conf: {
						type: 'file',
						editable: false,
						value: [
							{
								content: 'file content',
								name: 'file-name.txt',
								type: 'plain/txt',
								size: 10
							},
							{
								content: 'file content 2',
								name: 'file-name-2.txt',
								type: 'plain/txt',
								size: 10
							}
						]
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						throw new Error('Not implemented');
					}
				},
				{
					name: 'Optional',
					type: 'test',
					conf: {
						type: 'file',
						optional: true
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						throw new Error('Not implemented');
					}
				}
			]
		},
		/*************************/
		{
			name: 'Input hidden',
			type: 'group',
			tests: [
				{
					name: 'Basic',
					type: 'test',
					conf: {
						type: 'hidden'
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await expect(locator).toBeEnabled();
						await expect(locator).toBeHidden();
						await expect(locator).toBeAttached();
						assert.isNull(await callInstance('getValue'));
						await expect(locator).toHaveValue('');
					}
				},
				{
					name: 'Default value',
					type: 'test',
					conf: {
						type: 'hidden',
						value: 'abc'
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('INPUT', await callContainer('tagName'));
						await expect(locator).toBeEnabled();
						await expect(locator).toBeHidden();
						await expect(locator).toBeAttached();
						assert.areSame('abc', await callInstance('getValue'));
						await expect(locator).toHaveValue('abc');
					}
				},
				{
					name: 'Disabled',
					type: 'test',
					conf: {
						type: 'hidden',
						disabled: true
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('INPUT', await callContainer('tagName'));
						await expect(locator).toBeEnabled(); // disabled has no efect
						await expect(locator).toBeHidden();
						await expect(locator).toBeAttached();
						assert.isNull(await callInstance('getValue'));
						await expect(locator).toHaveValue('');
					}
				},
				{
					name: 'Not editable',
					type: 'test',
					conf: {
						type: 'hidden',
						editable: false
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('SPAN', await callContainer('tagName'));
						await expect(locator).toBeEnabled();
						await expect(locator).toBeHidden();
						await expect(locator).toBeAttached();
						assert.isNull(await callInstance('getValue'));
						await expect(locator).toHaveText('');
					}
				},
				{
					name: 'Not editable, default value',
					type: 'test',
					conf: {
						type: 'hidden',
						editable: false,
						value: 'abc'
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('SPAN', await callContainer('tagName'));
						await expect(locator).toBeEnabled();
						await expect(locator).toBeHidden();
						await expect(locator).toBeAttached();
						assert.areSame('abc', await callInstance('getValue'));
						await expect(locator).toHaveText('');
					}
				}
			]
		},
		/*************************/
		{
			name: 'Input color',
			type: 'group',
			tests: [
				{
					name: 'Basic',
					type: 'test',
					conf: {
						type: 'color'
					},
					verify: {
						base: async (expect, assert, page, callInstance, callContainer, locator)=>{
							assert.areSame('INPUT', await callContainer('tagName'));
							await expect(locator).toBeEnabled();
							await expect(locator).toBeVisible();
							await expect(locator).toBeAttached();
							assert.isNull(await callInstance('getValue'));
							await expect(locator).toHaveValue('#000000');
						},
						setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
							assert.isNull(await callInstance('getValue'));
							await expect(locator).toHaveValue('#000000');

							// set invalid value
							await callInstance('setValue', 'something');
							assert.isNull(await callInstance('getValue'));
							await expect(locator).toHaveValue('#000000');

							// set valid value
							await callInstance('setValue', '#ffffff');
							assert.areSame('#ffffff', await callInstance('getValue'));
							await expect(locator).toHaveValue('#ffffff');

							// clear
							await callInstance('clear');
							assert.isNull(await callInstance('getValue'));
							await expect(locator).toHaveValue('#000000');
						}
					}
				},
				{
					name: 'Default value',
					type: 'test',
					conf: {
						type: 'color',
						value: '#ff25e8'
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('INPUT', await callContainer('tagName'));
						await expect(locator).toBeEnabled();
						await expect(locator).toBeVisible();
						await expect(locator).toBeAttached();
						assert.areSame('#ff25e8', await callInstance('getValue'));
						await expect(locator).toHaveValue('#ff25e8');
					}
				},
				{
					name: 'Not editable',
					type: 'test',
					conf: {
						type: 'color',
						editable: false
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('SPAN', await callContainer('tagName'));
						await expect(locator).toBeAttached();
						await expect(locator).toHaveText('');
					}
				},
				{
					name: 'Not editable with default value',
					type: 'test',
					conf: {
						type: 'color',
						editable: false,
						value: '#ff25e8'
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('SPAN', await callContainer('tagName'));
						await expect(locator).toBeAttached();
						await expect(locator).toHaveText('#ff25e8');
					}
				},
				{
					name: 'Optional',
					type: 'test',
					conf: {
						type: 'color',
						optional: true
					},
					verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('DIV', await callContainer('tagName'));

						var checkbox = await locator.locator('.optional-input-switch');
						await expect(checkbox).toBeEnabled();
						await expect(checkbox).toBeVisible();
						await expect(checkbox).toBeAttached();
						assert.isFalse(await checkbox.isChecked());

						var input = await locator.locator('input.optional-input-field');
						await expect(input).toHaveAttribute('type', 'color');
						await expect(input).toBeDisabled();
						await expect(input).toBeVisible();
						await expect(input).toBeAttached();
					}
				}
			]
		}
	]
});