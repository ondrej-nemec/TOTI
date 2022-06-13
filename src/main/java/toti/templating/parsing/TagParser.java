package toti.templating.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ji.common.exceptions.LogicException;
import toti.templating.Parameter;
import toti.templating.Tag;
import toti.templating.TagVariableMode;
import toti.templating.parsing.enums.VariableSource;
import toti.templating.parsing.enums.TagType;
import toti.templating.parsing.structures.TagParserParam;

/**
 * Activate if previous == '<' and actual != '%'
 * @author Ondřej Němec
 *
 */
public class TagParser implements Parser {
	
	enum TagMode {
		TAG, NAN, PARAM_NAME, AFTER_PARAM, PARAM_VALUE_S, PARAM_VALUE_D;
	}
	
	enum ParMode {
		CANDIDATE, CONCATED_LEFT, CONCATED_RIGHT, CONCATED
	}
	
	private TagType type;
	//private boolean isHtmlTag = true;
	
	private String tagName = "";
	private String paramName = "";
	private String paramValue = null;
	
	private TagMode mode = TagMode.TAG;
	private boolean isCandidate = false;
	private List<TagParserParam> params = new LinkedList<>();
	
	private final Map<String, Tag> tags;
	private final Map<String, Parameter> parameters;
	
	private Tag tag;
	private TagVariableMode tagParamMode;
	private ParMode parmode;

	private final ParsingInfo info;
	
	public TagParser(char first, Map<String, Tag> tags, Map<String, Parameter> parameters, ParsingInfo info) {
		this.info = info;
		this.parameters = parameters;
		this.tags = tags;
		if (first == '/') {
			type = TagType.END;
		} else {
			accept('\u0000', first, false, false);
		}
	}
	
	@Override
	public boolean accept(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted) {
		if (actual == '\r') {
			return false;
		}
		if ((mode == TagMode.TAG || mode == TagMode.NAN || mode == TagMode.PARAM_NAME) && (isSingleQuoted || isDoubleQuoted)) {
			throw new LogicException("Tag syntax error: quotes" + info);
		}
		if (actual == '/' && !isSingleQuoted && !isDoubleQuoted) {
			isCandidate = true;
		} else if (previous == '/' && isCandidate && actual == '>') {
			if (type == TagType.END) {
				throw new LogicException("Close tag not ends with '/>'" + info);
			}
			type = TagType.SINGLE;
			finishParameter('\u0000');
			finishTagName();
			return true;
		} else if (previous == '/' && isCandidate && actual != '>') {
			throw new LogicException("Unexpected '/'" + info);
		} else if (actual == '>' && !isSingleQuoted && !isDoubleQuoted) {
			if (type == null) {
				type = TagType.START;
			}
			finishParameter('\u0000');
			finishTagName();
			return true;
		} else if (mode == TagMode.TAG && Character.isWhitespace(actual)) {
			mode = TagMode.NAN;
			finishTagName();
		} else if (mode == TagMode.TAG) {
			tagName += actual;
			
		// parameter name
		} else if (mode == TagMode.PARAM_NAME && Character.isWhitespace(actual)) {
			finishParamName();
			mode = TagMode.NAN;
		} else if (mode == TagMode.PARAM_NAME && actual == '=') {
			finishParamName();
			mode = TagMode.AFTER_PARAM;
		} else if (mode == TagMode.PARAM_NAME) {
			paramName += actual;
		} else if (mode == TagMode.NAN && actual == '=') {
			mode = TagMode.AFTER_PARAM;
		} else if (mode == TagMode.NAN && !Character.isWhitespace(actual) && !isSingleQuoted && !isDoubleQuoted) {
			mode = TagMode.PARAM_NAME;
			finishParameter('\u0000');
			paramName = "" + actual;
		// new param name, previous has no value
		} else if (mode == TagMode.AFTER_PARAM && !isSingleQuoted && !isDoubleQuoted && !Character.isWhitespace(actual)) { 
			finishParameter('\u0000');
			mode = TagMode.PARAM_NAME;
		// parameter value
		} else if (mode == TagMode.AFTER_PARAM && actual == '"') {
			mode = TagMode.PARAM_VALUE_D;
			paramValue = "";
		} else if (mode == TagMode.PARAM_VALUE_D && !isDoubleQuoted) {
			mode = TagMode.NAN;
			finishParameter(actual);
			
		} else if (mode == TagMode.AFTER_PARAM && actual == '\'') {
			mode = TagMode.PARAM_VALUE_S;
			paramValue = "";
		} else if (mode == TagMode.PARAM_VALUE_S && !isSingleQuoted) {
			mode = TagMode.NAN;
			finishParameter(actual);
		} else if (mode == TagMode.PARAM_VALUE_S && tag == null && actual == '"') {
			addParamValue("\\\""); // paramValue += "\\\"";
			
		} else if ((mode == TagMode.PARAM_VALUE_S || mode == TagMode.PARAM_VALUE_D) && actual == '\n') {
			//ignore // paramValue += "\\\\n";
		} else if (mode == TagMode.PARAM_VALUE_S || mode == TagMode.PARAM_VALUE_D) {
			addParamValue(actual); // paramValue += actual;
		}
		return false;
	}
	
	private void finishParamName() {
		if (tag != null) {
			tagParamMode = tag.getMode(paramName);
		}
	}
	
	private void addParamValue(Object text) {
		if (parmode != null) {
			parmode = parmode == ParMode.CANDIDATE ? ParMode.CONCATED_RIGHT : ParMode.CONCATED;
			paramValue += " + \"";
		}
		paramValue += text;
	}
	
	private void addVariableToParamValue(Object text) {
		paramValue += text;
	}

	private void finishTagName() {
		if (tagName.startsWith("t:")) {
			//isHtmlTag = false;
			tagName = tagName.substring(2);
			Tag tag = tags.get(tagName);
			if (tag == null) {
				throw new LogicException("Unknown TOTI tag " + tagName + info);
			}
			this.tag = tag;
		}
	}

	private void finishParameter(char quote) {
		if (!paramName.isEmpty()) {
			params.add(new TagParserParam(paramName, getParamValue(), quote));
		}
		parmode = null;
		paramValue = null;
		paramName = "";
	}
	
	private String getParamValue() {
		if (parmode != null) {
			switch (parmode) {
			// TODO need escape?
				case CONCATED: return String.format("\"%s\"", paramValue);
				case CONCATED_LEFT: return String.format("\"%s", paramValue);
				case CONCATED_RIGHT: return String.format("%s\"", paramValue);
				default: return paramValue;
			}
		}
		if (tag != null && tag.getMode(paramName) == TagVariableMode.DICTIONARY_VALUE) {
			return String.format("new DictionaryValue(\"%s\")", paramValue.replace("\"", "\\\""));
		}
		return paramValue;
	}
	
	private String formatParser(String calling) {
		switch (tagParamMode) {
			case STRING:
				return String.format("\" + (%s) + \"", calling);
			case CODE: return calling;
			case DICTIONARY_VALUE:
				if (paramValue.isEmpty()) {
					parmode = ParMode.CANDIDATE;
					return String.format("(%s)", calling);
				} else {
					parmode = ParMode.CONCATED_LEFT;
					return String.format("\" + (%s)", calling);
				}
			default: return calling;
		}
	}

	public TagType getTagType() {
		return type;
	}

	public boolean isHtmlTag() {
		return tag == null;
	}

	public String getName() {
		return tagName;
	}

	public List<TagParserParam> getParams() {
		return params;
	}

	public String getHtmlString() {
		StringBuilder paramString = new StringBuilder();
		params.forEach((param)->{
			paramString.append(" ");
			paramString.append(param.getName());
			if (param.isValue()) {
				paramString.append("=");
				String quote = (param.getQuote() == '"' ? "\\" : "") + param.getQuote();
				paramString.append(quote);
				if (param.isTag()) {
					paramString.append("\"+");
	        		Parameter p = parameters.get(param.getName());
	        		if (p == null) {
	        			throw new LogicException("Unknown TOTI parameter " + param.getName() + info);
	        		}
					paramString.append(p.getCode(param.getValue()));
					paramString.append("+\"");
				} else {
					paramString.append(param.getValue());
				}
				paramString.append(quote);
			}
		});
		return String.format(
			"<%s%s%s%s>",
			type == TagType.END ? "/" : "",
			tagName,
			paramString.toString(),
			type == TagType.SINGLE ? " /" : ""
		);
	}
	
	public String getTotiTag() {
		if (tag == null) {
			throw new LogicException("Unknown TOTI tag " + tagName + info);
		}
		Map<String, String> params = this.params.stream()
		        .collect(Collectors.toMap(TagParserParam::getName, e->{
		        	if (e.isTag()) {
		        		Parameter p = parameters.get(e.getName());
		        		if (p == null) {
		        			throw new LogicException("Unknown TOTI parameter " + e.getName() + info);
		        		}
		        		return p.getCode(e.getValue());
		        	}
		        	if (e.isValue()) {
		        		return e.getValue();
		        	}
		        	return "";
		        }));
		switch (type) {
			case END: return tag.getPairEndCode(params);
			case START: return tag.getPairStartCode(params);
			case SINGLE:
			default: return tag.getNotPairCode(params);
		}
	}

	public void addVariable(VariableParser parser) {
		if (paramValue != null) {
			// TODO test this
			if (tag != null) {
				addVariableToParamValue(formatParser(parser.getCalling(VariableSource.NO_ESCAPE)));
				// paramValue += formatParser(parser.getCalling(VariableSource.NO_ESCAPE));
			} else if (paramName.startsWith("on")) {
				addParamValue(getCodeFormat(parser.getCalling(VariableSource.JAVASCRIPT_PARAMETER), false));
				// paramValue += getCodeFormat(parser.getCalling(VariableSource.JAVASCRIPT_PARAMETER), false);
			} else if (paramName.equals("src") || paramName.equals("href") || paramName.equals("action")) {
				addParamValue(getCodeFormat(parser.getCalling(VariableSource.URL), false));
				// paramValue += getCodeFormat(parser.getCalling(VariableSource.URL), false);
			} else if (paramName.equals("style")) {
				addParamValue(getCodeFormat(parser.getCalling(VariableSource.STYLE_PARAMETER), false));
				// paramValue += getCodeFormat(parser.getCalling(VariableSource.STYLE_PARAMETER), false);
			} else {
				addParamValue(getCodeFormat(parser.getCalling(VariableSource.HTML), false));
				// paramValue += getCodeFormat(parser.getCalling(VariableSource.HTML), false);
			}
		} else {
			if (tag == null) {
				paramName += getCodeFormat(parser.getCalling(VariableSource.HTML), false);
			} else {
				paramName += getCodeFormat(parser.getCalling(VariableSource.NO_ESCAPE), false);
			}
		}
	}

	public void addCode(JavaParser parser) {
		if (paramValue != null) {
			if (tag != null) {
				addVariableToParamValue(formatParser(parser.getContent()));
				// paramValue += formatParser(parser.getContent());
			} else {
				addParamValue(getCodeFormat(parser.getContent(), true));
				// paramValue += getCodeFormat(parser.getContent(), true);
			}
		} else {
			paramName += getCodeFormat(parser.getContent(), true);
		}
	}
	
	private String getCodeFormat(String calling, boolean isCode) {
		if (calling.isEmpty()) {
			return "";
		}
		String base = isCode ? "(%s)" : "%s";
		return String.format("\" + " + base + " + \"", calling);
	}
	
}