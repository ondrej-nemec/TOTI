package toti.lib.files.access;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class FileList {

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
		Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(search);
		try {
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				URI uri = url.toURI();
				if (url.toString().startsWith("rsrc:")) {
					throw new IOException("Unsupported protocol: " + url);
				} else if (url.toString().startsWith("jar:") || url.toString().startsWith("jrt:")) {
					try (java.nio.file.FileSystem fs = FileSystems.newFileSystem(uri, Map.of())) {
						iterateFiles(result, fs.getPath("/" + search), search, recursive, filterFileType, FileMode.JAR);
					}
				} else if (url.toString().startsWith("file:")) {
					iterateFiles(result, Paths.get(uri), search, recursive, filterFileType, FileMode.RESOURCE);
				} else {
					throw new IOException("Unsupported protocol: " + url);
				}
			}
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
		File external = new File(search);
		if (external.exists()) {
			iterateFiles(result, external.toPath(), search, recursive, filterFileType, FileMode.EXTERNAL);
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

	private static void iterateFiles(List<FileInfo> result, Path path, String search, boolean recursive, SearchFilter filterFileType, FileMode mode) throws IOException {
		if (Files.isRegularFile(path)) {
			addFile(result, path, search, recursive, filterFileType, mode);
		} else if (Files.isDirectory(path)) {
			try (Stream<Path> stream = Files.list(path)) {
				Iterator<Path> i = stream.iterator();
				while (i.hasNext()) {
					addFile(result, i.next(), search, recursive, filterFileType, mode);
				}
			}
		}
	}

	private static void addFile(List<FileInfo> result, Path path, String search, boolean recursive, SearchFilter filterFileType, FileMode mode) throws IOException {
		boolean isDirectory = Files.isDirectory(path);
		boolean isFile = Files.isRegularFile(path);
		if (isDirectory && filterFileType == SearchFilter.FILES_ONLY) {
			// ignore
		} else if (isFile && filterFileType == SearchFilter.DIRECTORY_ONLY) {
			// ignore
		} else {
			String absolutePath = path.toAbsolutePath().toString().replace("\\", "/");
			int index = absolutePath.indexOf(search);
			/*String relativePath = "";
			int relPathStartIndex = index + search.length() + 1;
			if (relPathStartIndex < absolutePath.length()) {
				relativePath = absolutePath.substring(relPathStartIndex);
			}*/
			String name = path.getFileName().toString();
			result.add(new FileInfo(
				isDirectory ? FileType.DIRECTORY : (isFile ? FileType.FILE : FileType.UNKNOWN),
				mode, name, absolutePath.substring(index), absolutePath, Files.getLastModifiedTime(path).toMillis()
			));
		}
		if (isDirectory && recursive) {
			iterateFiles(result, path, search, recursive, filterFileType, mode);
		}
	}

}
