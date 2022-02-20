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
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import ji.common.Logger;
import ji.common.exceptions.LogicException;
import ji.common.functions.FileExtension;
import ji.common.structures.ThrowingFunction;
import ji.common.structures.Tuple2;
import toti.templating.parameters.HrefParameter;
import toti.templating.parameters.TitleParameter;
import toti.templating.parsing.TemplateParser;
import toti.templating.tags.*;

public class TemplateFactory {
	
	private static final List<Tag> CUSTOM_TAG_PROVIDERS = new LinkedList<>();
	private static final List<Parameter> CUSTOM_PARAMETERS_PROVIDERS = new LinkedList<>();

	private final String tempPath;
	private final boolean deleteAuxJavaClass;
	private final boolean minimalize;
	private final JavaCompiler compiler; // = ToolProvider.getSystemJavaCompiler();
	private final String templatePath;
	private final Map<String, TemplateFactory> modules;
	private final String module;
	private final String modulePath;
	private final Logger logger;
	
	public TemplateFactory(String tempPath, String templatePath, String module, String modulePath, Map<String, TemplateFactory> modules, Logger logger) {
		this(tempPath, templatePath, module, modulePath, modules, true, false, logger);
	}
	
	public TemplateFactory(
			String tempPath, 
			String templatePath,
			String module,
			String modulePath,
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
		this.modulePath = clear(modulePath);
		this.logger = logger;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			try {
				compiler = (JavaCompiler)Class.forName("com.sun.tools.javac.api.JavacTool").getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				logger.fatal("Cannot load compiler", e);
			}
		}
		this.compiler = compiler;
	}
	
	private String clear(String modulePath) {
		if (modulePath.startsWith("/")) {
			modulePath = modulePath.substring(1);
		}
		if (modulePath.endsWith("/")) {
			modulePath = modulePath.substring(0, modulePath.length() - 1);
		}
		return modulePath;
	}

/*
	public String getModuleName1() {
		return module;
	}
*/
	public Template getTemplate(String templateFile) throws Exception {
		if (!templateFile.startsWith("/")) {
			templateFile = "/" + templateFile;
		}
		return getTemplateWithAbsolutePath(templatePath + templateFile, (file)->{
			return getClassName(file, templatePath);
		}, modulePath);
	}

	public Template getModuleTemplate(String templateFile, String module) throws Exception {
		return modules.get(module).getTemplate(templateFile);
	}

	@Deprecated
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
			throw new LogicException("No template path set for this module: '" + this.module + "' (" + module + ")");
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
				Template template = (Template)loader.loadClass(className).getDeclaredConstructor().newInstance();
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
			return (Template)loader.loadClass(className).getDeclaredConstructor().newInstance();
		}
	}
	
	private void compileNewCache(String templateFile, String namespace, String className, long modificationTime, String module) throws Exception {
		File dir = new File(tempPath + "/" + namespace);
		dir.mkdirs();
		
		List<Tag> tags = initTags();
		tags.addAll(CUSTOM_TAG_PROVIDERS);
		List<Parameter> parameters = initParameters();
		parameters.addAll(CUSTOM_PARAMETERS_PROVIDERS);
		
		toti.templating.parsing.TemplateParser parser = new TemplateParser(
			tags.stream().collect(Collectors.toMap(Tag::getName, tag -> tag)),
			parameters.stream().collect(Collectors.toMap(Parameter::getName, par->par)),
			minimalize
		);
		String javaTempFile = parser.createTempCache(namespace, className, templateFile, tempPath, module, modificationTime);
		File file = new File(javaTempFile);
		
		
		/*
		
		compiler.run(null, null, null, file.getPath()); // streamy, kam se zapisuje
		
		/*/
		
		//System.err.println(System.getProperty("java.class.path"));
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		List<String> optionList = new ArrayList<String>();
		optionList.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));
		TemplateDiagnostic diagnostic = new TemplateDiagnostic(namespace, templateFile);
		compiler.getTask(diagnostic, null, diagnostic, optionList, null, fileManager.getJavaFileObjects(file)).call();
		if (diagnostic.isError()) {
			throw new TemplateException(diagnostic.getError());
		//	System.err.println(diagnostic.getError());
		//	throw new TemplateException("Some unknow syntax error in " + templateFile);
		}
		
		//*/
		
		if (deleteAuxJavaClass) {
			file.delete();
		}
	}

	// TODO test it
	private Tuple2<String, String> getClassName(File file, String templatePath) throws IOException {
		if (!file.getCanonicalPath().contains(file.getName())) {
			throw new TemplateException(
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

	/**
	 * Protected for test purpose only
	 * @return
	 */
	protected List<Tag> initTags() {
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
		tags.add(new TranslateTag());
		tags.add(new TryTag());
		tags.add(new VariableDefineTag());
		tags.add(new VariablePrintTag());
		tags.add(new VariableSetTag());
		tags.add(new WhileTag());
		tags.add(new LayoutTag());
		tags.add(new BlockTag());
		tags.add(new IncludeTag());
		tags.add(new ControlTag());
		tags.add(new FormError());
		tags.add(new FormInput());
		tags.add(new FormLabel());
		tags.add(new PermissionsTag());
		tags.add(new LinkTag());
		tags.add(new IfCurrentTag());
		return tags;
	}
	
	protected List<Parameter> initParameters() {
		List<Parameter> parameters = new ArrayList<>();
		parameters.add(new HrefParameter());
		parameters.add(new TitleParameter());
		return parameters;
	}
	
	public static void addTag(Tag tag) {
		CUSTOM_TAG_PROVIDERS.add(tag);
	}
	
	public static void addParameter(Parameter parameter) {
		CUSTOM_PARAMETERS_PROVIDERS.add(parameter);
	}
		
}
