package toti.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DynamicList implements Input {

	private final String type = "dynamic";
	private final String name;
	private final String id;
	private final List<Input> fields;
	
	private String title = null;
	private boolean useAddButton = true;
	private boolean useRemoveButton = true;
	
	public static DynamicList input(String name) {
		return new DynamicList(name);
	}
	
	private DynamicList(String name) {
		this.name = name;
		this.fields = new LinkedList<>();
		this.id = "id-" + name;
	}
	
	public DynamicList addInput(Input input) {
		fields.add(input);
		return this;
	}
	
	public DynamicList setTitle(String title) {
		this.title = title;
		return this;
	}
	
	/**
	 * Has effect only without form template
	 * @param useAddButton
	 * @return
	 */
	public DynamicList useAddButton(boolean useAddButton) {
		this.useAddButton = useAddButton;
		return this;
	}
	
	/**
	 * Has effect only without form template
	 * @param useRemoveButton
	 * @return
	 */
	public DynamicList useRemoveButton(boolean useRemoveButton) {
		this.useRemoveButton = useRemoveButton;
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("id", id);
		json.put("type", type);
		json.put("fields", fields);
		if (title != null) {
			json.put("title", title);
		}
		json.put("addButton", useAddButton);
		json.put("removeButton", useRemoveButton);
		return json;
	}

}
