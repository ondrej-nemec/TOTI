package toti.templating.parsing2;

import toti.templating.parsing2.enums.ParserType;
import toti.templating.parsing2.enums.VarState;

public class Parser {

	private final ParserType type;
	
	private boolean isSingleQuoted = false;
	private boolean isDoubleQuoted = false;
	
	private TagParser tagParser;
	private VariableParser varParser;
	private InLine inline;
	
	public Parser(VariableParser varParser) {
		this.varParser = varParser;
		this.type = ParserType.VARIABLE;
	}
	
	public Parser(TagParser tagParser) {
		this.tagParser = tagParser;
		this.type = ParserType.TAG;
	}
	
	public Parser(InLine inline) {
		this.inline = inline;
		this.type = ParserType.INLINE;
	}
	
	public Parser(ParserType type) {
		this.type = type;
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
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	public VarState addVariable(VariableParser var) {
		switch (type) {
			case VARIABLE:
				varParser.addVariable(var);
				return VarState.VAR; // another var continue
			case TAG:
				tagParser.addVariable(var);
				return VarState.NOTHING;
			case INLINE:
				inline.addVariable(var);
				return VarState.NOTHING;
			default: return null;
		}
	}

	public TagParser getTagParser() {
		return tagParser;
	}

	public VariableParser getVarParser() {
		return varParser;
	}

	public InLine getInline() {
		return inline;
	}
	
}
