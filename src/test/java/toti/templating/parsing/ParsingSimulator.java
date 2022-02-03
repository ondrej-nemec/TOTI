package toti.templating.parsing;

public class ParsingSimulator {
	
	public static boolean simulate(Parser parser, String text) {
		char previous = '\u0000';
		boolean isDoubleQuoted = false;
		boolean isSingleQuoted = false;
		for (char c : text.toCharArray()) {
			if (c == '"' && previous != '\\' && !isSingleQuoted) {
				isDoubleQuoted = !isDoubleQuoted;
			} else if (c == '\'' && previous != '\\' && !isDoubleQuoted) {
				isSingleQuoted = !isSingleQuoted;
			}
			boolean finished = parser.accept(previous, c, isSingleQuoted, isDoubleQuoted);
			if (finished) {
				return true;
			}
			previous = c;
		}
		return false;
	}
	
}
