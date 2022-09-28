package toti.control.columns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.control.inputs.Button;
import toti.control.inputs.Reset;

public class ButtonsColumn implements Column {
	
	private final String name;
	private String title;
	
	private boolean useResetButton = true;
	// TODO add refresh button
	private final List<Map<String, Object>> buttons = new LinkedList<>();
	private final List<Map<String, Object>> mainButtons = new LinkedList<>();
	
	public ButtonsColumn(String name) {
		this.name = name;
		this.title = name;
	}
	
	public ButtonsColumn addButton(Button button) {
		buttons.add(button.getInputSettings());
		return this;
	}
	
	public ButtonsColumn addMainButton(Button button) {
		mainButtons.add(button.getInputSettings());
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
		json.put("type", "buttons");
		json.put("title", title);
	//	json.put("useResetButton", useResetButton);
		json.put("sorting", false);
		json.put("buttons", buttons);

		List<Object> mains = new LinkedList<>(mainButtons);
		if (useResetButton) {
			mains.add(Reset.create("").getInputSettings());
		}
		json.put("mainButtons", mains);
		return json;
	}
	
}
