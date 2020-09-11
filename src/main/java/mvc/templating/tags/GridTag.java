package mvc.templating.tags;

import java.util.Map;

import mvc.control.Grid;
import mvc.templating.Tag;

public class GridTag implements Tag {

	@Override
	public String getName() {
		return "grid";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return String.format(
				"{"
				+ "Template temp = templateFactory.getFrameworkTemplate(\"%s\");"
				+ "temp.getClass().getDeclaredField(\"b\").set(temp,b);"
				+ "temp.getClass().getDeclaredField(\"blocks\").set(temp,blocks);"
				+ "Map<String, Object>gridVars=new HashMap<>(variables);"
				+ "mvc.control.Grid grid=(mvc.control.Grid)(variables.get(\"%s\"));"
				+ "gridVars.put(\"gridName\",grid.getName());"
				+ "gridVars.put(\"width\",grid.getWidth());"
				+ "gridVars.put(\"height\",grid.getHeight());"
				+ "gridVars.put(\"filtering\",grid.isFiltering());"
				+ "gridVars.put(\"inserting\",grid.isInserting());"
				+ "gridVars.put(\"editing\",grid.isEditing());"
				+ "gridVars.put(\"autoload\",grid.isAutoload());"
				+ "gridVars.put(\"sorting\",grid.isSorting());"
				+ "gridVars.put(\"paging\",grid.isPaging());"
				+ "gridVars.put(\"selecting\",grid.isSelecting());"
				+ "gridVars.put(\"pageLoading\",grid.isPageLoading());"
				+ "gridVars.put(\"pageSize\",grid.getPageSize());"
				+ "gridVars.put(\"pageButtonCount\",grid.getPageButtonCount());"
				+ "gridVars.put(\"apiUrl\",grid.getDataUrl());"
				+ "gridVars.put(\"fields\",grid.getColumns());"
				+ "gridVars.put(\"buttons\",grid.getButtons());"
				+ "temp.create(templateFactory,gridVars,translator);"
				+ "}",
				Grid.TEMPLATE_FILE,
				params.get("grid")
		);
	}

}
