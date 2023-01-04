package toti.control.inputs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DynamicList implements Input {

	private final String type = "dynamic";
	private final String name;
	private final List<Input> fields;
	private Map<String, Object> load;
	
	private String title = null;
	private boolean useAddButton = true;
	private boolean useRemoveButton = true;
	
	private boolean addFirstBlank = true;
	
	public static DynamicList input(String name) {
		return new DynamicList(name);
	}
	
	private DynamicList(String name) {
		this.name = name;
		this.fields = new LinkedList<>();
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
	
	public DynamicList setLoadData(String url, String method) {
        return setLoadData(url, method, new HashMap<>());
    }
	
	public DynamicList setAddFirstBlank(boolean addFirstBlank) {
		this.addFirstBlank = addFirstBlank;
		return this;
	}

    public DynamicList setLoadData(String url, String method, Map<String, String> params) {
        this.load = new HashMap<>();
        load.put("url", url);
        load.put("method", method);
        load.put("params", params);
        this.useAddButton = false;
        this.useRemoveButton = false;
        return this;
    }
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> json = new HashMap<>();
		json.put("name", name);
		json.put("type", type);
		json.put("fields", fields);
		if (title != null) {
			json.put("title", title);
		}
		if (load != null) {
            json.put("load", load);
        }
		json.put("addButton", useAddButton);
		json.put("removeButton", useRemoveButton);
		json.put("addFirrstBlank", addFirstBlank);
		return json;
	}

}
