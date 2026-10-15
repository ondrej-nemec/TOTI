require('/home/coder/project/playwright.spec.js')({
	name: 'Grid',
	create: (conf)=>{
		return Toti.Grid(conf);
	},
	tests: []
});