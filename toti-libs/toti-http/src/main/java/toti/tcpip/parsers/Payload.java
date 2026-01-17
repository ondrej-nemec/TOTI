package toti.tcpip.parsers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import toti.lib.common.structures.DictionaryValue;
import toti.tcpip.structures.RequestParameters;

public class Payload {

	public void writeItem(BiConsumer<String, Object> addValue, String name, Object value) {
		if (value == null) {
			addValue.accept(name, value);
		} else if (value instanceof Map || value instanceof RequestParameters) {
			writeItem(addValue, name, new DictionaryValue(value).getMap());
		} else if (value instanceof List) {
			writeItem(addValue, name, new DictionaryValue(value).getList());
		} else {
			addValue.accept(name, value);
		}
	}
	
	private void writeItem(BiConsumer<String, Object> addValue, String name, List<Object> params) {
		for (Object value : params) {
			writeItem(addValue, name + "[]", value);
		}
	}
	
	private void writeItem(BiConsumer<String, Object> addValue, String parent, Map<String, Object> params) {
		params.forEach((name, value)->{
			writeItem(
				addValue,
				parent == null ? name : String.format("%s[%s]", parent, name), 
				params.get(name)
			);
		});
	}
	
	/***********************/
	
	public void readItem(
			BiConsumer<String, Object> addParameter,
			Function<String, Object> getParameter,
			String key, String value) {
		key = key.replace("[]", "[=]").replace("[", "&").replace("]", "");
		String[] keys = key.split("\\&");
		int keyCount = StringUtils.countMatches(key, "&");
		if (keyCount == 0) {
			addParameter.accept(key, value);
			return;
		}
		readItem(addParameter, getParameter, keys, value);
	}

	private void readItem(
			BiConsumer<String, Object> addParameter,
			Function<String, Object> getParameter,
			String[] keys, String value) {
		String key = keys[0]; // URLDecoder.decode(keys[0], StandardCharsets.UTF_8.toString());
		Object o = getParameter.apply(key);
		if (o == null && keys[1].equals("=")) {
			addParameter.accept(key, new LinkedList<>());
		} else if (o == null) {
			addParameter.accept(key, new HashMap<>());
		}
		readItem(getParameter.apply(key), keys, 1, value);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void readItem(Object o, String[] keys, int index, String value) {
		if (index == keys.length) {
			return;
		}
		
		Object sub = null;
		if (o instanceof Map) {
			sub = Map.class.cast(o).get(keys[index]);
		} else if (o instanceof List) {
			List l = List.class.cast(o);
			if (!l.isEmpty()) {
				sub = l.get(l.size() - 1);
			}
		}
		boolean needInsert = false;
		if (index == keys.length - 1) {
			needInsert = true;
			sub = value;
		} else if (sub == null && keys[index+1].equals("=")) {
			needInsert = true;
			sub = new LinkedList<>();
		} else if (sub == null) {
			needInsert = true;
			sub = new HashMap<>();
		}
		if (needInsert && o instanceof Map) {
			Map.class.cast(o).put(keys[index], sub);
		} else if (needInsert && o instanceof List) {
			List.class.cast(o).add(sub);
		}
		readItem(sub, keys, index+1, value);
	}
}
