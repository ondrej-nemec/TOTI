package ji.json;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import ji.json.event.EventType;
import ji.json.providers.OutputProvider;

public class OutputJsonStream implements Closeable {

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
/*
	public void startDocument() throws JsonStreamException {
		provider.write("{");
		parent.add(true);
	}
	
	public void endDocument() throws JsonStreamException {
		parent.removeLast();
		provider.write(getFormat(true) + "}");
		provider.close();
	}
*/
	public void writeObjectValue(String name, Object value) throws JsonStreamException {
		boolean isFirst = checkFirst();
		provider.write(getFormat(isFirst) + String.format("\"%s\":%s", name, (formated ? " " : "") + getValue(value)));
	}

	public void writeObjectStart() throws JsonStreamException {
		boolean isFirst = checkFirst(EventType.OBJECT_START);
		provider.write(getFormat(isFirst) + "{");
		//level++;
	}
	
	public void writeObjectStart(String name) throws JsonStreamException {
		boolean isFirst = checkFirst(EventType.OBJECT_START);
		provider.write(getFormat(isFirst) + String.format("\"%s\":%s{", name, (formated ? " " : "")));
		//level++;
	}
	
	public void writeObjectEnd() throws JsonStreamException {
		//level--;
		provider.write(getFormat(true) + "}");
		parent.removeLast();
	}
	
	public void writeListValue(Object value) throws JsonStreamException {
		boolean isFirst = checkFirst();
		provider.write(getFormat(isFirst) + String.format("%s", getValue(value)));
	}
	
	public void writeListStart() throws JsonStreamException {
		boolean isFirst = checkFirst(EventType.LIST_START);
		provider.write(getFormat(isFirst) + "[");
		//level++;
	}
	
	public void writeListStart(String name) throws JsonStreamException {
		boolean isFirst = checkFirst(EventType.LIST_START);
		provider.write(getFormat(isFirst) + String.format("\"%s\":%s[", name, (formated ? " " : "")));
		//level++;
	}
	
	public void writeListEnd() throws JsonStreamException {
		//level--;
		provider.write(getFormat(true) + "]");
		parent.removeLast();
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
	
	private boolean checkFirst(EventType child) {
		if (parent.size() == 0) {
			parent.add(true);
			return true;
		}
		boolean isFirst = parent.getLast();
		if (isFirst) {
			parent.removeLast();
			parent.add(false);
		}
		parent.add(true);
		return isFirst;
	}
	
	private boolean checkFirst() {
		boolean isFirst = parent.getLast();
		if (isFirst) {
			parent.removeLast();
			parent.add(false);
		}
		return isFirst;
	}
	
	private String getFormat(boolean isFirst) {
		StringBuilder pre = new StringBuilder((isFirst ? "" : ","));
		/*if (formated && level == 0 && !isEmpty) {
			pre.append("\n");
		} else {
			isEmpty = false;
		}*/
		if (formated /*&& level > 0*/) {
			pre.append("\n");
			parent.forEach((item)->{
				pre.append("  ");
			});
		}
		return pre.toString();
	}

	@Override
	public void close() throws IOException {
		provider.close();
	}
	
}
