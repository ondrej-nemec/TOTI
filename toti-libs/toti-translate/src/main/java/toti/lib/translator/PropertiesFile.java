package toti.lib.translator;

import java.io.IOException;
import java.util.Properties;

import toti.lib.common.functions.PropertiesLoader;

public class PropertiesFile implements MessagesFile {
	
	private final Properties properties;

	// TODO test it
	public PropertiesFile(String file) throws IOException {
		this.properties = PropertiesLoader.loadProperties(file);
	}

	@Override
	public String getMessage(String key) {
		return properties.getProperty(key);
	}

}
