package toti.templating.parsing;

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
				+ "import java.util.Map;"
				+ "import java.util.HashMap;"
				+ "import translator.Translator;"
				+ "import toti.security.Authorizator;"
				+ "import toti.templating.TemplateFactory;"
				+ "public class %s implements Template{"
					+ "public StringBuilder b = new StringBuilder();"
					//+ "public StringBuilder main=b;"
					+ "public Map<String,StringBuilder>blocks=new HashMap<>();"
				+ "public long getLastModification(){return %sL;}"
				+ "public String create("
					+ "TemplateFactory templateFactory,"
					+ "Map<String, Object>variables,"
					+ "Translator translator,"
					+ "Authorizator authorizator"
				+ ")throws Exception{";
		String clazz2 = "}}";
		String tempFile = tempPath + "/" + namespace + "/" + className + ".java";
		
		Text.get().write((bw)->{
			bw.write(String.format(clazz1, namespace.replaceAll("/", "."), className, modificationTime));
			//bw.write("StringBuilder b = new StringBuilder();");
			bw.write("StringBuilder main=b;");
			//bw.write("Map<String,StringBuilder>blocks=new HashMap<>();");
			bw.write("Template layout=null;");
			bw.write("b.append(\"");			
			loadFile(fileName, bw, module);
			bw.write("\");");
			bw.write("if(layout!=null){"
					+ "layout.create(templateFactory,variables,translator, authorizator);"
					+ "}");
			bw.write("return b.toString();");
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
		char actual;
		char previous = '\u0000';
		
	//	boolean isQuoteNow = false;
		boolean isDoubleQuoted = false;
		boolean isSingleQuoted = false;
		boolean tagCandidate1 = false;
		boolean tagCandidate2 = false;
		boolean isClosingTag = false;		
		boolean isTag = false;	
		boolean isVariable = false;
		boolean isVariableCandidate = false;
		boolean commentCandidate1 = false;
		boolean commentCandidate2 = false;
		boolean closingCommentCandidate = false;
		boolean isComment = false;
		
		TagParser tagParser = null;
		int varIndex = 0;
		LinkedList<VariableParser> variableParsers = new LinkedList<>();
		while((actual = (char)br.read()) != (char)-1) {
			if (isComment && !commentCandidate1 && actual == '-') {
				commentCandidate1 = true;
			} else if (isComment && commentCandidate1 && actual == '-') {
				commentCandidate1 = false;
				commentCandidate2 = true;
			} else if (isComment && commentCandidate2 && actual == '%') {
				commentCandidate1 = false;
				commentCandidate2 = false;
				closingCommentCandidate = true;
			} else if (isComment && closingCommentCandidate && actual == '>') {
				closingCommentCandidate = false;
				isComment = false;
			} else if (isComment) {
				continue;
			// variables
			} else if (/*!isVariable &&*/ actual == '$') {
				isVariableCandidate = true;
			} else if (isVariableCandidate && actual == '{') {
				isVariable = true;
				isVariableCandidate = false;
				variableParsers.add(new VariableParser(varIndex++)); // variableParsers.size()
			} else if (isVariable) {
				boolean continu = variableParsers.getLast().parse(actual, previous);
				if (!continu) {
					VariableParser varParser = variableParsers.removeLast();
					if (variableParsers.size() > 0) {
						variableParsers.getLast().addVariable(varParser);
						isVariable = true;
					} else if (isTag) {
						tagParser.addVariable(varParser);
						isVariable = false;
					} else {						
						bw.accept("\");");
						bw.accept(varParser.getInit());
						bw.accept("b.append(Template.escapeVariable(" + varParser.getResult() + "));");
						bw.accept("b.append(\"");
						isVariable = false;
					}
				}
				//isVariable = continu;
			// tags
			} else if ((isTag || isVariable) && actual == '"' && previous != '\\' && !isSingleQuoted) {
				isDoubleQuoted = !isDoubleQuoted;
			//	isQuoteNow = true;
			} else if ((isTag || isVariable) && actual == '\'' && previous != '\\' && !isDoubleQuoted) {
				isSingleQuoted = !isSingleQuoted;
			//	isQuoteNow = true;
			} else if (!tagCandidate1 && actual == '<' && !isDoubleQuoted && !isSingleQuoted) {
				if (isTag || tagCandidate2) {
					throw new RuntimeException("Tag cannot be in tag");
				}
				tagCandidate1 = true;
			} else if (tagCandidate1 && actual == '/') {
				isClosingTag = true;
			} else if (tagCandidate1 && actual == 't') {
				tagCandidate2 = true;
				tagCandidate1 = false;
			} else if (tagCandidate2 && actual == ':') {
				tagCandidate1 = false;
				tagCandidate2 = false;
				isTag = true;
				tagParser = new TagParser(tags, isClosingTag);
			} else if (isTag /*&& !isQuoteNow*/) {
				boolean continu = tagParser.parse(actual, isSingleQuoted, isDoubleQuoted, previous);
				if (!continu) {
					bw.accept(tagParser.getString());
					tagParser = null;
					isClosingTag = false;
				}
				isTag = continu;
			} else if (tagCandidate1 && actual == '%') {
				tagCandidate1 = false;
				commentCandidate1 = true;
			} else if (commentCandidate1 && actual == '-') {
				commentCandidate1 = false;
				commentCandidate2 = true;
			} else if (commentCandidate2 && actual == '-') {
				commentCandidate2 = false;
				isComment = true;
			} else {
				if (actual == '\r') {
					// ignored
				}else if (actual == '\n') {
					bw.accept(
						"\");b.append(\"" + (minimalize ? "" : "\\n")
					);
				} else if (tagCandidate1 || tagCandidate2) {
					bw.accept("<" + (isClosingTag ? "/" : "") + (tagCandidate2 ? "t" : ""));
					bw.accept(((actual == '\\' || actual == '"') ? "\\" : "") + actual);
					tagCandidate1 = false;
					tagCandidate2 = false;
					isClosingTag = false;
				} else if (actual == '\\' || actual == '"') {
					bw.accept("\\");
					bw.accept(actual + "");
				}  else if (commentCandidate1 || commentCandidate2) {
					bw.accept("<%" + (commentCandidate2 ? "-" : ""));
					bw.accept(actual + "");
					commentCandidate1 = false;
					commentCandidate2 = false;
				} else if (isVariableCandidate) {
					bw.accept("$");
					bw.accept(actual + "");
					isVariableCandidate = false;
				} else {
					if (minimalize && (actual == '\t' || (actual == ' ' && previous == ' ') )) {
						// ignore
					} else {
						bw.accept(actual + "");
					}
				}
			}
			previous = actual;
		}
	}
}
