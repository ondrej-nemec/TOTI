var dateTests = [];
var timeTests = [];
var datetimeTests = [];
var monthTests = [];
var weekTest = [];

function createTimestamp(type, year, month, day, hour, minute, second, nano, week) {
	return {
		type: type,
		year: year,
		month: month,
		day: day,
		hour: hour,
		minute: minute,
		second: second,
		nano: nano,
		week: week
	};
}

var datetimeConfig = {
	'date': {
		collection: dateTests,
		type: 'date',
		values: [
			{
				input: '2025-02-11',
				text: '2025-02-11',
				value: createTimestamp('date', 2025, 2, 11, null, null, null, null, null)
			}
		],
		steps: [
			{
				value: 2,
				correct: '2025-03-11',
				incorrect: '2025-03-12'
			},
			{
				value: 10,
				correct: '2025-03-23',
				incorrect: '2025-03-24'
			},
			{
				value: 30,
				correct: '2025-03-13',
				incorrect: '2025-03-14'
			}
		],
		minMax: {
			value: '2025-02-11',
			error: createTimestamp('date', 2025, 2, 11, null, null, null, null, null),
			less: '2025-02-10',
			more: '2025-02-12'
		}
	},
	'time': {
		collection: timeTests,
		type: 'time',
		values: [
			{
				input: '14:44',
				text: '14:44',
				value: createTimestamp('time', null, null, null, 14, 44, null, null, null)
			},
			{
				input: '14:44:30',
				text: '14:44:30',
				value: createTimestamp('time', null, null, null, 14, 44, 30, null, null)
			},
			{
				input: '14:44:30.954',
				text: '14:44:30.954',
				value: createTimestamp('time', null, null, null, 14, 44, 30, 954, null)
			}
		],
		steps: [ 
			{
				value: 1,
				correct: '14:44:30',
				incorrect: '14:44:30.954'
			},
			{
				value: 60,
				correct: '14:44',
				incorrect: '14:44:30'
			},
			{
				value: 3600,
				correct: '14:00',
				incorrect: '14:01'
			},
			{
				value: 0.1,
				correct: '14:44:30.900',
				incorrect: '14:44:30.954'
			}
		],
		minMax: {
			value: '14:44:30',
			error: createTimestamp('time', null, null, null, 14, 44, 30, null, null),
			less: '14:44:29',
			more: '14:44:31'
		}
	},
	'datetime': {
		collection: datetimeTests,
		type: 'datetime-local',
		values: [
			{
				input: '2025-02-11 14:44',
				text: '2025-02-11 14:44',
				value: createTimestamp('datetime', 2025, 2, 11, 14, 44, NaN, null, null)
			},
			{
				input: '2025-02-11 14:44:30',
				text: '2025-02-11 14:44:30',
				value: createTimestamp('datetime', 2025, 2, 11, 14, 44, 30, null, null)
			},
			{
				input: '2025-02-11 14:44:30.954',
				text: '2025-02-11 14:44:30.954',
				value: createTimestamp('datetime', 2025, 2, 11, 14, 44, 30, 954, null)
			},
			{
				input: '2025-02-11T14:44',
				text: '2025-02-11 14:44',
				value: createTimestamp('datetime', 2025, 2, 11, 14, 44, null, null, null)
			},
			{
				input: '2025-02-11T14:44:30',
				text: '2025-02-11 14:44:30',
				value: createTimestamp('datetime', 2025, 2, 11, 14, 44, 30, null, null)
			},
			{
				input: '2025-02-11T14:44:30.954',
				text: '2025-02-11 14:44:30.954',
				value: createTimestamp('datetime', 2025, 2, 11, 14, 44, 30, 954, null)
			}
		],
		steps: [ 
			{
				value: 1,
				correct: '2025-02-11 14:44:30',
				incorrect: '2025-02-11 14:44:30.456'
			},
			{
				value: 60,
				correct: '2025-02-11 14:44',
				incorrect: '2025-02-11 14:44:30'
			},
			{
				value: 3600,
				correct: '2025-02-11 14:00',
				incorrect: '2025-02-11 14:44'
			},
			{
				value: 0.1,
				correct: '2025-02-11 14:44:30.200',
				incorrect: '2025-02-11 14:44:30.756'
			}
		// TODO 3600, 1800, 3600*6 - days step not working
		], 
		minMax: {
			value: '2025-02-11 14:44',
			error: createTimestamp('datetime', 2025, 2, 11, 14, 44, null, null, null),
			less: '2025-02-11 14:43',
			more: '2025-02-11 14:45'
		}
	},
	'month': {
		collection: monthTests,
		type: 'month',
		values: [
			{
				input: '2025-02',
				text: '2025-02',
				value: createTimestamp('month', 2025, 2, null, null, null, null, null, null)
			}
		],
		steps: [ 
			{
				value: 3,
				correct: '2025-07',
				incorrect: '2025-08'
			},
			{
				value: 5,
				correct: '2025-06',
				incorrect: '2025-08'
			}
		],
		minMax: {
			value: '2025-02',
			error: createTimestamp('month', 2025, 2, null, null, null, null, null, null),
			less: '2025-01',
			more: '2025-03'
		}
	},
	'week': {
		collection: weekTest,
		type: 'week',
		values: [
			{
				input: '2025-W07',
				text: '2025-W07',
				value: createTimestamp('week', 2025, null, null, null, null, null, null, 7)
			}
		],
		steps: [ 
			{
				value: 5,
				correct: '2025-W16',
				incorrect: '2025-W12'
			}
		],
		minMax: {
			value: '2025-W07',
			error: createTimestamp('week', 2025, null, null, null, null, null, null, 7),
			less: '2025-W06',
			more: '2025-W08'
		}
	}
};
for (const[name, conf] of Object.entries(datetimeConfig)) {
	conf.collection.push({
		name: 'Basic',
		type: 'test',
		conf: {
			type: name
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('INPUT', await callContainer('tagName'));
			await expect(locator).toHaveAttribute('type', conf.type);
			await expect(locator).toBeEnabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
		}
	});

	var defValueValues = [];
	conf.values.forEach((value)=>{
		defValueValues.push({
			name: 'Value "' + value['input'] + '"',
			type: 'test',
			conf: {
				type: name,
				value: value['input']
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toHaveValue(value['input'].replace(' ', 'T'));
				assert.areSame(value['value'], await callInstance('getValue'));
			}
		});
	});
	conf.collection.push({
		name: 'Default value',
		type: 'group',
		tests: defValueValues
	});
	conf.collection.push({
		name: 'Disabled',
		type: 'test',
		conf: {
			type: name,
			disabled: true
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('INPUT', await callContainer('tagName'));
			await expect(locator).toHaveAttribute('type', conf.type);
			await expect(locator).toBeDisabled();
			await expect(locator).toBeVisible();
			await expect(locator).toBeAttached();
		}
	});
	conf.collection.push({
		name: 'Not editable',
		type: 'test',
		conf: {
			type: name,
			editable: false
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.areSame('SPAN', await callContainer('tagName'));
			expect(locator).toBeAttached();
			expect(locator).toBeHidden();
			expect(locator).toHaveText('');
			assert.isNull(await callInstance('getValue'));
		}
	});

	var notEditableWithValue = [];
	conf.values.forEach((value)=>{
		notEditableWithValue.push({
			name: 'Not editable with value "' + value['input'] + '"',
			type: 'test',
			conf: {
				type: name,
				editable: false,
				value: value['input']
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				await expect(locator).toHaveText(value['text']);
				assert.areSame(value['value'], await callInstance('getValue'));
			}
		});
	});
	conf.collection.push({
		name: 'Not editable with value',
		type: 'group',
		tests: notEditableWithValue
	});


	conf.collection.push({
		name: 'Required',
		type: 'test',
		conf: {
			type: name,
			required: true
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.isNull(await callInstance('getValue'));
			assert.isFalse(await callInstance('isValid'));
			assert.areSame({"input.required":{}}, await callInstance('getErrors'));

			var value = conf.values[0];
			await callInstance('setValue', value['input']);
			assert.areSame(value['value'], await callInstance('getValue'));
			assert.isTrue(await callInstance('isValid'));
			assert.areSame({}, await callInstance('getErrors'));

			await callInstance('clear');
			assert.isNull(await callInstance('getValue'));
			assert.isFalse(await callInstance('isValid'));
			assert.areSame({"input.required":{}}, await callInstance('getErrors'));
		}
	});
	conf.collection.push({
		name: 'Min',
		type: 'test',
		conf: {
			type: name,
			min: conf.minMax.value
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.isNull(await callInstance('getValue'));
			assert.isTrue(await callInstance('isValid'));
			assert.areSame({}, await callInstance('getErrors'));

			await callInstance('setValue', conf.minMax.less);
			assert.isFalse(await callInstance('isValid'));
			assert.areSame({"input.toSmall":{m:conf.minMax.error}}, await callInstance('getErrors'));

			await callInstance('setValue', conf.minMax.more);
			assert.isTrue(await callInstance('isValid'));
			assert.areSame({}, await callInstance('getErrors'));
		}
	});
	conf.collection.push({
		name: 'Max',
		type: 'test',
		conf: {
			type: name,
			max: conf.minMax.value
		},
		verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
			assert.isNull(await callInstance('getValue'));
			assert.isTrue(await callInstance('isValid'));
			assert.areSame({}, await callInstance('getErrors'));

			await callInstance('setValue', conf.minMax.more);
			assert.isFalse(await callInstance('isValid'));
			assert.areSame({"input.tooBig":{m:conf.minMax.error}}, await callInstance('getErrors'));

			await callInstance('setValue', conf.minMax.less);
			assert.isTrue(await callInstance('isValid'));
			assert.areSame({}, await callInstance('getErrors'));
		}
	});
	var steps = [];
	conf.steps.forEach((step)=>{
		steps.push({
			name: 'Step "' + step.value + '"',
			type: 'test',
			conf: {
				type: name,
				step: step.value
			},
			verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
				assert.isNull(await callInstance('getValue'));
				assert.isTrue(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));

				await callInstance('setValue', step.incorrect);
				assert.isFalse(await callInstance('isValid'));
				assert.areSame({"input.stepIsWrong":{"s":step.value}}, await callInstance('getErrors'));

				await callInstance('setValue', step.correct);
				assert.isTrue(await callInstance('isValid'));
				assert.areSame({}, await callInstance('getErrors'));
			}
		});
	});
	conf.collection.push({
		name: 'Steps',
		type: 'group',
		tests: steps
	});
	
}
datetimeTests.push({
	name: 'datetime-local works same',
	type: 'test',
	conf: {
		type: 'datetime-local'
	},
	verify: async (expect, assert, page, callInstance, callContainer, locator)=>{
		assert.areSame('INPUT', await callContainer('tagName'));
		await expect(locator).toHaveAttribute('type', 'datetime-local');
		await expect(locator).toBeEnabled();
		await expect(locator).toBeVisible();
		await expect(locator).toBeAttached();
	}
});

require('../spec.js')({
	name: 'Inputs: Date Time inputs',
	create: (conf)=>{
		return Toti.createInput(conf);
	},
	tests: [
		{
			name: 'Input date',
			type: 'group',
			tests: dateTests
		},
		{
			name: 'Input time',
			type: 'group',
			tests: timeTests
		},
		{
			name: 'Input datetime',
			type: 'group',
			tests: datetimeTests
		},
		{
			name: 'Input week',
			type: 'group',
			tests: weekTest
		},
		{
			name: 'Input month',
			type: 'group',
			tests: monthTests
		}
	]
});