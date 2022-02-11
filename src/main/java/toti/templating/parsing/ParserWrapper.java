package toti.templating.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.templating.Parameter;
import toti.templating.Tag;
import toti.templating.parsing.enums.VariableSource;
import toti.templating.parsing.enums.ParserType;
import toti.templating.parsing.enums.TagType;

public class ParserWrapper {
	
	private boolean isSingleQuoted = false;
	private boolean isDoubleQuoted = false;
	
	private final Parser parser;
	private final ParserType type;
	
	public ParserWrapper(TagParser parser) {
		this.parser = parser;
		this.type = ParserType.TAG;
	}
	
	public ParserWrapper(VariableParser parser) {
		this.parser = parser;
		this.type = ParserType.VARIABLE;
	}
	
	public ParserWrapper(InLineParser parser) {
		this.parser = parser;
		this.type = ParserType.INLINE;
	}
	
	public ParserWrapper(JavaParser parser) {
		this.parser = parser;
		this.type = ParserType.JAVA;
	}
	
	/*******************/
	
	public void finishTag(LinkedList<String> htmlTags) {
		if (parser instanceof TagParser) {
			TagParser tag = TagParser.class.cast(parser);
			if (!tag.isHtmlTag()) {
				return;
			}
			if (tag.getTagType() == TagType.START) {
				htmlTags.add(tag.getName());
			}
			if (tag.getTagType() == TagType.END) {
				if (htmlTags.isEmpty() || !htmlTags.getLast().equals(tag.getName())) {
					// TODO throw or log or do somethid
					return;
				}
				htmlTags.removeLast();
			}
		}
	}
	
	public boolean accept(char previous, char actual) {
		return parser.accept(previous, actual, isSingleQuoted, isDoubleQuoted);
	}
	
	public ParserType getType() {
		return type;
	}
	
	public void addVariable(ParserWrapper var) {
		parser.addVariable(VariableParser.class.cast(var.parser));
	}
	
	public void addParser(ParserWrapper wrapper) {
		if (type == ParserType.TAG && wrapper.type == ParserType.INLINE) {
			TagParser.class.cast(parser).addInline(InLineParser.class.cast(wrapper.parser));
		}
	}
	
	/*******************/

	public boolean isSingleQuoted() {
		return isSingleQuoted;
	}

	public boolean isDoubleQuoted() {
		return isDoubleQuoted;
	}

	public void setSingleQuoted() {
		this.isSingleQuoted = !isSingleQuoted;
	}

	public void setDoubleQuoted() {
		this.isDoubleQuoted = !isDoubleQuoted;
	}
	
	/**
	 * @return null - no quote, false - single, true - double
	 */
/*	public Boolean isQuoted() {
		return (!isSingleQuoted && !isDoubleQuoted) ? null : isDoubleQuoted;
	}
*/
	/*******************/
	
	public boolean allowChildren() {
		switch (type) {
			case INLINE: return false;
			case JAVA: return false;
			case TAG: return true;
			case VARIABLE: return false;
			default: return true;
		}
	}
	
	public String getContent(Map<String, Tag> tags, Map<String, Parameter> parameters, String element, Boolean isDoubleQuoted) {
		switch (type) {
			case INLINE:
				return String.format("write(%s);", InLineParser.class.cast(parser).getCalling());
			case JAVA:
				JavaParser java = JavaParser.class.cast(parser);
				if (java.isReturning()) {
					return String.format("write(%s);", java.getContent());
				}
				return java.getContent();
			case TAG:
				TagParser tag = TagParser.class.cast(parser);
				if (tag.isHtmlTag()) {
					return String.format("write(\"%s\");",tag.getAsString(parameters));
				}
				return tag.getAsString(tags, parameters);
			case VARIABLE:
				VariableParser variable = VariableParser.class.cast(parser);
				if ("style".equals(element)) {
					return String.format("write(%s);", variable.getCalling(VariableSource.STYLE));
				}
				if ("script".equals(element)) {
					if (isDoubleQuoted == null) {
						return String.format("write(%s);", variable.getCalling(VariableSource.JAVASCRIPT));
					}
					if (isDoubleQuoted) {
						return String.format("write(%s);", variable.getCalling(VariableSource.JAVASCRIPT_D));
					}
					return String.format("write(%s);", variable.getCalling(VariableSource.JAVASCRIPT_S));
				}
				return String.format("write(%s);", variable.getCalling(VariableSource.HTML));
			default: return "";
		}
	}

}
