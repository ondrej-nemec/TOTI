package ji.env;

import java.io.IOException;
import java.util.Map;

import ji.common.functions.Env;
import ji.common.functions.InputStreamLoader;
import ji.common.structures.DictionaryValue;
import ji.files.text.Text;
import ji.json.JsonReader;
import ji.json.JsonStreamException;

public class JsonEnv implements Env {
	
	private final Map<String, Object> data;
	
	public static JsonEnv create(String path) throws JsonStreamException, IOException {
		String json = Text.get().read(br->br.asString(), InputStreamLoader.createInputStream(JsonEnv.class, path));
		return new JsonEnv(new DictionaryValue(new JsonReader().read(json)).getMap());
	}
	
	private JsonEnv(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public Object getValue(String key) {
		return data.get(key);
	}

	@Override
	public void clear() {
		data.clear();
	}

	@Override
	public Env getModule(String key) {
		return new JsonEnv(getMap(key));
	}

}
