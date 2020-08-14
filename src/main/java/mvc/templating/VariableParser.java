package mvc.templating;

import java.io.IOException;

public class VariableParser {
	
	private final StringBuilder builder = new StringBuilder();
	
	public VariableParser() {}
	
	public String getString() {
		return builder.toString();
	}
	
	public void addVariable(String var) {
		builder.append(var);
	}
	
	private boolean isSingleQuoted = false;
	private boolean isDoubleQuoted = false;
	
	enum State {
		VAR_NAME
	}
	
	private State state = State.VAR_NAME;
	private String varName = "";
	
	public boolean parse(char actual, char previous) throws IOException {
		if (actual == '"' && previous != '\\' && !isSingleQuoted) {
			isDoubleQuoted = !isDoubleQuoted;
		} else if (actual == '\'' && previous != '\\' && !isDoubleQuoted) {
			isSingleQuoted = !isSingleQuoted;
		}
		
		if (actual == '}' && !(isSingleQuoted || isDoubleQuoted)) {
			return false;
		} else if (actual == '.' && !isSingleQuoted && !isDoubleQuoted) {
			if (state == State.VAR_NAME) {
				// TODO
			}
		} else if (state == State.VAR_NAME) {
			varName += actual;
		}
		return true;
	}

}
