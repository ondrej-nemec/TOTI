package toti.templating.parsing2;

import toti.templating.parsing2.enums.ParserType;

public class Parser {

	private final ParserType type;
	
	private boolean isSingleQuoted = false;
	private boolean isDoubleQuoted = false;
	
	private VariableParser varParser;
	private TagParser tagParser;
	
	public Parser(ParserType type) {
		this.type = type;
	}
	
	public Parser(ParserType type, VariableParser varParser) {
		this.type = type;
		this.varParser = varParser;
	}
	
	public Parser(ParserType type, TagParser tagParser) {
		this.type = type;
		this.tagParser = tagParser;
	}

	public VariableParser getVarParser() {
		return varParser;
	}

	public TagParser getTagParser() {
		return tagParser;
	}

	public ParserType getType() {
		return type;
	}
	
	public boolean isQuoted() {
		return isSingleQuoted || isDoubleQuoted;
	}

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
	
}
