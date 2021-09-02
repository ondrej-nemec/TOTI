package toti.templating.parsing2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import common.Logger;
import common.exceptions.LogicException;
import common.functions.FileExtension;
import common.structures.MapInit;
import common.structures.ThrowingFunction;
import common.structures.Tuple2;
import core.text.Text;
import core.text.basic.WriteText;
import toti.logging.TotiLogger;
import toti.templating.Tag;
import toti.templating.Template;
import toti.templating.TemplateFactory;
import toti.templating.parsing2.TemplateParser;

public class TemplateParserEndToEndTest {

	private final String tempPath;
	private final boolean deleteAuxJavaClass;
	private final boolean minimalize;
	private final JavaCompiler compiler; // = ToolProvider.getSystemJavaCompiler();
	private final String templatePath;
	private final Map<String, TemplateFactory> modules;
	private final String module;
	private final Logger logger;
	
	public TemplateParserEndToEndTest(
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
		TemplateParser parser = new TemplateParser(tags.stream()
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
	
	private List<Tag> initTags(String namespace) {
		return Arrays.asList(
			new Tag() {
				
				@Override
				public String getPairStartCode(Map<String, String> params) {
					return "for (int j = 0; j < 5; j++) {"
							+ initNode(params, new LinkedList<>())
							+ "addVariable(\"j\", j);";
				}
				
				@Override
				public String getPairEndCode(Map<String, String> params) {
					return finishPaired()
							+ "}";
				}
				
				@Override
				public String getNotPairCode(Map<String, String> params) {
					return ""
					   + "write(\"Your message: \" +"
					   + params.get("message")
					   + ");";
				}
				
				@Override
				public String getName() {
					return "tagName";
				}
			}
		);
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
	
	
	public static void main(String[] args) {
		try {
			/*
			new TemplateParser(new HashMap<>(), false).createTempCache(
				"toti/templating/parsing2", // namespace
				"GeneratedTemplate", // class name
				"toti/templating/parsing2/template.jsp", // template
				"src/test/java/", // temp
				"",
				System.currentTimeMillis()
			);
			/*/
			File f = new File("temp/cache/toti_templating_parsing2/template.class");
			f.delete();
			
			TemplateParserEndToEndTest test = new TemplateParserEndToEndTest(
					"temp", "toti/templating/parsing2", "", new HashMap<>(), false, false, TotiLogger.getLogger("parsing")
			);
			Template t = test.getTemplate("template.jsp");
			Text.get().write((bw)->{
				try {
					WriteText.get().write(bw, t.create(null, 
						new MapInit<String, Object>()
						.append("title", "Hello World!")
						.append("age", 42)
						.append("map", new MapInit<>("key1", "some value").toMap())
						.toMap(),
						null, null));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, "temp/index.html", false);
			//*/
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
