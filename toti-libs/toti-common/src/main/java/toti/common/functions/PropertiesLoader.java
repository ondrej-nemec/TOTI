package toti.common.functions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Class loads <code>.properties</code> file and parses to {@link Properties}
 * <p>
 * File is loaded using {@link InputStreamLoader}, so it can be in or out classpath
 * 
 * @author Ondřej Němec
 * @see InputStreamLoader
 *
 */
public class PropertiesLoader {
	
	/**
	 * Load {@link Properties} with default encoding <code>UTF-8</code>
	 * 
	 * @param path String relative path to file
	 * @return {@link Properties} loaded properties
	 * @throws IOException
	 */
	public static Properties loadProperties(final String path) throws IOException {
		return loadProperties(path, "utf-8");
	}

	/**
	 * Load {@link Properties} with default encoding <code>UTF-8</code>
	 * 
	 * @param path String relative path to file
	 * @param charset String file encoding
	 * @return {@link Properties} loaded properties
	 * @throws IOException
	 */
	public static Properties loadProperties(final String path, String charset) throws IOException {
		Properties prop = new Properties();
		prop.load(new InputStreamReader(InputStreamLoader.createInputStream(PropertiesLoader.class, path), charset));
		return prop;
	}
	
}
