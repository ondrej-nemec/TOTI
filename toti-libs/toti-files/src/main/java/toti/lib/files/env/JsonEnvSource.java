package toti.lib.files.env;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import toti.lib.common.functions.InputStreamLoader;
import toti.lib.common.structures.DictionaryValue;
import toti.lib.files.json.JsonReader;
import toti.lib.files.text.Text;

public class JsonEnvSource {

	public static Map<Object, Value> parse(String path) throws IOException {
		String json = Text.get().read(br->br.asString(), InputStreamLoader.createInputStream(JsonEnvSource.class, path));
		DictionaryValue data = new DictionaryValue(new JsonReader().read(json));
		return parse(data).getMap();
	}

	private static Value parse(DictionaryValue dv) {
		if (dv.is(Map.class)) {
			Value value = new Value(new HashMap<>());
			dv.getDictionaryMap().forEach((k, v)->{
				value.getMap().put(k, parse(v));
			});
			return value;
		} else if (dv.is(Collection.class)) {
			Value value = new Value(new LinkedList<>());
			dv.getDictionaryList().forEach((i, v)->{
				value.getList().add(parse(v));
			});
			return value;
		} else {
			return new Value(dv.getValue());
		}
	}

}
