class Grid {
	container = null;
	columns = null;
	loadConfiguration = null;
	constructor(attributes, configuration) {
		console.log("Grid", attributes, configuration);
		if (!attributes.hasOwnProperty('load')) {
			throw new Error("Missing attribute 'load'");
		}
		if (!attributes.hasOwnProperty('columns')) {
			throw new Error("Missing attribute 'columns'");
		}

/*
		this.callbacks = configuration;
		if (!this.callbacks.hasOwnProperty('')) {
			this.callbacks. = ()=>{
				
			};
		}
		*/
	}
/*
	private boolean useRowSelection = false;
	private final List<Jsonable> columns = new LinkedList<>();
	private final List<Jsonable> actions = new LinkedList<>();
	
	// paging
	private List<Integer> pagesSizes = Arrays.asList(10, 20, 50, 100); // optional
	private int pageSize = 20; // required, negative means all
	private int pagesButtonCount = 5; // required, 0 means no buttons
	private boolean useLoadButton = false;
	
	private long refreshInterval = 0L; // in ms, 0 means no refresh
	private String rowRenderer = null;
	
	private int treeColumnIndex = -1;
	
	private String beforeRender = null;
	private String afterRender = null;
	private String beforeBind = null;
	private String afterBind = null;

	*/
		// TODO medoda pro render a refreshData
		/*
	z url a z cache nacist filtry a sortovani
	beforeRender calblack
		sortovani a filtry
	- iterovat sloupce a pridavat je (metodou?)
		- buttons (reset, reload), condition bude implementovano zde?
		- checkbox
		- value
		- secondRow

		- renderery - hlavne pro select a datetime

	pridate actions - metodou
	pridat page index
	pridat page size
	after render callback
		*/

	render() {}
	refreshData(clearPrevious = true) {}
	sortBy(name, direction, refresh = true) {}
	nextSort(name, refresh = true) {}
	filterBy(name, value, refresh = true) {}
	resetFilters(refresh = true) {}
	setPageIndex(pageIndex, refresh = true) {}
	addNextPage(refresh = true) {}
	setPageSize(pageSize, refresh = true) {}
	startRefresh() {}
	stopRefresh() {}
	getSelectedRow() {}
		// TODO metody pro pridavani ovladacich prvku, hlavne actions
};
function createGrid(configuration, settings) {
	return new Grid(configuration, settings);
}