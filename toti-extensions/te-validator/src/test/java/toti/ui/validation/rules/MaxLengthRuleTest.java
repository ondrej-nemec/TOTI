package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.validation.ValidationItem;

@RunWith(JUnitParamsRunner.class)
public class MaxLengthRuleTest {
	
	@Test
	@Parameters(method="dataIsErrorToShow")
	public void testIsErrorToShow(Object value, Integer bond, boolean expected) {
		MaxLengthRule rule = new MaxLengthRule(null, null);
		assertEquals(expected, rule.isErrorToShow(bond, value));
	}
	
	public Object[] dataIsErrorToShow() {
		Map<Object, Object> map = new HashMap<>();
		map.put("a", "a");
		map.put("b", "b");
		map.put("c", "c");
		return new Object[] {
			new Object[] { 10, 12, false },
			new Object[] { "abcdefg", 7, false },
			new Object[] { "abcdefg", 6, true },
			new Object[] { "abcdefg", 8, false },
			new Object[] { Arrays.asList(1, 2, 3), 3, false },
			new Object[] { Arrays.asList(1, 2, 3), 2, true },
			new Object[] { Arrays.asList(1, 2, 3), 4, false },
			new Object[] { map, 3, false },
			new Object[] { map, 2, true },
			new Object[] { map, 4, false },
		};
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null, null);
		item.setNewValue("newValue");
		
		MaxLengthRule rule = new MaxLengthRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
	
}
