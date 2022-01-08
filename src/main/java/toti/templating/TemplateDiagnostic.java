package toti.templating;

import java.io.IOException;
import java.io.Writer;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class TemplateDiagnostic extends Writer implements DiagnosticListener<JavaFileObject> {
	
	//private final StringBuilder builder = new StringBuilder();
	private boolean isOk = true;

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		//builder.append(cbuf);
		isOk = false;
	}

	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
		// TODO
	}

	@Override
	public void flush() throws IOException {}

	@Override
	public void close() throws IOException {}
	
	public boolean isError() {
		return isOk;
		//return builder.toString().isEmpty();
	}
	/*
	public String getError() {
		return builder.toString();
	}
*/
}
