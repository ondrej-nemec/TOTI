package mvc.control.gridColumns;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Column;

public class TextColumn extends Column {
	
	private Integer size = null;
	private Integer maxLength = null;
	private Integer minLength = null;
	
	public TextColumn(String name) {
		super(name, "value", "text");
	}
	
	public void setSize(Integer size) {
		this.size = size;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	@Override
	public Map<String, Object> getParams() {
		Map<String, Object> json = new HashMap<>();
		if (size != null) {
			json.put("size", size);
		}
		if (maxLength != null) {
			json.put("maxlength", maxLength);
		}
		if (minLength != null) {
			json.put("minlength", minLength);
		}
		return json;
	}

}
