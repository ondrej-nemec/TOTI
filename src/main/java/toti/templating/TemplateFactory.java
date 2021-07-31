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
import toti.templating.tags.BlockTag;
import toti.templating.tags.BreakTag;
import toti.templating.tags.CaseTag;
import toti.templating.tags.CatchTag;
import toti.templating.tags.ConsoleOutputTag;
import toti.templating.tags.ContinueTag;
import toti.templating.tags.ControlTag;
import toti.templating.tags.DefaultTag;
import toti.templating.tags.DoWhileTag;
import toti.templating.tags.ElseIfTag;
import toti.templating.tags.ElseTag;
import toti.templating.tags.FinallyTag;
import toti.templating.tags.ForEachTag;
import toti.templating.tags.ForTag;
import toti.templating.tags.FormError;
import toti.templating.tags.FormInput;
import toti.templating.tags.FormLabel;
import toti.templating.tags.IfTag;
import toti.templating.tags.IncludeTag;
import toti.templating.tags.LayoutTag;
import toti.templating.tags.PermissionsTag;
import toti.templating.tags.SwitchTag;
import toti.templating.tags.TranslateParamTag;
import toti.templating.tags.TranslateTag;
import toti.templating.tags.TryTag;
import toti.templating.tags.VariableDefineTag;
import toti.templating.tags.VariablePrintTag;
import toti.templating.tags.VariableSetTag;
import toti.templating.tags.WhileTag;

public class TemplateFactory {
	
	public boolean useOldImpl = true;
	
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
		
		String javaTempFile;
		if (useOldImpl) {
			logger.warn("Template is compiled with old parser");
			System.err.println("Template is compiled with old parser. " + tempPath);
			List<Tag> tags = initTags(namespace);
			tags.addAll(CUSTOM_TAG_PROVIDERS.stream().map(s->s.get()).collect(Collectors.toList()));
			TemplateParser parser = new TemplateParser(tags.stream()
				      .collect(Collectors.toMap(Tag::getName, tag -> tag)), minimalize);
			javaTempFile = parser.createTempCache(namespace, className, templateFile, tempPath, module, modificationTime);
		} else {
			List<Tag> tags = initTagsNew(namespace);
			tags.addAll(CUSTOM_TAG_PROVIDERS.stream().map(s->s.get()).collect(Collectors.toList()));
			toti.templating.parsing2.TemplateParser parser = new toti.templating.parsing2.TemplateParser(tags.stream()
				      .collect(Collectors.toMap(Tag::getName, tag -> tag)), minimalize);
			javaTempFile = parser.createTempCache(namespace, className, templateFile, tempPath, module, modificationTime);
		}
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
		tags.add(new LayoutTag(/*actualFileDir*/));
		tags.add(new BlockTag());
		tags.add(new IncludeTag(/*actualFileDir*/));
		tags.add(new ControlTag());
		tags.add(new FormError());
		tags.add(new FormInput());
		tags.add(new FormLabel());
		tags.add(new PermissionsTag());
		return tags;
	}

	private List<Tag> initTagsNew(String actualFileDir) {
		List<Tag> tags = new ArrayList<>();
		tags.add(new toti.templating.tags2.BreakTag());
		tags.add(new toti.templating.tags2.CaseTag());
		tags.add(new toti.templating.tags2.CatchTag());
		tags.add(new toti.templating.tags2.ConsoleOutputTag());
		tags.add(new toti.templating.tags2.ContinueTag());
		tags.add(new toti.templating.tags2.DefaultTag());
		tags.add(new toti.templating.tags2.DoWhileTag());
		tags.add(new toti.templating.tags2.ElseIfTag());
		tags.add(new toti.templating.tags2.ElseTag());
		tags.add(new toti.templating.tags2.FinallyTag());
		tags.add(new toti.templating.tags2.ForEachTag());
		tags.add(new toti.templating.tags2.ForTag());
		tags.add(new toti.templating.tags2.IfTag());
		tags.add(new toti.templating.tags2.SwitchTag());
		tags.add(new toti.templating.tags2.TranslateParamTag());
		tags.add(new toti.templating.tags2.TranslateTag());
		tags.add(new toti.templating.tags2.TryTag());
		tags.add(new toti.templating.tags2.VariableDefineTag());
		tags.add(new toti.templating.tags2.VariablePrintTag());
		tags.add(new toti.templating.tags2.VariableSetTag());
		tags.add(new toti.templating.tags2.WhileTag());
		tags.add(new toti.templating.tags2.LayoutTag(/*actualFileDir*/));
		tags.add(new toti.templating.tags2.BlockTag());
		tags.add(new toti.templating.tags2.IncludeTag(/*actualFileDir*/));
		tags.add(new toti.templating.tags2.ControlTag());
		tags.add(new toti.templating.tags2.FormError());
		tags.add(new toti.templating.tags2.FormInput());
		tags.add(new toti.templating.tags2.FormLabel());
		tags.add(new toti.templating.tags2.PermissionsTag());
		return tags;
	}
	
	public static void addTag(Supplier<Tag> tag) {
		CUSTOM_TAG_PROVIDERS.add(tag);
	}
		
}
