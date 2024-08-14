package ji.env;

import java.io.IOException;
import java.util.Properties;

import ji.common.functions.Env;
import ji.common.functions.PropertiesLoader;

public class PropertiesEnv implements Env {
	
	private final Properties properties;
	private final String key;
	
	public static PropertiesEnv create(String path) throws IOException {
		return new PropertiesEnv(PropertiesLoader.loadProperties(path), null);
	}
	
	public PropertiesEnv(Properties properties) {
		this(properties, null);
	}

	private PropertiesEnv(Properties properties, String key) {
		this.properties = properties;
		this.key = key;
	}
	
	@Override
	public Object getValue(String name) {
		if (key == null) {
			return properties.get(name);
		}
		return properties.get(key + "." + name);
	}

	@Override
	public void clear() {
		properties.clear();
	}

	@Override
	public Env getModule(String key) {
		return new PropertiesEnv(properties, (this.key == null ? "" : this.key + ".") + key);
	}

	@Override
	public String toString() {
		return key + ": " +  properties.toString();
	}

}
