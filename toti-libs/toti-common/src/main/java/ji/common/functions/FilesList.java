package ji.common.functions;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Loads file names from directory. 
 * Can be recursive or not. Is able to load names from directory in file system and from jar file.
 * 
 * @author Ondřej Němec
 *
 */
public class FilesList {
	
	// TODO maybe improve. use file extension, inputstream loader

	private final List<String> files;

	/**
	 * Create new FilesList
	 * 
	 * @param folder String path to folder or jar
	 * @param recursive boolean if load files in sub dirs
	 * @return new FilesList
	 * @throws Exception
	 */
	public static FilesList get(String folder, boolean recursive) throws Exception {
		return new FilesList(folder, recursive);
	}

	private FilesList(String folder, boolean recursive) throws Exception {
		this.files = getFiles(folder, recursive);
	}

	/***************/
	
	/**
	 * Get names of files in directory
	 * 
	 * @return {@link List} of {@link String}
	 */
	public List<String> getFiles() {
		return files;
	}

	private List<String> getFiles(String folder, boolean recursive) throws Exception {
		List<String> result = new LinkedList<>();
		Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(folder);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			if (url.toString().startsWith("rsrc:")) {
				throw new IOException("Unsupported protocol: " + url);
			// jar - in separated jar - gradle build
			} else if (url.toString().startsWith("jar:") || url.toString().startsWith("jrt:")) {
				URLConnection con = url.openConnection();
				List<String> files = new LinkedList<>();
				if (con instanceof JarURLConnection) {
					JarURLConnection connection = (JarURLConnection) url.openConnection();
					JarFile file = connection.getJarFile();
					Enumeration<JarEntry> entries = file.entries();
					List<String> dirs = new LinkedList<>();
					while (entries.hasMoreElements()) {
						JarEntry e = entries.nextElement();
						if (e.getName().equals(folder)) {
							continue;
						} else if (e.getName().startsWith(folder)) {
							if (e.getName().endsWith("/")) {
								if (!recursive) {
									dirs.add(e.getName());
								}
							} else {
								if (recursive || !dirs.contains(e.getName())) {
									files.add(e.getName().replace(folder + "/", ""));
								}
							}
						}
					}
				}
				result.addAll(files);
			} else if (url.toString().startsWith("file:")) {
				String path = url.getPath();
				File dir = new File(path);
				result.addAll(addFileName(dir, dir + File.separator, recursive));
			} else {
				throw new IOException("Unsupported protocol: " + url);
			}
		}
		File external = new File(folder);
		if (external.exists() && external.isDirectory()) {
			result.addAll(addFileName(external, external.getAbsolutePath() + File.separator, recursive));
		}
		result.sort(Comparator.naturalOrder());
		return result;
	}
	
	private List<String> addFileName(File dir, String replacement, boolean recursive) {
		List<String> result = new LinkedList<>();
		File[] files = dir.listFiles();
		if (files == null) {
			return result;
		}
		for (File f : files) {
			if (f.isDirectory()) {
				if (recursive) {
					result.addAll(
						addFileName(f, replacement, recursive)
					);
				}
			} else {
				result.add(f.getAbsolutePath().replace(replacement, "").replaceAll("\\\\", "/"));
			}
		}
		//Collections.sort(files);
		return result;
	}

	/*******************/
/*
	private List<String> jar(String expectedNamespace, boolean recursive) throws Exception {
		List<String> files = new LinkedList<>();
		for (URL url : new URL[] { ClassLoader.getSystemResource(expectedNamespace) }) {
			if (url == null) {
				continue;
			}
			URLConnection con = url.openConnection();
			if (con instanceof JarURLConnection) {
				JarURLConnection connection = (JarURLConnection) url.openConnection();
				JarFile file = connection.getJarFile();
				Enumeration<JarEntry> entries = file.entries();
				List<String> dirs = new LinkedList<>();
				while (entries.hasMoreElements()) {
					JarEntry e = entries.nextElement();
					if (e.getName().startsWith(expectedNamespace)) {
						if (e.getName().endsWith("/")) {
							if (!recursive) {
								dirs.add(e.getName());
							}
						} else {
							if (recursive || !dirs.contains(e.getName())) {
								files.add(e.getName().replace(expectedNamespace + "/", ""));
							}
						}
					}
				}
			} else if ("file".equals(url.getProtocol())){
				File file = new File(URLDecoder.decode(url.getPath(), "UTF-8"));
				files.addAll(
					addFileName(file, file + File.separator, recursive)
				);
			}
		}
		return files;
	}

	private List<String> rsrc(String expectedNamespace, boolean recursive) throws Exception {
		List<String> files = new LinkedList<>();
		for (URL url : new URL[] { ClassLoader.getSystemResource(expectedNamespace) }) {
			if (url == null) {
				continue;
			}
			JarURLConnection connection = (JarURLConnection) url.openConnection();
			JarFile file = connection.getJarFile();
			Enumeration<JarEntry> entries = file.entries();
			List<String> dirs = new LinkedList<>();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (e.getName().startsWith(expectedNamespace)) {
					if (e.getName().endsWith("/")) {
						if (!recursive) {
							dirs.add(e.getName());
						}
					} else {
						if (recursive || !dirs.contains(e.getName())) {
							files.add(e.getName().replace(expectedNamespace + "/", ""));
						}
					}
				}
			}
		}
		return files;
	}

	private List<String> dirTree(String folder, boolean recursive) throws Exception {
		folder = folder.startsWith("/") ? folder.substring(1) : folder;
		ClassLoader loader = getClass().getClassLoader();
		URL url = loader.getResource(folder);
		if (url == null) {
			File f = new File(folder);
			this.url = f.toURI().toURL();
			return addFileName(f, f.getAbsolutePath() + File.separator, recursive);
		}
		String path = url.getPath();
		File dir = new File(path);
		this.url = dir.toURI().toURL();
		return addFileName(dir, dir + File.separator, recursive);
	}
*/

}
