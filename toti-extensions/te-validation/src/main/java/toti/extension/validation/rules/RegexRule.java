package toti.extension.validation.rules;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;

public class RegexRule implements Rule {

	private final String regex;
	private final Supplier<String> onError;

	public RegexRule(String regex, Supplier<String> onError) {
		this.regex = regex;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		Matcher m = Pattern.compile(regex).matcher(item.getRawValue().toString());
		if (!m.find()) {
			item.addError(onError.get());
		}
		return new CheckResult(true);
	}

}
