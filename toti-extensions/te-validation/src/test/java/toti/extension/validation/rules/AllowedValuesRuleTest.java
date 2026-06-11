package toti.extension.validation.rules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AllowedValuesRuleTest implements RuleTest{

	@ParameterizedTest
	@MethodSource
	public void testCheck(
		Collection<Object> allowedList, Object rawValue, Object parsedValue,
		Set<Object> expectedErrors, boolean isMoreValidationPossible
	) {
		RuleTest.test(
			onError->new AllowedValuesRule(allowedList, onError), rawValue, parsedValue,
			expectedErrors, isMoreValidationPossible, parsedValue // rule not change value
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] {
				Arrays.asList(), "raw", "val", RuleTest.filled(), false
			},
			new Object[] {
				Arrays.asList("val"), "raw", "val", RuleTest.empty(), true
			},
			new Object[] {
				Arrays.asList("val"), "raw", null, RuleTest.empty(), true
			},
			new Object[] {
				Arrays.asList("val1", "val2"), "raw", "val", RuleTest.filled(), false
			},
			new Object[] {
				Arrays.asList("val1", "val2"), "raw", "val1", RuleTest.empty(), true
			},
			new Object[] {
				Arrays.asList(42), "raw", 42, RuleTest.empty(), true
			},
			new Object[] {
				Arrays.asList(42), "raw", "42", RuleTest.filled(), false
			},
			new Object[] {
				Arrays.asList("42"), "raw", 42, RuleTest.filled(), false
			}
		};
	}

}
