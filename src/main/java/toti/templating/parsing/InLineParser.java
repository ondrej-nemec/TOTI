package toti.templating.parsing;

/**
 * Activate if previous == '{' and actual == '{'
 * @author Ondřej Němec
 *
 */
public class InLineParser implements Parser {
	
	private final StringBuilder content = new StringBuilder();
	
	private boolean closeCandidate = false;

	@Override
	public boolean accept(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted) {
		if (!closeCandidate && actual == '}' && !isDoubleQuoted && !isSingleQuoted) {
			closeCandidate = true;
		} else if (closeCandidate && actual == '}' && previous == '}') {
			return true;
		} else if (closeCandidate) {
			closeCandidate = false;
			content.append("}");
			content.append(actual);
		} else {
			content.append(actual);
		}
		return false;
	}
	
	public String getCalling() {
		return content.toString();
	}

	@Override
	public void addVariable(VariableParser parser) {
		content.append(parser.getCalling());
	}
	
}
