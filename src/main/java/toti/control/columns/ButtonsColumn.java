package toti.control.columns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.control.inputs.Button;

public class ButtonsColumn implements Column {
	
	private final String name;
	private String title;
	
	private final List<Map<String, Object>> buttons = new LinkedList<>();
	private final List<Map<String, Object>> globalButtons = new LinkedList<>();
	
	public ButtonsColumn(String name) {
		this.name = name;
		this.title = name;
	}
	
	public ButtonsColumn addButton(Button button) {
		buttons.add(button.getInputSettings());
		return this;
	}
	
	public ButtonsColumn addGlobalButton(Button button) {
		globalButtons.add(button.getInputSettings());
		return this;
	}
	
	public ButtonsColumn setTitle(String title) {
		this.title = title;
		return this;
	}

	// TODO add refresh button
	public ButtonsColumn setResetFiltersButton(Button button) {
        Map<String, Object> settings = button.getInputSettings();
        settings.put("is_reset", true);
        globalButtons.add(settings);
        return this;
    }

	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("type", "buttons");
		json.put("title", title);
		json.put("useSorting", false);
		json.put("buttons", buttons);
		json.put("globalButtons", globalButtons);
		return json;
	}
	
}
