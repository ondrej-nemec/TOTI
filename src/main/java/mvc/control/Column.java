package mvc.control;

public class Column {

	private final String name;
	private final String type;
	private final String title;
	private String width = "50";
	private boolean sorting = true;
	private boolean filtering = true;
	
	private Html itemTemplate = null;
	private Html headerTemplate = null;
	private Html filterTemplate = null;
	private Html insertTemplate = null;
	
	public Column(String name, String title, String type) {
		this.name = name;
		this.title = title;
		this.type = type;
	}
	
	public Column setWidth(String width) {
		this.width = width;
		return this;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getWidth() {
		return width;
	}

	public Html getItemTemplate() {
		return itemTemplate;
	}

	public void setItemTemplate(Html itemTemplate) {
		this.itemTemplate = itemTemplate;
	}

	public Html getHeaderTemplate() {
		return headerTemplate;
	}

	public void setHeaderTemplate(Html headerTemplate) {
		this.headerTemplate = headerTemplate;
	}

	public Html getFilterTemplate() {
		return filterTemplate;
	}

	public void setFilterTemplate(Html filterTemplate) {
		this.filterTemplate = filterTemplate;
	}

	public Html getInsertTemplate() {
		return insertTemplate;
	}

	public boolean isSorting() {
		return sorting;
	}

	public void setSorting(boolean sorting) {
		this.sorting = sorting;
	}

	public boolean isFiltering() {
		return filtering;
	}

	public void setFiltering(boolean filtering) {
		this.filtering = filtering;
	}
	
}
