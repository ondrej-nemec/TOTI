Toti.load = (...configurations)=>{
	return Toti.utils.sleep(3000).then(()=>{
		var url = configurations[0].url;
		switch (url) {
			case '/inputs/text':
				return [
					'Loaded Option',
					'Some data',
					'Else'
				];
				break;
			case '/inputs/options':
				return [
					{
						value: 'x',
						title: 'X',
						disabled: true
					},
					{
						value: 'y',
						title: 'Y',
						disabled: false,
						options: [
							{
								value: 'y1',
								title: 'Y1'
							},
							{
								value: 'y2',
								title: 'Y2',
								selectable: true
							},
							{
								value: 'y3',
								title: 'Y3',
								selectable: false
							}
						]
					},
					{
						value: 'c',
						title: 'C',
						selectable: true,
						options: [
							{
								value: 'c4',
								title: 'C4',
								disabled: true
							},
							{
								value: 'c5',
								title: 'C5',
								options: [
									{
										value: 'c51',
										title: 'C51',
										disabled: true
									},
									{
										value: 'c52',
										title: 'C52',
									}
								]
							},
							{
								value: 'c2',
								title: 'C2',
								options: [
									{
										value: 'c23',
										title: 'C23',
										disabled: true
									}
								]
							}
						]
					},
					{
						value: 'cz',
						title: 'CZ',
						selectable: false,
						options: [
							{
								value: 'ca',
								title: 'CA',
								disabled: true
							},
							{
								value: 'cb',
								title: 'CB'
							}
						]
					}
				];
				break;
			case '/inputs/depends':
				return [
					{
						value: null,
						title: '---'
					},
					{
						value: 'x',
						title: 'X'
					},
					{
						value: 'y',
						title: 'Y'
					},
					{
						value: 'z',
						title: 'Z'
					}
				];
			/*case '/inputs/loadedList':
				return [
					{
						value: 'a',
						title: 'A'
					},
					{
						value: 'b',
						title: 'B'
					}
				];*/
			default:
				var moreTimes = {
					'/inputs/depends-tree-3': [
						{
							value: 'a',
							title: 'A'
						},
						{
							value: 'b',
							title: 'B'
						}
					],
					'/inputs/depends-tree-2': [
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
							title: 'B',
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
					],
					'/inputs/depends-tree-1': [
						{
							value: 'a1',
							title: 'A1',
							options: [
								{
									value: 'a11',
									title: 'A11'
								},
								{
									value: 'a12',
									title: 'A12'
								}
							]
						},
						{
							value: 'a2',
							title: 'A2',
							options: [
								{
									value: 'a21',
									title: 'A21'
								},
								{
									value: 'a22',
									title: 'A22'
								}
							]
						},
						{
							value: 'b1',
							title: 'B1',
							options: [
								{
									value: 'b11',
									title: 'B11'
								},
								{
									value: 'b12',
									title: 'B12'
								}
							]
						},
						{
							value: 'b2',
							title: 'B2',
							options: [
								{
									value: 'b21',
									title: 'B21'
								},
								{
									value: 'b22',
									title: 'B22'
								}
							]
						}
					],
					'/inputs/loadedList': [
						{
							value: 'a',
							title: 'A'
						},
						{
							value: 'b',
							title: 'B'
						}
					]
				};
				if (moreTimes.hasOwnProperty(url)) {
					return Toti.utils.sleep(7000).then(()=>{
						return moreTimes[url];
					});
				}
				throw new Error('Unsupported: ' + url);
		}
	});
};
const selectMock = 'selectMock';
function createSelectMock(conf, ...parents) {
	conf.real = conf.type;
	conf.type = selectMock;
	Toti.init({
		inputs: {
			selectMock: (attributes, factory)=>{
				attributes.type = attributes.real;
				var input = Toti.createInput(attributes);

				var mock = factory({
					parseValue: (value)=>{
						return input.parseValue(value);
					},
					create: (instance, editable, createAttributes)=>{
						var container = document.createElement('div');
						for (var i = parents.length - 1; i >= 0; i--) {
							var p = parents[i];
							container.appendChild(p.getContainer());
							container.appendChild(document.createElement('br'));
						}
						/*parents.forEach((p)=>{
							container.appendChild(p.getContainer());
							container.appendChild(document.createElement('br'));
						});*/
						container.appendChild(input.getContainer());
						return container;
					},
					setValue: (instance, container, value, editable)=>{},
					setDisabled: (instance, container, isDisabled, editable)=>{},
				});
				mock.setDisabled = (isDisabled)=>{
					input.setDisabled(isDisabled);
				};
				mock.setValue = (value)=>{
					input.setValue(value);
				};
				mock.isValid = ()=>{
					return input.isValid();
				};
				mock.getErrors = ()=>{
					return input.getErrors();
				};
				mock.getValue = ()=>{
					return input.getValue();
				};

				mock.setParentValue = (value, index = 0)=>{
					parents[index].setValue(value);
				};
				mock.getParentValue = (index = 0)=>{
					return parents[index].getValue();
				};
				mock.getParentCount = (index = 0)=>{
					return parents[index].getCount();
				};
				mock.clearParentValue = (index = 0)=>{
					parents[index].clear();
				};

				mock.setMainValue = (value)=>{
					input.setValue(value);
				};
				mock.getMainValue = ()=>{
					return input.getValue();
				};
				mock.getMainCount = ()=>{
					return input.getCount();
				};
				mock.clearMainValue = ()=>{
					input.clear();
				};

				return mock;
			}
		}
	});
	return Toti.createInput(conf);
}
function createCustomInputList(conf) {
	Toti.init({
		inputs: {
			inputList: (instance, container, input)=>{
				var title = document.createElement('span');
				title.innerText = input.getTitle();

				var element = document.createElement('div');
				element.appendChild(title);
				element.appendChild(input.getContainer());
				container.appendChild(element);
			}
		}
	});
	return Toti.createInput(conf);
}
var onChangeCallback = (index = null)=>{
	return (instance, oldValue)=>{
		var method = 'getChanges';
		var variable = 'mark';
		if (index !== null) {
			method += index;
			variable += index;
		}
		if (oldValue === undefined) {
			// init state
			instance[variable] = (index === null ? '' : index + "__") + 'undefined';
			// @deprecated
			instance.getCount = ()=>{
				return instance[variable];
			};
			instance[method] = ()=>{
				return instance[variable];
			};
		}
		var current = instance.getValue();
		if (typeof current === 'object') {
			current = JSON.stringify(current);
		}
		instance[variable] = instance[variable] + '_' + current;
	};
}
// need to be here - function must be available during constructor
var onTextChange1 = onChangeCallback(1);
var onTextChange2 = onChangeCallback(2);

var onSelectDepends1Change = onChangeCallback();
var onSelectMaster1Change = onChangeCallback();

var onSelectDepends2Change = onChangeCallback();
var onSelectMaster2Change = onChangeCallback();

var onSelectDepends3Change = onChangeCallback();
var onSelectMaster3Change = onChangeCallback();

var onSelectDepends4Change = onChangeCallback();
var onSelectMaster4Change = onChangeCallback();

var onSelectLoad1Change = onChangeCallback();
var onSelectLoad2Change = onChangeCallback();

var onSelectDependsTree1Change = onChangeCallback();
var onSelectDependsTree2Change = onChangeCallback();
var onSelectDependsTree3Change = onChangeCallback();

var onDynamicChange = onChangeCallback();
var onInputListChange = onChangeCallback();

var animationOrigin = Toti.animations;
animationOrigin.inputLoading = (parentContainer)=>{
	return {
		start: ()=>{},
		remove: ()=>{},
		failure: ()=>{}
	};
};
Toti.animations = animationOrigin;