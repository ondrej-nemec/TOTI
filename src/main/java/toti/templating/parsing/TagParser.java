package toti.templating.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ji.common.exceptions.LogicException;
import toti.templating.Tag;
import toti.templating.parsing.enums.TagType;
import toti.templating.parsing.structures.TagParserParam;

/**
 * Activate if previous == '<' and actual != '%'
 * @author Ondřej Němec
 *
 */
public class TagParser implements Parser {
	
	enum TagMode {
		TAG, NAN, PARAM_NAME, AFTER_PARAM, PARAM_VALUE;
	}
	
	private TagType type;
	private boolean isHtmlTag = true;
	
	private String tagName = "";
	private String paramName = "";
	private String paramValue = null;
	
	private TagMode mode = TagMode.TAG;
	private boolean isCandidate = false;
	private List<TagParserParam> params = new LinkedList<>();
	
	public TagParser(char first) {
		if (first == '/') {
			type = TagType.END;
		} else {
			accept('\u0000', first, false, false);
		}
	}
	
	@Override
	public boolean accept(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted) {
		if ((mode == TagMode.TAG || mode == TagMode.NAN || mode == TagMode.PARAM_NAME) && (isSingleQuoted || isDoubleQuoted)) {
			throw new LogicException("Tag syntax error: quotes");
		}
		if (actual == '/' && !isSingleQuoted && !isDoubleQuoted) {
			isCandidate = true;
		} else if (previous == '/' && isCandidate && actual == '>') {
			if (type == TagType.END) {
				throw new LogicException("Close tag not ends with />");
			}
			type = TagType.SINGLE;
			finishParameter('\u0000');
			finishTagName();
			return true;
		} else if (previous == '/' && isCandidate && actual != '>') {
			throw new LogicException("Unknow '/'");
		} else if (actual == '>' && !isSingleQuoted && !isDoubleQuoted) {
			if (type == null) {
				type = TagType.START;
			}
			finishParameter('\u0000');
			finishTagName();
			return true;
		} else if (mode == TagMode.TAG && actual == ' ') {
			mode = TagMode.NAN;
			finishTagName();
		} else if (mode == TagMode.TAG) {
			tagName += actual;
			
		// parameter name
		} else if (mode == TagMode.PARAM_NAME && actual == ' ') {
			mode = TagMode.NAN;
		} else if (mode == TagMode.PARAM_NAME && actual == '=') {
			mode = TagMode.AFTER_PARAM;
		} else if (mode == TagMode.PARAM_NAME) {
			paramName += actual;
		} else if (mode == TagMode.NAN && actual == '=') {
			mode = TagMode.AFTER_PARAM;
		} else if (mode == TagMode.NAN && actual != ' ' && !isSingleQuoted && !isDoubleQuoted) {
			mode = TagMode.PARAM_NAME;
			finishParameter('\u0000');
			paramName = "" + actual;
		// new param name, previous has no value
		} else if (mode == TagMode.AFTER_PARAM && !isSingleQuoted && !isDoubleQuoted && actual != ' ') { 
			finishParameter('\u0000');
			mode = TagMode.PARAM_NAME;
		// parameter value
		} else if (mode == TagMode.AFTER_PARAM && (actual == '\'' || actual == '"')) {
			mode = TagMode.PARAM_VALUE;
			paramValue = "";
		} else if (mode == TagMode.PARAM_VALUE && !isSingleQuoted && !isDoubleQuoted) {
			mode = TagMode.NAN;
			finishParameter(actual);
		} else if (mode == TagMode.PARAM_VALUE) {
			paramValue += actual;
		}
		return false;
	}
	
	private void finishTagName() {
		if (tagName.startsWith("t:")) {
			isHtmlTag = false;
			tagName = tagName.substring(2);
		}
	}

	private void finishParameter(char quote) {
		// TODO t: parameters
		if (!paramName.isEmpty()) {
			params.add(new TagParserParam(paramName, paramValue, quote));
		}
		paramValue = null;
		paramName = "";
	}
/*
	public void addVariable(VariableParser var) {
		// TODO check allready can be started param mode
		paramValue += var.getCalling();
		mode = TagMode.PARAM_VALUE;
	}
*/
	
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

	public String getAsString() {
		StringBuilder paramString = new StringBuilder();
		params.forEach((param)->{
			paramString.append(" ");
			paramString.append(param.getName());
			if (param.isValue()) {
				paramString.append("=");
				String quote = (param.getQuote() == '"' ? "\\" : "") + param.getQuote();
				paramString.append(quote);
				paramString.append(param.getValue());
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
	
	public String getAsString(Map<String, Tag> tags) {
		Tag tag = tags.get(tagName);
		if (tag == null) {
			throw new LogicException("Unknown TOTI tag " + tagName);
		}
		Map<String, String> params = this.params.stream()
		        .collect(Collectors.toMap(TagParserParam::getName, e ->e.isValue() ? e.getValue() : ""));
		switch (type) {
			case END: return tag.getPairEndCode(params);
			case START: return tag.getPairStartCode(params);
			case SINGLE:
			default: return tag.getNotPairCode(params);
		}
	}

	@Override
	public void addVariable(VariableParser parser) {
		if (paramValue != null) {
			paramValue += parser.getCalling();
		} else {
			paramName += parser.getCalling();
		}
	}
	
	public void addInline(InLineParser parser) {
		if (paramValue != null) {
			paramValue += String.format(getCodeFormat(),  parser.getCalling());
		} else {
			paramName += String.format(getCodeFormat(),  parser.getCalling());
		}
	}
	
	private String getCodeFormat() {
		if (isHtmlTag) {
			return "\" + (%s) + \"";
		}
		return "(%s)";
	}
	
}
