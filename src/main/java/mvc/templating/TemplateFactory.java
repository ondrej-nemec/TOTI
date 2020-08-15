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
import mvc.templating.tags.BreakTag;
import mvc.templating.tags.CaseTag;
import mvc.templating.tags.CatchTag;
import mvc.templating.tags.ConsoleOutputTag;
import mvc.templating.tags.ContinueTag;
import mvc.templating.tags.DefaultTag;
import mvc.templating.tags.DoWhileTag;
import mvc.templating.tags.ElseIfTag;
import mvc.templating.tags.ElseTag;
import mvc.templating.tags.FinallyTag;
import mvc.templating.tags.ForEachTag;
import mvc.templating.tags.ForTag;
import mvc.templating.tags.IfTag;
import mvc.templating.tags.SwitchTag;
import mvc.templating.tags.TranslateParamTag;
import mvc.templating.tags.TranslateTag;
import mvc.templating.tags.TryTag;
import mvc.templating.tags.VariableDefineTag;
import mvc.templating.tags.VariablePrintTag;
import mvc.templating.tags.VariableSetTag;
import mvc.templating.tags.WhileTag;

public class TemplateFactory {
	
	private static final List<Tag> tags = initTags();

	private final String tempPath;
	private final TemplateParser parser;
	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	
	public TemplateFactory(String tempPath) {
		this.tempPath = tempPath;
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

	private static List<Tag> initTags() {
		List<Tag> tags = new ArrayList<>();
		tags.add(new BreakTag());
		tags.add(new CaseTag());
		tags.add(new CatchTag());
		tags.add(new ConsoleOutputTag());
		tags.add(new ContinueTag());
		tags.add(new DefaultTag());
		tags.add(new DoWhileTag());
		tags.add(new ElseIfTag());
		tags.add(new ElseTag());
		tags.add(new FinallyTag());
		tags.add(new ForEachTag());
		tags.add(new ForTag());
		tags.add(new IfTag());
		tags.add(new SwitchTag());
		tags.add(new TranslateParamTag());
		tags.add(new TranslateTag());
		tags.add(new TryTag());
		tags.add(new VariableDefineTag());
		tags.add(new VariablePrintTag());
		tags.add(new VariableSetTag());
		tags.add(new WhileTag());
		return tags;
	}
	
	public static void addTag(Tag tag) {
		tags.add(tag);
	}
		
}
