package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class VariableSetTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		if ("name".equals(name)) {
			return TagVariableMode.CODE;
		}
		return TagVariableMode.CODE;
	}

	@Override
	public String getName() {
		return "set";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		return getNotPairCode(params);
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		// TODO value vs code: 123 vs i+1
		//*
		return String.format(
			"%s=%s;"
			+ "addVariable(\"%s\", %s);",
			params.get("name"), params.get("value"),
			params.get("name"), params.get("name")
		);
		// static <T> T parseVariable(Consumer<Object> addVariable, Object value, T object) {
		// 	addVariable.accept(value);
		// 	return (T)value;
		// }
		/*/
		String valueName = "v" + Math.abs(new Random().nextInt());
		return String.format(
		//	"System.err.println(getVariable(\"class_%s\"));"+
			"if(%s==null){"
			// + "%s=%s.getValue(getVariable(\"class_%s\").getClass());" // new DictionaryValue(%s)
				+ "%s=Template.parseVariable("
					+ "(%s)->addVariable(\"%s\",%s),"
					+ "%s.getValue(Class.class.cast(getVariable(\"class_%s\")))," // new DictionaryValue(%s)
					+ "%s"
				+ ");"
			+ "}else{"
				+ "%s=%s.getValue(%s.getClass());" // new DictionaryValue(%s)
				+ "addVariable(\"%s\", %s);"
			+ "}",
			params.get("name"), //if
			params.get("name"), 
			valueName, // consumer params
			params.get("name"), valueName, // add variable
			params.get("value"), params.get("name"), // dictionary value
			params.get("name"), // T param
			
			params.get("name"), params.get("value"), params.get("name"), // else
			params.get("name"), params.get("name") // add variable
		);
		//*/
	}

}
