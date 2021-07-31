package toti.templating.parsing2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map;

import common.exceptions.LogicException;
import common.functions.InputStreamLoader;
import common.structures.ThrowingConsumer;
import core.text.Text;
import toti.templating.Tag;
import toti.templating.parsing2.enums.InLineState;
import toti.templating.parsing2.enums.JavaState;
import toti.templating.parsing2.enums.ParserType;
import toti.templating.parsing2.enums.TagState;
import toti.templating.parsing2.enums.VarState;

public class TemplateParser {
	
	private final Map<String, Tag> tags;
	private final boolean minimalize;
	
	public TemplateParser(Map<String, Tag> tags, boolean minimalize) {
		this.tags = tags;
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
				+ "import toti.templating.Template;"
				+ "import toti.templating.parsing2.TagNode;"
				+ "import java.util.Map;"
				+ "import java.util.HashMap;"
				+ "import java.util.function.Consumer;"
				+ "import java.util.LinkedList;"
				+ "import translator.Translator;"
				+ "import toti.security.Authorizator;"
				+ "import toti.templating.TemplateFactory;"
				+ "public class %s implements Template{"
					+ "private LinkedList<TagNode> nodes = new LinkedList<>();"
				
				+ "private void write(Object data) {nodes.getLast().getBuilder().append(data);}"
				+ "private void addVariable(String name, Object value) {nodes.getLast().getVariables().put(name, value);}"
				+ "private Object getVariable(String name) {return nodes.getLast().getVariables().get(name);}"
				+ "private Consumer<Map<String, Object>> getBlock(String name) {return nodes.getLast().getBlocks().get(name);}"
				+ "private void addBlock(String name, Consumer<Map<String, Object>> value) {nodes.getLast().getBlocks().put(name, value);}"
				
				+ "private void initNode(Map<String, Object> variables) {Map<String, Object> params = new HashMap<>();Map<String, Consumer<Map<String, Object>>> blocks = new HashMap<>();if (nodes.size() > 0) {params.putAll(nodes.getLast().getVariables());blocks.putAll(nodes.getLast().getBlocks());}if (variables != null) {params.putAll(variables);}nodes.add(new TagNode(params, blocks));}"
				+ "private TagNode flushNode() {TagNode node = nodes.removeLast();if (nodes.size() > 0) {write(node.getBuilder().toString());}return node;}"
								
				+ "public long getLastModification(){return %sL;}"
				+ "public String create("
					+ "TemplateFactory templateFactory,"
					+ "Map<String, Object>variables,"
					+ "Translator translator,"
					+ "Authorizator authorizator"
				+ ")throws Exception{"
				+ "return create(templateFactory, variables, translator, authorizator, new LinkedList<>()).getBuilder().toString();"
				+ "}"
				+ "public TagNode create("
					+ "TemplateFactory templateFactory,"
					+ "Map<String, Object>variables,"
					+ "Translator translator,"
					+ "Authorizator authorizator,"
					+ "LinkedList<TagNode> nodes"
			+ ")throws Exception{";
		String clazz2 = "}}";
		String tempFile = tempPath + "/" + namespace + "/" + className + ".java";
		
		Text.get().write((bw)->{
			bw.write(String.format(clazz1, namespace.replaceAll("/", "."), className, modificationTime));
			bw.write("Template layout=null;this.nodes = nodes;initNode(variables);");
		//	bw.write("write(\"");			
			loadFile(fileName, bw, module);
		//	bw.write("\");");
			bw.write("if(layout!=null){"
					+ "layout.create(templateFactory,variables,translator, authorizator, this.nodes);"
					+ "}");
			bw.write("return flushNode();");
			bw.write(clazz2);
		}, tempFile, false);
		
		return tempFile;
	}
	
	private void loadFile(String fileName, BufferedWriter bw, String module) throws IOException {
		InputStream is = null;
		try {
			is = InputStreamLoader.createInputStream(this.getClass(), fileName);
		} catch (FileNotFoundException e1) {
			try {
				is = InputStreamLoader.createInputStream(this.getClass(), module + "/" + fileName);
			} catch (FileNotFoundException e2) {
				throw new FileNotFoundException("Template file not found: " + e1.getMessage() + " OR " + e2.getMessage());
			}
		}
		Text.get().read((br)->{
			loadFile(br, (text)->{
				bw.write(text);
			});			
			// TODO read s void
			return null;
		}, is, "utf-8"); // TODO maybe configurable
		is.close();
	}
	
	protected void loadFile(BufferedReader br, ThrowingConsumer<String, IOException> bw) throws IOException {
		JavaState javaState = JavaState.NOTHING;
		TagState tagState = TagState.NOTHING;
		VarState variableState = VarState.NOTHING;
		InLineState inLine = InLineState.NOTHING;
		
		char actual;
		char previous = '\u0000';
		String cache = "";
		ParsingNode node = new ParsingNode();
		node.add("write(\"");
		
		int level = 0;
		
		LinkedList<VariableParser> vars = new LinkedList<>();
		InLine inlineCode = null;
		TagParser tagParser = null;
		
		LinkedList<Parser> parsers = new LinkedList<>();
		
		while((actual = (char)br.read()) != (char)-1) {
			if (javaState != JavaState.COMMENT
					&& (tagState != TagState.NOTHING || variableState != VarState.NOTHING || inLine != InLineState.NOTHING)
			) {
				if (actual == '"' && previous != '\\' && !parsers.getLast().isSingleQuoted()) {
					parsers.getLast().setDoubleQuoted();
				} else if (actual == '\'' && previous != '\\' && !parsers.getLast().isDoubleQuoted()) {
					parsers.getLast().setSingleQuoted();
				}
			}
		/////// JAVA CODE
			if (actual == '<' && javaState == JavaState.NOTHING && tagState == TagState.NOTHING) {
				javaState = JavaState.CANDIDATE1;
				tagState = TagState.CANDIDATE;
				
				node.add(cache);
				cache = "<";
			} else if (javaState == JavaState.CANDIDATE1 && actual == '%') {
				javaState = JavaState.JAVA;
				tagState = TagState.NOTHING;
			} else if (javaState == JavaState.JAVA && actual != '-') {
				node.add("\");");
				node.add(actual);
				javaState = JavaState.CODE;
				addParser(parsers, new Parser(ParserType.JAVA));
			} else if (javaState == JavaState.JAVA && actual == '-') {
				javaState = JavaState.CANDIDATE_COMMENT;
				cache += "-";
			} else if (javaState == JavaState.CANDIDATE_COMMENT && actual == '-') {
				javaState = JavaState.COMMENT;
				cache = "";
				node.add("\");");
				addParser(parsers, new Parser(ParserType.JAVA));
			} else if (javaState == JavaState.COMMENT && actual == '-') {
				cache += "-";
				javaState = JavaState.CLOSE_COMMENT_CANDIDATE1;
			} else if (javaState == JavaState.CLOSE_COMMENT_CANDIDATE1 && actual == '-') {
				cache += '-';
				javaState = JavaState.CLOSE_COMMENT_CANDIDATE2;
			} else if (actual == '%' && (javaState == JavaState.CODE || javaState == JavaState.CLOSE_COMMENT_CANDIDATE2)) {
				cache += "%";
				javaState = JavaState.CLOSE_CANDIDATE;
			} else if (javaState == JavaState.CLOSE_CANDIDATE && actual == '>') {
				parsers.removeLast();
				javaState = JavaState.NOTHING;
				cache = "";
				node.add("write(\"");
			} else if (javaState == JavaState.CODE) {
				node.add(actual);
			} else if (javaState == JavaState.COMMENT) {
				// ignore
			} else if (javaState == JavaState.CANDIDATE1 || javaState == JavaState.CANDIDATE_COMMENT) {
				cache += actual;
				javaState = JavaState.NOTHING;				
			} else if (javaState == JavaState.CLOSE_COMMENT_CANDIDATE1 || javaState == JavaState.CLOSE_COMMENT_CANDIDATE2) {
				cache += actual;
				javaState = JavaState.COMMENT;
			} else if (javaState == JavaState.CLOSE_CANDIDATE) {
				cache += actual;
				javaState = previous == '-' ? JavaState.COMMENT : JavaState.CODE;
		/////////////// VARIABLES
			} else if (actual == '$' && variableState != VarState.CANDIDATE) {
				node.add(cache);
				cache = "$";
				variableState = VarState.CANDIDATE;
			} else if (variableState == VarState.CANDIDATE && actual == '{') {
				cache = "";
				variableState = VarState.VAR;
				vars.add(new VariableParser(++level));
				addParser(parsers, new Parser(ParserType.VARIABLE));
			} else if (actual == '}' && variableState == VarState.VAR && !parsers.getLast().isQuoted()) {
				VariableParser var = vars.removeLast();
				parsers.removeLast();
				cache = "";
				if (vars.size() > 0) {
					vars.getLast().addVariable(var);
					variableState = VarState.VAR; // another var continue
				} else if (inLine == InLineState.IN_LINE) {
					variableState = VarState.NOTHING;
					inlineCode.setPre(var.getDeclare());
					inlineCode.addContent(var.getCalling());
				} else if (tagState == TagState.TAG) {
					variableState = VarState.NOTHING;
					tagParser.addVariable(var);
				} else {
					variableState = VarState.NOTHING;		
					node.add("\");");
					node.add(var.getDeclare());
					if (var.escape()) {
						node.add("write(Template.escapeVariable(" + var.getCalling() + "));");
					} else {
						node.add("write(" + var.getCalling() + ");");
					}
					node.add("write(\"");
				}
			} else if (variableState == VarState.VAR) {
				vars.getLast().accept(previous, actual, parsers.getLast().isSingleQuoted(), parsers.getLast().isDoubleQuoted());
			} else if (variableState == VarState.CANDIDATE) {
				variableState = VarState.NOTHING;
				cache += actual;
		/////////////// IN LINE
			} else if (actual == '}' && inLine == InLineState.IN_LINE && !parsers.getLast().isQuoted()) {
				cache += actual;
				inLine = InLineState.CLOSE_CANDIDATE;
			} else if (inLine == InLineState.CLOSE_CANDIDATE && actual == '}' && !parsers.getLast().isQuoted()) {
				cache = "";
				inLine = InLineState.NOTHING;
				node.add("\");");
				node.add(inlineCode.getPre() + "");
				node.add("write(" + inlineCode.getContent().toString() + ");");
				node.add("write(\"");
				inlineCode = null;
				parsers.removeLast();
			} else if (actual == '{' && inLine == InLineState.NOTHING) {
				node.add(cache);
				cache = "{";
				inLine = InLineState.CANDIDATE;
			} else if (inLine == InLineState.CANDIDATE && actual == '{') {
				addParser(parsers, new Parser(ParserType.INLINE));
				cache = "";
				inLine = InLineState.IN_LINE;
				inlineCode = new InLine();
			} else if (inLine == InLineState.IN_LINE) {
				inlineCode.addContent(actual);
			} else if (inLine == InLineState.CANDIDATE) {
				inLine = InLineState.NOTHING;
				cache += actual;
		/////////////// TAGS
			} else if (tagState == TagState.CANDIDATE && previous == 't' && actual == ':') {
				javaState = JavaState.NOTHING;
				cache = "";
				tagState = TagState.TAG;
				tagParser = new TagParser(false);
				addParser(parsers, new Parser(ParserType.TAG));
			} else if (tagState == TagState.CANDIDATE && previous == '/' && actual == 't') {
				javaState = JavaState.NOTHING;
				cache += previous + actual;
				tagState = TagState.CLOSE_CANDIDATE;
			} else if (tagState == TagState.CLOSE_CANDIDATE && actual == ':') {
				cache = "";
				tagState = TagState.TAG;
				tagParser = new TagParser(true);
				addParser(parsers, new Parser(ParserType.TAG));
			} else if (tagState == TagState.TAG && actual == '/' && !parsers.getLast().isQuoted()) {
				cache += actual;
				tagState = TagState.INLINE_CLOSE_CANDIDATE;
			} else if (tagState == TagState.INLINE_CLOSE_CANDIDATE && actual == '>') {
				cache = "";
				tagState = TagState.NOTHING;
				node.add("\");");
				node.add(tagParser.getPre());
				node.add(tags.get(tagParser.getName()).getNotPairCode(tagParser.getParams()));
				tagParser = null;
				parsers.removeLast();
				node.add("write(\"");
			} else if (tagState == TagState.TAG && actual == '>' && !parsers.getLast().isQuoted()) {
				cache = "";
				tagState = TagState.NOTHING;
				node.add("\");");
				node.add(tagParser.getPre());
				if (tagParser.isClose()) {
					node.add(tags.get(tagParser.getName()).getPairEndCode(tagParser.getParams()));
				} else {
					node.add(tags.get(tagParser.getName()).getPairStartCode(tagParser.getParams()));
				}
				parsers.removeLast();
				node.add("write(\"");
			} else if (tagState == TagState.TAG) {
				tagParser.parse(previous, actual, parsers.getLast().isSingleQuoted(), parsers.getLast().isDoubleQuoted());
			} else if (tagState == TagState.CANDIDATE || tagState == TagState.CLOSE_CANDIDATE) {
				cache += actual;
				tagState = TagState.NOTHING;
				tagParser = null;
		///////////////
			} else {				
				node.add(cache);
				cache = "";
				if (actual == '\r') {
					// ignored
				} else if (actual == '\n') {
					node.add(
						"\");write(\"" + (minimalize ? "" : "\\n")
					);
				} else {
					if (minimalize && (actual == '\t' || (actual == ' ' && previous == ' ') )) {
						// ignore
					} else {
						if (actual == '\"' || actual == '\\') {
							node.add("\\");
						}
						node.add(actual + "");
					}
				}
			}
			
			previous = actual;
		}
		bw.accept(node.flush());
	}
	
	private void addParser(LinkedList<Parser> parsers, Parser parser) {
		if (parsers.size() == 0) {
			parsers.add(parser);
		} else if (parsers.size() > 0 && parser.getType() == ParserType.TAG) {
			throw new LogicException("Tag cannot be inside tag, variable, in-line cond or code");
		} else if (parsers.getLast().getType() == ParserType.VARIABLE && parser.getType() == ParserType.INLINE) {
			throw new LogicException("Inline condition cannot be inside variable");
		} else {
			parsers.add(parser);
		}
	}
}
