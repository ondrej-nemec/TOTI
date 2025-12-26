package toti.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.extensions.Translator;
import toti.validation.ValidationItem;
import toti.validation.ValidationResult;

public class ExpectedTypeRuleTest {

	@ParameterizedTest
	@MethodSource("dataCheck")
	public void testCheck(Class<?> expectedType, Object originValue, Object newValue, boolean canValidate, int times) {
		ValidationResult result = mock(ValidationResult.class);
		Translator translator = mock(Translator.class);
		Identity identity = mock(Identity.class);
		
		ValidationItem item = new ValidationItem("name", originValue, result, translator, identity);
		
		ExpectedTypeRule rule = new ExpectedTypeRule(expectedType, (t)->"error");
		rule.check(mock(Request.class), "propertyName", "ruleName", item);
		
		assertEquals(newValue, item.getNewValue());
		assertEquals(canValidate, item.canValidationContinue());
		verify(result, times(times)).addError("propertyName", "error");
	}
	
	public static Object[] dataCheck() {
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
