package toti.extension.validation.rules;

import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class RequiredItemRuleTest {

	@ParameterizedTest
	@MethodSource
	public void testCheck(boolean required, Object rawValue, Object parsedValue, Set<Object> expectedErrors, boolean isMoreValidationPossible) {
		RuleTest.test(
			errors->new RequiredItemRule(required, p->"Expected Error " + p),
			rawValue, parsedValue,
			expectedErrors, isMoreValidationPossible, parsedValue
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			// not required, values null
			new Object[] { false, null, null, RuleTest.empty(), true },
			// not required, origin null, new not
			new Object[] { false, null, "new value", RuleTest.empty(), true },
			// not required, origin not null, new null
			new Object[] { false, "origin", null, RuleTest.empty(), true },
			// not required, values not null
			new Object[] { false, "origin", "new value", RuleTest.empty(), true },
			// required, values null
			new Object[] { true, null, null, RuleTest.set("Expected Error Name"), false },
			// required, origin null, new not
			new Object[] { true, null, "new value", RuleTest.set("Expected Error Name"), false },
			// required, origin not null, new null
			new Object[] { true, "origin", null, RuleTest.set("Expected Error Name"), false },
			// required, values not null
			new Object[] { true, "origin", "new value", RuleTest.empty(), true },
		};
	}

}
