package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;

public class CallbackTag implements Tag {

	@Override
	public TagVariableMode getMode(String name) {
		return TagVariableMode.STRING;
	}

	@Override
	public String getName() {
		return "callback";
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
		return String.format(
			"((toti.templating.TemplateCallback)Class.forName(\"%s\").getDeclaredConstructor().newInstance()).call("
				+ "this,"
				+ "new DictionaryValue(variables.get(\"totiIdentity\")).getValue(toti.security.Identity.class),"
				+ "translator, authorizator, current"
			+ ");",
			params.get("name")
		);
	}

}
