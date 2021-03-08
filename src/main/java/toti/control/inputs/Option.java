package toti.control.inputs;

import java.util.HashMap;
import java.util.Map;

public class Option implements Input {

	private final String value;
	private final String title;
	private boolean disabled;
	private String group;
	
	public static Option input(String value, String title) {
		return new Option(value, title);
	}
	
	private Option(String value, String title) {
		this.value = value;
		this.title = title;
	}
	
	public Option setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}
	
	public Option setOptGroup(String optGroup) {
		this.group = optGroup;
		return this;
	}
	
	@Override
	public Map<String, Object> getInputSettings() {
		Map<String, Object> set = new HashMap<>();
		set.put("type", "option");
		set.put("value", value);
		set.put("title", title);
		if (disabled) {
			set.put("disabled", "true");
		}
		if (group != null) {
			set.put("optgroup", group);
		}
		return set;
	}

}
