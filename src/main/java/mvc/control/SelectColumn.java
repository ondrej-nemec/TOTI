package mvc.control;

import java.util.Map;

import json.JsonStreamException;
import json.OutputJsonStream;
import json.providers.OutputStringProvider;
import mvc.templating.Template;

public class SelectColumn extends Column {

	private final Map<String, String> options;
	
	public SelectColumn(String name, String title, Map<String, String> options) {
		super(name, title, "select");
		this.options = options;
	}
	
	public String getOptions() throws JsonStreamException {
		OutputStringProvider provider = new OutputStringProvider();
		OutputJsonStream stream = new OutputJsonStream(provider);
		stream.startDocument();
		stream.writeListStart();
		options.forEach((value, text)->{
			try {
				stream.writeObjectStart();
				stream.writeObjectValue("value", Template.escapeVariable(value));
				stream.writeObjectValue("text", Template.escapeVariable(text));
				stream.writeObjectEnd();
			} catch (JsonStreamException e) {
				throw new RuntimeException(e);
			}
		});
		stream.writeListEnd();
		return provider.getJson().substring(1);
	}

}
