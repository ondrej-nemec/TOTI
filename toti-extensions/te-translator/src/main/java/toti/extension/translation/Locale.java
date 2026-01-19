package toti.extension.translation;

import java.util.List;

public class Locale {
	private final String lang;
	private final boolean isLeftToRight;
	private final List<String> substitution;

	public Locale(String lang, boolean isLeftToRight, List<String> substitution) {
		this.lang = lang;
		this.isLeftToRight = isLeftToRight;
		this.substitution = substitution;
	}

	public String getLang() {
		return lang;
	}

	public boolean isLeftToRight() {
		return isLeftToRight;
	}

	public List<String> getSubstitution() {
		return substitution;
	}

	@Override
	public String toString() {
		return "Locale [lang=" + lang + ", isLeftToRight=" + isLeftToRight + ", substitution=" + substitution + "]";
	}
}
