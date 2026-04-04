package toti.lib.templating;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import toti.lib.common.functions.compiling.Compiler;
import toti.lib.files.access.FileInfo;
import toti.lib.files.access.FileList;
import toti.lib.files.access.SearchFilter;
import toti.lib.templating.parsing.TemplateParser;
import toti.lib.templating.structures.TemplateFile;
import toti.lib.templating.tags.BlockTag;
import toti.lib.templating.tags.BreakTag;
import toti.lib.templating.tags.CaseTag;
import toti.lib.templating.tags.CatchTag;
import toti.lib.templating.tags.ConsoleOutputTag;
import toti.lib.templating.tags.ContinueTag;
import toti.lib.templating.tags.DefaultTag;
import toti.lib.templating.tags.DoWhileTag;
import toti.lib.templating.tags.ElseIfTag;
import toti.lib.templating.tags.ElseTag;
import toti.lib.templating.tags.FinallyTag;
import toti.lib.templating.tags.ForEachTag;
import toti.lib.templating.tags.ForTag;
import toti.lib.templating.tags.IfTag;
import toti.lib.templating.tags.IncludeTag;
import toti.lib.templating.tags.LayoutTag;
import toti.lib.templating.tags.SwitchTag;
import toti.lib.templating.tags.TryTag;
import toti.lib.templating.tags.VariableDefineTag;
import toti.lib.templating.tags.VariablePrintTag;
import toti.lib.templating.tags.VariableSetTag;
import toti.lib.templating.tags.WhileTag;

public class TemplateFactory {
	
	private final List<Tag> customTags;
	private final List<Parameter> customParams;

	private final String tempPath;
	private final boolean deleteAuxJavaClass;
	private final boolean minimalize;
	private final Compiler compiler;
	//private final String templatePath;
	private final Map<String, String> modules;
	//private final String module;
	//private final String modulePath;
	private final Logger logger;
	
	public TemplateFactory(
		String tempPath,
		Map<String, String> modules,
		List<Tag> customTags,
		List<Parameter> customParams,
		Logger logger) {
		this(tempPath, modules, true, false, customTags, customParams, logger);
	}
	public TemplateFactory(
		String tempPath,
		Map<String, String> modules,
		boolean deleteAuxJavaClass,
		boolean minimalize,
		List<Tag> customTags,
		List<Parameter> customParams,
		Logger logger) {
		this(tempPath, modules, deleteAuxJavaClass, minimalize, customTags, customParams, new Compiler(logger), logger);
	}
	
	protected TemplateFactory(
			String tempPath,
			Map<String, String> modules,
			boolean deleteAuxJavaClass,
			boolean minimalize,
			List<Tag> customTags,
			List<Parameter> customParams,
			Compiler compiler,
			Logger logger) {
		String cachePath = tempPath + "/cache";
		File cacheDir = new File(cachePath);
		if (!cacheDir.exists() && !cacheDir.mkdirs()) {
			logger.warn("Temp cache dir cannot be created: " + cachePath);
		}
		if (!cacheDir.setExecutable(true, false) || !cacheDir.setWritable(true, false) || !cacheDir.setReadable(true, false)) {
			logger.warn("Temp cache dir cannot be set permissions: " + cachePath);
		}
		this.customParams = customParams;
		this.customTags = customTags;
		this.tempPath = cachePath;
		this.deleteAuxJavaClass = deleteAuxJavaClass;
		this.modules = new HashMap<>();
		modules.forEach((n, p)->addModule(n, p));
		this.minimalize = minimalize;
		this.logger = logger;
		this.compiler = compiler;
	}

	protected String clear(String modulePath) {
		modulePath = modulePath.replaceAll("\\\\", "/");
		if (modulePath.startsWith("/")) {
			modulePath = modulePath.substring(1);
		}
		if (modulePath.endsWith("/")) {
			modulePath = modulePath.substring(0, modulePath.length() - 1);
		}
		return modulePath;
	}

	public void addModule(String name, String path) {
		this.modules.put(name, clear(path));
	}

	public Template getTemplate(String moduleName, String fileRelativePath) throws Exception {
		TemplateFile file = createTemplateFile(moduleName, fileRelativePath);
		return loadTemplate(file);
	}

	private Template loadTemplate(TemplateFile file) throws Exception {
		File cacheDir = new File(tempPath);
		try (URLClassLoader loader = new URLClassLoader(new URL[] {
				cacheDir.toURI().toURL()},
				TemplateFactory.class.getClassLoader()
		)) {
			try {
				Template template = (Template)loader.loadClass(file.fullClassName())
					.getDeclaredConstructor(TemplateFactory.class).newInstance(this);
				if (file.lastModification() != template.getLastModification()) {
					logger.warn("Class " + file.fullClassName() + " has change, compile " + file.lastModification() + " vs " + template.getLastModification());
				} else {
					return template;
				}
			} catch (ClassNotFoundException e) {
				logger.warn("Class " + file.fullClassName() + " not found, compile");
			}
			compileNewCache(file);
		}
		try (URLClassLoader loader = new URLClassLoader(new URL[] {cacheDir.toURI().toURL()});) {
			return (Template)loader.loadClass(file.fullClassName())
				.getDeclaredConstructor(TemplateFactory.class).newInstance(this);
		}
	}

	protected TemplateFile createTemplateFile(String moduleName, String fileRelativePath) throws IOException {
		// relative to module path
		if (fileRelativePath == null || fileRelativePath.isEmpty()) {
			throw new TemplateException("No template filename was given");
		}
		String modulePath = modules.get(moduleName);
		if (modulePath == null) {
			throw new TemplateException(String.format("No module path found for '%s'", moduleName));
		}
		String templatePath = modulePath + (modulePath.isEmpty() ? "" : "/") + clear(fileRelativePath);
		List<FileInfo> filesList = FileList.get(templatePath, false, SearchFilter.FILES_ONLY);
		if (filesList.isEmpty()) {
			throw new TemplateException(String.format("No file found for folder='%s' and name='%s'", modulePath, fileRelativePath));
		}
		FileInfo file = filesList.get(0);
		if (!file.isFile()) {
			throw new TemplateException("File points to directory: " + file.getAbsolutePath());
		}

		String base = clear(
			templatePath
			.replace(modulePath, modulePath.replace("/", "_").replace("-", "_"))
			.replace(file.getFullName(), "")
		);

		String namespace = base.replace("/", ".");
		return new TemplateFile(
			moduleName,
			file.getName(), namespace,
			namespace + (namespace.isEmpty() ? "" : ".") + file.getName(),
			file.getLastModificationTime(),
			base, // relative to cache dir
			templatePath
		);
	}

	private void compileNewCache(TemplateFile templateFile) throws Exception {
		// TODO optimalize? init tags only once? and directly to map?
		List<Tag> tags = initTags();
		tags.addAll(customTags);
		List<Parameter> parameters = initParameters();
		parameters.addAll(customParams);
		
		TemplateParser parser = new TemplateParser(
			tags.stream().collect(Collectors.toMap(Tag::getName, tag -> tag)),
			parameters.stream().collect(Collectors.toMap(Parameter::getName, par->par)),
			minimalize
		);
		compileNewCache(templateFile, parser);
	}

	protected void compileNewCache(TemplateFile templateFile, TemplateParser parser ) throws Exception {
		File dir = new File(tempPath + "/" + templateFile.tempFileRelativeFolder());
		dir.mkdirs();
		dir.setExecutable(true, false);
		dir.setReadable(true, false);
		dir.setWritable(true, false);
		
		
		String javaTempFile = parser.createTempCache(templateFile, tempPath);
		File file = new File(javaTempFile);
		file.setExecutable(true, false);
		file.setReadable(true, false);
		file.setWritable(true, false);
		
		Optional<String> res = compiler.compile(file, templateFile.namespace(), templateFile.templateFullPath());
		if (res.isPresent()) {
			throw new TemplateException(res.get());
		}
		File auxFile = new File(javaTempFile.replace("java", "class"));
		auxFile.setExecutable(true, false);
		auxFile.setReadable(true, false);
		auxFile.setWritable(true, false);
		if (deleteAuxJavaClass) {
			file.delete();
		}
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
	//	tags.add(new TranslateTag());
		tags.add(new TryTag());
		tags.add(new VariableDefineTag());
		tags.add(new VariablePrintTag());
		tags.add(new VariableSetTag());
		tags.add(new WhileTag());
		tags.add(new LayoutTag());
		tags.add(new BlockTag());
		tags.add(new IncludeTag());
		return tags;
	}
	
	protected List<Parameter> initParameters() {
		List<Parameter> parameters = new ArrayList<>();
		return parameters;
	}
}
