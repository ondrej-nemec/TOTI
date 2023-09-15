package ext;

public class GridColumn {

	private final String name;
	private FilterMode filterMode = FilterMode.EQUALS;
	private boolean useInFilter;
	private boolean useInSorting;
	private final Class<?> clazz;
	private String sortingName;
    private boolean isCI;
    private boolean ignoreDiacritics;
	
	public GridColumn(String name) {
		this(name, String.class);
	}
	
	public GridColumn(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
		this.useInFilter = true;
		this.useInSorting = true;
		this.sortingName = name;
	}
	
	public GridColumn setSortingName(String sortingName) {
		this.sortingName = sortingName;
		return this;
	}
	
	public GridColumn setFilterMode(FilterMode filterMode) {
		this.filterMode = filterMode;
		return this;
	}
	
	public GridColumn setUseInSorting(boolean useInSorting) {
		this.useInSorting = useInSorting;
		return this;
	}
	
	public GridColumn setUseInFilter(boolean useInFilter) {
		this.useInFilter = useInFilter;
		return this;
	}
	
	public GridColumn setCI(boolean isCI) {
		this.isCI = isCI;
		return this;
	}
	
	public GridColumn setIgnoreDiacritics(boolean ignoreDiacritics) {
		this.ignoreDiacritics = ignoreDiacritics;
		return this;
	}

	public String getName() {
		return name;
	}

	public boolean isUseInFilter() {
		return useInFilter;
	}

	public boolean isUseInSorting() {
		return useInSorting;
	}
	
	public FilterMode getFilterMode() {
		return filterMode;
	}
	
	public String getSortingName() {
		return sortingName;
	}

	public Class<?> getType() {
		return clazz;
	}

	public boolean isCI() {
		return isCI;
	}
	
	public boolean isIgnoreDiacritics() {
		return ignoreDiacritics;
	}
	
	@Override
	public String toString() {
		return "GridColumn [name=" + name + ", useInFilter=" + useInFilter + ", useInSorting=" + useInSorting
				+ ", clazz=" + clazz + "]";
	}
}
