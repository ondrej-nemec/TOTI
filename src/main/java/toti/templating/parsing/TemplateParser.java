package toti.templating.parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ji.common.functions.InputStreamLoader;
import ji.common.structures.DictionaryValue;
import ji.common.structures.MapInit;
import ji.common.structures.ThrowingConsumer;
import ji.common.structures.ThrowingSupplier;
import ji.files.text.Text;
import ji.translator.Translator;
import toti.security.Authorizator;
import toti.templating.Parameter;
import toti.templating.Tag;
import toti.templating.Template;
import toti.templating.TemplateException;
import toti.templating.TemplateFactory;
import toti.templating.parsing.enums.ParserType;
import toti.templating.parsing.structures.TagNode;
import toti.url.Link;
import toti.url.MappedUrl;

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
	
	public String createTempCache(
			String namespace,
			String className,
			String fileName, 
			String tempPath,
			String module,
			long modificationTime) throws IOException {
		String preClass = namespace.length() == 0 ? "%s" : "package %s;";
		String clazz1 = preClass
				+ String.format("import %s;", Map.class.getCanonicalName())
				+ String.format("import %s;", HashMap.class.getCanonicalName())
				+ String.format("import %s;", LinkedList.class.getCanonicalName())
				+ String.format("import %s;", ThrowingConsumer.class.getCanonicalName())
				+ String.format("import %s;", ThrowingSupplier.class.getCanonicalName())
				+ String.format("import %s;", DictionaryValue.class.getCanonicalName())
				+ String.format("import %s;", MapInit.class.getCanonicalName())
				+ String.format("import %s;", Translator.class.getCanonicalName())
				+ String.format("import %s;", Authorizator.class.getCanonicalName())
				+ String.format("import %s;", TemplateFactory.class.getCanonicalName())
				+ String.format("import %s;", MappedUrl.class.getCanonicalName())
				+ String.format("import %s;", Template.class.getCanonicalName())
				+ String.format("import %s;", TagNode.class.getCanonicalName())
				+ String.format("import %s;", TemplateException.class.getCanonicalName())
				+ String.format("import %s;", Link.class.getCanonicalName())
				+ "public class %s implements Template{"
					+ "private LinkedList<TagNode> nodes = new LinkedList<>();"
				
				+ "private void write(Object data) {nodes.getLast().getBuilder().append(data);}"
				+ "private void addVariable(String name, Object value) {nodes.getLast().getVariables().put(name, value);}"
				+ "private Object getVariable(String name) {return nodes.getLast().getVariables().get(name);}"
				+ "private Object getVariable(ThrowingSupplier<Object, Exception> supplier) throws Exception {return supplier.get();}"
			//	+ "private ThrowingConsumer<Map<String, Object>, Exception> getBlock(String name) {return nodes.getLast().getBlocks().get(name);}"
                + "private ThrowingConsumer<Map<String, Object>,Exception> getBlock(String name,boolean required){ThrowingConsumer<Map<String,Object>,Exception> myBlock=nodes.getLast().getBlocks().get(name);if(myBlock==null&&required){throw new TemplateException(\"Missing block: \"+name);}else if(myBlock!=null){return myBlock;}else{return (p)->{};}}"
                + "private void addBlock(String name, ThrowingConsumer<Map<String, Object>, Exception> value) {nodes.getLast().getBlocks().put(name, value);}"

                + "private void initNode(Map<String, Object> variables) {Map<String, Object> params = new HashMap<>();Map<String, ThrowingConsumer<Map<String, Object>, Exception>> blocks = new HashMap<>();if (nodes.size() > 0) {params.putAll(nodes.getLast().getVariables());blocks.putAll(nodes.getLast().getBlocks());}if (variables != null) {params.putAll(variables);}nodes.add(new TagNode(params, blocks));}"
				+ "private TagNode flushNode() {TagNode node = nodes.removeLast();if (nodes.size() > 0) {write(node.getBuilder().toString());nodes.getLast().updateVariables(node);}return node;}"
								
				+ "public long getLastModification(){return %sL;}"
				+ "public String _create("
					+ "TemplateFactory templateFactory,"
					+ "Map<String, Object>variables,"
					+ "Translator translator,"
					+ "Authorizator authorizator,"
					+ "LinkedList<TagNode> nodes,"
					+ "MappedUrl current"
			+ ")throws Exception{";
		String clazz2 = "}}";
		String tempFile = tempPath + "/" + namespace + "/" + className + ".java";
		
		Text.get().write((bw)->{
			bw.write(String.format(clazz1, namespace.replaceAll("/", "."), className, modificationTime));
			bw.write("Template layout=null;this.nodes = nodes;initNode(variables);");
			loadFile(fileName, bw, module);
			bw.write("if(layout!=null){"
					+ "layout._create(templateFactory,variables,translator, authorizator, this.nodes,current);"
					+ "}");
			bw.write("return flushNode().getBuilder().toString();");
			bw.write(clazz2);
		}, tempFile, false);
		
		return tempFile;
	}
	
	private void loadFile(String fileName, BufferedWriter bw, String module) throws IOException {
		InputStream is = null;
		try {
			is = InputStreamLoader.createInputStream(this.getClass(), module + "/" + fileName);
		} catch (FileNotFoundException e1) {
			try {
				is = InputStreamLoader.createInputStream(this.getClass(), fileName);
			} catch (FileNotFoundException e2) {
				throw new FileNotFoundException("Template file not found: " + e1.getMessage() + " OR " + e2.getMessage());
			}
		}
		Text.get().read((br)->{
			parse(br, (text)->{
				bw.write(text);
			});			
			// TODO read s void
			return null;
		}, is, "utf-8"); // TODO maybe configurable
		is.close();
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
	
	protected void parse(BufferedReader br, ThrowingConsumer<String, IOException> bw) throws IOException {
		
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
			ParserWrapper last = parsers.size() > 0 ? parsers.getLast() : null;
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
				parsers.add(new ParserWrapper(new TagParser(actual, tags, parameters)));
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
				parsers.add(new ParserWrapper(new VariableParser(level++)));
			} else if (previous == '$' && actual != '{') {
				if (last == null) {
					writeText(node, cache, previous);
					writeText(node, previous, actual);
				} else {
					writeParser(node, parsers, htmlTag, last, cache, previous, null);
					writeParser(node, parsers, htmlTag, last, previous, actual, null);
				}
				cache = DEF;
			// inline
			/*} else if ((last == null || last.allowChildren()) && previous != '{' && actual == '{') {
				//candidate
				cache = previous;
			} else if ((last == null || last.allowChildren()) && previous == '{' && actual == '{') {
				parsers.add(new ParserWrapper(new InLineParser()));
			} else if ((last == null || last.allowChildren()) && previous == '{' && actual != '{') {
				if (last == null) {
					writeText(node, cache, previous);
					writeText(node, previous, actual);
				} else {
					writeParser(node, parsers, htmlTag, last, cache, previous, null);
					writeParser(node, parsers, htmlTag, last, previous, actual, null);
				}
				cache = DEF;*/
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
			if (parsers.size() > 0) {
				if (last.getType() == ParserType.VARIABLE) {
					parsers.getLast().addVariable(last);
				} else {
					parsers.getLast().addCode(last);
				}
			} else {
				node.append("\");");
				node.append(last.getContent(
					htmlTags.size() == 0 ? null : htmlTags.getLast(),
					isDoubleQuoted
				));
				node.append("write(\"");
			}
		}
	}
	
	private void writeText(StringBuilder node, char previous, char actual) {
		if (actual == '\r') {
			// ignored
		} else if (actual == '\n') {
			node.append(
				(minimalize ? "" : "\\n") + "\");write(\""
			);
		} else {
			if (minimalize && (actual == '\t' || (actual == ' ' && previous == ' ') )) {
				// ignore
			} else {
				if (actual == '\"' || actual == '\\') {
					node.append("\\");
				}
				node.append(actual + "");
			}
		}
	}

}
