package mvc.templating;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TagParser {
	
	private final StringBuilder builder = new StringBuilder();
	
	private final Map<String, Tag> tags;
	
	private final boolean isClosingTag;
	
	public TagParser(Map<String, Tag> tags, boolean isClosingTag) {
		this.tags = tags;
		this.isClosingTag = isClosingTag;
	}
	
	public String getString() {
		return builder.toString();
	}
	
	private String tagName = "";
	private boolean isTagName = true;
	
	private String paramName = "";
	private String paramNameCache = "";
	private boolean isParamName = false;
	
	private String paramValue = "";
	private boolean isParamValue = false;
	private Map<String, String> params = new HashMap<>();
	
	public void addVariable(String var) {
		if (isTagName) {
			// TODO throw or add as param name
		} else if (isParamName) {
			paramName += var;
		} else if (isParamValue) {
			paramValue += var;
		} else {
			// no mode
			isParamName = true;
			paramName += var;
		}
	}
	
	public boolean parse(char actual, boolean isSingleQuoted, boolean isDoubleQuoted, char previous) throws IOException {
		// tag end
		if (actual == '/' && !isDoubleQuoted && !isSingleQuoted) {
			// ignored
		} else if (actual == '>' && !isDoubleQuoted && !isSingleQuoted) {
			if (isParamValue) {
				params.put(paramNameCache, paramValue);
			} else if (isParamName) {
				params.put(paramName, "");
			}
			builder.append("\");");
			if (tags.get(tagName) != null) {
				if (previous == '/') {
					builder.append(tags.get(tagName).getNotPairCode(params));
				} else if (isClosingTag) {
					builder.append(tags.get(tagName).getPairEndCode(params));
				} else {
					builder.append(tags.get(tagName).getPairStartCode(params));
				}
			} // TODO maybe else log
			builder.append("b.append(\"");
			return false;
		// tag name		
		} else if (isTagName && actual != ' ') {
			tagName += actual;
		} else if (isTagName && actual == ' ') {
			isTagName = false;
		// param name
		} else if (!isParamName && actual != ' ' && actual != '=' && !isDoubleQuoted && !isSingleQuoted) {
			if (!paramValue.isEmpty()) {
				params.put(paramNameCache, paramValue);
				paramValue = "";
			}
			paramName += actual;
			isParamName = true;
		} else if (isParamName && (actual == ' ' || actual == '=')) {
			isParamName = false;
			params.put(paramName, "");
			paramNameCache = paramName;
			paramName = "";
		} else if (isParamName) {
			paramName += actual;
		// param value
		} else if (!isParamValue && !isParamName && (isDoubleQuoted || isSingleQuoted)) {
			isParamValue = true;
			paramValue += actual;
		} else if (isParamValue && !isDoubleQuoted && !isSingleQuoted) {
			isParamValue = false;
			params.put(paramNameCache, paramValue);
			paramNameCache = "";
			paramValue = "";
		} else if (isParamValue && (isDoubleQuoted || isSingleQuoted)) {
			paramValue += actual;
		}
		return true;
	}

}
