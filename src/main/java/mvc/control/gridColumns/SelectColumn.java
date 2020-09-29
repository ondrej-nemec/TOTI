package mvc.control.gridColumns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mvc.control.Column;
import mvc.templating.Template;

public class SelectColumn extends Column {

	private final Map<String, String> options;
	
	public SelectColumn(String name, Map<String, String> options) {
		super(name, "value", "select");
		this.options = options;
	}

	@Override
	public Map<String, Object> getParams() {
		Map<String, Object> json = new HashMap<>();
		List<Map<String, Object>> opt = new LinkedList<>();
		options.forEach((value, text)->{
			Map<String, Object> param = new HashMap<>();
			param.put("value", value);
			param.put("text", Template.escapeVariable(text));
			opt.add(param);
		});
		json.put("options", opt);
		return json;
	}

}
