package toti.templating.tags;

import java.util.Map;

import toti.templating.Tag;
import toti.templating.TagVariableMode;
import toti.templating.TemplateException;

public class ForEachTag implements Tag {
	
	@Override
	public TagVariableMode getMode(String name) {
		if ("map".equals(name)) {
			return TagVariableMode.DICTIONARY_VALUE;
		}
		if ("collection".equals(name)) {
			return TagVariableMode.DICTIONARY_VALUE;
		}
		return TagVariableMode.CODE;
	}

	@Override
	public String getName() {
		return "foreach";
	}

	@Override
	public String getPairStartCode(Map<String, String> params) {
		if (params.get("map") != null) {
			String[] keyP = parseItem(params.get("key"));
			String[] valueP = parseItem(params.get("value"));
			return String.format(
				"for(%s %s:Template.toMap(%s,%s.class,%s.class).keySet()){"
				+ "%s %s=Template.toMap(%s,%s.class,%s.class).get(%s);"
				+ "initNode(new HashMap<>());"
				+ "addVariable(\"%s\", %s);"
				+ "addVariable(\"%s\", %s);",
				keyP[0], keyP[1], params.get("map"), keyP[0], valueP[0],
				valueP[0], valueP[1], params.get("map"), keyP[0], valueP[0], keyP[1], 
				keyP[1], keyP[1], valueP[1], valueP[1]
			);
		}
		String[] item = parseItem(params.get("item"));
		return String.format(
				"for(%s %s:Template.toIterable(%s,%s.class)){"
				+ "initNode(new HashMap<>());"
				+ "addVariable(\"%s\", %s);",
				item[0], item[1], params.get("collection"), item[0],
				item[1], item[1]
		);
	}
	
	private String[] parseItem(String item) {
		item = item.trim();
		String[] items = item.split(" +");
		if (items.length > 2) {
			throw new TemplateException("Foreach tag: incorrect syntax (" + item + ")");
		}
		if (items.length == 1) {
			return new String[] {"Object", item};
		}
		return items;
	}

	@Override
	public String getPairEndCode(Map<String, String> params) {
		return "flushNode();}";
	}

	@Override
	public String getNotPairCode(Map<String, String> params) {
		return "";
	}

}
