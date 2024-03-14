package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.validation.ValidationItem;

@RunWith(JUnitParamsRunner.class)
public class RegexRuleTest {

	@Test
	@Parameters(method="dataIsErrorToShow")
	public void testIsErrorToShow(Object value, String regex, boolean expected) {
		RegexRule rule = new RegexRule(null, null);
		assertEquals(expected, rule.isErrorToShow(regex, value));
	}
	
	public Object[] dataIsErrorToShow() {
		return new Object[] {
			new Object[] { "aaa", "[a]?", false },
			new Object[] { "aaa", "[b]+", true },
		};
	}
	
	@Test
	public void testGetValue() {
		ValidationItem item = new ValidationItem("name", "origin", null, null, null);
		item.setNewValue("newValue");
		
		RegexRule rule = new RegexRule(null, null);
		assertEquals("origin", rule.getValue(item));
	}
}
