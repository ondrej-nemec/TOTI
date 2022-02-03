package toti.application;

public class GridColumn {

	private final String name;
	private boolean useInFilter;
	private boolean useInSorting;
	private final Class<?> clazz;
	
	public GridColumn(String name) {
		this(name, String.class);
	}
	
	public GridColumn(String name, Class<?> clazz) {
		this.name = name;
		this.clazz = clazz;
		this.useInFilter = true;
		this.useInSorting = true;
	}
	
	public GridColumn setUseInSorting(boolean useInSorting) {
		this.useInSorting = useInSorting;
		return this;
	}
	
	public GridColumn setUseInFilter(boolean useInFilter) {
		this.useInFilter = useInFilter;
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

	public Class<?> getType() {
		return clazz;
	}
}
