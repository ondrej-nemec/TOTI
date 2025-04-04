async function verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, name, addButton, inputs, buttonsEnabled) {
	await expect(locator).toBeAttached();
	await expect(locator).toBeVisible();

	assert.areSame('DIV', await callContainer('tagName'));
	var index = 0;
	var childs = await locator.locator('> div');

	var buttonContainer = await childs.nth(index++);
	if (addButton) {
		var button = await buttonContainer.locator('> button');
		await expect(button).toHaveText('+');
		await expect(button).toHaveClass('toti-dynamic-add');
		await expect(button).toHaveAttribute('data-name', name);
		if (buttonsEnabled) {
			await expect(button).toBeEnabled();
		} else {
			await expect(button).toBeDisabled();
		}
	} else {
		assert.areSame('', await buttonContainer.innerHTML());
	}
	for (const settings of inputs) {
		var inputContainer = await locator.locator('> div').nth(index++);
		var title = await inputContainer.locator('> span');
		await expect(title).toHaveText(settings.title);

		if (settings.removeButton) {
			var button = await inputContainer.locator('> button');
			await expect(button).toHaveText('-');
			await expect(button).toHaveClass("toti-dynamic-remove");
			await expect(button).toHaveAttribute('data-name', name);
			if (buttonsEnabled) {
				await expect(button).toBeEnabled();
			} else {
				await expect(button).toBeDisabled();
			}
		} else {
			await expect(inputContainer.locator('> button')).toHaveCount(0);
		}
		var input = await inputContainer.locator('input');
		await expect(input).toBeAttached();
		await expect(input).toHaveValue(settings.value);
	}
	await expect(childs).toHaveCount(index);
}

var specialInputTests = []
specialInputTests.push({
	name: 'Input dynamicInput',
	type: 'group',
	tests: [
		{
			name: 'Basic',
			type: 'test',
			conf: {
				type: 'dynamicInput',
				field: {
					type: 'test-standart',
					name: "text1",
					title: 'Text {i}'
				},
				onChange: ['onDynamicChange']
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
						// init state
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: ''
						}
					], true);
					assert.areSame([null], await callInstance('getValue'));
					await callInstance('addField');
					await callInstance('addField');
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: ''
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: ''
						},
						{
							removeButton: true,
							title: 'Text 2',
							value: ''
						}
					], true);
					assert.areSame([null, null, null], await callInstance('getValue'));
						// remove last
					await callInstance('removeField');
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: ''
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: ''
						}
					], true);
					assert.areSame([null, null], await callInstance('getValue'));
						// remove first
					await callInstance('removeField', 0);
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: ''
						}
					], true);
					assert.areSame([null], await callInstance('getValue'));
				},
				setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setValue', ['123', '987', '456']);
					assert.areSame(['123', '987', '456'], await callInstance('getValue'));
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: '123'
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: '987'
						},
						{
							removeButton: true,
							title: 'Text 2',
							value: '456'
						}
					], true);
						// remove second
					await callInstance('removeField', 1);
					assert.areSame(['123', '456'], await callInstance('getValue'));
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: '123'
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: '456'
						}
					], true);

						// set again
					await callInstance('setValue', ['147', '258', '369']);
					assert.areSame(['147', '258', '369'], await callInstance('getValue'));
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: '147'
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: '258'
						},
						{
							removeButton: true,
							title: 'Text 2',
							value: '369'
						}
					], true);

						// set again
					await callInstance('setValue', ['159', '357']);
					assert.areSame(['159', '357'], await callInstance('getValue'));
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: '159'
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: '357'
						}
					], true);
				},
				clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame([null], await callInstance('getValue'));
					await callInstance('clear');
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [], true);
					assert.areSame([], await callInstance('getValue'));
				},
				buttonClickAndFillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var childs = await locator.locator('> div');
					var button = await childs.nth(0).locator('> button');


						// init state
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: ''
						}
					], true);
					assert.areSame([null], await callInstance('getValue'));

					{
						var input = await childs.nth(1).locator('input');

							// fill input
						await input.fill('something');
						await input.blur(); // lost focus on input to fire onchange event
						assert.areSame(['something'], await callInstance('getValue'));

						// fill again
						await input.fill('else');
						await input.blur(); // lost focus on input to fire onchange event
						assert.areSame(['else'], await callInstance('getValue'));
					}
					{
						var removeButton = await childs.nth(1).locator('button');
						await removeButton.click();
						await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [], true);
						assert.areSame([], await callInstance('getValue'));
					}
					{
						// add two fields
						var addButton = await childs.nth(0).locator('button');
						await addButton.click();
						await addButton.click();
						assert.areSame([null, null], await callInstance('getValue'));

						var input2 = await childs.nth(2).locator('input');
						await input2.fill('second');
						await input2.blur(); // lost focus on input to fire onchange event
						assert.areSame([null, 'second'], await callInstance('getValue'));

						var input1 = await childs.nth(1).locator('input');
						await input1.fill('first');
						await input1.blur(); // lost focus on input to fire onchange event
						assert.areSame(['first', 'second'], await callInstance('getValue'));

						await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
							{
								removeButton: true,
								title: 'Text 0',
								value: 'first'
							},
							{
								removeButton: true,
								title: 'Text 1',
								value: 'second'
							}
						], true);

						// remove first
						var removeButton1 = await childs.nth(1).locator('button');
						await removeButton1.click();
						assert.areSame(['second'], await callInstance('getValue'));

						await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
							{
								removeButton: true,
								title: 'Text 0',
								value: 'second'
							}
						], true);
					}
				},
				onChangeSetValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame([null], await callInstance('getValue'));
					assert.areSame(
						'undefined_[]_[null]',
						await callInstance('getChanges')
					);

					await callInstance('setValue', ['147', '258', '369']);

					assert.areSame(['147', '258', '369'], await callInstance('getValue'));
					console.log(await callInstance('getChanges'));
					assert.areSame(
						'undefined_[]_[null]_["147","258","369"]',
						await callInstance('getChanges')
					);
				},
				onChangeAddButton: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame(
						'undefined_[]_[null]',
						await callInstance('getChanges')
					);

					var childs = await locator.locator('> div');
					var addButton = await childs.nth(0).locator('> button');
					await addButton.click();
					await addButton.click();
					assert.areSame([null, null, null], await callInstance('getValue'));
					assert.areSame(
						'undefined_[]_[null]_[null,null]_[null,null,null]',
						await callInstance('getChanges')
					);
				},
				onChangeFillAddedInputs: async (expect, assert, page, callInstance, callContainer, locator)=>{
					var childs = await locator.locator('> div');
					var addButton = await childs.nth(0).locator('> button');
					await addButton.click();

					var input2 = await childs.nth(2).locator('input');
					await input2.fill('second');
					await input2.blur(); // lost focus on input to fire onchange event
					assert.areSame([null, 'second'], await callInstance('getValue'));
					assert.areSame(
						'undefined_[]_[null]_[null,null]_[null,"second"]',
						await callInstance('getChanges')
					);

					var input1 = await childs.nth(1).locator('input');
					await input1.fill('first');
					await input1.blur(); // lost focus on input to fire onchange event
					assert.areSame(['first', 'second'], await callInstance('getValue'));
					assert.areSame(
						'undefined_[]_[null]_[null,null]_[null,"second"]_["first","second"]',
						await callInstance('getChanges')
					);
				},
				onChangeWrongValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setValue', ['147', '258', '369']);
					assert.areSame(['147', '258', '369'], await callInstance('getValue'));
					assert.areSame(
						'undefined_[]_[null]_["147","258","369"]',
						await callInstance('getChanges')
					);

					await callInstance('setValue', 'complete wrong value');
					assert.areSame([], await callInstance('getValue'));
					assert.areSame(
						'undefined_[]_[null]_["147","258","369"]_[]',
						await callInstance('getChanges')
					);
				},
				onChangeRemoveButton: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await callInstance('setValue', ['147', '258', '369']);

					var childs = await locator.locator('> div');
					var removeButton = await childs.nth(2).locator('button');
					await removeButton.click();

					assert.areSame(['147', '369'], await callInstance('getValue'));
					assert.areSame(
						'undefined_[]_[null]_["147","258","369"]_["147","369"]',
						await callInstance('getChanges')
					);
				}
			}
		},
		{
				name: 'Default values',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					value: [
						'value 1',
						'value 2',
						'value 3'
					],
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: {
					base: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
							{
								removeButton: true,
								title: 'Text 0',
								value: 'value 1'
							},
							{
								removeButton: true,
								title: 'Text 1',
								value: 'value 2'
							},
							{
								removeButton: true,
								title: 'Text 2',
								value: 'value 3'
							}
						], true);
					},
					setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await callInstance('setValue', [159, 357]);
						await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
							{
								removeButton: true,
								title: 'Text 0',
								value: '159'
							},
							{
								removeButton: true,
								title: 'Text 1',
								value: '357'
							}
						], true);
					}
				}
			},
			{
				name: 'Disabled',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					disabled: true,
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [], false);

					await callInstance('setDisabled', false);
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [], true);

					await callInstance('setDisabled', true);
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [], false);

				}
			},
			{
				name: 'Disabled with value',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					disabled: true,
					value: [123, true, 'text'],
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: '123'
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: 'true'
						},
						{
							removeButton: true,
							title: 'Text 2',
							value: 'text'
						}
					], false);

					await callInstance('setDisabled', false);
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: '123'
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: 'true'
						},
						{
							removeButton: true,
							title: 'Text 2',
							value: 'text'
						}
					], true);

					await callInstance('setDisabled', true);
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: '123'
						},
						{
							removeButton: true,
							title: 'Text 1',
							value: 'true'
						},
						{
							removeButton: true,
							title: 'Text 2',
							value: 'text'
						}
					], false);
				}
			},
			{
				name: 'Not editable',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					editable: false,
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeAttached();
					await expect(locator).not.toBeVisible();
					assert.areSame('<div></div>', await locator.innerHTML());

				}
			},
			{
				name: 'Not editable with values',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					editable: false,
					value: [
						'value 1',
						'value 2',
						'value 3'
					],
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeAttached();
					await expect(locator).toBeVisible();
					var childs = await locator.locator('> div');
					await expect(childs).toHaveCount(4);

					assert.areSame('', await childs.nth(0).innerHTML());

					for (var i = 0; i < 3; i++) {
						var inputContainer = await locator.locator('> div').nth(i + 1);
						var title = await inputContainer.locator('> span');
						await expect(title).toHaveText('Text ' + i);

						await expect(inputContainer.locator('> button')).toHaveCount(0);
						var input = await inputContainer.locator('input');
						await expect(input).toBeAttached();
						await expect(input).toHaveValue('value ' + (i+1));
					}
				}
			},
			{
				name: 'Not add first blank',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					addFirstBlank: false,
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [], true);
				}
			},
			{
				name: 'Required',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					required: true,
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					await callInstance('removeField');
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({"input.required":{}}, await callInstance('getErrors'));

					await callInstance('addField');
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
				}
			},
			{
				name: 'Not use add button',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					useAddButton: false,
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', false, [
						{
							removeButton: true,
							title: 'Text 0',
							value: ''
						}
					], true);
				}
			},
			{
				name: 'Not use remove button',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					useRemoveButton: false,
					field: {
						type: 'test-standart',
						name: "text1",
						title: 'Text {i}'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
						{
							removeButton: false,
							title: 'Text 0',
							value: ''
						}
					], true);
				}
			},
			{
				name: 'With input list',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					field: {
						type: 'inputList',
						name: 'input-list',
						title: 'Input List {i}',
						fields: [
							{
								type: 'test-standart',
								name: "text1",
								title: 'Text 1'
							},
							{
								type: 'test-standart',
								name: "text2",
								title: 'Text 2'
							}
						]
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeAttached();
					await expect(locator).toBeVisible();

					assert.areSame('DIV', await callContainer('tagName'));
					var childs = await locator.locator('> div');
					await expect(childs).toHaveCount(2);

					var buttonContainer = await childs.nth(0);
					var addButton = await buttonContainer.locator('> button');
					await expect(addButton).toHaveText('+');
					await expect(addButton).toHaveClass('toti-dynamic-add');
					await expect(addButton).toHaveAttribute('data-name', "null");
					await expect(addButton).toBeEnabled();

					var inputList = await childs.nth(1);
					var removeButton = await inputList.locator('> button');
					await expect(removeButton).toHaveText('-');
					await expect(removeButton).toHaveClass("toti-dynamic-remove");
					await expect(removeButton).toHaveAttribute('data-name', "null");
					await expect(removeButton).toBeEnabled();
					
					var title = await inputList.locator('> span');
					await expect(title).toHaveCount(0);

					var inputs = await inputList.locator('> div');
					await expect(inputs).toHaveCount(1);

					await expect(inputs.locator('> span')).toHaveCount(1);
					await expect(inputs.locator('> div')).toHaveCount(2);
					// TODO more detailed test of inputList?
				}
			},
			{
				name: 'Dynamic in dynamic',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					name: 'dynamicInput',
					field: {
						type: 'dynamicInput',
						name: 'sub-dynamic-input',
						title: 'Dynamc List {i}',
						field: {
							type: 'test-standart',
							name: "text",
							title: 'Text {i}'
						}
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await expect(locator).toBeAttached();
					await expect(locator).toBeVisible();

					assert.areSame('DIV', await callContainer('tagName'));
					var childs = await locator.locator('> div');
					await expect(childs).toHaveCount(2);

					var buttonContainer = await childs.nth(0);
					var addButton = await buttonContainer.locator('> button');
					await expect(addButton).toHaveText('+');
					await expect(addButton).toHaveClass('toti-dynamic-add');
					await expect(addButton).toHaveAttribute('data-name', "dynamicInput");
					await expect(addButton).toBeEnabled();

					var list = await childs.nth(1);
					var removeButton = await list.locator('> button');
					await expect(removeButton).toHaveText('-');
					await expect(removeButton).toHaveClass("toti-dynamic-remove");
					await expect(removeButton).toHaveAttribute('data-name', "dynamicInput");
					await expect(removeButton).toBeEnabled();
					
					var title = await list.locator('> span');
					await expect(title).toHaveCount(0);

					var dynamic = await list.locator('> div');
					await expect(dynamic).toHaveCount(1);
					// TODO more validation - now fails on name - dynamicInput[1457982][sub-dynamic-input]
					/*await verifyDynamicFields(expect, assert, page, callInstance, callContainer, dynamic, 'null', true, [
						{
							removeButton: true,
							title: 'Text 0',
							value: ''
						}
					], true);*/
				}
			},
			{
				name: 'Input has rules',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					name: 'dynamicInput',
					field: {
						type: 'number',
						name: 'sub-number',
						title: 'Number {i}',
						required: true,
						min: 4,
						max: 10
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					// remove field
					await callInstance('removeField');
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					// add field again
					await callInstance('addField');
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					// set value
					await callInstance('setValue', [123])
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					// set value
					await callInstance('setValue', [8])
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
				}
			},
			{
				name: 'Min count 5',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					name: 'dynamicInput',
					field: {
						type: 'test-standart',
						name: 'sub-number',
						title: 'Sub {i}'
					},
					min: 5
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({"input.dynamicMinLenght":{"m":5,"l":1}}, await callInstance('getErrors'));

					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');

					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					await callInstance('removeField');
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({"input.dynamicMinLenght":{"m":5,"l":4}}, await callInstance('getErrors'));
				}
			},
			{
				name: 'Max count 5',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					name: 'dynamicInput',
					field: {
						type: 'test-standart',
						name: 'sub-number',
						title: 'Sub {i}'
					},
					max: 5
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({"input.dynamicMaxLenght":{"m":5,"l":6}}, await callInstance('getErrors'));

					await callInstance('removeField');
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
				}
			},
			{
				name: 'Min count 5 with required input',
				type: 'test',
				conf: {
					type: 'dynamicInput',
					name: 'dynamicInput',
					field: {
						type: 'test-standart',
						name: 'sub-number',
						title: 'Sub {i}',
						required: true
					},
					min: 5
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({"input.dynamicMinLenght":{"m":5,"l":1}}, await callInstance('getErrors'));

					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');
					await callInstance('addField');
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));

					await callInstance('setValue', [1, 2, 3, 4, 5]);

					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
				}
		},
		{
			name: 'Input has default value',
			type: 'test',
			conf: {
				type: 'dynamicInput',
				field: {
					type: 'test-standart',
					name: "text1",
					title: 'Text {i}',
					value: 'something'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
					{
						removeButton: true,
						title: 'Text 0',
						value: 'something'
					}
				], true);
				assert.areSame(['something'], await callInstance('getValue'));

				await callInstance('addField');

				await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
					{
						removeButton: true,
						title: 'Text 0',
						value: 'something'
					},
					{
						removeButton: true,
						title: 'Text 1',
						value: 'something'
					}
				], true);
				assert.areSame(['something', 'something'], await callInstance('getValue'));
			}
		},
		{
			name: 'Default value + Input has default value',
			type: 'test',
			conf: {
				type: 'dynamicInput',
				value: [
					'value 1',
					'value 2',
					'value 3'
				],
				field: {
					type: 'test-standart',
					name: "text1",
					title: 'Text {i}',
					value: 'something'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
					{
						removeButton: true,
						title: 'Text 0',
						value: 'value 1'
					},
					{
						removeButton: true,
						title: 'Text 1',
						value: 'value 2'
					},
					{
						removeButton: true,
						title: 'Text 2',
						value: 'value 3'
					}
				], true);
				assert.areSame(['value 1', 'value 2', 'value 3'], await callInstance('getValue'));

				await callInstance('addField');

				await verifyDynamicFields(expect, assert, page, callInstance, callContainer, locator, 'null', true, [
					{
						removeButton: true,
						title: 'Text 0',
						value: 'value 1'
					},
					{
						removeButton: true,
						title: 'Text 1',
						value: 'value 2'
					},
					{
						removeButton: true,
						title: 'Text 2',
						value: 'value 3'
					},
					{
						removeButton: true,
						title: 'Text 3',
						value: 'something'
					}
				], true);
				assert.areSame(['value 1', 'value 2', 'value 3', 'something'], await callInstance('getValue'));
			}
		}
	]
});


async function verifyInputList(expect, assert, page, callInstance, callContainer, locator, name, inputs) {
	await expect(locator).toBeAttached();
	await expect(locator).toBeVisible();
	assert.areSame('DIV', await callContainer('tagName'));
	
	if (name === null) {
		await expect(locator).not.toHaveAttribute('name');
	} else {
		await expect(locator).toHaveAttribute('name', name);
	}
	
	var childs = await locator.locator('> div');
	await expect(childs).toHaveCount(inputs.length);

	for (var i = 0; i < inputs.length; i++) {
		var settings = inputs[i];
		var inputContainer = await childs.nth(i);
		var input = await inputContainer.locator('> input');
		await expect(input).toBeAttached();
		await expect(input).toBeVisible();
		await expect(input).toHaveAttribute('name', settings.name);
		await expect(input).toHaveValue(settings.value);
		if (settings.disabled) {
			await expect(input).toBeDisabled();
		} else {
			await expect(input).toBeEnabled();
		}
	}
}
specialInputTests.push({
	name: 'Input inputList',
	type: 'group',
	tests: [
		{
			name: 'With name',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				fields: [
					{
						type: 'test-standart',
						name: "text1"
					},
					{
						type: 'test-standart',
						name: "text2"
					}
				],
				onChange: ['onInputListChange']
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
						{
							name: 'input-list[text1]',
							value: '',
							disabled: false
						},
						{
							name: 'input-list[text2]',
							value: '',
							disabled: false
						}
					]);
					assert.areSame({
						text1: null,
						text2: null
					}, await callInstance('getValue'));
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
				},
				setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					// set
					await callInstance('setValue', {
						text1: 'aaa',
						text2: 'bbb'
					});
					await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
						{
							name: 'input-list[text1]',
							value: 'aaa',
							disabled: false
						},
						{
							name: 'input-list[text2]',
							value: 'bbb',
							disabled: false
						}
					]);
					assert.areSame({
						text1: 'aaa',
						text2: 'bbb'
					}, await callInstance('getValue'));

					// clear
					await callInstance('clear');
					await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
						{
							name: 'input-list[text1]',
							value: '',
							disabled: false
						},
						{
							name: 'input-list[text2]',
							value: '',
							disabled: false
						}
					]);
					assert.areSame({
						text1: null,
						text2: null
					}, await callInstance('getValue'));

					// set
					await callInstance('setValue', {
						text1: 'aaa',
						text2: 'bbb'
					});
					// override set
					await callInstance('setValue', {
						text1: 'xxx',
						text2: 'yyy'
					});
					await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
						{
							name: 'input-list[text1]',
							value: 'xxx',
							disabled: false
						},
						{
							name: 'input-list[text2]',
							value: 'yyy',
							disabled: false
						}
					]);
					// partial set
					await callInstance('setValue', {
						text1: 'xxx'
					});
					await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
						{
							name: 'input-list[text1]',
							value: 'xxx',
							disabled: false
						},
						{
							name: 'input-list[text2]',
							value: '',
							disabled: false
						}
					]);
					assert.areSame({
						text1: 'xxx',
						text2: null
					}, await callInstance('getValue'));
				},
				setNotExisting: async (expect, assert, page, callInstance, callContainer, locator)=>{
					// set
					await callInstance('setValue', {
						text1: 'aaa',
						text2: 'bbb',
						text3: 'ccc'
					});
					await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
						{
							name: 'input-list[text1]',
							value: 'aaa',
							disabled: false
						},
						{
							name: 'input-list[text2]',
							value: 'bbb',
							disabled: false
						}
					]);
					assert.areSame({
						text1: 'aaa',
						text2: 'bbb'
					}, await callInstance('getValue'));
				},
				fillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame({
						text1: null,
						text2: null
					}, await callInstance('getValue'));

					var childs = await locator.locator('> div');
					var input1 = await childs.nth(0).locator('input');
					var input2 = await childs.nth(1).locator('input');

					await input1.fill('val1');
					await input1.blur();
					assert.areSame({
						text1: 'val1',
						text2: null
					}, await callInstance('getValue'));

					await input2.fill('val2');
					await input2.blur();
					assert.areSame({
						text1: 'val1',
						text2: 'val2'
					}, await callInstance('getValue'));
				},
				onChange: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame(
						'undefined_{"text1":null,"text2":null}',
						await callInstance('getChanges')
					);

					await callInstance('setValue', {
						text1: 'aaa',
						text2: 'bbb'
					});
					assert.areSame(
						'undefined_{"text1":null,"text2":null}_{"text1":"aaa","text2":"bbb"}',
						await callInstance('getChanges')
					);

					var childs = await locator.locator('> div');
					var input1 = await childs.nth(0).locator('input');
					var input2 = await childs.nth(1).locator('input');


					await input2.fill('second');
					await input2.blur(); // lost focus on input to fire onchange event
					assert.areSame({
						text1: 'aaa',
						text2: 'second'
					}, await callInstance('getValue'));
					assert.areSame(
						'undefined_{"text1":null,"text2":null}_{"text1":"aaa","text2":"bbb"}'
						+ '_{"text1":"aaa","text2":"second"}',
						await callInstance('getChanges')
					);

					await input1.fill('first');
					await input1.blur(); // lost focus on input to fire onchange event
					assert.areSame({
						text1: 'first',
						text2: 'second'
					}, await callInstance('getValue'));
					assert.areSame(
						'undefined_{"text1":null,"text2":null}_{"text1":"aaa","text2":"bbb"}'
						+ '_{"text1":"aaa","text2":"second"}_{"text1":"first","text2":"second"}',
						await callInstance('getChanges')
					);

					await callInstance('setValue', 'complete wrong value');
					assert.areSame({
						text1: null,
						text2: null
					}, await callInstance('getValue'));
					assert.areSame(
						'undefined_{"text1":null,"text2":null}_{"text1":"aaa","text2":"bbb"}'
						+ '_{"text1":"aaa","text2":"second"}_{"text1":"first","text2":"second"}'
						+ '_{"text1":null,"text2":null}',
						await callInstance('getChanges')
					);
				}
			}
		},
		{
			name: 'Without main name',
			type: 'test',
			conf: {
				type: 'inputList',
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
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyInputList(expect, assert, page, callInstance, callContainer, locator, null, [
					{
						name: 'null[text1]',
						value: '',
						disabled: false
					},
					{
						name: 'null[text2]',
						value: '',
						disabled: false
					}
				]);
			}
		},
		{
			name: 'Without input names',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				fields: [
					{
						type: 'test-standart'
					},
					{
						type: 'test-standart'
					}
				]
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
					{
						name: 'input-list[0]',
						value: '',
						disabled: false
					},
					{
						name: 'input-list[1]',
						value: '',
						disabled: false
					}
				]);
				assert.areSame({
					0: null,
					1: null
				}, await callInstance('getValue'));

					// set
				await callInstance('setValue', {
					0: 'aaa',
					1: 'bbb'
				});
				await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
					{
						name: 'input-list[0]',
						value: 'aaa',
						disabled: false
					},
					{
						name: 'input-list[1]',
						value: 'bbb',
						disabled: false
					}
				]);
				assert.areSame({
					0: 'aaa',
					1: 'bbb'
				}, await callInstance('getValue'));
			}
		},
		{
			name: 'Required',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				required: true,
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
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame({
					text1: null,
					text2: null
				}, await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));

				// set
				await callInstance('setValue', {
					text1: 'aaa',
					text2: 'bbb'
				});
				assert.isTrue(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));
			}
		},
		{
			name: 'Input has rules',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				fields: [
					{
						type: 'test-standart',
						name: "text1",
						required: true
					},
					{
						type: 'test-standart',
						name: 'text2',
						required: true
					}
				]
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame({
					text1: null,
					text2: null
				}, await callInstance('getValue'));
				assert.isFalse(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));

				// set
				await callInstance('setValue', {
					text1: 'aaa',
					text2: 'bbb'
				});
				assert.isTrue(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));
			}
		},
		{
			name: 'Default value',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				fields: [
					{
						type: 'test-standart',
						name: "text1"
					},
					{
						type: 'test-standart',
					}
				],
				value: {
					'text1': 'value1',
					1: 'value2'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
					{
						name: 'input-list[text1]',
						value: 'value1',
						disabled: false
					},
					{
						name: 'input-list[1]',
						value: 'value2',
						disabled: false
					}
				]);
				assert.areSame({
					text1: 'value1',
					1: 'value2'
				}, await callInstance('getValue'));
			}
		},
		{
			name: 'Inputs default value',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				fields: [
					{
						type: 'test-standart',
						name: "text1",
						value: 'value1'
					},
					{
						type: 'test-standart',
						value: 'value2'
					}
				]
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
					{
						name: 'input-list[text1]',
						value: 'value1',
						disabled: false
					},
					{
						name: 'input-list[1]',
						value: 'value2',
						disabled: false
					}
				]);
				assert.areSame({
					text1: 'value1',
					1: 'value2'
				}, await callInstance('getValue'));
			}
		},
		{
			name: 'Not editable',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				editable: false,
				fields: [
					{
						type: 'test-standart',
						name: "text1"
					},
					{
						type: 'test-standart',
					}
				]
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				// now editable not efect child
				await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
					{
						name: 'input-list[text1]',
						value: '',
						disabled: false
					},
					{
						name: 'input-list[1]',
						value: '',
						disabled: false
					}
				]);
			}
		},
		{
			name: 'Not editable with default value',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				editable: false,
				fields: [
					{
						type: 'test-standart',
						name: "text1"
					},
					{
						type: 'test-standart',
					}
				],
				value: {
					'text1': 'value1',
					1: 'value2'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				// now editable not efect child
				await verifyInputList(expect, assert, page, callInstance, callContainer, locator, 'input-list', [
					{
						name: 'input-list[text1]',
						value: 'value1',
						disabled: false
					},
					{
						name: 'input-list[1]',
						value: 'value2',
						disabled: false
					}
				]);
			}
		},
		{
			name: 'Custom print',
			type: 'test',
			create: (conf)=>{
				return createCustomInputList(conf);
			},
			conf: {
				type: 'inputList',
				name: 'customPrint',
				fields: [
					{
						type: 'test-standart',
						name: "text1",
						title: 'Text 1'
					},
					{
						type: 'test-standart',
						name: "text2",
						title: 'Text 2'
					}
				]
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toBeAttached();
				await expect(locator).toBeVisible();
				assert.areSame('DIV', await callContainer('tagName'));
				await expect(locator).toHaveAttribute('name', 'customPrint');
				
				var childs = await locator.locator('> div');
				await expect(childs).toHaveCount(2);

				for (var i = 0; i < 2; i++) {
					var index = i + 1;

					var inputContainer = await childs.nth(i);

					var title = await inputContainer.locator('span');
					await expect(title).toHaveText('Text ' + index);

					var input = await inputContainer.locator('> input');
					await expect(input).toBeAttached();
					await expect(input).toBeVisible();
					await expect(input).toHaveAttribute('name', 'customPrint[text' + index + ']');
					await expect(input).toHaveValue('');
					await expect(input).toBeEnabled();
				}
			}
		},
		{
			name: 'Named in unnamed',
			type: 'test',
			conf: {
				type: 'inputList',
				fields: [
					{
						type: 'test-standart',
						name: "text1"
					},
					{
						type: 'inputList',
						name: 'inherit-input',
						fields: [
							{
								type: 'test-standart',
								name: "text21"
							},
							{
								type: 'test-standart',
								name: "text22"
							}
						]
					}
				]
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toBeAttached();
				await expect(locator).toBeVisible();
				assert.areSame('DIV', await callContainer('tagName'));
				await expect(locator).not.toHaveAttribute('name');
				
				var childs = await locator.locator('> div');
				await expect(childs).toHaveCount(2);
				{
					var inputContainer = await childs.nth(0);
					var input = await inputContainer.locator('> input');
					await expect(input).toHaveAttribute('name', 'null[text1]');
					await expect(input).toBeAttached();
					await expect(input).toBeVisible();
					await expect(input).toBeEnabled();
					await expect(input).toHaveValue('');
				}
				{
					var sublistContainer = await childs.nth(1);
					var sublist = await sublistContainer.locator('> div');
					await expect(sublist).toHaveAttribute('name', 'null[inherit-input]');
					var items = await sublist.locator('> div');
					{
						var inputContainer = await items.nth(0);
						var input = await inputContainer.locator('> input');
						await expect(input).toHaveAttribute('name', 'null[inherit-input][text21]');
						await expect(input).toBeAttached();
						await expect(input).toBeVisible();
						await expect(input).toBeEnabled();
						await expect(input).toHaveValue('');
					}
					{
						var inputContainer = await items.nth(1);
						var input = await inputContainer.locator('> input');
						await expect(input).toHaveAttribute('name', 'null[inherit-input][text22]');
						await expect(input).toBeAttached();
						await expect(input).toBeVisible();
						await expect(input).toBeEnabled();
						await expect(input).toHaveValue('');
					}
				}
			}
		},
		{
			name: 'Unamed in named',
			type: 'test',
			conf: {
				type: 'inputList',
				name: 'input-list',
				fields: [
					{
						type: 'test-standart',
						name: "text1"
					},
					{
						type: 'inputList',
						fields: [
							{
								type: 'test-standart',
								name: "text21"
							},
							{
								type: 'test-standart',
								name: "text22"
							}
						]
					}
				]
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toBeAttached();
				await expect(locator).toBeVisible();
				assert.areSame('DIV', await callContainer('tagName'));
				await expect(locator).toHaveAttribute('name', 'input-list');
				
				var childs = await locator.locator('> div');
				await expect(childs).toHaveCount(2);
				{
					var inputContainer = await childs.nth(0);
					var input = await inputContainer.locator('> input');
					await expect(input).toHaveAttribute('name', 'input-list[text1]');
					await expect(input).toBeAttached();
					await expect(input).toBeVisible();
					await expect(input).toBeEnabled();
					await expect(input).toHaveValue('');
				}
				{
					var sublistContainer = await childs.nth(1);
					var sublist = await sublistContainer.locator('> div');
					await expect(sublist).toHaveAttribute('name', 'input-list[1]');
					var items = await sublist.locator('> div');
					{
						var inputContainer = await items.nth(0);
						var input = await inputContainer.locator('> input');
						await expect(input).toHaveAttribute('name', 'input-list[1][text21]');
						await expect(input).toBeAttached();
						await expect(input).toBeVisible();
						await expect(input).toBeEnabled();
						await expect(input).toHaveValue('');
					}
					{
						var inputContainer = await items.nth(1);
						var input = await inputContainer.locator('> input');
						await expect(input).toHaveAttribute('name', 'input-list[1][text22]');
						await expect(input).toBeAttached();
						await expect(input).toBeVisible();
						await expect(input).toBeEnabled();
						await expect(input).toHaveValue('');
					}
				}
			}
		}/*,
			{
				name: 'Default value',
				type: 'test',
				conf: {
					type: 'inputList',
					name: 'input-list',
					fields: [
						{
							type: 'test-standart',
							name: "text1"
						},
						{
							type: 'test-standart',
							name: "text2"
						}
					],
					value: {
						text1: 'abc',
						text2: '123'
					}
				},
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Disabled',
				type: 'test',
				conf: {
					type: 'inputList',
					name: 'input-list',
					disabled: true,
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
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			},
			{
				name: 'Not editable',
				type: 'test',
				conf: {
					type: 'inputList',
					name: 'input-list',
					editable: false,
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
				verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
					throw new Error('Not implemented');
				}
			}
			*/
	]
});


async function verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, name, inputs) {
	await expect(locator).toBeAttached();
	// empty is not visible: await expect(locator).toBeVisible();
	assert.areSame('DIV', await callContainer('tagName'));
	
	if (name === null) {
		await expect(locator).not.toHaveAttribute('name');
	} else {
		await expect(locator).toHaveAttribute('name', name);
	}
	
	var childs = await locator.locator('> div');
	await expect(childs).toHaveCount(inputs.length);

	for (var i = 0; i < inputs.length; i++) {
		var settings = inputs[i];
		var inputContainer = await childs.nth(i);

		var title = await inputContainer.locator('> span');
		if (settings.title === null) {
			await expect(title).toHaveCount(0);
		} else {
			await expect(title).toHaveText(settings.title);
		}

		var input = await inputContainer.locator('input');
		await expect(input).toBeAttached();
		await expect(input).toBeVisible();
		await expect(input).toHaveAttribute('name', settings.name);
		await expect(input).toHaveValue(settings.value);
		if (settings.disabled) {
			await expect(input).toBeDisabled();
		} else {
			await expect(input).toBeEnabled();
		}
	}
}
specialInputTests.push({
	name: 'Input loadedList',
	type: 'group',
	tests: [
		{
			name: 'With text input',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart'
				},
				onChange: ['onLoadedListChange']
			},
			verify: {
				base: async (expect, assert, page, callInstance, callContainer, locator)=>{
					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
					assert.areSame({}, await callInstance('getValue'));

					// sleep
					await new Promise(resolve => setTimeout(resolve, 6000));

					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
						{
							name: 'LoadedList[a]',
							disabled: false,
							value: '',
							title: null
						},
						{
							name: 'LoadedList[b]',
							disabled: false,
							value: '',
							title: null
						}
					]);
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
					assert.areSame({
						a: null,
						b: null
					}, await callInstance('getValue'));
				},
				setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame({}, await callInstance('getValue'));
					
					// set before load
					await callInstance('setValue', {
						a: 'X',
						b: 'Y'
					});
					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);
					assert.areSame({}, await callInstance('getValue'));

					// sleep
					await new Promise(resolve => setTimeout(resolve, 12000));
					assert.areSame({
						a: null,
						b: null
					}, await callInstance('getValue'));

					await callInstance('setValue', {
						a: 'X',
						b: 'Y'
					});
					// check
					assert.areSame({
						a: 'X',
						b: 'Y'
					}, await callInstance('getValue'));
					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
						{
							name: 'LoadedList[a]',
							disabled: false,
							value: 'X',
							title: null
						},
						{
							name: 'LoadedList[b]',
							disabled: false,
							value: 'Y',
							title: null
						}
					]);

					// clear
					await callInstance('clear');
					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
						{
							name: 'LoadedList[a]',
							disabled: false,
							value: '',
							title: null
						},
						{
							name: 'LoadedList[b]',
							disabled: false,
							value: '',
							title: null
						}
					]);
					assert.areSame({
						a: null,
						b: null
					}, await callInstance('getValue'));

				},
				setNotExisting: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame({}, await callInstance('getValue'));
					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);
					
					// sleep
					await new Promise(resolve => setTimeout(resolve, 12000));

					await callInstance('setValue', {
						a: 'X',
						b: 'Y',
						c: 'Z'
					});
					// check
					assert.areSame({
						a: 'X',
						b: 'Y'
					}, await callInstance('getValue'));
					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
						{
							name: 'LoadedList[a]',
							disabled: false,
							value: 'X',
							title: null
						},
						{
							name: 'LoadedList[b]',
							disabled: false,
							value: 'Y',
							title: null
						}
					]);

				},
				fillValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
					// sleep
					await new Promise(resolve => setTimeout(resolve, 12000));

					assert.areSame({
						a: null,
						b: null
					}, await callInstance('getValue'));

					var childs = await locator.locator('> div');
					var input1 = await childs.nth(0).locator('input');
					var input2 = await childs.nth(1).locator('input');

					await input1.fill('val1');
					await input1.blur();
					assert.areSame({
						a: 'val1',
						b: null
					}, await callInstance('getValue'));

					await input2.fill('val2');
					await input2.blur();
					assert.areSame({
						a: 'val1',
						b: 'val2'
					}, await callInstance('getValue'));
				},
				onChange: async (expect, assert, page, callInstance, callContainer, locator)=>{
					assert.areSame(
						'undefined_{}',
						await callInstance('getChanges')
					);

					// sleep
					await new Promise(resolve => setTimeout(resolve, 12000));
					assert.areSame(
						'undefined_{}_{"a":null,"b":null}',
						await callInstance('getChanges')
					);

					await callInstance('setValue', {
						a: 'aaa',
						b: 'bbb'
					});
					assert.areSame(
						'undefined_{}_{"a":null,"b":null}_{"a":"aaa","b":"bbb"}',
						await callInstance('getChanges')
					);

					var childs = await locator.locator('> div');
					var input1 = await childs.nth(0).locator('input');
					var input2 = await childs.nth(1).locator('input');


					await input2.fill('second');
					await input2.blur(); // lost focus on input to fire onchange event
					assert.areSame({
						a: 'aaa',
						b: 'second'
					}, await callInstance('getValue'));
					assert.areSame(
						'undefined_{}_{"a":null,"b":null}_{"a":"aaa","b":"bbb"}'
						+ '_{"a":"aaa","b":"second"}',
						await callInstance('getChanges')
					);

					await input1.fill('first');
					await input1.blur(); // lost focus on input to fire onchange event
					assert.areSame({
						a: 'first',
						b: 'second'
					}, await callInstance('getValue'));
					assert.areSame(
						'undefined_{}_{"a":null,"b":null}_{"a":"aaa","b":"bbb"}'
						+ '_{"a":"aaa","b":"second"}_{"a":"first","b":"second"}',
						await callInstance('getChanges')
					);

					await callInstance('setValue', 'complete wrong value');
					assert.areSame({
						a: null,
						b: null
					}, await callInstance('getValue'));
					assert.areSame(
						'undefined_{}_{"a":null,"b":null}_{"a":"aaa","b":"bbb"}'
						+ '_{"a":"aaa","b":"second"}_{"a":"first","b":"second"}'
						+ '_{"a":null,"b":null}',
						await callInstance('getChanges')
					);
				}
			}
		},
		{
			name: 'With title',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart',
					title: 'Input {i}'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));

				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
					{
						name: 'LoadedList[a]',
						disabled: false,
						value: '',
						title: 'Input A'
					},
					{
						name: 'LoadedList[b]',
						disabled: false,
						value: '',
						title: 'Input B'
					}
				]);
			}
		},
		{
			name: 'With text and def value',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart'
				},
				value: {
					a: 'value A',
					b: 'value B'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));
				
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
					{
						name: 'LoadedList[a]',
						disabled: false,
						value: 'value A',
						title: null
					},
					{
						name: 'LoadedList[b]',
						disabled: false,
						value: 'value B',
						title: null
					}
				]);
			}
		},
		{
			name: 'With text and input def value',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart',
					value: 'something'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);
				assert.areSame({}, await callInstance('getValue'));

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));
				
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
					{
						name: 'LoadedList[a]',
						disabled: false,
						value: 'something',
						title: null
					},
					{
						name: 'LoadedList[b]',
						disabled: false,
						value: 'something',
						title: null
					}
				]);
				assert.areSame({
					a: 'something',
					b: 'something'
				}, await callInstance('getValue'));
			}
		},
		{
			name: 'With select',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'select',
					options: [
						{
							value: 'a',
							title: 'A',
							selectable: false,
							options: [
								{
									value: 'a1',
									title: 'A1'
								},
								{
									value: 'a2',
									title: 'A2'
								}
							]
						},
						{
							value: 'b',
							title: 'B',
							selectable: false,
							options: [
								{
									value: 'b1',
									title: 'B1'
								},
								{
									value: 'b2',
									title: 'B2'
								}
							]
						}
					]
				},
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);
					assert.isFalse(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
					assert.areSame({}, await callInstance('getValue'));

					// sleep
					await new Promise(resolve => setTimeout(resolve, 12000));

					await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
						{
							name: 'LoadedList[a]',
							disabled: false,
							value: '',
							title: null
						},
						{
							name: 'LoadedList[b]',
							disabled: false,
							value: '',
							title: null
						}
					]);
					assert.isTrue(await callInstance('isValid'));
					assert.areSame({}, await callInstance('getErrors'));
					assert.areSame({
						a: null,
						b: null
					}, await callInstance('getValue'));
			}
		},
		{
			name: 'With select and def value',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'select',
					options: [
						{
							value: 'a',
							title: 'A',
							selectable: false,
							options: [
								{
									value: 'a1',
									title: 'A1'
								},
								{
									value: 'a2',
									title: 'A2'
								}
							]
						},
						{
							value: 'b',
							title: 'B',
							selectable: false,
							options: [
								{
									value: 'b1',
									title: 'B1'
								},
								{
									value: 'b2',
									title: 'B2'
								}
							]
						}
					]
				},
				value: {
					a: 'a1',
					b: 'b2'
				}
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 12000));
				
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
					{
						name: 'LoadedList[a]',
						disabled: false,
						value: 'A1',
						title: null
					},
					{
						name: 'LoadedList[b]',
						disabled: false,
						value: 'B2',
						title: null
					}
				]);
			}
		},
		{
			name: 'Disabled',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart'
				},
				disabled: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				// disabled has no efect to childs
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));

				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
					{
						name: 'LoadedList[a]',
						disabled: false,
						value: '',
						title: null
					},
					{
						name: 'LoadedList[b]',
						disabled: false,
						value: '',
						title: null
					}
				]);
			}
		},
		{
			name: 'Required',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart'
				},
				required: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isFalse(await callInstance('isValid'));
				// errors are empty - not loaded
				assert.areSame({}, await callInstance('getErrors'));
				assert.areSame({}, await callInstance('getValue'));

				// sleep
				await new Promise(resolve => setTimeout(resolve, 12000));

				assert.isTrue(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));
				assert.areSame({
					a: null,
					b: null
				}, await callInstance('getValue'));
			}
		},
		{
			name: 'Inputs with rules',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart',
					required: true
				},
				required: true
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isFalse(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));
				assert.areSame({}, await callInstance('getValue'));

				// sleep
				await new Promise(resolve => setTimeout(resolve, 12000));

				assert.isFalse(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));
				assert.areSame({
					a: null,
					b: null
				}, await callInstance('getValue'));

				await callInstance('setValue', {
					a: 'X',
					b: 'Y'
				});

				assert.isTrue(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));
				assert.areSame({
					a: 'X',
					b: 'Y'
				}, await callInstance('getValue'));
			}
		},
		{
			name: 'Not editable',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart'
				},
				editable: false
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				// has no efect to childs
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));

				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
					{
						name: 'LoadedList[a]',
						disabled: false,
						value: '',
						title: null
					},
					{
						name: 'LoadedList[b]',
						disabled: false,
						value: '',
						title: null
					}
				]);
			}
		},
		{
			name: 'Not editable with value',
			type: 'test',
			conf: {
				type: 'loadedList',
				name: 'LoadedList',
				load: {
					url: '/inputs/loadedList',
					method: 'get',
					params: {}
				},
				field: {
					type: 'test-standart'
				},
				value: {
					a: 'value a',
					b: 'value b'
				},
				editable: false
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				// has no efect to childs
				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));

				await verifyLoadedList(expect, assert, page, callInstance, callContainer, locator, 'LoadedList', [
					{
						name: 'LoadedList[a]',
						disabled: false,
						value: 'value a',
						title: null
					},
					{
						name: 'LoadedList[b]',
						disabled: false,
						value: 'value b',
						title: null
					}
				]);
			}
		}
	]
});

require('../spec.js')({
	name: 'Inputs: Special inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: specialInputTests
});