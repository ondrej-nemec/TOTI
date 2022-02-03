package toti.templating.parsing;

import java.util.Map;

import toti.templating.Tag;
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
	
	public boolean allowVariable() {
		switch (type) {
			case INLINE: return true;
			case JAVA: return false;
			case TAG: return true;
			case VARIABLE: return true;
			default: return true;
		}
	}
	
	public String getContent(Map<String, Tag> tags) {
		switch (type) {
			case INLINE:
				return String.format("write(%s);", InLineParser.class.cast(parser).getCalling());
			case JAVA:
				return JavaParser.class.cast(parser).getContent();
			case TAG:
				TagParser tag = TagParser.class.cast(parser);
				if (tag.isHtmlTag()) {
					return String.format("write(\"%s\");",tag.getAsString());
				}
				return tag.getAsString(tags);
			case VARIABLE:
				VariableParser variable = VariableParser.class.cast(parser);
				if (variable.escape()) {
					return String.format("write(Template.escapeVariable(%s));", variable.getCalling());
				}
				return String.format("write(%s);", variable.getCalling());
			default: return "";
		}
	}

}
