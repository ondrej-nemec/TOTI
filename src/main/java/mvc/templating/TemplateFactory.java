package mvc.templating;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import common.FileExtension;

public class TemplateFactory {

	private final String tempPath;
	private final TemplateParser parser;
	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	
	public TemplateFactory(String tempPath) {
		this.tempPath = tempPath;
		parser = new TemplateParser(getTags());
	}

	public Template getTemplate(String templateFile, Map<String, Object> variables) throws Exception {
		File file = new File(templateFile);
		String className = getClassName(file);
		try (URLClassLoader loader = new URLClassLoader(new URL[] {
				new File(tempPath + "/" + className).toURI().toURL()});
		) {
			try {
				Template template = (Template)loader.loadClass(className).newInstance();
				if (file.lastModified() != template.getLastModification()) {
					compileNewCache(templateFile, className, file.lastModified());
				} else {
					return template;
				}
			} catch (ClassNotFoundException e) {
				compileNewCache(templateFile, className, file.lastModified());
			}
			return (Template)loader.loadClass(className).newInstance();
		}
	}
	
	private void compileNewCache(String templateFile, String className, long modificationTime) throws IOException {
		String javaTempFile = parser.createTempCache(className, templateFile, tempPath, modificationTime);
		File file = new File(javaTempFile);
		compiler.run(null, null, null, file.getPath()); // streamy, kam se zapisuje
		file.delete();
	}
	
	private String getClassName(File file) {
		return new FileExtension(file.getName()).getName();
	}
	
	private Map<String, Tag> getTags() {
		Map<String, Tag> map = new HashMap<>();
		// TODO
		return map;
	}
		
}
