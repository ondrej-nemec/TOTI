package toti.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InputList implements Input {

	private final String type;
	private final String name;
	private final String id;
	private final List<Input> fields;
	
	public static InputList input(String name) {
		return new InputList(name);
	}
/*
	public static InputList input() {
		return new InputList(null);
	}
*/
	private InputList(String name) {
		this.name = name;
		this.id = "id-" + name;
		this.type = "list";
		this.fields = new LinkedList<>();
	}
	
	public InputList addInput(Input input) {
		fields.add(input);
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		if (name != null) {
			json.put("name", name);
		}
		json.put("id", id);
		json.put("type", type);
		json.put("fields", fields);
		return json;
	}
	
}
