package toti.extension.validation.rules;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class RegexRuleTest {

	@ParameterizedTest
	@MethodSource
	public void testCheck(String regex, Object parsedValue, Set<Object> expectedErrors) {
		RuleTest.test(
			// raw value is not used
			onError->new RegexRule(regex, onError), 1000, parsedValue,
			expectedErrors, true, parsedValue
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] { "[a]?", "aaa", RuleTest.empty() },
			new Object[] { "[b]+", "aaa", RuleTest.filled() },
		};
	}

}
