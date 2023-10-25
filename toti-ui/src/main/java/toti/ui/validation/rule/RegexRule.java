package toti.ui.validation.rule;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ji.translator.Translator;

public class RegexRule extends SimpleRule<String> {

	public RegexRule(String value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(String regex, Object o) {
		Matcher m = Pattern.compile(regex).matcher(o.toString());
		return !m.find();
	}

}
