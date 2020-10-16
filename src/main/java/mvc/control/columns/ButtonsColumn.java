package mvc.control.columns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import json.JsonStreamException;
import mvc.control.Jsonable;
import mvc.control.inputs.Button;
import mvc.templating.Template;

public class ButtonsColumn implements Jsonable, Column {
	
	private final String name;
	private final String type; // value, buttons, actions
	private String title;
	
	private final boolean useSorting = false;
	private final List<Button> buttons = new LinkedList<>();
	
	public ButtonsColumn(String name) {
		this.name = name;
		this.type = "buttons";
		this.title = name;
	}
	
	public ButtonsColumn addButton(Button button) {
		buttons.add(button);
		return this;
	}
	
	public ButtonsColumn setTitle(String title) {
		this.title = title;
		return this;
	}

	public String toJsonString() throws JsonStreamException {
		Map<String, Object> json = new HashMap<>();
		json.put("name", Template.escapeVariable(name));
		json.put("type", type);
		json.put("title", Template.escapeVariable(title));
		json.put("sorting", useSorting);
		json.put("buttons", buttons);
		return toJson(json);
	}
	
}
