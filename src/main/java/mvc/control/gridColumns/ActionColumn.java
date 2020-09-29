package mvc.control.gridColumns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.structures.Tuple2;
import mvc.control.Column;
import mvc.control.Html;

public class ActionColumn extends Column {
	
	private final List<Tuple2<String, String>> actions = new LinkedList<>();
	
	public ActionColumn() {
		super("actions", "actions", null);
	}
	
	public void addAction(String title, String link) {
		actions.add(new Tuple2<>(title, Html.replaceItemValues(link)));
	}
	
	public List<Tuple2<String, String>> getActions() {
		return actions;
	}

	@Override
	public Map<String, Object> getParams() {
		return new HashMap<>();
	}

}
