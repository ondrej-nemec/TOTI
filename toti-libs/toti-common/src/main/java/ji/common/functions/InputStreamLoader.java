package ji.common.functions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class provide usefull function for creating {@link InputStream} pointing to file
 * 
 * @author Ondřej Němec
 *
 */
public class InputStreamLoader {

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
	public static InputStream createInputStream(Class<?> clazz, final String path) throws IOException {
		try {
			InputStream is = clazz.getResourceAsStream("/" + path);
			if (is != null) {
				return is;
			}
		} catch (Exception e) { /* ignored */ }
		try {
			InputStream is = new FileInputStream(path);
			return is;
		} catch (Exception e) { /* ignored */ }
		throw new FileNotFoundException(path);
	}
	
}
