package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.answers.request.Request;
import toti.ui.validation.ValidationItem;
import toti.ui.validation.ValidationResult;

@RunWith(JUnitParamsRunner.class)
public class RequiredItemRuleTest {

	@Test
	@Parameters(method="dataCheck")
	public void testCheck(boolean required, Object originValue, Object newValue, int times, boolean canValidate) {
		ValidationResult result = mock(ValidationResult.class);
		Translator translator = mock(Translator.class);
		
		ValidationItem item = new ValidationItem(originValue, result, translator);
		item.setNewValue(newValue);
		
		Request request = mock(Request.class);
		
		RequiredItemRule rule = new RequiredItemRule(required, (t, p)->"error");
		rule.check(request, "propertyName", "ruleName", item);
		
		assertEquals(newValue, item.getNewValue());
		assertEquals(canValidate, item.canValidationContinue());
		verify(result, times(times)).addError("propertyName", "error");
	}
	
	public Object[] dataCheck() {
		return new Object[] {
			// not required, values null
			new Object[] { false, null, null, 0, false },
			// not required, origin null, new not
			new Object[] { false, null, "new value", 0, false },
			// not required, origin not null, new null
			new Object[] { false, "origin", null, 0, false },
			// not required, values not null
			new Object[] { false, "origin", "new value", 0, true },
			// required, values null
			new Object[] { true, null, null, 1, false },
			// required, origin null, new not
			new Object[] { true, null, "new value", 1, false },
			// required, origin not null, new null
			new Object[] { true, "origin", null, 1, false },
			// required, values not null
			new Object[] { true, "origin", "new value", 0, true },
		};
	}
}
