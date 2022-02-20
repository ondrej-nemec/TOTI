package toti.templating;

import java.io.IOException;
import java.io.Writer;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

public class TemplateDiagnostic extends Writer implements DiagnosticListener<JavaFileObject> {
	
	private final String namespace;
	private final String file;
	
	private final StringBuilder builder = new StringBuilder();

	public TemplateDiagnostic(String namespace, String file) {
		this.namespace = namespace;
		this.file = file;
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {}

	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		if (diagnostic.getKind() == Kind.ERROR) {
			builder.append(String.format(
				"%s.%s Compilation error: %s",
				namespace, file,
				diagnostic.getMessage(null)
			));
		}
		/*
		System.out.println("code: " + diagnostic.getCode());
		System.out.println("column: " + diagnostic.getColumnNumber());
		System.out.println("end postiong: " + diagnostic.getEndPosition());
		System.out.println("lnie nubmer: " + diagnostic.getLineNumber());
		System.out.println("positiong: " + diagnostic.getPosition());
		System.out.println("start postiong: " + diagnostic.getStartPosition());
		System.out.println("Kind: " + diagnostic.getKind());
		System.out.println("source: " + diagnostic.getSource());
		System.out.println("message: " + diagnostic.getMessage(null)); 
		System.out.println();
		*/
	}

	@Override
	public void flush() throws IOException {}

	@Override
	public void close() throws IOException {}
	
	public boolean isError() {
		return !builder.toString().isEmpty();
	}

	public String getError() {
		return builder.toString();
	}

}
