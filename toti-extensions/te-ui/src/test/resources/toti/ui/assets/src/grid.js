function createGrid(configuration) {
	return new class {
		container = null;
		columns = null;
		constructor(configuration) {
			console.log("Grid", configuration, Toti);
		}
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
/*
// TODO
		*/
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
}