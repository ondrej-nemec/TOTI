package mvc.control;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Grid {
	
	public static final String TEMPLATE_FILE = "mvc/templates/Grid.jsp";
	
	private final String name;
	private final String dataUrl;

	private String width = "auto";
	private String height = "auto";
	
	private boolean heading = true;
	private boolean filtering = true;
	private boolean autoload = true;
	private boolean sorting = true;
	private boolean selecting = true;
	
	private boolean paging = true;
	private boolean pageLoading = true;	
	private int pageSize = 20;
	private int pageButtonCount = 5;
	
	private boolean inserting = false;
	private boolean editing = false;
	
	private final List<Column> columns = new LinkedList<>();
	private GridButtons buttons = null;
	
	// itemTemplate, headerTemplate, filterTemplate, insertTemplate
		
	// editButton, modeSwitchButton, deleteButton, clearFilteringButton
	
	// validate
	
	// deleteConfirm: , rowRenderer, rowClick
	
	/**
	 * required:
	 * <script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
	 * <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jsgrid/1.5.3/jsgrid.min.js"></script>
	 * recomended:
	 * <link type="text/css" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jsgrid/1.5.3/jsgrid.min.css" />
	 * <link type="text/css" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jsgrid/1.5.3/jsgrid-theme.min.css" />
	 * @param name
	 */
	public Grid(String name, String dataUrl) {
		this.name = name;
		this.dataUrl = dataUrl;
	}
	
	public String getName() {
		return name;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public boolean isHeading() {
		return heading;
	}

	public boolean isFiltering() {
		return filtering;
	}

	public boolean isAutoload() {
		return autoload;
	}

	public boolean isSorting() {
		return sorting;
	}

	public boolean isSelecting() {
		return selecting;
	}

	public boolean isPaging() {
		return paging;
	}

	public boolean isPageLoading() {
		return pageLoading;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageButtonCount() {
		return pageButtonCount;
	}

	public boolean isInserting() {
		return inserting;
	}

	public boolean isEditing() {
		return editing;
	}
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public GridButtons getButtons() {
		return buttons;
	}

	public void setSize(String width, String height) {
		this.width = width;
		this.height = height;
	}
	
	public Column addTextColumn(String name, String title) {
		Column col = new Column(name, title, "text");
		columns.add(col);
		return col;
	}
	
	public Column addNumberColumn(String name, String title) {
		Column col = new Column(name, title, "number");
		columns.add(col);
		return col;
	}
	
	public Column addCheckboxColumn(String name, String title) {
		Column col = new Column(name, title, "checkbox");
		columns.add(col);
		return col;
	}
	
	public Column addDateTimeColumn(String name, String title) {
		Column col = new Column(name, title, "datetime");
		columns.add(col);
		return col;
	}
	
	public Column addSelectColumn(String name, String title, Map<String, String> options) {
		Column col = new SelectColumn(name, title, options);
		columns.add(col);
		return col;
	}
	
	public GridButtons setButtons() {
		GridButtons buttons = new GridButtons();
		this.buttons = buttons;
		return buttons;
	}
	
}
