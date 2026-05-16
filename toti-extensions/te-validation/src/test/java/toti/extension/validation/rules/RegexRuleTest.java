package toti.extension.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.validation.ValidationItem;

public class RegexRuleTest {

	@ParameterizedTest
	@MethodSource("dataIsErrorToShow")
	public void testIsErrorToShow(Object value, String regex, boolean expected) {
		RegexRule rule = new RegexRule(null, null);
		assertEquals(expected, rule.isErrorToShow(regex, value));
	}
	
	public static Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { "aaa", "[a]?", false },
			new Object[] { "aaa", "[b]+", true },
		};
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null);
		item.setNewValue("newValue");
		
		RegexRule rule = new RegexRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
}
