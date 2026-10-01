const validateSelect = {
	option: async(expect, assert, text, item)=>{
		await expect(text).toHaveClass('toti-select-option');
		await expect(text).toHaveText(item.name);

		await expect(text).toHaveAttribute('data-disabled', item.isDisabled + '');
		await expect(text).toHaveAttribute('data-selectable', item.canSelect + '');

		if (!item.canSelect) {
			await expect(text).toHaveClass('toti-select-option');
		}
		if (item.hasOwnProperty('selected') && item.selected) {
			await expect(text).toHaveAttribute('data-selected', 'true');
		} else {
			await expect(text).toHaveAttribute('data-selected', 'false');
		}
	},
	options: async(expect, assert, options, values, isChild = false)=>{
		var list = await options.locator('> div');
		await expect(list).toHaveCount(values.length);
		for (var index = 0; index < values.length; index++) {
			var item = values[index];

			var option = await list.nth(index);
			await expect(option).toHaveAttribute('visible', item.visible + '');
			if (item.hidden) { // hidden by parent
				await expect(option).toBeHidden();
			} else {
				await expect(option).toBeVisible();
			}

			if (isChild) {
				await expect(option).toHaveClass('toti-select-child');
			} else {
				await expect(option).not.toHaveClass('toti-select-child');
			}

			if (item.isGroup) {
				var group = await option.locator('> div');
				await expect(group).toHaveClass('toti-select-group');
				var groupParts = await group.locator('> div');

				var legend = groupParts.nth(0);
				await expect(legend).toHaveClass('toti-select-group-legend');
				var legends = await legend.locator('> div')
				await validateSelect.option(expect, assert, await legends.nth(0), item);
				
				var images = await legends.nth(1);
				await expect(images).toHaveClass('toti-select-group-control');
				await expect(images.locator('> img')).toHaveCount(2);


				var sub = await groupParts.nth(1);
				await expect(sub).toHaveClass('toti-select-childs');
				await validateSelect.options(expect, assert, sub, item.childs, true);
			} else {
				await validateSelect.option(expect, assert, await option.locator('> div'), item);
			}
		}
	}
}
const validateRadio = {
	option: async(expect, assert, container, item, name)=>{
		await expect(container).toBeVisible();

		await expect(container).toHaveCount(1);
		if (!item.canSelect) {
			await expect(container).toHaveText(item.name);
			return;
		}
		var option = await container.locator('> div');
		var input = await option.locator('> input');
		await expect(input).toHaveCount(1);
		await expect(input).toBeVisible();
		await expect(input).toHaveValue(item.value);
		await expect(input).toHaveAttribute('name', name);

		var label = await option.locator('> label');
		await expect(label).toHaveCount(1);
		await expect(label).toHaveText(item.name);
		await expect(label).toBeVisible();

		var id = await input.getAttribute('id');
		var forAttr = await label.getAttribute('for');
		assert.areSame(id, forAttr);

		if (item.isDisabled) {
			await expect(input).toBeDisabled();
		} else {
			await expect(input).toBeEnabled();
		}
		if (item.hasOwnProperty('selected') && item.selected) {
			await expect(input).toBeChecked();
		} else {
			await expect(input).not.toBeChecked();
		}
	},
	options: async(expect, assert, options, values, name)=>{
		var list = await options.locator('> div');
		await expect(list).toHaveCount(values.length);
		for (var index = 0; index < values.length; index++) {
			var item = values[index];

			var option = await list.nth(index);
			// no hide implemented
			await expect(option).toBeVisible();
			if (item.isGroup) {
				var group = await option.locator('> fieldset');

				var legend = await group.locator('> legend');
				await expect(legend).toHaveCount(1);
				await validateRadio.option(expect, assert, legend, item, name);

				await validateRadio.options(expect, assert, group, item.childs, name);
			} else {
				await validateRadio.option(expect, assert, option, item, name);
			}
		}
	}
}
var optionsInputSelect = [];
var optionsInputRadiolist = [];
var index = 0;
[
	{
		name: 'Basic',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'a',
						title: 'A'
					},
					{
						value: 'b',
						title: 'B'
					},
					{
						value: 'c',
						title: 'C'
					},
					{
						value: 'd',
						title: 'D',
						disabled: true
					},
					{
						value: 'e',
						title: 'E',
						selectable: false
					}
				]
			};
		},
		radiolist: {
			base: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('SPAN', await callContainer('tagName'));
				await expect(locator).toBeEnabled();
				await expect(locator).toBeVisible();
				await expect(locator).toBeAttached();
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));
			},
			setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isNull(await callInstance('getValue'));

				// correct value
				await callInstance('setValue', 'a');
				assert.areSame('a', await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));

				// wrong value
				await callInstance('setValue', 'x');
				assert.isNull(await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));

				// disabled value
				await callInstance('setValue', 'd');
				assert.areSame('d', await callInstance('getValue'));
				assert.isFalse(await callInstance('isValid'));
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, selected: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));

				// not selectable value
				await callInstance('setValue', 'e');
				assert.isNull(await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));
			},
			clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isNull(await callInstance('getValue'));

				// set value
				await callInstance('setValue', 'a');
				assert.areSame('a', await callInstance('getValue'));
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));

				// clear
				await callInstance('clear');
				assert.isNull(await callInstance('getValue'));

				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));
			},
			disable: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));

				// disable
				await callInstance('setDisabled', true);
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));

				// enable
				await callInstance('setDisabled', false);
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));
			},
			selectValueFromOptions: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));
				assert.isNull(await callInstance('getValue'));

				var input = await locator.locator('input').nth(0);
				await input.click();

				assert.areSame('a', await callInstance('getValue'));
				await validateRadio.options(expect, assert, locator, [
					{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
					{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'E', value: 'e', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				], await callInstance('getName'));
			},
			afterLoadCallback: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await page.evaluateHandle(()=>{
					function after(name, value) {
						return () =>{
							window[name] = value;
						};
					}
					window.item.afterOptionLoad(after('a', 'A'));
					window.item.afterOptionLoad(after('b', 'B'));
				});

				//await new Promise(resolve => setTimeout(resolve, 6000));
				
				assert.areSame('A-B', await page.evaluate(()=>{
					return window['a'] + '-' + window['b'];
				}));
			}
		},
		select: {
			base: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.areSame('DIV', await callContainer('tagName'));

				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();
				await expect(input).toBeVisible();
				await expect(input).toBeAttached();
				await expect(input).toHaveValue('');

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				await expect(options).toBeHidden();

				await validateSelect.options(expect, assert, options, [
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'D', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'E', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]);
			},
			setValue: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				var options = await locator.locator('.toti-select-options');

				await expect(input).toHaveValue('');
				assert.isNull(await callInstance('getValue'));

				// correct value
				await callInstance('setValue', 'a');
				await expect(input).toHaveValue('A');
				await expect(input).not.toHaveClass('toti-select-disabled');
				assert.areSame('a', await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));

				// wrong value
				await callInstance('setValue', 'x');
				await expect(input).toHaveValue('');
				await expect(input).not.toHaveClass('toti-select-disabled');
				assert.isNull(await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));

				// set disabled
				await callInstance('setValue', 'd');
				await expect(input).toHaveValue('D');
				assert.areSame('d', await callInstance('getValue'));
				await expect(input).toHaveClass(/toti-select-disabled/);
				assert.isFalse(await callInstance('isValid'));

				// not selectable
				await callInstance('setValue', 'e');
				await expect(input).toHaveValue('');
				assert.isNull(await callInstance('getValue'));
				await expect(input).not.toHaveClass('toti-select-disabled');
				assert.isTrue(await callInstance('isValid'));

			},
			clear: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				var options = await locator.locator('.toti-select-options');

				await expect(input).toHaveValue('');
				assert.isNull(await callInstance('getValue'));

				// set value
				await callInstance('setValue', 'a');
				await expect(input).toHaveValue('A');
				assert.areSame('a', await callInstance('getValue'));

				// clear
				await callInstance('clear');
				await expect(input).toHaveValue('');
				assert.isNull(await callInstance('getValue'));
			},
			expandOptions: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				await expect(options).toBeHidden();

				await input.click();
				await expect(options).toBeVisible();
				await validateSelect.options(expect, assert, options, [
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'D', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: false, childs: []},
					{name: 'E', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: []}
				]);

				await input.click();
				await expect(options).toBeHidden();
				await validateSelect.options(expect, assert, options, [
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'D', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'E', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]);
			},
			disable: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				await expect(options).toBeHidden();

				// disable
				await callInstance('setDisabled', true);
				await expect(input).toBeDisabled();

				// not possible perform: await input.click();
				await expect(options).toBeHidden();
				// not possible perform: await input.click();

				// enable
				await callInstance('setDisabled', false);
				await expect(input).toBeEnabled();
				await expect(options).toBeHidden();

				await input.click();
				await expect(options).toBeVisible();
			},
			selectValueFromOptions: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				var options = await locator.locator('.toti-select-options');

				var option = await options.locator('.toti-select-option').nth(1);
/* // not possible perform
				// click on hidden option
				await option.click();
				await expect(options).toBeHidden();
				await expect(input).toHaveValue('');
				assert.isNull(await callInstance('getValue'));
*/
				// expand options and click on option
				await input.click();
				await expect(options).toBeVisible();
				await option.click();
				await expect(options).toBeHidden();

				await expect(input).toHaveValue('B');
				assert.areSame('b', await callInstance('getValue'));

				await validateSelect.options(expect, assert, options, [
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'D', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'E', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]);
			},
			afterLoadCallback: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await page.evaluateHandle(()=>{
					function after(name, value) {
						return () =>{
							window[name] = value;
						};
					}
					window.item.afterOptionLoad(after('a', 'A'));
					window.item.afterOptionLoad(after('b', 'B'));
				});

				//await new Promise(resolve => setTimeout(resolve, 6000));
				
				assert.areSame('A-B', await page.evaluate(()=>{
					return window['a'] + '-' + window['b'];
				}));
			}
		}
	},
	{
		name: 'Special options',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'standartValue',
						title: 'Standart value'
					},
					{
						value: 'anotherValue',
						title: 'Another value'
					},
					{
						value: null,
						title: 'Null value'
					},
					{
						value: '',
						title: 'Empty'
					},
					{
						value: 'd',
						title: 'Disabled',
						disabled: true
					}
				]
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validateRadio.options(expect, assert, locator, [
				{name: 'Standart value', value: 'standartValue', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Another value', value: 'anotherValue', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Null value', value: 'null', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
				{name: 'Empty', value: '', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Disabled', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []}
			], await callInstance('getName'));
			assert.isNull(await callInstance('getValue'));
			// TODO

			await callInstance('setValue', 'standartValue');
			await validateRadio.options(expect, assert, locator, [
				{name: 'Standart value', value: 'standartValue', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
				{name: 'Another value', value: 'anotherValue', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Null value', value: 'null', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Empty', value: '', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Disabled', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []}
			], await callInstance('getName'));
			assert.areSame('standartValue', await callInstance('getValue'));

			await callInstance('setValue', '');
			await validateRadio.options(expect, assert, locator, [
				{name: 'Standart value', value: 'standartValue', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Another value', value: 'anotherValue', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Null value', value: 'null', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Empty', value: '', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
				{name: 'Disabled', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []}
			], await callInstance('getName'));
			assert.areSame('', await callInstance('getValue'));

			await callInstance('clear');
			await validateRadio.options(expect, assert, locator, [
				{name: 'Standart value', value: 'standartValue', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Another value', value: 'anotherValue', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Null value', value: 'null', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
				{name: 'Empty', value: '', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'Disabled', value: 'd', canSelect: true, isGroup: false, isDisabled: true, childs: []}
			], await callInstance('getName'));
			assert.isNull(await callInstance('getValue'));
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			var options = await locator.locator('.toti-select-options');
			await validateSelect.options(expect, assert, options, [
				{name: 'Standart value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Another value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Null value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
				{name: 'Empty', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Disabled', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
			]);
			assert.isNull(await callInstance('getValue'));
			await expect(input).toHaveValue('Null value');

			await callInstance('setValue', 'standartValue');
			await validateSelect.options(expect, assert, options, [
				{name: 'Standart value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
				{name: 'Another value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Null value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Empty', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Disabled', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
			]);
			assert.areSame('standartValue', await callInstance('getValue'));
			await expect(input).toHaveValue('Standart value');

			await callInstance('setValue', '');
			await validateSelect.options(expect, assert, options, [
				{name: 'Standart value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Another value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Null value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Empty', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
				{name: 'Disabled', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
			]);
			assert.areSame('', await callInstance('getValue'));
			await expect(input).toHaveValue('Empty');

			await callInstance('clear');
			await validateSelect.options(expect, assert, options, [
				{name: 'Standart value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Another value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Null value', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
				{name: 'Empty', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Disabled', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
			]);
			assert.isNull(await callInstance('getValue'));
			await expect(input).toHaveValue('Null value');
		}
	},
	{
		name: 'Disabled',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				disabled: true,
				options: [
					{
						value: 'a',
						title: 'A'
					},
					{
						value: 'b',
						title: 'B'
					},
					{
						value: 'c',
						title: 'C'
					}
				]
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validateRadio.options(expect, assert, locator, [
				{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: true, childs: []},
				{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: true, childs: []},
				{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: true, childs: []}
			], await callInstance('getName'));
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			await expect(input).toBeDisabled();

			var options = await locator.locator('.toti-select-options');
			await expect(options).toBeAttached();
			await expect(options).toBeHidden();

			// enable
			await callInstance('setDisabled', false);
			await expect(input).toBeEnabled();
			await expect(options).toBeHidden();

			await input.click();
			await expect(options).toBeVisible();
			// TODO use validateSelect?
		}
	},
	{
		name: 'Disabled with default value',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				disabled: true,
				options: [
					{
						value: 'a',
						title: 'A'
					},
					{
						value: 'b',
						title: 'B'
					},
					{
						value: 'c',
						title: 'C'
					}
				],
				value: 'c'
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validateRadio.options(expect, assert, locator, [
				{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: true, childs: []},
				{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: true, childs: []},
				{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: true, selected: true, childs: []}
			], await callInstance('getName'));
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			await expect(input).toBeDisabled();

			var options = await locator.locator('.toti-select-options');
			await expect(options).toBeAttached();
			await expect(options).toBeHidden();
			
			// check value
			await expect(input).toHaveValue('C');
			assert.areSame('c', await callInstance('getValue'));

			// enable
			await callInstance('setDisabled', false);
			await expect(input).toBeEnabled();
			await expect(options).toBeHidden();

			await input.click();
			await expect(options).toBeVisible();

			// check value
			await expect(input).toHaveValue('C');
			assert.areSame('c', await callInstance('getValue'));
			// TODO use validateSelect?
		}
	},
	{
		name: 'Not editable',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				editable: false,
				value: 'a',
				options: [
					{
						value: 'a',
						title: 'A'
					},
					{
						value: 'b',
						title: 'B'
					},
					{
						value: 'c',
						title: 'C'
					}
				]
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('SPAN', await callContainer('tagName'));
			expect(locator).toBeAttached();
			expect(locator).toBeVisible();
			expect(locator).toHaveText('A');
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('SPAN', await callContainer('tagName'));
			expect(locator).toBeAttached();
			expect(locator).toBeVisible();
			expect(locator).toHaveText('A');
		}
	},
	{
		name: 'Prompt',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'a',
						title: 'A'
					},
					{
						value: 'b',
						title: 'B'
					}
				],
				prompt: 'Please select value'
			};
		},
		/*radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// radiolist not support promt
		},*/
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			await expect(input).toBeEnabled();

			var options = await locator.locator('.toti-select-options');
			await expect(options).toBeAttached();
			await expect(options).toBeHidden();
			
			await expect(input).toHaveAttribute('placeholder', 'Please select value');
			await expect(input).toHaveValue('');
			assert.isNull(await callInstance('getValue'));
		}
	},
	{
		name: 'Default Value',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'a',
						title: 'A',
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
						title: 'B'
					}
				],
				value: 'a1'
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('a1', await callInstance('getValue'));
			assert.isTrue(await callInstance('isValid'));

			await validateRadio.options(expect, assert, locator, [
				{name: 'A', value: 'a', canSelect: true, isGroup: true, isDisabled: false, childs: [
					{name: 'A1', value: 'a1', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
					{name: 'A2', value: 'a2', canSelect: true, isGroup: false, isDisabled: false, childs: []}
				]},
				{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []}
			], await callInstance('getName'));
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			await expect(input).toBeEnabled();

			var options = await locator.locator('.toti-select-options');
			await expect(options).toBeAttached();
			await expect(options).toBeHidden();
			
			await expect(input).toHaveValue('A1');
			assert.areSame('a1', await callInstance('getValue'));
			assert.isTrue(await callInstance('isValid'));

			await validateSelect.options(expect, assert, options, [
				{name: 'A', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]},
				{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
			]);

			input.click();
			await validateSelect.options(expect, assert, options, [
				{name: 'A', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: false, childs: [
					{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, selected: true, childs: []},
					{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
				]},
				{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []}
			]);
		}
	},
	{
		name: 'Search',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'aa',
						title: 'AA'
					},
					{
						value: 'bb',
						title: 'BB'
					},
					{
						value: 'ab',
						title: 'AB'
					},
					{
						value: 'aabb',
						title: 'AABB'
					},
					{
						value: 'abb',
						title: 'ABB',
						disabled: true

					},
					{
						value: 'aab',
						title: 'AAB',
						selectable: false

					}
				],
				search: true
			};
		},
		/*radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// radiolist not support search
		},*/
		select: {
			withDifferentInput: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				await expect(options).toBeHidden();
				await validateSelect.options(expect, assert, options, [
					{name: 'AA', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'BB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'AB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'AABB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'ABB', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'AAB', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]);

				await input.pressSequentially('AA');

				await expect(options).toBeVisible();
				await validateSelect.options(expect, assert, options, [
					{name: 'AA', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'BB', visible: false, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'AB', visible: false, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'AABB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'ABB', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'AAB', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: []}
				]);

				await input.fill(''); // clear
				await input.pressSequentially('BB');

				await expect(options).toBeVisible();
				await validateSelect.options(expect, assert, options, [
					{name: 'AA', visible: false, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'BB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'AB', visible: false, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'AABB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'ABB', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: false, childs: []},
					{name: 'AAB', visible: false, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]);
			},
			fillAndClickOutside: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				await expect(options).toBeHidden();
				assert.isNull(await callInstance('getValue'));
				// not working - document.onclick is missing in test
/*
				// fill unfinished text
				await input.pressSequentially('a');
				await expect(options).toBeVisible();

				await page.locator('body').click();
				await expect(options).toBeHidden();
				assert.isNull(await callInstance('getValue'));
*/
			},
			searchAndSelectAnother: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();

				// write something
				await input.pressSequentially('AA');
				// select something not from listed
				await callInstance('setValue', 'ab');
				await expect(input).toHaveValue('AB');
				assert.isTrue(await callInstance('isValid'));
			}
		}
	},
	{
		name: 'Def value is disabled',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'a',
						title: 'A'
					},
					{
						value: 'b',
						title: 'B'
					},
					{
						value: 'c',
						title: 'C'
					},
					{
						value: 'd',
						title: 'D',
						disabled: true
					}
				],
				value: 'd'
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validateRadio.options(expect, assert, locator, [
				{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'B', value: 'b', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'C', value: 'c', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'D', value: 'd', canSelect: true, isGroup: false, isDisabled: true, selected: true, childs: []}
			], await callInstance('getName'));
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			await expect(input).toBeEnabled();

			var options = await locator.locator('.toti-select-options');
			await expect(options).toBeAttached();
			await expect(options).toBeHidden();
			
			await expect(input).toHaveValue('D');
			assert.areSame('d', await callInstance('getValue'));
			assert.isFalse(await callInstance('isValid'));
		}
	},
	{
		name: 'Structured options',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'a',
						title: 'A',
						disabled: false
					},
					{
						value: 'b',
						title: 'B',
						disabled: true,
						options: [
							{
								value: 'b1',
								title: 'B1'
							},
							{
								value: 'b2',
								title: 'B2',
								selectable: true
							},
							{
								value: 'b3',
								title: 'B3',
								selectable: false
							}
						]
					},
					{
						value: 'c',
						title: 'C',
						selectable: false,
						options: [
							{
								value: 'c1',
								title: 'C1',
								disabled: true
							},
							{
								value: 'c2',
								title: 'C2',
								options: [
									{
										value: 'c21',
										title: 'C21',
										disabled: true
									},
									{
										value: 'c22',
										title: 'C22',
									}
								]
							}
						]
					}
				]
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validateRadio.options(expect, assert, locator, [
				{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'B', value: 'b', canSelect: true, isGroup: true, isDisabled: true, childs: [
					{name: 'B1', value: 'b1', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'B2', value: 'b2', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'B3', value: 'b3', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				]},
				{name: 'C', value: '', canSelect: false, isGroup: true, isDisabled: false, childs: [
					{name: 'C1', value: 'c1', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'C2', value: 'c2', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'C21', value: 'c21', canSelect: true, isGroup: false, isDisabled: true, childs: []},
						{name: 'C22', value: 'c22', canSelect: true, isGroup: false, isDisabled: false, childs: []}
					]}
				]}
			], await callInstance('getName'));
		},
		select: {
			data: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				await expect(options).toBeHidden();

				await validateSelect.options(expect, assert, options, [
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B3', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'C', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'C1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'C2', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
							{name: 'C21', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
							{name: 'C22', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
						]}
					]}
				]);
			},
			showHideGroup: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				
				await input.click();
				await expect(options).toBeVisible();

				async function validateOptions(cHidden, c2Hidden) {
					return validateSelect.options(expect, assert, options, [
						{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
						{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: false, childs: [
							{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: false, childs: []},
							{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: false, childs: []},
							{name: 'B3', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: []}
						]},
						{name: 'C', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: [
							{name: 'C1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: cHidden, childs: []},
							{name: 'C2', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: cHidden, childs: [
								{name: 'C21', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: c2Hidden, childs: []},
								{name: 'C22', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: c2Hidden, childs: []}
							]}
						]}
					]);
				}

				var cOption = await options.locator('> div').nth(2);
				var cControl = await cOption.locator('> div > .toti-select-group-legend .toti-select-group-control > img');
				var cChilds = await cOption.locator('> div > .toti-select-childs');
				await expect(cControl).toHaveCount(2);
				var cShow = await cControl.nth(0);
				var cHide = await cControl.nth(1);

				var c2Option = await cOption.locator('> div > .toti-select-childs > div').nth(1);
				var c2Control = await c2Option.locator('> div > .toti-select-group-legend .toti-select-group-control > img');
				var c2Childs = await c2Option.locator('> div > .toti-select-childs');
				await expect(c2Control).toHaveCount(2);
				var c2Show = await c2Control.nth(0);
				var c2Hide = await c2Control.nth(1);


				await expect(options).toBeVisible();

				await expect(cShow).toBeHidden();
				await expect(cHide).toBeVisible();
				await expect(cChilds).toBeVisible();

				await expect(c2Show).toBeHidden();
				await expect(c2Hide).toBeVisible();
				await expect(c2Childs).toBeVisible();

				await validateOptions(false, false);

				/* hide and show c2*/

				// hide c2
				c2Hide.click();

				await expect(options).toBeVisible();

				await expect(cShow).toBeHidden();
				await expect(cHide).toBeVisible();
				await expect(cChilds).toBeVisible();

				await expect(c2Show).toBeVisible();
				await expect(c2Hide).toBeHidden();
				await expect(c2Childs).toBeHidden();

				await validateOptions(false, true);

				// show c2
				c2Show.click();
				await expect(options).toBeVisible();
				
				await expect(cShow).toBeHidden();
				await expect(cHide).toBeVisible();
				await expect(cChilds).toBeVisible();

				await expect(c2Show).toBeHidden();
				await expect(c2Hide).toBeVisible();
				await expect(c2Childs).toBeVisible();

				await validateOptions(false, false);

				/* hide c2 then hide and show c */

				// hide c2
				c2Hide.click();

				await expect(options).toBeVisible();
				
				await expect(cShow).toBeHidden();
				await expect(cHide).toBeVisible();
				await expect(cChilds).toBeVisible();

				await expect(c2Show).toBeVisible();
				await expect(c2Hide).toBeHidden();
				await expect(c2Childs).toBeHidden();

				await validateOptions(false, true);

				// hide c
				cHide.click();

				await expect(options).toBeVisible();
				
				await expect(cShow).toBeVisible();
				await expect(cHide).toBeHidden();
				await expect(cChilds).toBeHidden();

				await expect(c2Show).toBeHidden();
				await expect(c2Hide).toBeHidden();
				await expect(c2Childs).toBeHidden();

				await validateOptions(true, true);

				// show c
				cShow.click();

				await expect(options).toBeVisible();
				
				await expect(cShow).toBeHidden();
				await expect(cHide).toBeVisible();
				await expect(cChilds).toBeVisible();

				await expect(c2Show).toBeVisible();
				await expect(c2Hide).toBeHidden();
				await expect(c2Childs).toBeHidden();

				await validateOptions(false, true);

				/* show c2 then hide and show c*/

				// show c2
				c2Show.click();

				await expect(options).toBeVisible();
				
				await expect(cShow).toBeHidden();
				await expect(cHide).toBeVisible();
				await expect(cChilds).toBeVisible();

				await expect(c2Show).toBeHidden();
				await expect(c2Hide).toBeVisible();
				await expect(c2Childs).toBeVisible();

				await validateOptions(false, false);

				// hide c
				cHide.click();

				await expect(options).toBeVisible();
				
				await expect(cShow).toBeVisible();
				await expect(cHide).toBeHidden();
				await expect(cChilds).toBeHidden();

				await expect(c2Show).toBeHidden();
				await expect(c2Hide).toBeHidden();
				await expect(c2Childs).toBeHidden();

				await validateOptions(true, true);

				// show c
				cShow.click();

				await expect(options).toBeVisible();
				
				await expect(cShow).toBeHidden();
				await expect(cHide).toBeVisible();
				await expect(cChilds).toBeVisible();

				await expect(c2Show).toBeHidden();
				await expect(c2Hide).toBeVisible();
				await expect(c2Childs).toBeVisible();

				await validateOptions(false, false);
			}
		}
	},
	{
		name: 'Load',
		// default value for load makes no sence - content is unknown before load
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [],
				load: {
					url: '/inputs/options',
					method: 'get',
					params: {}
				},
				onChange: ['onSelectLoad1Change']
			};
		},
		radiolist: {
			afterLoadCallback: async (expect, assert, page, callInstance, callContainer, locator)=>{
				/*await page.evaluateHandle(()=>{
					function after(name, value) {
						return () =>{
							window[name] = value;
						};
					}
					window.item.afterOptionLoad(after('a', 'A'));
					window.item.afterOptionLoad(after('b', 'B'));
				});
				assert.areSame('undefined-undefined', await page.evaluate(()=>{
					return window['a'] + '-' + window['b'];
				}));*/
				assert.areSame('undefined_null', await callInstance('getCount'));

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));
				
				/*assert.areSame('A-B', await page.evaluate(()=>{
					return window['a'] + '-' + window['b'];
				}));*/
				assert.areSame('undefined_null', await callInstance('getCount'));
			},
			data: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await validateRadio.options(expect, assert, locator, []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));
				await validateRadio.options(expect, assert, locator, [
					{name: 'X', value: 'x', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'Y', value: 'y', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'Y1', value: 'y1', canSelect: true, isGroup: false, isDisabled: false, childs: []},
						{name: 'Y2', value: 'y2', canSelect: true, isGroup: false, isDisabled: false, childs: []},
						{name: 'Y3', value: 'y3', canSelect: false, isGroup: true, isDisabled: false, childs: []}
					]},
					{name: 'C', value: 'c', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'C4', value: 'c4', canSelect: true, isGroup: false, isDisabled: true, childs: []},
						{name: 'C5', value: 'c5', canSelect: true, isGroup: true, isDisabled: false, childs: [
							{name: 'C51', value: 'c51', canSelect: true, isGroup: false, isDisabled: true, childs: []},
							{name: 'C52', value: 'c52', canSelect: true, isGroup: false, isDisabled: false, childs: []}
						]},
						{name: 'C2', value: 'c2', canSelect: true, isGroup: true, isDisabled: false, childs: [
							{name: 'C23', value: 'c23', canSelect: true, isGroup: false, isDisabled: true, childs: []}
						]}
					]},
					{name: 'CZ', value: 'cz', canSelect: false, isGroup: true, isDisabled: false, childs: [
						{name: 'CA', value: 'ca', canSelect: true, isGroup: false, isDisabled: true, childs: []},
						{name: 'CB', value: 'cb', canSelect: true, isGroup: false, isDisabled: false, childs: []}
					]},
				], await callInstance('getName'));
			}
		},
		select: {
			afterLoadCallback: async (expect, assert, page, callInstance, callContainer, locator)=>{
				/*await page.evaluateHandle(()=>{
					function after(name, value) {
						return () =>{
							window[name] = value;
						};
					}
					window.item.afterOptionLoad(after('a', 'A'));
					window.item.afterOptionLoad(after('b', 'B'));
				});
				assert.areSame('undefined-undefined', await page.evaluate(()=>{
					return window['a'] + '-' + window['b'];
				}));*/
				assert.areSame('undefined_null', await callInstance('getCount'));

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));
				
				/*assert.areSame('A-B', await page.evaluate(()=>{
					return window['a'] + '-' + window['b'];
				}));*/
				assert.areSame('undefined_null', await callInstance('getCount'));
			},
			data: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var input = await locator.locator('.toti-select-input');
				await expect(input).toBeEnabled();

				var options = await locator.locator('.toti-select-options');
				await expect(options).toBeAttached();
				await expect(options).toBeHidden();

				await validateSelect.options(expect, assert, options, []);

				// sleep
				await new Promise(resolve => setTimeout(resolve, 6000));
				
				await validateSelect.options(expect, assert, options, [
					{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'Y3', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'C', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'C4', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'C5', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
							{name: 'C51', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
							{name: 'C52', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
						]},
						{name: 'C2', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
							{name: 'C23', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
						]}
					]},
					{name: 'CZ', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'CA', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'CB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
				]);
			}
		}
	},
	{
		name: 'Load with default value',
		// default value for load makes no sence - content is unknown before load
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [],
				load: {
					url: '/inputs/options',
					method: 'get',
					params: {}
				},
				value: 'y1',
				onChange: ['onSelectLoad2Change']
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validateRadio.options(expect, assert, locator, []);
			assert.areSame('undefined_null', await callInstance('getCount'));

				// sleep
			await new Promise(resolve => setTimeout(resolve, 6000));
			await validateRadio.options(expect, assert, locator, [
				{name: 'X', value: 'x', canSelect: true, isGroup: false, isDisabled: true, childs: []},
				{name: 'Y', value: 'y', canSelect: true, isGroup: true, isDisabled: false, childs: [
					{name: 'Y1', value: 'y1', canSelect: true, isGroup: false, isDisabled: false, selected: true, childs: []},
					{name: 'Y2', value: 'y2', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'Y3', value: 'y3', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				]},
				{name: 'C', value: 'c', canSelect: true, isGroup: true, isDisabled: false, childs: [
					{name: 'C4', value: 'c4', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'C5', value: 'c5', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'C51', value: 'c51', canSelect: true, isGroup: false, isDisabled: true, childs: []},
						{name: 'C52', value: 'c52', canSelect: true, isGroup: false, isDisabled: false, childs: []}
					]},
					{name: 'C2', value: 'c2', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'C23', value: 'c23', canSelect: true, isGroup: false, isDisabled: true, childs: []}
					]}
				]},
				{name: 'CZ', value: 'cz', canSelect: false, isGroup: true, isDisabled: false, childs: [
					{name: 'CA', value: 'ca', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'CB', value: 'cb', canSelect: true, isGroup: false, isDisabled: false, childs: []}
				]},
			], await callInstance('getName'));
			assert.areSame('undefined_null_y1', await callInstance('getCount'));
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			await expect(input).toBeEnabled();

			var options = await locator.locator('.toti-select-options');
			await expect(options).toBeAttached();
			await expect(options).toBeHidden();

			await expect(input).toHaveValue('');
			await validateSelect.options(expect, assert, options, []);
			assert.areSame('undefined_null', await callInstance('getCount'));

				// sleep
			await new Promise(resolve => setTimeout(resolve, 6000));

			await expect(input).toHaveValue('Y1');
			await validateSelect.options(expect, assert, options, [
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'C', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'C4', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'C5', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'C51', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'C52', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'C2', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'C23', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]}
				]},
				{name: 'CZ', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'CA', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'CB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
				]},
			]);
			assert.areSame('undefined_null_y1', await callInstance('getCount'));
		}
	},
	{
		name: 'Options with load',
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_' + index++,
				options: [
					{
						value: 'a',
						title: 'A',
						disabled: false
					},
					{
						value: 'b',
						title: 'B',
						disabled: true,
						options: [
							{
								value: 'b1',
								title: 'B1'
							},
							{
								value: 'b2',
								title: 'B2',
								selectable: true
							},
							{
								value: 'b3',
								title: 'B3',
								selectable: false
							}
						]
					},
					{
						value: 'c',
						title: 'C',
						selectable: false,
						options: [
							{
								value: 'c1',
								title: 'C1',
								disabled: true
							},
							{
								value: 'c2',
								title: 'C2',
								options: [
									{
										value: 'c21',
										title: 'C21',
										disabled: true
									},
									{
										value: 'c22',
										title: 'C22',
									}
								]
							}
						]
					}
				],
				load: {
					url: '/inputs/options',
					method: 'get',
					params: {}
				}
			};
		},
		radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			await validateRadio.options(expect, assert, locator, [
				{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'B', value: 'b', canSelect: true, isGroup: true, isDisabled: true, childs: [
					{name: 'B1', value: 'b1', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'B2', value: 'b2', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'B3', value: 'b3', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				]},
				{name: 'C', value: '', canSelect: false, isGroup: true, isDisabled: false, childs: [
					{name: 'C1', value: 'c1', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'C2', value: 'c2', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'C21', value: 'c21', canSelect: true, isGroup: false, isDisabled: true, childs: []},
						{name: 'C22', value: 'c22', canSelect: true, isGroup: false, isDisabled: false, childs: []}
					]}
				]}
			], await callInstance('getName'));

			// sleep
			await new Promise(resolve => setTimeout(resolve, 6000));

			await validateRadio.options(expect, assert, locator, [
				{name: 'A', value: 'a', canSelect: true, isGroup: false, isDisabled: false, childs: []},
				{name: 'B', value: 'b', canSelect: true, isGroup: true, isDisabled: true, childs: [
					{name: 'B1', value: 'b1', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'B2', value: 'b2', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'B3', value: 'b3', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				]},
				{name: 'C', value: 'c', canSelect: false, isGroup: true, isDisabled: false, childs: [
					{name: 'C1', value: 'c1', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'C2', value: 'c2', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'C21', value: 'c21', canSelect: true, isGroup: false, isDisabled: true, childs: []},
						{name: 'C22', value: 'c22', canSelect: true, isGroup: false, isDisabled: false, childs: []},
						{name: 'C23', value: 'c23', canSelect: true, isGroup: false, isDisabled: true, childs: []}
					]},
					{name: 'C4', value: 'c4', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'C5', value: 'c5', canSelect: true, isGroup: true, isDisabled: false, childs: [
						{name: 'C51', value: 'c51', canSelect: true, isGroup: false, isDisabled: true, childs: []},
						{name: 'C52', value: 'c52', canSelect: true, isGroup: false, isDisabled: false, childs: []}
					]}
				]},
				{name: 'X', value: 'x', canSelect: true, isGroup: false, isDisabled: true, childs: []},
				{name: 'Y', value: 'y', canSelect: true, isGroup: true, isDisabled: false, childs: [
					{name: 'Y1', value: 'y1', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'Y2', value: 'y2', canSelect: true, isGroup: false, isDisabled: false, childs: []},
					{name: 'Y3', value: 'y3', canSelect: false, isGroup: true, isDisabled: false, childs: []}
				]},
				{name: 'CZ', value: 'cz', canSelect: false, isGroup: true, isDisabled: false, childs: [
					{name: 'CA', value: 'ca', canSelect: true, isGroup: false, isDisabled: true, childs: []},
					{name: 'CB', value: 'cb', canSelect: true, isGroup: false, isDisabled: false, childs: []}
				]},
			], await callInstance('getName'));
		},
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var input = await locator.locator('.toti-select-input');
			await expect(input).toBeEnabled();

			var options = await locator.locator('.toti-select-options');
			await expect(options).toBeAttached();
			await expect(options).toBeHidden();
			
			await validateSelect.options(expect, assert, options, [
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
					{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'B3', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'C', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'C1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'C2', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'C21', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'C22', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]}
				]}
			]);

			// sleep
			await new Promise(resolve => setTimeout(resolve, 6000));

			await validateSelect.options(expect, assert, options, [
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
					{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'B3', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'C', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'C1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'C2', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'C21', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'C22', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'C23', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
					{name: 'C4', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'C5', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'C51', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'C52', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]}
				]},
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'CZ', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'CA', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'CB', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
				]},
			]);
		}
	},
	{
		name: 'Depends',
		create: (conf)=>{
			var master = Toti.createInput(conf.master);
			delete conf.master;
			return createSelectMock(conf, master);
		},
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_depends',
				options: [
					{ value: 'x', title: 'X' },
					{ value: 'y', title: 'Y' },
					{ value: 'a', title: 'A', selectable: false, options: [
						{ value: 'a1', title: 'A1' },
						{ value: 'a2', title: 'A2' }
					]},
					{ value: 'b', title: 'B', options: [
						{ value: 'b1', title: 'B1' },
						{ value: 'b2', title: 'B2' }
					]}
				],
				depends: 'totiSelect-1',
				onChange: [
					// function is not supported by playwright
					/*function() {
						console.log('callback');
					},*/
					'onSelectDepends1Change'
				],
				master: {
					type: 'select',
					name: 'totiSelect',
					options: [
						{ value: null, title: '---' },
						{ value: 'a', title: 'A' },
						{ value: 'b', title: 'B' },
						{ value: 'c', title: 'C' }
					],
					unique: 'totiSelect-1',
					onChange: [ 'onSelectMaster1Change' ]
				}
			};
		},
		/*radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// radiolist not support depends
		},*/
		select: {
			initial: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var masterInput = await locator.locator('[name=totiSelect] .toti-select-input');
				await expect(masterInput).toBeAttached();
				var masterOptions = await locator.locator('[name=totiSelect] .toti-select-options');
				await expect(masterOptions).toBeAttached();

				var input = await locator.locator('[name=testInput_depends] .toti-select-input');
				await expect(input).toBeAttached();
				var options = await locator.locator('[name=testInput_depends] .toti-select-options');
				await expect(options).toBeAttached();

				assert.isNull(await callInstance('getMainValue'));
				await expect(input).toHaveValue('');
				assert.areSame('undefined_null', await callInstance('getMainCount'));
				await validateSelect.options(expect, assert, options, [
					{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
				]);
				assert.isNull(await callInstance('getParentValue'));
				await expect(masterInput).toHaveValue('---');
				assert.areSame('undefined_null', await callInstance('getParentCount'));
				await validateSelect.options(expect, assert, masterOptions, [
					{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]);
			},
			setChild: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var masterInput = await locator.locator('[name=totiSelect] .toti-select-input');
				await expect(masterInput).toBeAttached();
				var masterOptions = await locator.locator('[name=totiSelect] .toti-select-options');
				await expect(masterOptions).toBeAttached();

				var input = await locator.locator('[name=testInput_depends] .toti-select-input');
				await expect(input).toBeAttached();
				var options = await locator.locator('[name=testInput_depends] .toti-select-options');
				await expect(options).toBeAttached();

				assert.isTrue(await callInstance('isValid'));

				async function check(mainValue, mainTitle, mainCount, before, after, parentValue, parentTitle, parentCount, parent) {
					assert.areSame(mainValue, await callInstance('getMainValue'));
					await expect(input).toHaveValue(mainTitle);
					assert.areSame(mainCount, await callInstance('getMainCount'));
					assert.isTrue(await callInstance('isValid'));
					await expect(input).not.toHaveClass(/toti-select-disabled/);

					// visible until next resize
					await expect(options).toBeHidden();
					await validateSelect.options(expect, assert, options, before);

					await input.click(); // check state after displayed options
					await expect(options).toBeVisible();
					await validateSelect.options(expect, assert, options, after);
					await input.click(); 

					assert.areSame(parentValue, await callInstance('getParentValue'));
					await expect(masterInput).toHaveValue(parentTitle);
					assert.areSame(parentCount, await callInstance('getParentCount'));
					await validateSelect.options(expect, assert, masterOptions, parent);
				}


				// set
				await callInstance('setMainValue', 'a1');
				await check('a1', 'A1', 'undefined_null_a1', [
					{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
				], [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, selected: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []}
					]},
					{name: 'B', visible: false, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
				], 'a', 'A', 'undefined_null_a', [
					{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]);

				// set again - no callbacks
				await callInstance('setMainValue', 'a1');
				await check('a1', 'A1', 'undefined_null_a1', [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'B', visible: false, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
				], [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, selected: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []}
					]},
					{name: 'B', visible: false, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
				], 'a', 'A', 'undefined_null_a', [
					{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]);

				// set from same group
				await callInstance('setMainValue', 'a2');
				await check('a2', 'A2', 'undefined_null_a1_a2', [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []}
					]},
					{name: 'B', visible: false, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
				], [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, selected: true, childs: []}
					]},
					{name: 'B', visible: false, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
				], 'a', 'A', 'undefined_null_a', [
					{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]);

				// set another value
				await callInstance('setMainValue', 'b2');
				await check('b2', 'B2', 'undefined_null_a1_a2_b2', [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
					{name: 'B', visible: false, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []}
					]},
				], [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: false, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
					{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: false, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, selected: true, childs: []}
					]},
				], 'b', 'B', 'undefined_null_a_b', [
					{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]);

				// set null value
				await callInstance('clearMainValue');
				await check(null, '', 'undefined_null_a1_a2_b2_null', [
					{name: 'X', visible: false, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y', visible: false, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'A', visible: false, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
				], [
					{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'Y', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: false, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []}
					]},
					{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: false, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: false, childs: []}
					]},
				], null, '---', 'undefined_null_a_b_null', [
					{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]);
			},
			setParent: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var masterInput = await locator.locator('[name=totiSelect] .toti-select-input');
				await expect(masterInput).toBeAttached();
				var masterOptions = await locator.locator('[name=totiSelect] .toti-select-options');
				await expect(masterOptions).toBeAttached();

				var input = await locator.locator('[name=testInput_depends] .toti-select-input');
				await expect(input).toBeAttached();
				var options = await locator.locator('[name=testInput_depends] .toti-select-options');
				await expect(options).toBeAttached();

				// set
				await callInstance('setParentValue', 'a');

				assert.areSame('a', await callInstance('getParentValue'));
				await expect(masterInput).toHaveValue('A');
				assert.areSame('undefined_null_a', await callInstance('getParentCount'));
				await validateSelect.options(expect, assert, masterOptions, [
					{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'B', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'C', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]);

				assert.isNull(await callInstance('getMainValue'));
				await expect(input).toHaveValue('');
				assert.areSame('undefined_null', await callInstance('getMainCount'));
				await validateSelect.options(expect, assert, options, [
					{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'A', visible: true, canSelect: false, isGroup: true, isDisabled: false, hidden: true, childs: [
						{name: 'A1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
						{name: 'A2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
					]},
					{name: 'B', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
						{name: 'B1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
						{name: 'B2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
					]},
				]);
				await expect(input).not.toHaveClass(/toti-select-disabled/);
			},
			setParentAndChild: async (expect, assert, page, callInstance, callContainer, locator)=>{
				var masterInput = await locator.locator('[name=totiSelect] .toti-select-input');
				await expect(masterInput).toBeAttached();
				var masterOptions = await locator.locator('[name=totiSelect] .toti-select-options');
				await expect(masterOptions).toBeAttached();

				var input = await locator.locator('[name=testInput_depends] .toti-select-input');
				await expect(input).toBeAttached();
				var options = await locator.locator('[name=testInput_depends] .toti-select-options');
				await expect(options).toBeAttached();

				// set
				await callInstance('setParentValue', 'a');

				assert.areSame('a', await callInstance('getParentValue'));
				await expect(masterInput).toHaveValue('A');
				assert.areSame('undefined_null_a', await callInstance('getParentCount'));

				assert.isNull(await callInstance('getMainValue'));
				await expect(input).toHaveValue('');
				assert.areSame('undefined_null', await callInstance('getMainCount'));

				// set child with wrong value
				await callInstance('setMainValue', 'b1');

				assert.areSame('b', await callInstance('getParentValue'));
				await expect(masterInput).toHaveValue('B');
				assert.areSame('undefined_null_a_b', await callInstance('getParentCount'));

				assert.areSame('b1', await callInstance('getMainValue'));
				await expect(input).toHaveValue('B1');
				assert.areSame('undefined_null_b1', await callInstance('getMainCount'));
				assert.isTrue(await callInstance('isValid'));
			}
		}
	},
	{
		name: 'Depends on Load: def value',
		create: (conf)=>{
			var master = Toti.createInput(conf.master);
			delete conf.master;
			return createSelectMock(conf, master);
		},
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_depends_load',
				options: [
					{ value: 'x', title: 'X' },
					{ value: 'y', title: 'Y', options: [
						{ value: 'y1', title: 'Y1' },
						{ value: 'y2', title: 'Y2' },
						{ value: 'y3', title: 'Y3' }
					]},
					{ value: 'z', title: 'Z', options: [
						{ value: 'z1', title: 'Z1' },
						{ value: 'z2', title: 'Z2' },
						{ value: 'z3', title: 'Z3' }
					]},
					{ value: 'a', title: 'A' }
				],
				value: 'y1',
				depends: 'totiSelect-2',
				onChange: [ 'onSelectDepends2Change' ],
				master: {
					type: 'select',
					name: 'totiSelect',
					options: [],
					load: {
						url: '/inputs/depends',
						method: 'get',
						params: {}
					},
					unique: 'totiSelect-2',
					onChange: [ 'onSelectMaster2Change' ]
				}
			};
		},
		/*radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// radiolist not support depends
		},*/
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var masterInput = await locator.locator('[name=totiSelect] .toti-select-input');
			await expect(masterInput).toBeAttached();
			var masterOptions = await locator.locator('[name=totiSelect] .toti-select-options');
			await expect(masterOptions).toBeAttached();

			var input = await locator.locator('[name=testInput_depends_load] .toti-select-input');
			await expect(input).toBeAttached();
			var options = await locator.locator('[name=testInput_depends_load] .toti-select-options');
			await expect(options).toBeAttached();


			assert.isNull(await callInstance('getParentValue'));
			assert.areSame('undefined_null', await callInstance('getParentCount'));
			await expect(masterInput).toHaveValue('');
			await validateSelect.options(expect, assert, masterOptions, []);

			assert.areSame('y1', await callInstance('getMainValue'));
			assert.areSame('undefined_y1', await callInstance('getMainCount'));
			await expect(input).toHaveValue('Y1');
			await validateSelect.options(expect, assert, options, [
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]},
				{name: 'Z', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Z1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Z2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Z3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
			]);

			// sleep
			await new Promise(resolve => setTimeout(resolve, 6000));

			assert.areSame('y', await callInstance('getParentValue'));
			assert.areSame('undefined_null_y', await callInstance('getParentCount'));
			await expect(masterInput).toHaveValue('Y');
			await validateSelect.options(expect, assert, masterOptions, [
				{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
				{name: 'Z', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
			]);

			assert.areSame('y1', await callInstance('getMainValue'));
			assert.areSame('undefined_y1', await callInstance('getMainCount'));
			await expect(input).toHaveValue('Y1');
			await validateSelect.options(expect, assert, options, [
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]},
				{name: 'Z', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
					{name: 'Z1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Z2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Z3', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
				]},
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
			]);
		}
	},
	{
		name: 'Depends on load and def value',
		create: (conf)=>{
			var master = Toti.createInput(conf.master);
			delete conf.master;
			return createSelectMock(conf, master);
		},
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_depends_load_def',
				options: [
					{ value: 'x', title: 'X' },
					{ value: 'y', title: 'Y', options: [
						{ value: 'y1', title: 'Y1' },
						{ value: 'y2', title: 'Y2' },
						{ value: 'y3', title: 'Y3' }
					]},
					{ value: 'z', title: 'Z', options: [
						{ value: 'z1', title: 'Z1' },
						{ value: 'z2', title: 'Z2' },
						{ value: 'z3', title: 'Z3' }
					]},
					{ value: 'a', title: 'A' }
				],
				depends: 'totiSelect-3',
				onChange: [ 'onSelectDepends3Change' ],
				master: {
					type: 'select',
					name: 'totiSelect',
					options: [],
					load: {
						url: '/inputs/depends',
						method: 'get',
						params: {}
					},
					value: 'y',
					unique: 'totiSelect-3',
					onChange: [ 'onSelectMaster3Change' ]
				}
			};
		},
		/*radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// radiolist not support depends
		},*/
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var masterInput = await locator.locator('[name=totiSelect] .toti-select-input');
			await expect(masterInput).toBeAttached();
			var masterOptions = await locator.locator('[name=totiSelect] .toti-select-options');
			await expect(masterOptions).toBeAttached();

			var input = await locator.locator('[name=testInput_depends_load_def] .toti-select-input');
			await expect(input).toBeAttached();
			var options = await locator.locator('[name=testInput_depends_load_def] .toti-select-options');
			await expect(options).toBeAttached();


			assert.isNull(await callInstance('getParentValue'));
			assert.areSame('undefined_null', await callInstance('getParentCount'));
			await expect(masterInput).toHaveValue('');
			await validateSelect.options(expect, assert, masterOptions, []);

			assert.isNull(await callInstance('getMainValue'));
			assert.areSame('undefined_null', await callInstance('getMainCount'));
			await expect(input).toHaveValue('');
			await validateSelect.options(expect, assert, options, [
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]},
				{name: 'Z', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Z1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Z2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Z3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
			]);

			// sleep
			await new Promise(resolve => setTimeout(resolve, 6000));

			assert.areSame('y', await callInstance('getParentValue'));
			assert.areSame('undefined_null_y', await callInstance('getParentCount'));
			await expect(masterInput).toHaveValue('Y');
			await validateSelect.options(expect, assert, masterOptions, [
				{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
				{name: 'Z', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
			]);

			assert.isNull(await callInstance('getMainValue'));
			assert.areSame('undefined_null', await callInstance('getMainCount'));
			await expect(input).toHaveValue('');
			await validateSelect.options(expect, assert, options, [
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]},
				{name: 'Z', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
					{name: 'Z1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Z2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Z3', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []}
				]},
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
			]);
		}
	},
	{
		name: 'Depends on load: both def value',
		create: (conf)=>{
			var master = Toti.createInput(conf.master);
			delete conf.master;
			return createSelectMock(conf, master);
		},
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_depends_load_both_def',
				options: [
					{ value: 'x', title: 'X' },
					{ value: 'y', title: 'Y', options: [
						{ value: 'y1', title: 'Y1' },
						{ value: 'y2', title: 'Y2' },
						{ value: 'y3', title: 'Y3' }
					]},
					{ value: 'z', title: 'Z', options: [
						{ value: 'z1', title: 'Z1' },
						{ value: 'z2', title: 'Z2' },
						{ value: 'z3', title: 'Z3' }
					]},
					{ value: 'a', title: 'A' }
				],
				value: 'z1',
				depends: 'totiSelect-4',
				onChange: [ 'onSelectDepends4Change' ],
				master: {
					type: 'select',
					name: 'totiSelect',
					options: [],
					load: {
						url: '/inputs/depends',
						method: 'get',
						params: {}
					},
					value: 'y',
					unique: 'totiSelect-4',
					onChange: [ 'onSelectMaster4Change' ]
				}
			};
		},
		/*radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// radiolist not support depends
		},*/
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var masterInput = await locator.locator('[name=totiSelect] .toti-select-input');
			await expect(masterInput).toBeAttached();
			var masterOptions = await locator.locator('[name=totiSelect] .toti-select-options');
			await expect(masterOptions).toBeAttached();

			var input = await locator.locator('[name=testInput_depends_load_both_def] .toti-select-input');
			await expect(input).toBeAttached();
			var options = await locator.locator('[name=testInput_depends_load_both_def] .toti-select-options');
			await expect(options).toBeAttached();

			assert.isNull(await callInstance('getParentValue'));
			assert.areSame('undefined_null', await callInstance('getParentCount'));
			await expect(masterInput).toHaveValue('');
			await validateSelect.options(expect, assert, masterOptions, []);

			assert.areSame('z1', await callInstance('getMainValue'));
			assert.areSame('undefined_z1', await callInstance('getMainCount'));
			await expect(input).toHaveValue('Z1');
			await validateSelect.options(expect, assert, options, [
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				]},
				{name: 'Z', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Z1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'Z2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Z3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
			]);
			await expect(input).not.toHaveClass(/toti-select-disabled/);

			// sleep
			await new Promise(resolve => setTimeout(resolve, 6000));

			assert.areSame('z', await callInstance('getParentValue'));
			assert.areSame('undefined_null_y_z', await callInstance('getParentCount'));
			await expect(masterInput).toHaveValue('Z');
			await validateSelect.options(expect, assert, masterOptions, [
				{name: '---', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
				{name: 'Z', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
			]);

			assert.areSame('z1', await callInstance('getMainValue'));
			assert.areSame('undefined_z1', await callInstance('getMainCount'));
			await expect(input).toHaveValue('Z1');
			await validateSelect.options(expect, assert, options, [
				{name: 'X', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
				{name: 'Y', visible: true, canSelect: true, isGroup: true, isDisabled: true, hidden: true, childs: [
					{name: 'Y1', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y2', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
					{name: 'Y3', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
				]},
				{name: 'Z', visible: true, canSelect: true, isGroup: true, isDisabled: false, hidden: true, childs: [
					{name: 'Z1', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, selected: true, childs: []},
					{name: 'Z2', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []},
					{name: 'Z3', visible: true, canSelect: true, isGroup: false, isDisabled: false, hidden: true, childs: []}
				]},
				{name: 'A', visible: true, canSelect: true, isGroup: false, isDisabled: true, hidden: true, childs: []},
			]);
			await expect(input).not.toHaveClass(/toti-select-disabled/);
		}
	},
	{
		name: 'Depends: tree',
		create: (conf)=>{
			var master2 = Toti.createInput(conf.master2);
			delete conf.master2;
			var master1 = Toti.createInput(conf.master1);
			delete conf.master1;
			return createSelectMock(conf, master1, master2);
		},
		conf: (type)=>{
			return {
				type: type,
				name: 'testInput_depends_tree',
				options: [
					{
						value: null,
						title: '---'
					}
				],
				load: {
					url: '/inputs/depends-tree-1',
					method: 'get',
					params: {}
				},
				value: 'b12',
				depends: 'totiSelect-tree-2',
				onChange: [ 'onSelectDependsTree1Change' ],
				master1: {
					type: 'select',
					name: 'totiSelect1',
					options: [
						{
							value: null,
							title: '---'
						}
					],
					load: {
						url: '/inputs/depends-tree-2',
						method: 'get',
						params: {}
					},
					value: 'b1',
					unique: 'totiSelect-tree-2',
					depends: 'totiSelect-tree-3',
					onChange: [ 'onSelectDependsTree2Change' ]
				},
				master2: {
					type: 'select',
					name: 'totiSelect2',
					options: [
						{
							value: null,
							title: '---'
						}
					],
					load: {
						url: '/inputs/depends-tree-3',
						method: 'get',
						params: {}
					},
					unique: 'totiSelect-tree-3',
					onChange: [ 'onSelectDependsTree3Change' ]
				}
			};
		},
		/*radiolist: async (expect, assert, page, callInstance, callContainer, locator)=>{
			// radiolist not support depends
		},*/
		select: async (expect, assert, page, callInstance, callContainer, locator)=>{
			var masterInput2 = await locator.locator('[name=totiSelect2] .toti-select-input');
			await expect(masterInput2).toBeAttached();

			var masterInput1 = await locator.locator('[name=totiSelect1] .toti-select-input');
			await expect(masterInput1).toBeAttached();

			var input = await locator.locator('[name=testInput_depends_tree] .toti-select-input');
			await expect(input).toBeAttached();


			assert.isNull(await callInstance('getParentValue', 1));
			await expect(masterInput2).toHaveValue('---');
			assert.areSame('undefined_null', await callInstance('getParentCount', 1));

			assert.isNull(await callInstance('getParentValue', 0));
			await expect(masterInput1).toHaveValue('');
			assert.areSame('undefined_null', await callInstance('getParentCount', 0));

			assert.isNull(await callInstance('getMainValue'));
			assert.areSame('undefined_null', await callInstance('getMainCount'));
			await expect(input).toHaveValue('');

			// sleep
			await new Promise(resolve => setTimeout(resolve, 10000));

			assert.areSame('b', await callInstance('getParentValue', 1));
			assert.areSame('undefined_null_b', await callInstance('getParentCount', 1));
			await expect(masterInput2).toHaveValue('B');

			assert.areSame('b1', await callInstance('getParentValue', 0));
			assert.areSame('undefined_null_b1', await callInstance('getParentCount', 0));
			await expect(masterInput1).toHaveValue('B1');

			assert.areSame('b12', await callInstance('getMainValue'));
			assert.areSame('undefined_null_b12', await callInstance('getMainCount'));
			await expect(input).toHaveValue('B12');
		}
	}
].forEach((item)=>{
	function prepare(list, code) {
		if (!item.hasOwnProperty(code)) {
			return;
		}
		var prepared = {
			type: 'test',
			name: item.name,
			conf: item.conf(code),
			verify: item[code]
		};
		if (item.hasOwnProperty('create')) {
			prepared.create = item.create;
		}		list.push(prepared);
		return prepared;
	}
	prepare(optionsInputSelect, 'select');
	prepare(optionsInputRadiolist, 'radiolist');
});

require('../spec.js')({
	name: 'Inputs: OptionInputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: [
		{
			type: 'group',
			name: 'Select',
			tests: optionsInputSelect
		},
		{
			type: 'group',
			name: 'Radiolist',
			tests: optionsInputRadiolist
		}
	]
});