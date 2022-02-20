package toti.templating.parsing;

import java.util.LinkedList;

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
			case JAVA: return false;
			case TAG: return true;
			case VARIABLE: return false;
			default: return true;
		}
	}
	
	public void addVariable(ParserWrapper var) {
		VariableParser variable = VariableParser.class.cast(var.parser);
		switch (type) {
			case JAVA:
				JavaParser.class.cast(parser).addVariable(variable);
				break;
			case TAG:
				TagParser.class.cast(parser).addVariable(variable);
				break;
			case VARIABLE:
				VariableParser.class.cast(parser).addVariable(variable);
				break;
			default:
				break;
		}
	}
	
	public void addCode(ParserWrapper wrapper) {
		JavaParser code = JavaParser.class.cast(wrapper.parser);
		switch (type) {
			case TAG:
				TagParser.class.cast(parser).addCode(code);
				break;
			case JAVA:
			case VARIABLE:
			default:
				break;
		}
	}
	
	public String getContent(String element, Boolean isDoubleQuoted) {
		switch (type) {
			case JAVA:
				JavaParser java = JavaParser.class.cast(parser);
				if (java.isReturning()) {
					return String.format("write(%s);", java.getContent());
				}
				return java.getContent();
			case TAG:
				TagParser tag = TagParser.class.cast(parser);
				if (tag.isHtmlTag()) {
					return String.format("write(\"%s\");",tag.getHtmlString());
				}
				return tag.getTotiTag();
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
