package toti.files.env;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import toti.common.functions.PropertiesLoader;
import toti.common.structures.DictionaryValue;

public class PropertiesEnvSource {

	public static Map<Object, Value> parse(String path) throws IOException {
		Properties prop = PropertiesLoader.loadProperties(path);
		Map<Object, Value> result = new HashMap<>();
		prop.forEach((raw, value)->{
			/*
			parse(result, raw.toString(), value);
			/*/
			parse(isMap->{
				return new Value(result);
			}, raw.toString(), value);
			//*/
		});
		return result;
	}

	/*private static void parse(Map<Object, Value> result, String key, Object value) {
		int firstDotIndex = key.indexOf(".");

		String moduleKey = key;
		if (firstDotIndex > -1) {
			moduleKey = key.substring(0, firstDotIndex);
		}
		if (firstDotIndex > -1) {
			String subKey = key.substring(firstDotIndex + 1);
			Map<Object, Value> subResult = new HashMap<>();
			if (result.containsKey(moduleKey)) {
				subResult = result.get(moduleKey).getMap();
			} else {
				result.put(moduleKey, new Value(subResult));
			}
			parse(subResult, subKey, value);
		} else {
			result.put(key, new Value(value));
		}
	}*/


	private static void parse(Function<Value, Value> parentFactory, String key, Object value) {
		int firstDotIndex = key.indexOf(".");
		String moduleKey = key;
		String subKey = "";
		if (firstDotIndex > -1) {
			moduleKey = key.substring(0, firstDotIndex);
			subKey = key.substring(firstDotIndex + 1);
		}
		DictionaryValue dv = new DictionaryValue(moduleKey);
		if (dv.is(Integer.class)) {
			Value parent = parentFactory.apply(createValue(false));
			if (subKey.isEmpty()) {
				parent.getList().add(new Value(value));
			} else {
				parse(val->{
					if (parent.getList().size() > dv.getInteger()) {
						return parent.getList().get(dv.getInteger());
					} else {
						parent.getList().add(val);
						return val;
					}
				}, subKey, value);
			}
		} else {
			Value parent = parentFactory.apply(createValue(true));
			if (subKey.isEmpty()) {
				parent.getMap().put(moduleKey, new Value(value));
			} else {
				parse(val->{
					if (parent.getMap().containsKey(dv.getString())) {
						return parent.getMap().get(dv.getString());
					} else {
						parent.getMap().put(dv.getString(), val);
						return val;
					}
				}, subKey, value);
			}
		}
	}

	private static Value createValue(boolean isMap) {
		if (isMap) {
			return new Value(new HashMap<>());
		}
		return new Value(new LinkedList<>());
	}

}
