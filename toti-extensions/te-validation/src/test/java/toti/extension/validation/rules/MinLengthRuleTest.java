package toti.extension.validation.rules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;

import toti.extension.validation.Validator;

public class MinLengthRuleTest {

	@ParameterizedTest
	@MethodSource
	public void testCheck(int minLength, Object parsedValue, Set<Object> expectedErrors) {
		RuleTest.test(
			// raw value is not used
			onError->new MinLengthRule(minLength, onError), mock(Validator.class), parsedValue,
			expectedErrors, true, parsedValue
		);
	}
	
	public static Object[] testCheck() {
		Map<Object, Object> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		return new Object[] {
			new Object[] { 6, null, RuleTest.empty() },
			new Object[] { 12, 10, RuleTest.filled() },
			new Object[] { 6, "abcdefg", RuleTest.empty() },
			new Object[] { 7, "abcdefg", RuleTest.empty() },
			new Object[] { 8, "abcdefg", RuleTest.filled() },
			new Object[] { 3, Arrays.asList(1, 2, 3), RuleTest.empty() },
			new Object[] { 2, Arrays.asList(1, 2, 3), RuleTest.empty() },
			new Object[] { 4, Arrays.asList(1, 2, 3), RuleTest.filled() },
			new Object[] { 2, map, RuleTest.empty() },
			new Object[] { 3, map, RuleTest.empty() },
			new Object[] { 4, map, RuleTest.filled() },
		};
	}

}
