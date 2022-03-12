package toti.control.columns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.control.inputs.Button;

public class ButtonsColumn implements Column {
	
	private final String name;
	private final String type; // value, buttons, actions
	private String title;
	
	private boolean useResetButton = true;
	private final List<Map<String, Object>> buttons = new LinkedList<>();
	
	public ButtonsColumn(String name) {
		this.name = name;
		this.type = "buttons";
		this.title = name;
	}
	
	public ButtonsColumn addButton(Button button) {
		buttons.add(button.getInputSettings());
		return this;
	}
	
	public ButtonsColumn setResetFiltersButton(boolean useResetButton) {
		this.useResetButton = useResetButton;
		return this;
	}
	
	public ButtonsColumn setTitle(String title) {
		this.title = title;
		return this;
	}

	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("type", type);
		json.put("title", title);
		json.put("reset", useResetButton);
		json.put("sorting", false);
		json.put("buttons", buttons);
		return json;
	}
	
}
