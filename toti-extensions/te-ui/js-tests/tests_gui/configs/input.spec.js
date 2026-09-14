var standartValidate = {
	container: async (expect, assert, page, callInstance, callContainer, locator)=>{
		assert.areSame('INPUT', await callContainer('tagName'));
		await expect(locator).toHaveAttribute('type', 'text');
	},
	isDisabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toBeDisabled();
	},
	isEnabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toBeEnabled();
	},
	valueMatch: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		// assert.areSame(value, await callContainer('value'));
		await expect(locator).toHaveValue(value);
	},
	emptyValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
		assert.areSame('', await callContainer('value'));
	},
	notEditableContainer: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		assert.areSame('SPAN', await callContainer('tagName'));
	},
	notEditableValueMatch: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		await expect(locator).toContainText(value);
	},
	notEditableEmptyValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toContainText('');
	},
	parameter: async (expect, assert, page, callInstance, callContainer, locator, name, value)=>{
		await expect(locator).toHaveAttribute(name, value);
	}
};
var optionalValidate = {
	container: async (expect, assert, page, callInstance, callContainer, locator)=>{
		assert.areSame('DIV', await callContainer('tagName'));

		var checkbox = await locator.locator('.optional-input-switch');
		await expect(checkbox).toBeEnabled();
		await expect(checkbox).toBeVisible();
		await expect(checkbox).toBeAttached();
		assert.isFalse(await checkbox.isChecked());

		var input = await locator.locator('.optional-input-field');
		await expect(input).toBeDisabled();
		await expect(input).toBeVisible();
		await expect(input).toBeAttached();
	},
	isDisabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
		var input = await locator.locator('.optional-input-field');
		await expect(input).toBeDisabled();

		var checkbox = await locator.locator('.optional-input-switch');
		await expect(checkbox).toBeDisabled();
	},
	isEnabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
		var checkbox = await locator.locator('.optional-input-switch');
		await expect(checkbox).toBeEnabled();

		var input = await locator.locator('.optional-input-field');
		assert.areSame(await checkbox.isChecked(), await input.isEnabled());
	},
	valueMatch: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		var checkbox = await locator.locator('.optional-input-switch');
		assert.isTrue(await checkbox.isChecked());

		var input = await locator.locator('.optional-input-field');
		// if disabled, this still disabled: await expect(input).toBeEnabled();

		await expect(input).toHaveValue(value);
		/*assert.areSame(value, await page.evaluate(()=>{
			return document.querySelector('.optional-input-field').value;
		}));*/
	},
	emptyValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
		var checkbox = await locator.locator('.optional-input-switch');
		assert.isFalse(await checkbox.isChecked());

		var input = await locator.locator('.optional-input-field');
		await expect(input).toBeDisabled();
		// range default value
		await expect(input).toHaveValue('50');
		/*assert.areSame('50', await page.evaluate(()=>{
			return document.querySelector('.optional-input-field').value;
		}));*/
	},
	notEditableContainer: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		assert.areSame('SPAN', await callContainer('tagName'));
	},
	notEditableValueMatch: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		await expect(locator).toContainText(value);
	},
	notEditableEmptyValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toContainText('');
	},
	parameter: async (expect, assert, page, callInstance, callContainer, locator, name, value)=>{
		var input = await locator.locator('.optional-input-field');
		var checkbox = await locator.locator('.optional-input-switch');
		switch (name) {
			case 'class':
				await expect(locator).toHaveClass(/.*class1 class2 class3.*/);
				break;
			case 'title':
			case 'data-a':
			case 'data-b':
				await expect(locator).toHaveAttribute(name, value);
				break;
			case 'name':
				await expect(locator).toHaveAttribute('data-name', value);
			case 'placeholder':
				await expect(input).toHaveAttribute(name, value);
				break;
			default:
				throw new Error('Validate parameter: unsorted parameter: ' + name);
		}
	}
};
var checkValidate = {
	container: async (expect, assert, page, callInstance, callContainer, locator)=>{
		assert.areSame('INPUT', await callContainer('tagName'));
		await expect(locator).toHaveAttribute('type', 'checkbox');
	},
	isDisabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toBeDisabled();
	},
	isEnabled: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toBeEnabled();
	},
	valueMatch: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		// assert.areSame('on', await callContainer('value'));
		await expect(locator).toHaveValue('on');
		// assert.areSame(value, await callContainer('checked'));
		assert.areSame(value, await locator.isChecked());
	},
	emptyValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
		// assert.areSame('on', await callContainer('value'));
		await expect(locator).toHaveValue('on');
		// assert.areSame(false, await callContainer('checked'));
		assert.areSame(false, await locator.isChecked());
	},
	notEditableContainer: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		assert.areSame('SPAN', await callContainer('tagName'));
	},
	notEditableValueMatch: async (expect, assert, page, callInstance, callContainer, locator, value)=>{
		await expect(locator).toContainText(value + '');
	},
	notEditableEmptyValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
		await expect(locator).toContainText('');
	},
	parameter: async (expect, assert, page, callInstance, callContainer, locator, name, value)=>{
		await expect(locator).toHaveAttribute(name, value);
	}
};

var baseInputConf = [];
var _placeHolderTests = (type, validate)=>[
	{
		type: 'test',
		name: 'Placeholder',
		conf: {
			type: type,
			placeholder: 'Write some number'
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validate.parameter(expect, assert, page, callInstance, callContainer, locator, 'placeholder', 'Write some number');
		},
	},
	{
		type: 'test',
		name: 'Placeholder Not editable',
		conf: {
			type: type,
			placeholder: 'Write some number',
			editable: false
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await expect(locator).toContainText('');
		},
	}
];
var _requiredTest = (type, validate, defValue)=>{
	return {
		type: 'test',
		name: 'Required',
		conf: {
			type: type,
			required: true
		},
		verify: {
			base: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isFalse(await callInstance('isExcluded'));
				assert.isTrue(await callInstance('isRequired'));
				assert.isFalse(await callInstance('isDisabled'));
				await validate.isEnabled(expect, assert, page, callInstance, callContainer, locator);
							
				await callInstance('setRequired', false);
				assert.isFalse(await callInstance('isRequired'));
				await expect(locator).toBeEnabled();
			},
			validityNeedValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isTrue(await callInstance('isRequired'));

				// not valid - missing value
				assert.isFalse(await callInstance('isValid'));
				// valid if required with value
				await callInstance('setValue', defValue);
				assert.isTrue(await callInstance('isValid'));

				// valid if no value and not required
				await callInstance('setRequired', false);
				await callInstance('clear');
				assert.isTrue(await callInstance('isValid'));
			}
		}
	};
};
[
	{
		name: 'Standart input',
		type: 'test-standart',
		defValue: 'Some value',
		defValue2: 'Another value',
		emptyValue: null,
		setChanges: [ 'undefined_null', 'undefined_null_Some value', 'undefined_null_Some value_Another value' ],
		clearChanges: [ 'undefined_null' ],
		useRequired: true,
		specificTests: [
			..._placeHolderTests('test-standart', standartValidate),
			_requiredTest('test-standart', standartValidate, 'Some value')
		],
		validate: standartValidate
	},
	{
		name: 'Optional input',
		type: 'test-optional',
		defValue: '42',
		defValue2: '73',
		emptyValue: null,
		setChanges: [ 'undefined_null', 'undefined_null_42', 'undefined_null_42_73' ],
		clearChanges: [ 'undefined_null' ],
		useRequired: false,
		specificTests: [
			..._placeHolderTests('test-optional', optionalValidate),
			{
				type: 'test',
				name: 'Iteracting with is used',
				conf: {
					type: 'test-optional'
				},
				verify: {
					setUsed: async (expect, assert, page, callInstance, callContainer, locator)=>{
						var checkbox = await locator.locator('.optional-input-switch');
						var input = await locator.locator('.optional-input-field');

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set used true
						await callInstance('setUsed', true);
						assert.isTrue(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isTrue(await checkbox.isChecked());
						await expect(input).toBeEnabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set used false
						await callInstance('setUsed', false);
						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));
					},
					setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
						var checkbox = await locator.locator('.optional-input-switch');
						var input = await locator.locator('.optional-input-field');

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set
						await callInstance('setValue', 27);
						assert.isTrue(await callInstance('isUsed'));
						assert.areSame(27, await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isTrue(await checkbox.isChecked());
						await expect(input).toBeEnabled();
						assert.areSame('27', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						/*// set
						await callInstance('setValue', '');
						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));*/
					},
					clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
						var checkbox = await locator.locator('.optional-input-switch');
						var input = await locator.locator('.optional-input-field');

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set
						await callInstance('setValue', 27);
						assert.isTrue(await callInstance('isUsed'));
						assert.areSame(27, await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isTrue(await checkbox.isChecked());
						await expect(input).toBeEnabled();
						assert.areSame('27', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// clear
						await callInstance('clear');
						console.log(await callInstance('isUsed'));
						console.log(await checkbox.isChecked());

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));
					}
				}
			},
			{
				type: 'test',
				name: 'Disabled: Iteracting with is used',
				conf: {
					type: 'test-optional',
					disabled: true
				},
				verify: {
					setUsed: async (expect, assert, page, callInstance, callContainer, locator)=>{
						var checkbox = await locator.locator('.optional-input-switch');
						var input = await locator.locator('.optional-input-field');

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set used true
						await callInstance('setUsed', true);
						assert.isTrue(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isTrue(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set used false
						await callInstance('setUsed', false);
						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));
					},
					setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
						var checkbox = await locator.locator('.optional-input-switch');
						var input = await locator.locator('.optional-input-field');

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set
						await callInstance('setValue', 27);
						assert.isTrue(await callInstance('isUsed'));
						assert.areSame(27, await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isTrue(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('27', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						/*// set
						await callInstance('setValue', '');
						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeEnabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));*/
					},
					clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
						var checkbox = await locator.locator('.optional-input-switch');
						var input = await locator.locator('.optional-input-field');

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// set
						await callInstance('setValue', 27);
						assert.isTrue(await callInstance('isUsed'));
						assert.areSame(27, await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isTrue(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('27', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));

						// clear
						await callInstance('clear');
						console.log(await callInstance('isUsed'));
						console.log(await checkbox.isChecked());

						assert.isFalse(await callInstance('isUsed'));
						assert.isNull(await callInstance('getValue'));
						await expect(checkbox).toBeDisabled();
						assert.isFalse(await checkbox.isChecked());
						await expect(input).toBeDisabled();
						assert.areSame('50', await page.evaluate(()=>{
							return document.querySelector('.optional-input-field').value;
						}));
					}
				}
			}
		],
		validate: optionalValidate
	},
	{
		name: 'Check input',
		type: 'test-check',
		defValue: true,
		defValue2: true,
		emptyValue: false,
		setChanges: [ 'undefined_false', 'undefined_false_true', 'undefined_false_true' ],
		clearChanges: [ 'undefined_false' ],
		useRequired: true,
		specificTests: [,
			_requiredTest('test-standart', standartValidate, 'Some value'),
			{
				type: 'test',
				name: 'Not editable options',
				conf: {
					type: 'test-check',
					editable: false,
					options: {
						true: 'Yes',
						false: 'No'
					}
				},
				verify: {
					base: async (expect, assert, page, callInstance, callContainer, locator)=>{
						assert.areSame('SPAN', await callContainer('tagName'));
						await expect(locator).toContainText('No');
					},
					setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
						await expect(locator).toContainText('No');

						await callInstance('setValue', true);
						await expect(locator).toContainText('Yes');
					}
				}
			}
		],
		validate: checkValidate
	}
].forEach((spec)=>{
	var singleTests = [];
	singleTests.push({
		type: 'test',
		name: 'Basic',
		conf: {
			type: spec.type,
			onChange: ['onTextChange1', 'onTextChange2']
		},
		verify: {
			base: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toBeEnabled();
				await expect(locator).toBeVisible();
				await expect(locator).toBeAttached();

			      	// TODO test type?
			      	// var instanceType = await callInstance('getType');
			      	// var containerType = await

				assert.isNull(await callInstance('getName'));
				await spec.validate.container(expect, assert, page, callInstance, callContainer, locator);
			},
			flags: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isFalse(await callInstance('isExcluded'));
				assert.isFalse(await callInstance('isRequired'));
				assert.isFalse(await callInstance('isDisabled'));
				await spec.validate.isEnabled(expect, assert, page, callInstance, callContainer, locator);

				await callInstance('setExcluded', true);
				var isExcluded = await callInstance('isExcluded');
				assert.isTrue(isExcluded);

				if (spec.useRequired) {
					await callInstance('setRequired', true);
					assert.isTrue(await callInstance('isRequired'));
				}

				await callInstance('setDisabled', true);
				assert.isTrue(await callInstance('isDisabled'));
				await spec.validate.isDisabled(expect, assert, page, callInstance, callContainer, locator);

				await callInstance('setExcluded', false);
				assert.isFalse(await callInstance('isExcluded'));

				if (spec.useRequired) {
					await callInstance('setRequired', false);
					assert.isFalse(await callInstance('isRequired'));
				}

				await callInstance('setDisabled', false);
				assert.isFalse(await callInstance('isDisabled'));
				await spec.validate.isEnabled(expect, assert, page, callInstance, callContainer, locator);
			},
			value: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame(spec.emptyValue, await callInstance('getValue')); // assert.isNull(await callInstance('getValue'));
				await spec.validate.emptyValue(expect, assert, page, callInstance, callContainer, locator);
				assert.areSame('1__' + spec.setChanges[0], await callInstance('getChanges1'));
				assert.areSame('2__' + spec.setChanges[0], await callInstance('getChanges2'));

				// set
				await callInstance('setValue', spec.defValue);
				assert.areSame(spec.defValue, await callInstance('getValue'));
				await spec.validate.valueMatch(expect, assert, page, callInstance, callContainer, locator, spec.defValue);
				assert.areSame('1__' + spec.setChanges[1], await callInstance('getChanges1'));
				assert.areSame('2__' + spec.setChanges[1], await callInstance('getChanges2'));

				// set same value - no callback called
				await callInstance('setValue', spec.defValue);
				assert.areSame(spec.defValue, await callInstance('getValue'));
				await spec.validate.valueMatch(expect, assert, page, callInstance, callContainer, locator, spec.defValue);
				assert.areSame('1__' + spec.setChanges[1], await callInstance('getChanges1'));
				assert.areSame('2__' + spec.setChanges[1], await callInstance('getChanges2'));

				// set again
				await callInstance('setValue', spec.defValue2);
				assert.areSame(spec.defValue2, await callInstance('getValue'));
				await spec.validate.valueMatch(expect, assert, page, callInstance, callContainer, locator, spec.defValue2);
				assert.areSame('1__' + spec.setChanges[2], await callInstance('getChanges1'));
				assert.areSame('2__' + spec.setChanges[2], await callInstance('getChanges2'));
			},
			onChangeWithSource: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await page.evaluateHandle(()=>{
					function after(name) {
						var method = 'getSourced' + name;
						var variable = 'sourced' + name;
						// init state
						window.item[variable] = 'undefined';
						window.item[method] = ()=>{
							return window.item[variable];
						};
						return (instance, oldValue) =>{
							instance[variable] = instance[variable] + '_' + instance.getValue();
						};
					}
					window.item.onChange(after('1'), 'source1');
					window.item.onChange(after('2'), 'source2');
				});

				var changeValue = 'undefined_' + spec.emptyValue;

				assert.areSame(spec.emptyValue, await callInstance('getValue'));
				assert.areSame('1__' + changeValue, await callInstance('getChanges1'));
				assert.areSame('2__' + changeValue, await callInstance('getChanges2'));

				assert.areSame('undefined', await callInstance('getSourced1'));
				assert.areSame('undefined', await callInstance('getSourced2'));

				// set
				changeValue += '_' + spec.defValue;
				await callInstance('setValue', spec.defValue, 'source1');
				assert.areSame('1__' + changeValue, await callInstance('getChanges1'));
				assert.areSame('2__' + changeValue, await callInstance('getChanges2'));
				assert.areSame('undefined', await callInstance('getSourced1'));
				assert.areSame('undefined_' + spec.defValue, await callInstance('getSourced2'));

				// clear
				changeValue += '_' + spec.emptyValue;
				await callInstance('clear');
				assert.areSame('1__' + changeValue, await callInstance('getChanges1'));
				assert.areSame('2__' + changeValue, await callInstance('getChanges2'));
				assert.areSame('undefined_' + spec.emptyValue, await callInstance('getSourced1'));
				assert.areSame('undefined_' + spec.defValue + '_' + spec.emptyValue, await callInstance('getSourced2'));

				// set
				changeValue += '_' + spec.defValue2;
				await callInstance('setValue', spec.defValue2, 'source2');
				assert.areSame('1__' + changeValue, await callInstance('getChanges1'));
				assert.areSame('2__' + changeValue, await callInstance('getChanges2'));
				assert.areSame(
					'undefined_' + spec.emptyValue + '_' + spec.defValue2,
					await callInstance('getSourced1')
				);
				assert.areSame(
					'undefined_' + spec.defValue + '_' + spec.emptyValue,
					await callInstance('getSourced2')
				);

				// clear
				changeValue += '_' + spec.emptyValue;
				await callInstance('clear', 'source2');
				assert.areSame('1__' + changeValue, await callInstance('getChanges1'));
				assert.areSame('2__' + changeValue, await callInstance('getChanges2'));
				assert.areSame(
					'undefined_' + spec.emptyValue + '_' + spec.defValue2 + '_' + spec.emptyValue,
					await callInstance('getSourced1')
				);
				assert.areSame(
					'undefined_' + spec.defValue + '_' + spec.emptyValue,
					await callInstance('getSourced2')
				);
			},
			clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await callInstance('clear');
				assert.areSame(spec.emptyValue, await callInstance('getValue')); // assert.isNull(await callInstance('getValue'));
				await spec.validate.emptyValue(expect, assert, page, callInstance, callContainer, locator);
				assert.areSame('1__' + spec.clearChanges[0], await callInstance('getChanges1'));
				assert.areSame('2__' + spec.clearChanges[0], await callInstance('getChanges2'));

				// clear again
				await callInstance('clear');
				assert.areSame(spec.emptyValue, await callInstance('getValue')); // assert.isNull(await callInstance('getValue'));
				await spec.validate.emptyValue(expect, assert, page, callInstance, callContainer, locator);
				assert.areSame('1__' + spec.clearChanges[0], await callInstance('getChanges1'));
				assert.areSame('2__' + spec.clearChanges[0], await callInstance('getChanges2'));
			},
			errors: async (expect, assert, page, callInstance, callContainer, locator)=>{
				{
					var errors = await callInstance('getErrors');
					assert.areSame({}, errors);
					var isValid = await callInstance('isValid');
					assert.isTrue(isValid);
				}
				{
					await page.evaluate(()=>{
						window.item.addValidation('test', ()=>false);
					});
					
					var errorsBefore = await callInstance('getErrors');
					assert.areSame({}, errorsBefore);

					var isValid = await callInstance('isValid');
					assert.isFalse(isValid);

					var errorsAfter = await callInstance('getErrors');
					assert.areSame({}, errorsAfter);
				}
				{
					await page.evaluate(()=>{
						window.item.addValidation('test', (instance)=>{
							instance.addError('testingError', {param: 'value'});
							return false;
						});
					});
					
					var errorsBefore = await callInstance('getErrors');
					assert.areSame({}, errorsBefore);

					var isValid = await callInstance('isValid');
					assert.isFalse(isValid);

					var errorsAfter = await callInstance('getErrors');
					assert.areSame({"testingError":{"param":"value"}}, errorsAfter);
				}
				{
					await callInstance('clearErrors');
					var errors = await callInstance('getErrors');
					assert.areSame({}, errors);
				}
				{
					// automatic clear after value change
					var isValid = await callInstance('isValid');
					assert.isFalse(isValid);

					await callInstance('setValue', spec.defValue);
					var errors = await callInstance('getErrors');
					assert.areSame({}, errors);
				}
			}
		}
	});
	singleTests.push({
		type: 'test',
		name: 'With name',
		conf: {
			type: spec.type,
			name: 'text-input'
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();

			assert.areSame('text-input', await callInstance('getName'));
			await spec.validate.parameter(expect, assert, page, callInstance, callContainer, locator, 'name', 'text-input');
		}
	});

	singleTests.push({
		type: 'test',
		name: 'With title',
		conf: {
			type: spec.type,
			title: 'Input title'
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();

			assert.areSame('Input title', await callInstance('getTitle'));
		}
	});

	singleTests.push({
		type: 'test',
		name: 'With tooltip',
		conf: {
			type: spec.type,
			tooltip: 'Input tooltip'
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();

			assert.isNull(await callInstance('getTitle'));
			await spec.validate.parameter(expect, assert, page, callInstance, callContainer, locator, 'title', 'Input tooltip');
		}
	});
	singleTests.push({
		type: 'test',
		name: 'Default value',
		conf: {
			type: spec.type,
			value: spec.defValue
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame(spec.defValue, await callInstance('getValue'));
			await spec.validate.valueMatch(expect, assert, page, callInstance, callContainer, locator, spec.defValue);
		},
	});
	singleTests.push({
		type: 'test',
		name: 'Disabled',
		conf: {
			type: spec.type,
			disabled: true
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.isFalse(await callInstance('isExcluded'));
			assert.isFalse(await callInstance('isRequired'));
			assert.isTrue(await callInstance('isDisabled'));

			await spec.validate.isDisabled(expect, assert, page, callInstance, callContainer, locator);
			assert.isTrue(await callInstance('isValid'));

			await callInstance('setDisabled', false);
			await spec.validate.isEnabled(expect, assert, page, callInstance, callContainer, locator);
			assert.isTrue(await callInstance('isValid'));
		},
	});
	singleTests.push({
		type: 'test',
		name: 'Excluded',
		conf: {
			type: spec.type,
			exclude: true
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.isTrue(await callInstance('isExcluded'));
			assert.isFalse(await callInstance('isRequired'));
			assert.isFalse(await callInstance('isDisabled'));

			await spec.validate.isEnabled(expect, assert, page, callInstance, callContainer, locator);
			assert.isTrue(await callInstance('isValid'));

			await callInstance('setExcluded', false);
			await spec.validate.isEnabled(expect, assert, page, callInstance, callContainer, locator);
			assert.isTrue(await callInstance('isValid'));
		},
	});
	singleTests.push({
		type: 'test',
		name: 'Not editable',
		conf: {
			type: spec.type,
			editable: false
		},
		verify: {
			base: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isTrue(await callInstance('isExcluded'));
				assert.isFalse(await callInstance('isRequired'));
				assert.isFalse(await callInstance('isDisabled'));
				assert.isTrue(await callInstance('isValid'));
				await spec.validate.notEditableContainer(expect, assert, page, callInstance, callContainer, locator);
				
				await page.evaluate(()=>{
					window.item.addValidation('test', ()=>false);
				});
				assert.isTrue(await callInstance('isValid'));

				await callInstance('setExcluded', false);
				assert.isTrue(await callInstance('isExcluded'));

			},
			setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame(spec.emptyValue, await callInstance('getValue')); // assert.isNull(await callInstance('getValue'));
				await spec.validate.notEditableEmptyValue(expect, assert, page, callInstance, callContainer, locator);

				await callInstance('setValue', spec.defValue);
				assert.areSame(spec.defValue, await callInstance('getValue'));
				await spec.validate.notEditableValueMatch(expect, assert, page, callInstance, callContainer, locator, spec.defValue);
			}
		}
	});
	/*singleTests.push({
		type: 'test',
		name: 'Title',
		conf: {
			type: spec.type,
			title: 'Testing input'
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await spec.validate.parameter(expect, assert, page, callInstance, callContainer, locator, 'title', 'Testing input');
		},
	});*/
	singleTests.push({
		type: 'test',
		name: 'Custom parameter: class',
		conf: {
			type: spec.type,
			class: 'class1 class2 class3'
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await spec.validate.parameter(expect, assert, page, callInstance, callContainer, locator, 'class', 'class1 class2 class3');
		},
	});
	singleTests.push({
		type: 'test',
		name: 'Custom parameter: data-x',
		conf: {
			type: spec.type,
			'data-a': 'A',
			'data-b': 'B'
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await spec.validate.parameter(expect, assert, page, callInstance, callContainer, locator, 'data-a', 'A');
			await spec.validate.parameter(expect, assert, page, callInstance, callContainer, locator, 'data-b', 'B');
		},
	});
	spec.specificTests.forEach((t)=>{
		singleTests.push(t);
	});
	/*
	{
				type: 'group',
				name: 'FULL',
				tests: [
					{
						type: 'test',
						name: 'Editable',
						conf: {
							type: spec.type,
							name: 'full-input',
							'data-a': 'A',
							'data-b': 'B',
							class: 'class1 class2 class3',
							title: 'Testing input',
							placeholder: 'Write something',
							required: true,
							value: 'Some value',
							editable: true,
	      // reverted
							excluded: false,
							disabled: false
						}
					},
					{
						type: 'test',
						name: 'Not editable',
						conf: {
							type: spec.type,
							name: 'full-input',
							'data-a': 'A',
							'data-b': 'B',
							class: 'class1 class2 class3',
							title: 'Testing input',
							placeholder: 'Write something',
							required: true,
							value: 'Some value',
							editable: false,
	          // reverted
							excluded: false,
							disabled: false
						}
					}
				]
			}
	*/

	baseInputConf.push({
		type: 'group',
		name: spec.name,
		tests: singleTests
	});
});

require('../spec.js')({
	name: 'Inputs: Base Inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: baseInputConf
});