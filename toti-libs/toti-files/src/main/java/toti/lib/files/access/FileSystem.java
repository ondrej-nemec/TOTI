package toti.lib.files.access;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileSystem {
	
	// TODO get one file by name, not by folder

	public static List<FileInfo> get(String search, boolean recursive, SearchFilter filterFileType) throws IOException {
		if (search == null) {
			throw new NullPointerException();
		}
		search = search.replace("\\", "/"); // replace \ with /
		if (search.endsWith("/")) {
			search = search.substring(0, search.length() - 1);
		}
		if (search.startsWith("/") || search.contains(":")) {
			throw new RuntimeException("Absolute paths are not allowed");
		}
		if (search.equals("")) {
			throw new RuntimeException("Empty search is not allowed");
		}
		List<FileInfo> result = new LinkedList<>();
		Consumer<FileInfo> addFile = (fileInfo)->{
			if (fileInfo.isDirectory()) {
				if (filterFileType != SearchFilter.FILES_ONLY) {
					result.add(fileInfo);
				}
			} else if (filterFileType != SearchFilter.DIRECTORY_ONLY) {
				if (recursive) {
					result.add(fileInfo);
				} else if (fileInfo.isInSearchRoot()) {
					result.add(fileInfo);
				}
			}
		};
		Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(search);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			if (url.toString().startsWith("rsrc:")) {
				throw new IOException("Unsupported protocol: " + url);
			// jar - in separated jar - gradle build
			} else if (url.toString().startsWith("jar:") || url.toString().startsWith("jrt:")) {
				addJar(search, url, addFile);
			} else if (url.toString().startsWith("file:")) {
				String path = url.getPath();
				File file = new File(path);
				addResourceAndExternal(file, search, addFile, recursive, FileMode.RESOURCE);
			} else {
				throw new IOException("Unsupported protocol: " + url);
			}
		}
		File external = new File(search);
		if (external.exists()) {
			addResourceAndExternal(external, search, addFile, recursive, FileMode.EXTERNAL);
		}

		result.sort((a, b)->{
			int compare = a.getRelativePath().compareTo(b.getRelativePath());
			if (compare != 0) {
				return compare;
			}
			compare = a.getMode().compareTo(b.getMode());
			if (compare != 0) {
				return compare;
			}
			return a.getAbsolutePath().compareTo(b.getAbsolutePath());
		});
		return result;
	}
	
	private static void addJar(String search, URL url, Consumer<FileInfo> addFile) throws IOException {
		URLConnection con = url.openConnection();
		if (con instanceof JarURLConnection connection) {
			JarFile file = connection.getJarFile();
			Enumeration<JarEntry> entries = file.entries();
			while (entries.hasMoreElements()) {
				JarEntry e = entries.nextElement();
				if (e.getName().startsWith(search)) {
					String relativePath = "";
					String name = "";
					String absolutePath = url.toString();
					FileType type = FileType.FILE;
					if (e.getName().equals(search)) {
						int index = e.getName().lastIndexOf("/");
						if (index == -1) {
							name = e.getName();
						} else {
							name = e.getName().substring(index + 1);
						}
					} else if (e.getName().equals(search + "/")) {
						type = FileType.DIRECTORY;
						name = ""; // search root
					} else {
						relativePath = e.getName().replace(search + "/", "");
						if (relativePath.endsWith("/")) {
							relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
							type = FileType.DIRECTORY;
						}
						int index = relativePath.lastIndexOf("/");
						if (index == -1) {
							name = relativePath;
						} else {
							name = relativePath.substring(index + 1);
						}
						if (!relativePath.isEmpty()) {
							absolutePath += "/" + relativePath;
						}
					}

					addFile.accept(new FileInfo(
						type, FileMode.JAR,
						name, relativePath, absolutePath,
						e.getLastModifiedTime().toMillis()
					));
				}
			}
		}
	}
	
	private static void addResourceAndExternal(File rootFile, String search, Consumer<FileInfo> addFile, boolean recursive, FileMode mode) {
		if (rootFile.isFile()) {
			addFile(rootFile, addFile, search, recursive, mode);
		} else if (rootFile.isDirectory()) {
			for (File f : rootFile.listFiles()) {
				addFile(f, addFile, search, recursive, mode);
			}
		}
	}

	private static void addFile(File file, Consumer<FileInfo> addFile, String search, boolean recursive, FileMode mode) {
		String absolutePath = file.getAbsolutePath().replaceAll("\\\\", "/");
		int index = absolutePath.indexOf(search);
		if (index < 0) {
			// TODO
		}
		String relativePath = "";
		int relPathStartIndex = index + search.length() + 1;
		if (relPathStartIndex < absolutePath.length()) {
			relativePath = absolutePath.substring(relPathStartIndex);
		}
		FileInfo fileInfo = new FileInfo(
			file.isDirectory() ? FileType.DIRECTORY : (file.isFile() ? FileType.FILE : FileType.UNKNOWN),
			mode, file.getName(), relativePath, absolutePath, file.lastModified()
		);
		addFile.accept(fileInfo);
		if (fileInfo.isDirectory() && recursive) {
			addResourceAndExternal(file, search, addFile, recursive, mode);
		}
	}

}
