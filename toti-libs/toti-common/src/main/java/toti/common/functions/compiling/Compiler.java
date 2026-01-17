package toti.common.functions.compiling;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.logging.log4j.Logger;

public class Compiler {
	
	private final JavaCompiler compiler;

	public Compiler(Logger logger) {
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
	
	public Optional<String> compile(File javaFile) {
		return compile(javaFile, "", javaFile.getAbsolutePath());
	}
	
	public Optional<String> compile(File javaFile, String namespace, String templateFile) {
		CompilingDiagnostic diagnostic = new CompilingDiagnostic(namespace, templateFile);
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostic, null, null);
		List<String> optionList = new ArrayList<String>();
		optionList.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));
		//compiler.run(null, null, tempFile.getPath());
		/*boolean isOk = */compiler.getTask(
			diagnostic, null, diagnostic, optionList, null, fileManager.getJavaFileObjects(javaFile)
		).call();
		if (diagnostic.isError()) {
			return Optional.of(diagnostic.getError());
		}
		return Optional.empty();
	}
	
}
