package toti.lib.files.access;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class FileUtils {

	public static FileName parseName(String filePath) {
		int i = filePath.lastIndexOf('.');
		if (i == -1) {
			return new FileName(filePath, "");
		} else if (i == 0) {
			return new FileName("", filePath.substring(i+1));
		} else if (i > 0) {
			return new FileName(filePath.substring(0, i), filePath.substring(i+1));
		} else {
			return new FileName("", "");
		}
	}

	/**
	 * Create {@link InputStream} pointing to file.
	 * 
	 * File can be in compiled <code>jar</code>, resources or source directory of IDE or outside.
	 * At first, method tries load file using <code>getResourceAsStream</code>, then {@link FileInputStream}.
	 * <strong>Path to file has to be relative without first '/'.</strong>
	 * 
	 * @param clazz {@link Class} some class of your project for current class loader
	 * @param path String relative path to file without first '/'
	 * @return {@link InputStream} created stream
	 * @throws IOException
	 */
	public static InputStream createInputStream(String path) throws IOException {
		path = path.replace("\\", "/");
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		try {
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			if (is != null) {
				return is;
			}
		} catch (Exception e) { /* ignored */ }
		try {
			InputStream is = new FileInputStream(path);
			return is;
		} catch (FileNotFoundException e) { /* ignored */ }
		throw new FileNotFoundException(path);
	}
	
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
		prop.load(new InputStreamReader(createInputStream(path), charset));
		return prop;
	}

}
