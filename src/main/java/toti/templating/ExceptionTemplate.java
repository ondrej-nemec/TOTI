package toti.templating;

import java.util.Map;

import toti.security.Authorizator;
import translator.Translator;

public class ExceptionTemplate implements Template {
	
	private final Throwable t;
	
	public ExceptionTemplate(Throwable t) {
		this.t = t;
	}

	@Override
	public long getLastModification() {
		return 0;
	}

	@Override
	public String create(TemplateFactory templateFactory, Map<String, Object> variables, Translator translator, Authorizator authorizator)
			throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.append("Exception occured:<br>");
		printException(t, builder);
		return builder.toString();
	}
	
	private void printException(Throwable t, StringBuilder builder) {
		builder.append(t.getClass() + " " + t.getMessage() + "<br>");
		for (StackTraceElement el : t.getStackTrace()) {
			builder.append(String.format(
					"	%s.%s (%s:%s)<br>",
					el.getClassName(),
					el.getMethodName(),
					el.getFileName(),
					el.getLineNumber()
			));
		}
		if (t.getCause() != null) {
			builder.append("<br>Caused:<br>");
			printException(t.getCause(), builder);
		}
	}

}
