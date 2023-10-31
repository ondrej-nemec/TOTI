package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.validation.ValidationItem;

@RunWith(JUnitParamsRunner.class)
public class AllowedValuesRuleTest {

	@Test
	@Parameters(method="dataIsErrorToShow")
	public void testIsErrorToShow(Collection<Object> allowedList, Object value, boolean expected) {
		AllowedValuesRule rule = new AllowedValuesRule(null, null);
		assertEquals(expected, rule.isErrorToShow(allowedList, value));
	}
	
	public Object[] dataIsErrorToShow() {
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
		ValidationItem item = new ValidationItem("origin", null, null);
		item.setNewValue("newValue");
		
		AllowedValuesRule rule = new AllowedValuesRule(null, null);
		assertEquals("newValue", rule.getValue(item));
	}
	
}
