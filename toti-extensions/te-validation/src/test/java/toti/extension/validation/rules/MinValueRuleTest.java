package toti.extension.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.validation.ValidationItem;

public class MinValueRuleTest {
	
	@ParameterizedTest
	@MethodSource("dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		MinValueRule rule = new MinValueRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public static Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { 12, 10, false },
			new Object[] { 10, 12, true },
			new Object[] { 10, 10, false },
			new Object[] { "12", 10, false },
			new Object[] { "", 12, true },
			new Object[] { null, 12, true },
			new Object[] { "x", 12, true },
		};
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null);
		item.setNewValue("newValue");
		
		MinValueRule rule = new MinValueRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
}
