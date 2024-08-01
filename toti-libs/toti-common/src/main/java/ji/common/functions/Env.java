package ji.common.functions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import ji.common.structures.Dictionary;
import ji.common.structures.MapDictionary;

/**
 * Application configuration.
 * Can be loaded from file using {@link PropertiesLoader}.
 * Works like {@link MapDictionary}, so you can get variables with type.
 * 
 * <pre>
 * Env env = new Env("conf/app.properties");
 * // or alternativelly
 * Properties prop = ...;
 * Env env2 = new Env(prop);
 * </pre>
 * 
 * @author Ondřej Němec
 *
 */
public class Env implements Dictionary<String> {
	
	private final Properties properties;
	private final String key;
	
	/**
	 * Create Env from given file.
	 * 
	 * @param path String path to properties file. Path can be relative or absolute. Path can be in or outside classpath.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Env(final String path) throws FileNotFoundException, IOException {
		this(PropertiesLoader.loadProperties(path), null); // loadProperties(path);
	}
	
	/**
	 * Create Env using given properties
	 * 
	 * @param properties {@link Properties}
	 */
	public Env(final Properties properties) {
		this(properties, null);
	}
	
	private Env(Properties properties, String key) {
		this.properties = properties;
		this.key = key;
	}

	/**
	 * Provide {@link Properties} given in constructor or loaded from file
	 * 
	 * @return {@link Properties}
	 */
	protected Properties getProperties() {
		return properties;
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
	
	/**
	 * Returns new instance of {@link Env}. In new Env are accessible only parameters starts with given key.
	 * 
	 * <pre>
	 * Env env = ...;
	 * String value = env.getProperty("key.subkey"); // works
	 * 
	 * Env sub = env.getModule("key");
	 * String value2 = sub.getProperty("subkey"); // works
	 * String value3 = sub.getProperty("key.subkey"); // returns null
	 * </pre>
	 * 
	 * @param key
	 * @return {@link Env}
	 */
	public Env getModule(String key) {
		return new Env(properties, (this.key == null ? "" : this.key + ".") + key);
	}

	@Override
	public String toString() {
		return key + ": " +  properties.toString();
	}
	
}
