package toti.extension.validation.rules;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ExpectedTypeRuleTest {

	@ParameterizedTest
	@MethodSource
	public void testCheck(
		Class<?> expectedType, Object rawValue, Object parsedValue,
		Set<Object> expectedErrors, boolean isMoreValidationPossible, Object newValue
	) {
		RuleTest.test(
			onError->new ExpectedTypeRule(expectedType, onError), rawValue, parsedValue,
			expectedErrors, isMoreValidationPossible, newValue
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] {
				String.class, null, "X", RuleTest.empty(), true, null
			},
			new Object[] {
				Integer.class, null, "X", RuleTest.empty(), true, null
			},
			new Object[] {
				String.class, "null", "X", RuleTest.empty(), true, "null"
			},
			new Object[] {
				Integer.class, "null", "X", RuleTest.empty(), true, null
			},
			new Object[] {
				String.class, "someText", "X", RuleTest.empty(), true, "someText"
			},
			new Object[] {
				Integer.class, "someText", "X", RuleTest.filled(), false, "X"
			},
			new Object[] {
				String.class, 42, "X", RuleTest.empty(), true, "42"
			},
			new Object[] {
				Double.class, 42, "X", RuleTest.empty(), true, 42.0
			},
			new Object[] {
				Integer.class, "42", "X", RuleTest.empty(), true, 42
			}
		};
	}
	
}
