package mvc.templating;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import common.FileExtension;
import mvc.templating.tags.ConsoleOutputTag;
import mvc.templating.tags.ForTag;
import mvc.templating.tags.IfTag;

public class TemplateFactory {

	private final String tempPath;
	private final TemplateParser parser;
	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	
	public TemplateFactory(String tempPath) {
		this.tempPath = tempPath;
		List<Tag> tags = getTags();
		parser = new TemplateParser(tags.stream()
			      .collect(Collectors.toMap(Tag::getName, tag -> tag)));
	}

	public Template getTemplate(String templateFile) throws Exception {
		File file = new File(templateFile);
		String className = getClassName(file);
		File cacheDir = new File(tempPath);
		try (URLClassLoader loader = new URLClassLoader(new URL[] {cacheDir.toURI().toURL()});) {
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
		}
		try (URLClassLoader loader = new URLClassLoader(new URL[] {cacheDir.toURI().toURL()});) {
			return (Template)loader.loadClass(className).newInstance();
		}
	}
	
	private void compileNewCache(String templateFile, String className, long modificationTime) throws IOException {
		String javaTempFile = parser.createTempCache(className, templateFile, tempPath, modificationTime);
		File file = new File(javaTempFile);
		compiler.run(null, null, null, file.getPath()); // streamy, kam se zapisuje
		//file.delete();
	}
	
	private String getClassName(File file) {
		return new FileExtension(file.getName()).getName();
	}
	
	private List<Tag> getTags() {
		List<Tag> tags = new ArrayList<>();
		// TODO
		tags.add(new ConsoleOutputTag());
		tags.add(new ForTag());
		tags.add(new IfTag());
		return tags;
	}
		
}
