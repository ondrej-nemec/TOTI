package toti.validation.rules;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.validation.ValidationItem;

public class AllowedValuesRuleTest {

	@ParameterizedTest
	@MethodSource("dataIsErrorToShow")
	public void testIsErrorToShow(Collection<Object> allowedList, Object value, boolean expected) {
		AllowedValuesRule rule = new AllowedValuesRule(null, null);
		assertEquals(expected, rule.isErrorToShow(allowedList, value));
	}
	
	public static Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] {
				Arrays.asList(), "val", true
			},
			new Object[] {
				Arrays.asList("val"), "val", false
			},
			new Object[] {
				Arrays.asList("val1", "val2"), "val", true
			},
			new Object[] {
				Arrays.asList("val1", "val2"), "val1", false
			},
			new Object[] {
				Arrays.asList(42), 42, false
			},
			new Object[] {
				Arrays.asList(42), "42", true
			},
			new Object[] {
				Arrays.asList("42"), 42, true
			}
		};
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null, null);
		item.setNewValue("newValue");
		
		AllowedValuesRule rule = new AllowedValuesRule(null, null);
		assertEquals("newValue", rule.getValue(item));
	}
	
}
