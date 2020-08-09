package mvc.templating;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import core.text.Text;

public class TemplateParser {
	
	private final Map<String, Tag> tags;
	
	public TemplateParser(Map<String, Tag> tags) {
		this.tags = tags;
	}
	
	public String createTempCache(
			String className,
			String fileName, 
			String tempPath,
			long modificationTime) throws IOException {
		String clazz1 = "import mvc.templating.Template;" 
				+ "import java.util.Map;"
				+ "public class %s implements Template"
				+ "{public long getLastModification(){return %sL;}"
				+ "public String create(Map<String, Object>variables)throws Exception{";
		String clazz2 = "}}";
		String tempFile = tempPath + "/" + className + ".java";
		
		Text.write((bw)->{
			bw.write(String.format(clazz1, className, modificationTime));
			bw.write("StringBuilder b = new StringBuilder();");
			bw.write("b.append(\"");			
			loadFile(fileName, bw);
			bw.write("\");");
			bw.write("return b.toString();");
			bw.write(clazz2);
		}, tempFile, false);
		
		return tempFile;
	}
	
	private void loadFile(String fileName, BufferedWriter bw) throws IOException {
		Text.read((br)->{
			loadFile(br, bw);			
			// TODO read s void
			return null;
		}, fileName);
	}
	
	protected void loadFile(BufferedReader br, BufferedWriter bw) throws IOException {
		char actual;
		char previous = '\u0000';
		
		boolean isDoubleQuoted = false;
		boolean isSingleQuoted = false;
		boolean tagCandidate1 = false;
		boolean tagCandidate2 = false;
		boolean isClosingTag = false;
		
		boolean isTag = false;
		String tagName = "";
		boolean isTagBody = false;
		boolean isTagParamName = false;
		String tagParamName = "";
		String tagParamNameCache = "";
		boolean isTagParamValue = false;
		String tagParamValue = "";
		Map<String, String> params = new HashMap<>();
		
		
		boolean isVariable = false;
		boolean isVariableCandidate = false;
		String variable = "";
		//List<String> devinedVariables = new ArrayList<>();
		while((actual = (char)br.read()) != (char)-1) {
			if (actual == '$') {
				isVariableCandidate = true;
				isTagParamName = isTagParamName || (!isTagParamValue && isTagBody && !isDoubleQuoted && !isSingleQuoted);
				isTagParamValue = isTagParamValue || (!isTagParamName && isTagBody && (isDoubleQuoted || isSingleQuoted));
			} else if (isVariableCandidate && actual == '{') {
				isVariable = true;
				isVariableCandidate = false;
			} else if (isVariable && actual == '}') {
				if (isTagBody) {
					String appendString = "\" + Template.escapreVariable(variables.get(\"" +  variable + "\").toString()) + \"";
					if (isTagParamName) {
						tagParamName +=  "\" + Template.escapreVariable(variables.get(\"" +  variable + "\").toString()) + \"";
					} else if (isTagParamValue) {
						// TODO tohle porad nejde
						tagParamValue +=  "Template.escapreVariable(variables.get(\"" +  variable + "\").toString())";
					}
				/*	if (!devinedVariables.contains(variable)) {
						bw.write("\");");
						bw.write("String " + variable + " = Template.escapreVariable(variables.get(\"" + variable + "\"));");
						bw.write("b.append(\"");
					}
					if (isTagParamValue) {
						tagParamValue += "\" +  + \"";
					}*/
				} else {
					bw.write(	
							"\");"
							+ "b.append(Template.escapreVariable(variables.get(\"" + variable + "\").toString()));"
							+ "b.append(\""
						);
				}
				isVariable = false;
				variable = "";
			} else if (isVariable && (actual == ' ' || actual == '\n' || actual == '\r' || actual == '"' || actual == '\'')) {
				throw new RuntimeException("Variable cannot be interupted");
			} else if (isVariable) {
				variable += actual;			
			/*** quotes ****/
			} else if ((isTag || isTagBody) && actual == '"' && previous != '\\' && !isSingleQuoted) {
				isDoubleQuoted = !isDoubleQuoted;
			} else if ((isTag || isTagBody) && actual == '\'' && previous != '\\' && !isDoubleQuoted) {
				isSingleQuoted = !isSingleQuoted;
			/***** tag start ****/
			} else if (!tagCandidate1 && actual == '<' && !isDoubleQuoted && !isSingleQuoted) {
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
			/*** tag end ****/
			} else if ((isTag || isTagBody) && actual == '/' && !isDoubleQuoted && !isSingleQuoted) {
				// ignored
			} else if ((isTag || isTagBody) && actual == '>' && !isDoubleQuoted && !isSingleQuoted) {
				if (isTagParamValue) {
					params.put(tagParamNameCache, tagParamValue);
				}
				bw.write("\");");
				if (tags.get(tagName) != null) {
					if (isClosingTag) {
						bw.write(tags.get(tagName).getClosingCode(params));
					} else {
						bw.write(tags.get(tagName).getStartingCode(params));
					}
				} // TODO maybe else log
				bw.write("b.append(\"");
				isTag = false;
				isTagBody = false;
				isClosingTag = false;
				isTagParamName = false;
				isTagParamValue = false;
				tagName = "";
				tagParamName = "";
				tagParamNameCache = "";
				tagParamValue = "";
				params = new HashMap<>();
			/*** tag split ***/
			} else if (isTag && actual != ' ') {
				tagName += actual;
			} else if (isTag && actual == ' ') {
				isTag = false;
				isTagBody = true;
			/**** tag params  *****/
			} else if (isTagBody && !isTagParamName && actual != ' ' && !isDoubleQuoted && !isSingleQuoted) {
				isTagParamName = true;
				tagParamName += actual;
			} else if (isTagParamName && (actual == ' ' || actual == '=')) {
				isTagParamName = false;
				params.put(tagParamName, "");
				tagParamNameCache = tagParamName;
				tagParamName = "";
			} else if (isTagParamName) {
				tagParamName += actual;
			} else if (isTagBody && !isTagParamValue && (isDoubleQuoted || isSingleQuoted)) {
				isTagParamValue = true;
				tagParamValue += actual;
			} else if (isTagParamValue && !isDoubleQuoted && !isSingleQuoted) {
				isTagParamValue = false;
				params.put(tagParamNameCache, tagParamValue);
				tagParamNameCache = "";
				tagParamValue = "";
			} else if (isTagParamValue && (isDoubleQuoted || isSingleQuoted)) {
				tagParamValue += actual;
			/*** just write non special tags and text ***/
			} else {
				if (actual == '\r') {
					// ignored
				} else if (actual == '\\' || actual == '"') {
					bw.write('\\');
					bw.write(actual);
				} else if (actual == '\n') {
					bw.write(
						"\");b.append(\"\\n"
					);
				} else if (tagCandidate1 || tagCandidate2) {
					bw.write("<" + (isClosingTag ? "/" : "") + (tagCandidate2 ? "t" : ""));
					bw.write(actual);
					tagCandidate1 = false;
					tagCandidate2 = false;
					isClosingTag = false;

				} else if (isVariableCandidate) {
					bw.write('$');
					bw.write(actual);
				} else {
					bw.write(actual);
				}
			}
			previous = actual;
		}
	}
	

	/**
	 * promenna - inicializace, set a vypis
	 * <t:var type="" name="" value=""/> <t:set name="" value=""/> <t:out name="" />
	 * preklad
	 * <>
	 * try catch finally
	 * if else if else
	 * switch case default
	 * import, layout, bloky
	 * for, while + continue, break
	 * link/url
	 * 
	 * dump
	 * 
	 * obecny vypis promenne s a bez escapovani
	 
	 */
}
