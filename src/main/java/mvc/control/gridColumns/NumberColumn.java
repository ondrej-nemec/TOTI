package mvc.control.gridColumns;

import java.util.HashMap;
import java.util.Map;

import mvc.control.Column;

public class NumberColumn extends Column {

	private Integer step = null;
	private Integer min = null;
	private Integer max = null;
	
	public NumberColumn(String name) {
		super(name, "value", "number");
	}
	
	public void setStep(Integer step) {
		this.step = step;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	@Override
	public Map<String, Object> getParams() {
		Map<String, Object> json = new HashMap<>();
		if (step != null) {
			json.put("step", step);
		}
		if (max != null) {
			json.put("max", max);
		}
		if (min != null) {
			json.put("min", min);
		}
		return json;
	}
	
}
