const totiUtils  = require('../src/utils');

test('Clone create deep copy of simple object', () => {
	var source = {
		a: 'A',
		b: {
			b1: 123,
			b2: true,
			b3: [
				'x', 'y', 42
			]
		},
		c: [ 'c1', 'c2' ]
	};
	var clone = totiUtils.clone(source);
  	expect(clone).toStrictEqual({
		a: 'A',
		b: {
			b1: 123,
			b2: true,
			b3: [
				'x', 'y', 42
			]
		},
		c: [ 'c1', 'c2' ]
	});
	// change origin
	source.b.b2 = false;
  	expect(source).toStrictEqual({
		a: 'A',
		b: {
			b1: 123,
			b2: false,
			b3: [
				'x', 'y', 42
			]
		},
		c: [ 'c1', 'c2' ]
	});
  	expect(clone).toStrictEqual({
		a: 'A',
		b: {
			b1: 123,
			b2: true,
			b3: [
				'x', 'y', 42
			]
		},
		c: [ 'c1', 'c2' ]
	});
});
test('Clone with function', () => {
	var funcA = ()=>{
		console.log('Function A');
	};
	var funcB = ()=>{
		console.log('Function B');
	};
	var source = {
		f: [ funcA ]
	};
	var clone = totiUtils.clone(source);
  	expect(clone).toStrictEqual({
		f: [ funcA ]
	});
	// change origin
	source.f = [ funcB ];
  	expect(source).toStrictEqual({
		f: [ funcB ]
	});
  	expect(clone).toStrictEqual({
		f: [ funcA ]
	});
});