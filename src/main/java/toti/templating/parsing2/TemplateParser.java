package toti.templating.parsing2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Map;

import common.functions.InputStreamLoader;
import common.structures.ThrowingConsumer;
import core.text.Text;
import toti.templating.Tag;
import toti.templating.parsing2.enums.InLineState;
import toti.templating.parsing2.enums.JavaState;
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
		
		boolean isSingleQuoted = false;
		boolean isDoubleQuoted = false;
		int level = 0;
		
		LinkedList<VariableParser> vars = new LinkedList<>();
		InLine inlineCode = null;
		TagParser tagParser = null;
		
		while((actual = (char)br.read()) != (char)-1) {
			if (javaState != JavaState.COMMENT
					&& (tagState != TagState.NOTHING || variableState != VarState.NOTHING || inLine != InLineState.NOTHING)
			) {
				if (actual == '"' && previous != '\\' && !isSingleQuoted) {
					isDoubleQuoted = !isDoubleQuoted;
				} else if (actual == '\'' && previous != '\\' && !isDoubleQuoted) {
					isSingleQuoted = !isSingleQuoted;
				}
			}
			
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
			} else if (javaState == JavaState.JAVA && actual == '-') {
				javaState = JavaState.CANDIDATE_COMMENT;
				cache += "-";
			} else if (javaState == JavaState.CANDIDATE_COMMENT && actual == '-') {
				javaState = JavaState.COMMENT;
				cache = "";
				node.add("\");");
			} else if (javaState == JavaState.COMMENT && actual == '-') {
				cache += "-";
				javaState = JavaState.CLOSE_COMMENT_CANDIDATE1;
			} else if (javaState == JavaState.CLOSE_COMMENT_CANDIDATE1 && actual == '-') {
				cache += '-';
				javaState = JavaState.CLOSE_COMMENT_CANDIDATE2;
			} else if (actual == '%' && (javaState == JavaState.CODE || javaState == JavaState.CLOSE_COMMENT_CANDIDATE2) && !(isDoubleQuoted && isSingleQuoted)) {
				cache += "%";
				javaState = JavaState.CLOSE_CANDIDATE;
			} else if (javaState == JavaState.CLOSE_CANDIDATE && actual == '>') {
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
		///////////////
			} else if (actual == '$' && variableState != VarState.CANDIDATE) {
				node.add(cache);
				cache = "$";
				variableState = VarState.CANDIDATE;
			} else if (variableState == VarState.CANDIDATE && actual == '{') {
				cache = "";
				variableState = VarState.VAR;
				vars.add(new VariableParser(++level));
			} else if (actual == '}' && variableState == VarState.VAR && !isSingleQuoted && !isDoubleQuoted) {
				VariableParser var = vars.removeLast();
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
				vars.getLast().accept(previous, actual, isSingleQuoted, isDoubleQuoted);
			} else if (variableState == VarState.CANDIDATE) {
				variableState = VarState.NOTHING;
				cache += actual;
		///////////////
			} else if (actual == '}' && inLine == InLineState.IN_LINE && !isSingleQuoted && !isDoubleQuoted) {
				cache += actual;
				inLine = InLineState.CLOSE_CANDIDATE;
			} else if (inLine == InLineState.CLOSE_CANDIDATE && actual == '}' && !isSingleQuoted && !isDoubleQuoted) {
				cache = "";
				inLine = InLineState.NOTHING;
				node.add("\");");
				node.add(inlineCode.getPre() + "");
				node.add("write(" + inlineCode.getContent().toString() + ");");
				node.add("write(\"");
				inlineCode = null;
			} else if (actual == '{' && inLine == InLineState.NOTHING && !isSingleQuoted && !isDoubleQuoted) {
				node.add(cache);
				cache = "{";
				inLine = InLineState.CANDIDATE;
			} else if (inLine == InLineState.CANDIDATE && actual == '{' && !isSingleQuoted && !isDoubleQuoted) {
				cache = "";
				inLine = InLineState.IN_LINE;
				inlineCode = new InLine();
			} else if (inLine == InLineState.IN_LINE) {
				inlineCode.addContent(actual);
			} else if (inLine == InLineState.CANDIDATE) {
				inLine = InLineState.NOTHING;
				cache += actual;
		///////////////
			} else if (tagState == TagState.CANDIDATE && previous == 't' && actual == ':') {
				javaState = JavaState.NOTHING;
				cache = "";
				tagState = TagState.TAG;
				tagParser = new TagParser(false);
			} else if (tagState == TagState.CANDIDATE && previous == '/' && actual == 't') {
				javaState = JavaState.NOTHING;
				cache += previous + actual;
				tagState = TagState.CLOSE_CANDIDATE;
			} else if (tagState == TagState.CLOSE_CANDIDATE && actual == ':') {
				cache = "";
				tagState = TagState.TAG;
				tagParser = new TagParser(true);
			} else if (tagState == TagState.TAG && actual == '/' && !isDoubleQuoted && !isSingleQuoted) {
				cache += actual;
				tagState = TagState.INLINE_CLOSE_CANDIDATE;
			} else if (tagState == TagState.INLINE_CLOSE_CANDIDATE && actual == '>') {
				cache = "";
				tagState = TagState.NOTHING;
				node.add("\");");
				node.add(tagParser.getPre());
				node.add(tags.get(tagParser.getName()).getNotPairCode(tagParser.getParams()));
				tagParser = null;
				node.add("write(\"");
			} else if (tagState == TagState.TAG && actual == '>' && !isDoubleQuoted && !isSingleQuoted) {
				cache = "";
				tagState = TagState.NOTHING;
				node.add("\");");
				node.add(tagParser.getPre());
				if (tagParser.isClose()) {
					node.add(tags.get(tagParser.getName()).getPairEndCode(tagParser.getParams()));
				} else {
					node.add(tags.get(tagParser.getName()).getPairStartCode(tagParser.getParams()));
				}
				node.add("write(\"");
			} else if (tagState == TagState.TAG) {
				tagParser.parse(actual, isSingleQuoted, isDoubleQuoted, previous);
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
}
