package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.validation.ValidationItem;

@RunWith(JUnitParamsRunner.class)
public class MaxValueRuleTest {
	
	@Test
	@Parameters(method="dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		MaxValueRule rule = new MaxValueRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public Object[] dataIsErrorToShow() {
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
		ValidationItem item = new ValidationItem("origin", null, null);
		item.setNewValue("newValue");
		
		MaxValueRule rule = new MaxValueRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
}
