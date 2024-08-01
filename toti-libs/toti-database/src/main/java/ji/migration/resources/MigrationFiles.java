package ji.migration.resources;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import ji.common.functions.FileExtension;
import ji.common.functions.FilesList;

public class MigrationFiles {
	
	private final URL dir;
	private final List<String> files;
		
	public MigrationFiles(String folder) throws IOException {
		try {
			FilesList list= FilesList.get(folder, false);
			this.dir = list.getURL();
			this.files = loadFiles(list.getFiles());
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	/***************************/
	
	public URL getDir() {
		return dir;
	}

	public List<String> getFiles() {
		return files;
	}

	private List<String> loadFiles(List<String> files) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		List<String> loadedFiles = new LinkedList<>();
		for (String fileName : files) {
			File file = new File(fileName);
			FileExtension fe = new FileExtension(fileName);
			switch (fe.getExtension()) {
				case "java":
					if (!loadedFiles.contains(fe.getName() + ".class")) {
						compiler.run(null, null, null, file.getPath()); // streamy, kam se zapisuje
						loadedFiles.add(fe.getName() + ".class");
					}
					break;
				case "sql":
				case "class": 
					loadedFiles.add(file.getName());
					break;
				default: break;
			}
		}
		return loadedFiles;
	}

}
