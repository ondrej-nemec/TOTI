package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ui.validation.ValidationItem;
import toti.ui.validation.ValidationResult;

@RunWith(JUnitParamsRunner.class)
public class ExpectedTypeRuleTest {

	@Test
	@Parameters(method="dataCheck")
	public void testCheck(Class<?> expectedType, Object originValue, Object newValue, boolean canValidate, int times) {
		ValidationResult result = mock(ValidationResult.class);
		Translator translator = mock(Translator.class);
		
		ValidationItem item = new ValidationItem(originValue, result, translator);
		
		ExpectedTypeRule rule = new ExpectedTypeRule(expectedType, (t)->"error");
		rule.check("propertyName", "ruleName", item);
		
		assertEquals(newValue, item.getNewValue());
		assertEquals(canValidate, item.canValidationContinue());
		verify(result, times(times)).addError("propertyName", "error");
	}
	
	public Object[] dataCheck() {
		return new Object[] {
			new Object[] {
				String.class, "someText", "someText", true, 0
			},
			new Object[] {
				Integer.class, "someText", "someText", false, 1
			},
			new Object[] {
				String.class, 42, "42", true, 0
			},
			new Object[] {
				Double.class, 42, 42.0, true, 0
			},
			new Object[] {
				Integer.class, "42", 42, true, 0
			}
		};
	}
	
}
