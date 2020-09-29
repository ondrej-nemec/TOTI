package mvc.control;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import mvc.control.gridColumns.ActionColumn;
import mvc.control.gridColumns.GridButton;

public class Grid {
	
	public static final String TEMPLATE_FILE = "mvc/templates/Grid.jsp";
	
	private final String name;
	private final String sourceUrl;
	private String rowUnique = "id";
	private String sourceMethod = "GET";
	private int pagesButtonCount = 5;
	private List<Integer> pageSizes = Arrays.asList(5, 10, 20, 50, 100);
	private int defatulPageSize = 20;
	
	private final List<Column> columns = new LinkedList<>();
	private final List<GridButton> buttons = new LinkedList<>();
	private final List<ActionColumn> actions = new LinkedList<>();
	
	public Grid(String name, String sourceUrl) {
		this.name = name;
		this.sourceUrl = sourceUrl;
	}

	public static String getTemplateFile() {
		return TEMPLATE_FILE;
	}

	public String getName() {
		return name;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public String getRowUnique() {
		return rowUnique;
	}

	public String getSourceMethod() {
		return sourceMethod;
	}

	public int getPagesButtonCount() {
		return pagesButtonCount;
	}

	public List<Integer> getPageSizes() {
		return pageSizes;
	}

	public int getDefatulPageSize() {
		return defatulPageSize;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public List<GridButton> getButtons() {
		return buttons;
	}

	public List<ActionColumn> getActions() {
		return actions;
	}

	public void setRowUnique(String rowUnique) {
		this.rowUnique = rowUnique;
	}

	public void setSourceMethod(String sourceMethod) {
		this.sourceMethod = sourceMethod;
	}

	public void setPagesButtonCount(int pagesButtonCount) {
		this.pagesButtonCount = pagesButtonCount;
	}

	public void setPageSizes(List<Integer> pageSizes) {
		this.pageSizes = pageSizes;
	}

	public void setDefatulPageSize(int defatulPageSize) {
		this.defatulPageSize = defatulPageSize;
	}

	public void addColumn(Column column) {
		columns.add(column);
	}
	
	public void addButtons(GridButton button) {
		buttons.add(button);
	}

	public void addAction(ActionColumn action) {
		actions.add(action);
	}
}
