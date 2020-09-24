package mvc.templating;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import common.FileExtension;
import common.structures.ThrowingFunction;
import common.structures.Tuple2;
import mvc.templating.tags.BreakTag;
import mvc.templating.tags.CaseTag;
import mvc.templating.tags.CatchTag;
import mvc.templating.tags.ConsoleOutputTag;
import mvc.templating.tags.ContinueTag;
import mvc.templating.tags.DefaultTag;
import mvc.templating.parsing.TemplateParser;
import mvc.templating.tags.BlockTag;
import mvc.templating.tags.DoWhileTag;
import mvc.templating.tags.ElseIfTag;
import mvc.templating.tags.ElseTag;
import mvc.templating.tags.FinallyTag;
import mvc.templating.tags.ForEachTag;
import mvc.templating.tags.ForTag;
import mvc.templating.tags.GridTag;
import mvc.templating.tags.IfTag;
import mvc.templating.tags.IncludeTag;
import mvc.templating.tags.LayoutTag;
import mvc.templating.tags.SwitchTag;
import mvc.templating.tags.TranslateParamTag;
import mvc.templating.tags.TranslateTag;
import mvc.templating.tags.TryTag;
import mvc.templating.tags.VariableDefineTag;
import mvc.templating.tags.VariablePrintTag;
import mvc.templating.tags.VariableSetTag;
import mvc.templating.tags.WhileTag;

public class TemplateFactory {
	
	private static final List<Supplier<Tag>> CUSTOM_TAG_PROVIDERS = new LinkedList<>();

	private final String tempPath;
//	private final String templatePath;
	private final boolean deleteAuxJavaClass;
//	private final TemplateParser parser;
	private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	
	public TemplateFactory(String tempPath/*, String templatePath*/) {
		this(tempPath/*, templatePath*/, true);
	}
	
	public TemplateFactory(String tempPath, /*String templatePath, */boolean deleteAuxJavaClass) {
		String cachePath = tempPath + "/cache";
		new File(cachePath).mkdir();
		this.tempPath = cachePath;
	//	this.templatePath = templatePath;
		this.deleteAuxJavaClass = deleteAuxJavaClass;
	}
	
	public Template getTemplate(String templateFile, String templatePath) throws Exception {
		return getTemplateWithAbsolutePath(templatePath + templateFile, (file)->{
			return getClassName(file, templatePath);
		});
	}
	
	public Template getFrameworkTemplate(String templateFile) throws Exception {
		return getTemplateWithAbsolutePath(templateFile, (file)->{
			return new Tuple2<>("framework", new FileExtension(file.getName()).getName());
		});
	}

	private Template getTemplateWithAbsolutePath(
			String templateFile,
			ThrowingFunction<File, Tuple2<String, String>, IOException> getClassNameAndNamespace) throws Exception {
		File file = new File(/*templatePath + */templateFile);
		Tuple2<String, String> classNameAndNamespace = getClassNameAndNamespace.apply(file);// getClassName(file);
		File cacheDir = new File(tempPath);
		String className = 
				classNameAndNamespace._1().replaceAll("/", ".")
				+ (classNameAndNamespace._1().length() == 0 ? "" : ".")
				+ classNameAndNamespace._2();
		
		try (URLClassLoader loader = new URLClassLoader(new URL[] {cacheDir.toURI().toURL()});) {
			try {
				Template template = (Template)loader.loadClass(className).newInstance();
				if (file.lastModified() != template.getLastModification()) {
					compileNewCache(/*templatePath +*/ templateFile, classNameAndNamespace._1(), classNameAndNamespace._2(), file.lastModified());
				} else {
					return template;
				}
			} catch (ClassNotFoundException e) {
				compileNewCache(/*templatePath + */templateFile, classNameAndNamespace._1(), classNameAndNamespace._2(), file.lastModified());
			}
		}
		try (URLClassLoader loader = new URLClassLoader(new URL[] {cacheDir.toURI().toURL()});) {
			return (Template)loader.loadClass(className).newInstance();
		}
	}
	
	private void compileNewCache(String templateFile, String namespace, String className, long modificationTime) throws IOException {
		File dir = new File(tempPath + "/" + namespace);
		dir.mkdirs();
		
		List<Tag> tags = initTags(namespace);
		tags.addAll(CUSTOM_TAG_PROVIDERS.stream().map(s->s.get()).collect(Collectors.toList()));
		TemplateParser parser = new TemplateParser(tags.stream()
			      .collect(Collectors.toMap(Tag::getName, tag -> tag)));
		
		String javaTempFile = parser.createTempCache(namespace, className, templateFile, tempPath, modificationTime);
		File file = new File(javaTempFile);
		compiler.run(null, null, null, file.getPath()); // streamy, kam se zapisuje
		if (deleteAuxJavaClass) {
			file.delete();
		}
	}
	
	private Tuple2<String, String> getClassName(File file, String templatePath) throws IOException {
		String moduleName = templatePath.replaceAll("\\\\", "_").replaceAll("/", "_");
		String namespace = moduleName + file.getCanonicalPath()
				.replace(new File(templatePath).getCanonicalPath(), "")
				.substring(1)
				.replace(file.getName(), "")
				.replaceAll("\\\\", "/");
		namespace = (namespace.length() == 0) ? "" : namespace.substring(0, namespace.length() - 1); // "/" at the file end
		return new Tuple2<>(namespace, new FileExtension(file.getName()).getName());
	}

	private List<Tag> initTags(String actualFileDir) {
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
		tags.add(new LayoutTag(actualFileDir));
		tags.add(new BlockTag());
		tags.add(new IncludeTag(actualFileDir));
		tags.add(new GridTag());
		return tags;
	}
	
	public static void addTag(Supplier<Tag> tag) {
		CUSTOM_TAG_PROVIDERS.add(tag);
	}
		
}
