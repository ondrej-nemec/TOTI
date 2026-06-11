package toti.extension.validation.rules;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MinValueRuleTest {
	
	@ParameterizedTest
	@MethodSource
	public void testCheck(Number minValue, Object parsedValue, Set<Object> expectedErrors) {
		RuleTest.test(
			onError->new MinValueRule(minValue, onError), "not number", parsedValue,
			expectedErrors, true, parsedValue
		);
	}

	public static Object[] testCheck() {
		return new Object[] {
			new Object[] { 10, 12, RuleTest.empty() },
			new Object[] { 12, 10, RuleTest.filled() },
			new Object[] { 10, 10, RuleTest.empty() },
			new Object[] { 10, "12", RuleTest.empty() },
			new Object[] { 12, "", RuleTest.empty() },
			new Object[] { 12, null, RuleTest.empty() },
			new Object[] { 12, "x", RuleTest.filled() },
		};
	}

}
