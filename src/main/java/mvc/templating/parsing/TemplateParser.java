package mvc.templating.parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import common.structures.ThrowingConsumer;
import core.text.InputStreamLoader;
import core.text.Text;
import mvc.templating.Tag;

public class TemplateParser {
	
	private final Map<String, Tag> tags;
	
	public TemplateParser(Map<String, Tag> tags) {
		this.tags = tags;
	}
	
	public String createTempCache(
			String namespace,
			String className,
			String fileName, 
			String tempPath,
			long modificationTime) throws IOException {
		String preClass = namespace.length() == 0 ? "%s" : "package %s;";
		String clazz1 = preClass
				+ "import mvc.templating.Template;" 
				+ "import java.util.Map;"
				+ "import java.util.HashMap;"
				+ "import translator.Translator;"
				+ "import mvc.templating.TemplateFactory;"
				+ "public class %s implements Template{"
					+ "public StringBuilder b = new StringBuilder();"
					//+ "public StringBuilder main=b;"
					+ "public Map<String,StringBuilder>blocks=new HashMap<>();"
				+ "public long getLastModification(){return %sL;}"
				+ "public String create("
					+ "TemplateFactory templateFactory,"
					+ "Map<String, Object>variables,"
					+ "Translator translator"
				+ ")throws Exception{";
		String clazz2 = "}}";
		String tempFile = tempPath + "/" + namespace + "/" + className + ".java";
		
		Text.write((bw)->{
			bw.write(String.format(clazz1, namespace.replaceAll("/", "."), className, modificationTime));
			//bw.write("StringBuilder b = new StringBuilder();");
			bw.write("StringBuilder main=b;");
			//bw.write("Map<String,StringBuilder>blocks=new HashMap<>();");
			bw.write("Template layout=null;");
			bw.write("b.append(\"");			
			loadFile(fileName, bw);
			bw.write("\");");
			bw.write("if(layout!=null){"
					+ "layout.create(templateFactory,variables,translator);"
					+ "}");
			bw.write("return b.toString();");
			bw.write(clazz2);
		}, tempFile, false);
		
		return tempFile;
	}
	
	private void loadFile(String fileName, BufferedWriter bw) throws IOException {
		Text.read((br)->{
			loadFile(br, (text)->{
				bw.write(text);
			});			
			// TODO read s void
			return null;
		}, InputStreamLoader.createInputStream(this.getClass(), fileName), "utf-8"); // TODO maybe configurable
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
			} else if (actual == '<' && !isDoubleQuoted && !isSingleQuoted) {
				if (tagCandidate1 || isTag || tagCandidate2) {
					// TODO throw tags cannot be in tags
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
				} else if (actual == '\\' || actual == '"') {
					bw.accept("\\");
					bw.accept(actual + "");
				} else if (actual == '\n') {
					bw.accept(
						"\");b.append(\"\\n"
					);
				} else if (tagCandidate1 || tagCandidate2) {
					bw.accept("<" + (isClosingTag ? "/" : "") + (tagCandidate2 ? "t" : ""));
					bw.accept(actual + "");
					tagCandidate1 = false;
					tagCandidate2 = false;
					isClosingTag = false;
				} else if (commentCandidate1 || commentCandidate2) {
					bw.accept("<%" + (commentCandidate2 ? "-" : ""));
					bw.accept(actual + "");
					commentCandidate1 = false;
					commentCandidate2 = false;
				} else if (isVariableCandidate) {
					bw.accept("$");
					bw.accept(actual + "");
				} else {
					bw.accept(actual + "");
				}
			}
			previous = actual;
		}
	}
}