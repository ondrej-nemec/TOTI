package toti.templating.parsing;

/**
 * Activated if previous == '<' and actual == '%'
 * @author Ondřej Němec
 *
 */
public class JavaParser implements Parser {

	enum State {
		NOTHING,
		COMMENT_CANDIDATE,
		JAVA, COMMENT,
		CLOSE_COMMENT_CANDIDATE1, CLOSE_COMMENT_CANDIDATE2, CLOSE_COMMENT_CANDIDATE3,
		CLOSE_CANDIDATE
	}
	
	private State state = State.JAVA;
	private String cache = "";
	private boolean firstAccept = true;
	
	private final StringBuilder content = new StringBuilder();
	private boolean isReturning = false;
	
	@Override
	public boolean accept(char previous, char actual, boolean isSingleQuoted, boolean isDoubleQuoted) {
		boolean firstAccept = this.firstAccept;
		this.firstAccept = false;
		if (firstAccept && actual == '-') {
			state = State.COMMENT_CANDIDATE;
			cache += actual;
		} else if (state == State.COMMENT_CANDIDATE && actual == '-') {
			cache = "";
			state = State.COMMENT;
		} else if (state == State.COMMENT_CANDIDATE) { // revert of previous
			state = State.JAVA;
			content.append(cache);
			cache = "";
		} else if (firstAccept && actual == '='){
			state = State.JAVA;
			isReturning = true;
			return false;
		} else if (state == State.COMMENT && actual == '-') {
			state = State.CLOSE_COMMENT_CANDIDATE1;
		} else if (state == State.CLOSE_COMMENT_CANDIDATE1 && actual == '-') {
			state = State.CLOSE_COMMENT_CANDIDATE2;
		} else if (state == State.CLOSE_COMMENT_CANDIDATE1) { // revert of previous
			state = State.COMMENT;
		} else if (state == State.CLOSE_COMMENT_CANDIDATE2 && actual == '%') {
			state = State.CLOSE_COMMENT_CANDIDATE3;
		} else if (state == State.CLOSE_COMMENT_CANDIDATE2) { // revert of previous
			state = State.COMMENT;
		} else if (state == State.CLOSE_COMMENT_CANDIDATE3 && actual == '>') {
			state = State.NOTHING;
			return true;
		} else if (state == State.CLOSE_COMMENT_CANDIDATE3) { // revert of previous
			state = State.COMMENT;
		} else if (state == State.JAVA && actual == '%') {
			cache += actual;
			state = State.CLOSE_CANDIDATE;
		} else if (state == State.CLOSE_CANDIDATE && actual == '>') {
			state = State.NOTHING;
			cache = "";
			return true;
		} else if (state == State.CLOSE_CANDIDATE) { // revert of previous
			state = State.JAVA;
			content.append(cache);
			cache = "";
		}
		if (state == State.JAVA) {
			content.append(actual);
		}
		return false;
	}

	public String getContent() {
		return content.toString();
	}
	
	public boolean isReturning() {
		return isReturning;
	}

	@Override
	public void addVariable(VariableParser parser) {
		// this is not supported
	}

}
