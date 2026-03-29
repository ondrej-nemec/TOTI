package toti.lib.templating.parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.MapInit;
import toti.lib.common.structures.ThrowingConsumer;
import toti.lib.common.structures.ThrowingSupplier;
import toti.lib.files.access.FileUtils;
import toti.lib.files.text.Text;
import toti.lib.templating.Parameter;
import toti.lib.templating.Tag;
import toti.lib.templating.Template;
import toti.lib.templating.TemplateException;
import toti.lib.templating.TemplateFactory;
import toti.lib.templating.TemplateParameters;
import toti.lib.templating.parsing.enums.ParserType;
import toti.lib.templating.parsing.structures.TagNode;
import toti.lib.templating.structures.TemplateFile;

/** version 2.1 */
public class TemplateParser {
	
	private final static char DEF = '\u0000';
	
	private final Map<String, Tag> tags;
	private final Map<String, Parameter> parameters;
	private final boolean minimalize;
	
	public TemplateParser(
			Map<String, Tag> tags,
			Map<String, Parameter> parameters,
			boolean minimalize) {
		this.tags = tags;
		this.parameters = parameters;
		this.minimalize = minimalize;
	}
	
	public String createTempCache(TemplateFile file, String tempDirPath) throws IOException {
		String preClass = file.namespace().isEmpty() ? "" : "package " + file.namespace() + ";";
		String clazz1 = preClass
				+ String.format("import %s;", Map.class.getCanonicalName())
				+ String.format("import %s;", HashMap.class.getCanonicalName())
				+ String.format("import %s;", LinkedList.class.getCanonicalName())
				+ String.format("import %s;", ThrowingConsumer.class.getCanonicalName())
				+ String.format("import %s;", ThrowingSupplier.class.getCanonicalName())
				+ String.format("import %s;", DictionaryValue.class.getCanonicalName())
				+ String.format("import %s;", MapInit.class.getCanonicalName())
				+ String.format("import %s;", TemplateFactory.class.getCanonicalName())
				+ String.format("import %s;", Template.class.getCanonicalName())
				+ String.format("import %s;", TagNode.class.getCanonicalName())
				+ String.format("import %s;", TemplateException.class.getCanonicalName())
				+ String.format("import %s;", TemplateParameters.class.getCanonicalName())
				
				+ String.format("public class %s implements Template, TemplateParameters{", file.className())
					+ "private LinkedList<TagNode> nodes = new LinkedList<>();"
					+ String.format("private final String moduleName = \"%s\";", file.moduleName())
				+ "private void write(Object data) {nodes.getLast().getBuilder().append(data);}"
				+ "public void addVariable(String name, Object value) {nodes.getLast().getVariables().put(name, value);}"
				+ "public Object getVariable(String name) {return nodes.getLast().getVariables().get(name);}"
				+ "private Object getVariable(ThrowingSupplier<Object, Exception> supplier) throws Exception {return supplier.get();}"
				+ "private ThrowingConsumer<Map<String, Object>,Exception> getBlock(String name,boolean required){ThrowingConsumer<Map<String,Object>,Exception> myBlock=nodes.getLast().getBlocks().get(name);if(myBlock==null&&required){throw new TemplateException(\"Missing block: \"+name);}else if(myBlock!=null){return myBlock;}else{return (p)->{};}}"
				+ "private void addBlock(String name, ThrowingConsumer<Map<String, Object>, Exception> value) {nodes.getLast().getBlocks().put(name, value);}"

				+ "private void initNode(Map<String, Object> variables) {Map<String, Object> params = new HashMap<>();Map<String, ThrowingConsumer<Map<String, Object>, Exception>> blocks = new HashMap<>();if (nodes.size() > 0) {params.putAll(nodes.getLast().getVariables());blocks.putAll(nodes.getLast().getBlocks());}if (variables != null) {params.putAll(variables);}nodes.add(new TagNode(params, blocks));}"
				+ "private TagNode flushNode() {TagNode node = nodes.removeLast();if (nodes.size() > 0) {write(node.getBuilder().toString());nodes.getLast().updateVariables(node);}return node;}"
								
				+ String.format("public long getLastModification(){return %sL;}", file.lastModification())
				+ "public String _create("
					+ "TemplateFactory templateFactory,"
					+ "Map<String, Object>variables,"
					+ "LinkedList<TagNode> nodes,"
					+ "int parent"
			+ ")throws Exception{";
		String clazz2 = "}}";
		String tempFile = tempDirPath + "/" + file.tempFileRelativeFolder() + "/" + file.className() + ".java";
		
		Text.get().write((bw)->{
			bw.write(clazz1);
			bw.write("Template layout=null;this.nodes = nodes;initNode(variables);");
			loadFile(file.templateFullPath(), bw.getBufferedWriter());
			bw.write("if(layout!=null){"
					+ "layout._create(templateFactory,variables,this.nodes,this.hashCode());"
					+ "}");
			bw.write("return flushNode().getBuilder().toString();");
			bw.write(clazz2);
		}, tempFile, false);
		
		return tempFile;
	}
	
	private void loadFile(String fileName, BufferedWriter bw) throws IOException {
		ParsingInfo info = new ParsingInfo(fileName);
		try (InputStream is = FileUtils.createInputStream(fileName);) {
			info.setFilePath(fileName);
			Text.get().read((br)->{
				parse(br.getBufferedReader(), (text)->{
					bw.write(text);
				}, info);
				return null;
			}, is, "utf-8"); // TODO maybe configurable
		}
	}
	
	/*
	 * k kodu nic nesmi byt
	 * v poznamce nic nesmi byt
	 * 
	 * v promene muze byt jina prenna
	 * 
	 * v inline muze byt promenna - ne komentar - funguguje / * * / a //
	 * v tagu muze byt komentar, kod, promena, inline
	 * 
	 */
	
	protected void parse(BufferedReader br, ThrowingConsumer<String, IOException> bw, ParsingInfo info) throws IOException {
		int level = 0;
		LinkedList<ParserWrapper> parsers = new LinkedList<>();
		LinkedList<String> htmlTag = new LinkedList<>();
		StringBuilder node = new StringBuilder();

		char actual;
		char previous = DEF;
		char cache = DEF;
		
		boolean isSingleQuoted = false;
		boolean isDoubleQuoted = false;
		
		node.append("write(\"");
		while((actual = (char)br.read()) != (char)-1) {
			if (actual == '\n') {
				info.addLine();
			}
			ParserWrapper last = !parsers.isEmpty() ? parsers.getLast() : null;
			if (last != null) {
				if (actual == '"' && previous != '\\' && !last.isSingleQuoted()) {
					last.setDoubleQuoted();
				} else if (actual == '\'' && previous != '\\' && !last.isDoubleQuoted()) {
					last.setSingleQuoted();
				}
			} else {
				if (actual == '"' && previous != '\\' && !isSingleQuoted) {
					isDoubleQuoted = !isDoubleQuoted;
				} else if (actual == '\'' && previous != '\\' && !isDoubleQuoted) {
					isSingleQuoted = !isSingleQuoted;
				}
			}
			// java, comment, tag
			if ((last == null || last.allowChildren()) && actual == '<') {
				//candidate
				cache = previous;
			} else if ((last == null || last.allowChildren()) && previous == '<' && actual == '%') {
				parsers.add(new ParserWrapper(new JavaParser()));
			} else if ((last == null /*|| last.allowChildren()*/) && previous == '<' && !Character.isLetter(actual) && actual != '/') {
				//if (last == null) {
					writeText(node, cache, previous);
					writeText(node, previous, actual);
				/*} else {
					writeParser(node, parsers, htmlTag, last, cache, previous, null);
					writeParser(node, parsers, htmlTag, last, previous, actual, null);
				}*/
				cache = DEF;
			} else if ((last == null /*|| last.allowChildren()*/) && previous == '<') {
				parsers.add(new ParserWrapper(new TagParser(actual, tags, parameters, info)));
			} else if (last != null && previous == '<' && actual == '$') {
				writeParser(node, parsers, htmlTag, last, cache, previous, null);
				cache = previous;
			} else if (last != null && last.allowChildren() && previous == '<') {
				writeParser(node, parsers, htmlTag, last, cache, previous, null);
				writeParser(node, parsers, htmlTag, last, previous, actual, null);
			// variable
			} else if (actual == '$') {
				//candidate
				cache = previous;
			} else if (previous == '$' && actual == '{') {
				parsers.add(new ParserWrapper(new VariableParser(level++, info)));
			} else if (previous == '$' && actual != '{') {
				if (last == null) {
					writeText(node, cache, previous);
					writeText(node, previous, actual);
				} else {
					writeParser(node, parsers, htmlTag, last, cache, previous, null);
					writeParser(node, parsers, htmlTag, last, previous, actual, null);
				}
				cache = DEF;
			// write
			} else if (last != null) {
				writeParser(
					node, parsers, htmlTag, last, previous, actual,
					!isDoubleQuoted && !isSingleQuoted ? null : isDoubleQuoted
				);
			} else {
				writeText(node, previous, actual);
			}
			previous = actual;
		}
		node.append("\");");
		bw.accept(node.toString());
	}
	
	// TODO if called twice in one row - can be problem
	private void writeParser(
			StringBuilder node, LinkedList<ParserWrapper> parsers,
			LinkedList<String> htmlTags,
			ParserWrapper last, char previous, char actual, Boolean isDoubleQuoted) {
		if (last.accept(previous, actual)) {
			parsers.removeLast();
			last.finishTag(htmlTags);
			if (!parsers.isEmpty()) {
				if (last.getType() == ParserType.VARIABLE) {
					parsers.getLast().addVariable(last);
				} else {
					parsers.getLast().addCode(last);
				}
			} else {
				node.append("\");");
				node.append(last.getContent(
					htmlTags.isEmpty()? null : htmlTags.getLast(),
					isDoubleQuoted
				));
				node.append("write(\"");
			}
		}
	}
	
	private void writeText(StringBuilder node, char previous, char actual) {
		switch (actual) {
			case '\r'->{
				// ignored
			}
			case '\n'->{
				node.append(String.format("%s\");write(\"", minimalize ? "" : "\\n"));
			}
			default->{
				if (minimalize && (actual == '\t' || (actual == ' ' && previous == ' ') )) {
					// ignore
				} else {
					if (actual == '\"' || actual == '\\') {
						node.append("\\");
					}
					node.append(actual);
				}
			}
		}
	}

}
