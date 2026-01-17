package toti.extension.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.extension.validation.ValidationItem;
import toti.extension.validation.ValidationResult;
import toti.extension.validation.rules.RequiredItemRule;
import toti.extensions.Translator;

public class RequiredItemRuleTest {

	@ParameterizedTest
	@MethodSource("dataCheck")
	public void testCheck(boolean required, Object originValue, Object newValue, int times, boolean canValidate) {
		ValidationResult result = mock(ValidationResult.class);
		Translator translator = mock(Translator.class);
		Identity identity = mock(Identity.class);
		
		ValidationItem item = new ValidationItem("name", originValue, result, translator, identity);
		item.setNewValue(newValue);
		
		Request request = mock(Request.class);
		
		RequiredItemRule rule = new RequiredItemRule(required, (t, p)->"error");
		rule.check(request, "propertyName", "ruleName", item);
		
		assertEquals(newValue, item.getNewValue());
		assertEquals(canValidate, item.canValidationContinue());
		verify(result, times(times)).addError("propertyName", "error");
	}
	
	public static Object[] dataCheck() {
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
