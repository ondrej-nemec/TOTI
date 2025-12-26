package ji.json;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import ji.json.providers.OutputProvider;

public class OutputJsonStream implements Closeable {

	enum StartMode { START, END }

	//	enum OrderMode { STAR}

	private final OutputProvider provider;
	
	private final LinkedList<Boolean> parent = new LinkedList<>();
	
	private final boolean formated;
	//private int level = 0;
	//private boolean isEmpty = true;
	
	public OutputJsonStream(OutputProvider provider) {
		this(provider, false);
	}
	
	public OutputJsonStream(OutputProvider provider, boolean formated) {
		this.provider = provider;
		this.formated = formated;
	}

	public void writeObjectValue(String name, Object value) throws JsonStreamException {
		provider.write(getFormat(null) + String.format("\"%s\":%s", name, (formated ? " " : "") + getValue(value)));
	}

	public void writeObjectStart() throws JsonStreamException {
		provider.write(getFormat(StartMode.START) + "{");
	}
	
	public void writeObjectStart(String name) throws JsonStreamException {
		provider.write(getFormat(StartMode.START) + String.format("\"%s\":%s{", name, (formated ? " " : "")));
	}
	
	public void writeObjectEnd() throws JsonStreamException {
		provider.write(getFormat(StartMode.END) + "}");
	}
	
	public void writeListValue(Object value) throws JsonStreamException {
		provider.write(getFormat(null) + String.format("%s", getValue(value)));
	}
	
	public void writeListStart() throws JsonStreamException {
		provider.write(getFormat(StartMode.START) + "[");
	}
	
	public void writeListStart(String name) throws JsonStreamException {
		provider.write(getFormat(StartMode.START) + String.format("\"%s\":%s[", name, (formated ? " " : "")));
	}
	
	public void writeListEnd() throws JsonStreamException {
		provider.write(getFormat(StartMode.END) + "]");
	}
	
	private String getValue(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof Boolean) {
			return value.toString();
		}
		if (value instanceof Number) {
			return value.toString();
		}
		return String.format(
			"\"%s\"",
			value.toString()
				.replace("\\", "\\\\") // replace \ with \\
				.replace("\"", "\\\"") // replace " with \"
				.replace("\n", "\\n") // replace \n with \\n
				.replace("\r", "\\r") // replace \r with \\r
				.replace("\t", "\\t") // replace \t with \\t
				.replace("\b", "\\b") // replace \b with \\b
				.replace("\f", "\\f") // replace \f with \\f
		);
	}

	private String getFormat(StartMode mode) {
		StringBuilder pre = new StringBuilder();
		
		boolean newLine = false;
		int tabs = 0;
		if (parent.isEmpty()) { // initial state
			// ignore
		} else if (parent.getLast() == null) { // object or list is starting
			parent.removeLast();
			parent.add(true);

			newLine = true;
			tabs = parent.size();
		} else if (mode != StartMode.END) { // another not ending element
			parent.removeLast();
			parent.add(false);
			pre.append(",");
			
			newLine = true;
			tabs = parent.size();
		}

		if (mode == StartMode.START) {
			parent.add(null);
		} else if (mode == StartMode.END) {
			parent.removeLast();
			
			newLine = true;
			tabs = parent.size();
		}
		if (formated && newLine) {
			pre.append("\n");
		}
		if (formated && newLine) {
			for (int i = 0; i < tabs; i++) {
				pre.append("  ");
			}
		}
		return pre.toString();
	}

	@Override
	public void close() throws IOException {
		provider.close();
	}
	
}
