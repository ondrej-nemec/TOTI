package toti.templating.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ji.common.exceptions.LogicException;
import toti.templating.Parameter;
import toti.templating.Tag;
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
	
	private TagType type;
	private boolean isHtmlTag = true;
	
	private String tagName = "";
	private String paramName = "";
	private String paramValue = null;
	
	private TagMode mode = TagMode.TAG;
	private boolean isCandidate = false;
	private List<TagParserParam> params = new LinkedList<>();
	
	private final Map<String, Tag> tags;
	private final Map<String, Parameter> parameters;
	
	private Tag tag;
	
	public TagParser(char first, Map<String, Tag> tags, Map<String, Parameter> parameters) {
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
			throw new LogicException("Tag syntax error: quotes.");
		}
		if (actual == '/' && !isSingleQuoted && !isDoubleQuoted) {
			isCandidate = true;
		} else if (previous == '/' && isCandidate && actual == '>') {
			if (type == TagType.END) {
				throw new LogicException("Close tag not ends with '/>'.");
			}
			type = TagType.SINGLE;
			finishParameter('\u0000');
			finishTagName();
			return true;
		} else if (previous == '/' && isCandidate && actual != '>') {
			throw new LogicException("Unknow '/'.");
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
			mode = TagMode.NAN;
		} else if (mode == TagMode.PARAM_NAME && actual == '=') {
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
		} else if (mode == TagMode.PARAM_VALUE_S && actual == '"') {
			paramValue += "\\\"";
			
		} else if ((mode == TagMode.PARAM_VALUE_S || mode == TagMode.PARAM_VALUE_D) && actual == '\n') {
			//paramValue += "\\\\n";
		} else if (mode == TagMode.PARAM_VALUE_S || mode == TagMode.PARAM_VALUE_D) {
			paramValue += actual;
		}
		return false;
	}
	
	private void finishTagName() {
		if (tagName.startsWith("t:")) {
			isHtmlTag = false;
			tagName = tagName.substring(2);
			Tag tag = tags.get(tagName);
			if (tag == null) {
				throw new LogicException("Unknown TOTI tag " + tagName);
			}
			this.tag = tag;
		}
	}

	private void finishParameter(char quote) {
		if (!paramName.isEmpty()) {
			params.add(new TagParserParam(paramName, paramValue, quote));
		}
		paramValue = null;
		paramName = "";
	}

	public TagType getTagType() {
		return type;
	}

	public boolean isHtmlTag() {
		return isHtmlTag;
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
	        			throw new LogicException("Unknown TOTI parameter " + param.getName());
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
			throw new LogicException("Unknown TOTI tag " + tagName);
		}
		Map<String, String> params = this.params.stream()
		        .collect(Collectors.toMap(TagParserParam::getName, e->{
		        	if (e.isTag()) {
		        		Parameter p = parameters.get(e.getName());
		        		if (p == null) {
		        			throw new LogicException("Unknown TOTI parameter " + e.getName());
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
			if (!isHtmlTag) {
				paramValue += getVariableFormat(parser.getCalling(VariableSource.NO_ESCAPE));
			} else if (paramName.startsWith("on")) {
				paramValue += getCodeFormat(parser.getCalling(VariableSource.JAVASCRIPT_PARAMETER), false);
			} else if (paramName.equals("src") || paramName.equals("href") || paramName.equals("action")) {
				paramValue += getCodeFormat(parser.getCalling(VariableSource.URL), false);
			} else if (paramName.equals("style")) {
				paramValue += getCodeFormat(parser.getCalling(VariableSource.STYLE_PARAMETER), false);
			} else {
				paramValue += getCodeFormat(parser.getCalling(VariableSource.HTML), false);
			}
		} else {
			if (isHtmlTag) {
				paramName += getCodeFormat(parser.getCalling(VariableSource.HTML), false);
			} else {
				paramName += getCodeFormat(parser.getCalling(VariableSource.NO_ESCAPE), false);
			}
		}
	}

	public void addCode(JavaParser parser) {
		if (paramValue != null) {
			if (!isHtmlTag) {
				paramValue += getVariableFormat(parser.getContent());
			} else {
				paramValue += getCodeFormat(parser.getContent(), true);
			}
		} else {
			paramName += getCodeFormat(parser.getContent(), true);
		}
	}
	
	private String getVariableFormat(String calling) {
		boolean split = tag.splitTextForVariable(paramName);
		return
			(split ? "\" + " : "")
			+ getCodeFormat(calling, false)
			+ (split ? "+ \"" : "");
	}
	
	private String getCodeFormat(String calling, boolean isCode) {
		if (calling.isEmpty()) {
			return "";
		}
		String base = isCode ? "(%s)" : "%s";
		if (isHtmlTag) {
			return String.format("\" + " + base + " + \"", calling);
		}
		return String.format(base, calling);
	}
	
}