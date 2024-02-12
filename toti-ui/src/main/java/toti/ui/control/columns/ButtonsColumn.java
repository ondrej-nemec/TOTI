package toti.ui.control.columns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import toti.ui.control.inputs.Button;

public class ButtonsColumn implements Column {
	
	private final String name;
	private String title;
	
	private final List<Button> buttons = new LinkedList<>();
	private final List<Button> globalButtons = new LinkedList<>();
	
	public ButtonsColumn(String name) {
		this.name = name;
		this.title = name;
	}
	
	public ButtonsColumn addButton(Button button) {
		buttons.add(button);
		return this;
	}
	
	public ButtonsColumn addGlobalButton(Button button) {
		globalButtons.add(button);
		return this;
	}
	
	public ButtonsColumn setTitle(String title) {
		this.title = title;
		return this;
	}

	// TODO add refresh button
	public ButtonsColumn setResetFiltersButton(Button button) {
		globalButtons.add(button);
		return this;
	}

	@Override
	public Map<String, Object> getGridSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("type", "buttons");
		json.put("title", title);
		json.put("useSorting", false);
		json.put("buttons", buttons.stream().map(b->b.getInputSettings()).collect(Collectors.toList()));
		json.put("globalButtons", globalButtons.stream().map(b->b.getGlobalSettings()).collect(Collectors.toList()));
		return json;
	}
	
}
