package toti.ui.backend.help;

import java.util.HashMap;
import java.util.Map;

import ji.json.Jsonable;

public class Help implements Jsonable {

	private final Object value;
	private final Object title;
	private final String optgroup;
	private final boolean disabled;
	private final Map<String, Object> params;
	
	public Help(Object value, Object title) {
		this(value, title, null, false);
	}
	
	public Help(Object value, Object title, String optgroup, boolean disabled) {
		this.value = value;
		this.title = title;
		this.optgroup = optgroup;
		this.disabled = disabled;
		this.params = new HashMap<>();
	}

	public Object getValue() {
		return value;
	}

	public Object getTitle() {
		return title;
	}

	public String getOptgroup() {
		return optgroup;
	}

	public boolean isDisabled() {
		return disabled;
	}
	
	public Help addParameter(String name, Object value) {
		this.params.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return "Help [value=" + value + ", title=" + title + ", optgroup=" + optgroup + ", disabled=" + disabled + "]";
	}
	
	@Override
	public Object toJson() {
		Map<String, Object> params = new HashMap<>(this.params);
		params.put("value", value);
		params.put("title", title);
		params.put("optgroup", optgroup);
		params.put("disabled", disabled);
		return params;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (disabled ? 1231 : 1237);
		result = prime * result + ((optgroup == null) ? 0 : optgroup.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Help other = (Help) obj;
		if (disabled != other.disabled) {
			return false;
		}
		if (optgroup == null) {
			if (other.optgroup != null) {
				return false;
			}
		} else if (!optgroup.equals(other.optgroup)) {
			return false;
		}
		if (params == null) {
			if (other.params != null) {
				return false;
			}
		} else if (!params.equals(other.params)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
	
}
