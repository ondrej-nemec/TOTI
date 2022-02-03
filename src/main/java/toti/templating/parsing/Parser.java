package toti.templating.parsing;

public interface Parser {

	/**
	 * 
	 * @return if parsing finished
	 */
	boolean accept(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted);
	
	void addVariable(VariableParser parser);
	
}
