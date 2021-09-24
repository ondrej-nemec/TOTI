package toti.templating;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import common.Logger;
import common.exceptions.LogicException;
import common.functions.FileExtension;
import common.structures.ThrowingFunction;
import common.structures.Tuple2;
import toti.templating.parsing.TemplateParser;

public class TemplateFactory {
	
	private static final List<Supplier<Tag>> CUSTOM_TAG_PROVIDERS = new LinkedList<>();

	private final String tempPath;
	private final boolean deleteAuxJavaClass;
	private final boolean minimalize;
	private final JavaCompiler compiler; // = ToolProvider.getSystemJavaCompiler();
	private final String templatePath;
	private final Map<String, TemplateFactory> modules;
	private final String module;
	private final Logger logger;
	
	public TemplateFactory(String tempPath, String templatePath, String module, Map<String, TemplateFactory> modules, Logger logger) {
		this(tempPath, templatePath, module, modules, true, false, logger);
	}
	
	public TemplateFactory(
			String tempPath, 
			String templatePath,
			String module,
			Map<String, TemplateFactory> modules,
			boolean deleteAuxJavaClass,
			boolean minimalize,
			Logger logger) {
		String cachePath = tempPath + "/cache/" + module;
		new File(cachePath).mkdir();
		this.tempPath = cachePath;
		this.templatePath = templatePath;
		this.deleteAuxJavaClass = deleteAuxJavaClass;
		this.modules = modules;
		this.minimalize = minimalize;
		this.module = module;
		this.logger = logger;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			try {
				compiler = (JavaCompiler)Class.forName("com.sun.tools.javac.api.JavacTool").newInstance();
			} catch (Exception e) {
				logger.fatal("Cannot load compiler", e);
			}
		}
		this.compiler = compiler;
	}
	
	public String getModuleName() {
		return module;
	}
	
	public Template getTemplate(String templateFile) throws Exception {
		if (!templateFile.startsWith("/")) {
			templateFile = "/" + templateFile;
		}
		return getTemplateWithAbsolutePath(templatePath + templateFile, (file)->{
			return getClassName(file, templatePath);
		}, module);
	}

	public Template getModuleTemplate(String templateFile, String module) throws Exception {
		return modules.get(module).getTemplate(templateFile);
	}

	public Template getFrameworkTemplate(String templateFile) throws Exception {
		return getTemplateWithAbsolutePath(templateFile, (file)->{
			return new Tuple2<>("toti", new FileExtension(file.getName()).getName());
		}, "");
	}

	private Template getTemplateWithAbsolutePath(
			String templateFile,
			ThrowingFunction<File, Tuple2<String, String>, IOException> getClassNameAndNamespace,
			String module) throws Exception {
		if (templatePath == null) {
			throw new LogicException("No template path set for this module: '" + module + "'");
		}
		long lastModifition = -1;
		URL url = null;
		if (url == null) {
			url = getClass().getResource("/" + module + "/" + templateFile);
		}
		if (url == null) {
			File file = new File(module + "/" + templateFile);
			url = file.toURI().toURL();
			lastModifition = file.lastModified();
		}
		if (url == null) {
			url = getClass().getResource("/" + templateFile);
		}
		if (url == null || lastModifition == 0) {
			File file = new File(templateFile);
			url = file.toURI().toURL();
			lastModifition = file.lastModified();
		}
		File file = new File(templateFile);
		Tuple2<String, String> classNameAndNamespace = getClassNameAndNamespace.apply(file);
		File cacheDir = new File(tempPath);
		String className = 
				classNameAndNamespace._1().replaceAll("/", ".")
				+ (classNameAndNamespace._1().length() == 0 ? "" : ".")
				+ classNameAndNamespace._2();
		try (URLClassLoader loader = new URLClassLoader(new URL[] {
				cacheDir.toURI().toURL()},
				TemplateFactory.class.getClassLoader()
		)) {
			try {
				Template template = (Template)loader.loadClass(className).newInstance();
				if (lastModifition != template.getLastModification()) {
					logger.warn("Class " + className + " has change, compile " + lastModifition + " vs " + template.getLastModification());
					compileNewCache(templateFile, classNameAndNamespace._1(), classNameAndNamespace._2(), lastModifition, module);
				} else {
					return template;
				}
			} catch (ClassNotFoundException e) {
				logger.warn("Class " + className + " not found, compile");
				compileNewCache(templateFile, classNameAndNamespace._1(), classNameAndNamespace._2(), lastModifition, module);
			}
		}
		try (URLClassLoader loader = new URLClassLoader(new URL[] {cacheDir.toURI().toURL()});) {
			return (Template)loader.loadClass(className).newInstance();
		}
	}
	
	private void compileNewCache(String templateFile, String namespace, String className, long modificationTime, String module) throws IOException {
		File dir = new File(tempPath + "/" + namespace);
		dir.mkdirs();
		
		List<Tag> tags = initTags(namespace);
		tags.addAll(CUSTOM_TAG_PROVIDERS.stream().map(s->s.get()).collect(Collectors.toList()));
		toti.templating.parsing.TemplateParser parser = new TemplateParser(tags.stream()
			      .collect(Collectors.toMap(Tag::getName, tag -> tag)), minimalize);
		String javaTempFile = parser.createTempCache(namespace, className, templateFile, tempPath, module, modificationTime);
		File file = new File(javaTempFile);
		
		
		/*
		
		compiler.run(null, null, null, file.getPath()); // streamy, kam se zapisuje
		
		/*/
		
		//System.err.println(System.getProperty("java.class.path"));
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		List<String> optionList = new ArrayList<String>();
		optionList.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));
		compiler.getTask(null, null, null, optionList, null, fileManager.getJavaFileObjects(file)).call();
		
		//*/
		
		if (deleteAuxJavaClass) {
			file.delete();
		}
	}
	
	// TODO test it
	private Tuple2<String, String> getClassName(File file, String templatePath) throws IOException {
		if (!file.getCanonicalPath().contains(file.getName())) {
			throw new RuntimeException(
				"File name is misspeled: " + file.getName()
				+ ". Did you mean " + file.getCanonicalFile().getName() + " (" + file.getCanonicalPath() + ")?"
			);
		}
		String moduleName = templatePath.replaceAll("\\\\", "_").replaceAll("/", "_");
		String namespace = moduleName + file.getCanonicalPath()
				.replace(new File(templatePath).getCanonicalPath(), "")
				//.substring(1)
				.replace(file.getName(), "")
				.replaceAll("\\\\", "/");
		namespace = (namespace.length() == 0) 
				? ""
				: namespace.charAt(namespace.length() - 1) == '/' 
					? namespace.substring(0, namespace.length() - 1)
					: namespace; // "/" at the file end
		return new Tuple2<>(namespace, new FileExtension(file.getName()).getName());
	}

	private List<Tag> initTags(String actualFileDir) {
		List<Tag> tags = new ArrayList<>();
		tags.add(new toti.templating.tags.BreakTag());
		tags.add(new toti.templating.tags.CaseTag());
		tags.add(new toti.templating.tags.CatchTag());
		tags.add(new toti.templating.tags.ConsoleOutputTag());
		tags.add(new toti.templating.tags.ContinueTag());
		tags.add(new toti.templating.tags.DefaultTag());
		tags.add(new toti.templating.tags.DoWhileTag());
		tags.add(new toti.templating.tags.ElseIfTag());
		tags.add(new toti.templating.tags.ElseTag());
		tags.add(new toti.templating.tags.FinallyTag());
		tags.add(new toti.templating.tags.ForEachTag());
		tags.add(new toti.templating.tags.ForTag());
		tags.add(new toti.templating.tags.IfTag());
		tags.add(new toti.templating.tags.SwitchTag());
		tags.add(new toti.templating.tags.TranslateParamTag());
		tags.add(new toti.templating.tags.TranslateTag());
		tags.add(new toti.templating.tags.TryTag());
		tags.add(new toti.templating.tags.VariableDefineTag());
		tags.add(new toti.templating.tags.VariablePrintTag());
		tags.add(new toti.templating.tags.VariableSetTag());
		tags.add(new toti.templating.tags.WhileTag());
		tags.add(new toti.templating.tags.LayoutTag(/*actualFileDir*/));
		tags.add(new toti.templating.tags.BlockTag());
		tags.add(new toti.templating.tags.IncludeTag(/*actualFileDir*/));
		tags.add(new toti.templating.tags.ControlTag());
		tags.add(new toti.templating.tags.FormError());
		tags.add(new toti.templating.tags.FormInput());
		tags.add(new toti.templating.tags.FormLabel());
		tags.add(new toti.templating.tags.PermissionsTag());
		return tags;
	}
	
	public static void addTag(Supplier<Tag> tag) {
		CUSTOM_TAG_PROVIDERS.add(tag);
	}
		
}
