package toti.extension.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.validation.ValidationItem;

public class MaxValueRuleTest {
	
	@ParameterizedTest
	@MethodSource("dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		MaxValueRule rule = new MaxValueRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public static Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { 10, 12, false },
			new Object[] { 12, 10, true },
			new Object[] { 10, 10, false },
			new Object[] { "10", 12, false },
			new Object[] { "", 12, true },
			new Object[] { null, 12, true },
			new Object[] { "x", 12, true },
		};
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null);
		item.setNewValue("newValue");
		
		MaxValueRule rule = new MaxValueRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
}
