package toti.extension.validation.rules;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import toti.application.extensions.Translator;
import toti.extension.validation.ValidationItem;
import toti.extension.validation.ValidationResult;
import toti.extension.validation.Validator;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureMapRuleTest {
	
	@ParameterizedTest
	@MethodSource("dataCheck")
	public void testCheck(Object originValue, Object newValue,
			boolean canValidate, int errorCalling, int validatorCalling,
			String propertyName, String format, RequestParameters params) {
		ValidationResult result = mock(ValidationResult.class);
		Translator translator = mock(Translator.class);
		
		ValidationItem item = new ValidationItem("name", originValue, result, translator);
		
		ValidationResult subResult = mock(ValidationResult.class);
		Validator validator = mock(Validator.class);
		when(validator.validate(any(), any(), any(Translator.class))).thenReturn(subResult);
		
		StructureMapRule rule = new StructureMapRule(validator, (t)->"error");
		rule.check(propertyName, "ruleName", item);
		
		assertEquals(newValue, item.getNewValue());
		assertEquals(canValidate, item.canValidationContinue());
		verify(validator, times(validatorCalling)).validate(format, params, translator);
		verify(result, times(errorCalling)).addError(propertyName, "error");
		verify(result, times(validatorCalling)).addSubResult(subResult);
	}
	
	public static Object[] dataCheck() {
		return new Object[] {
			new Object[] {
				MapInit.create().append("a", "b").append("x", "y").toMap(),
				new RequestParameters().put("a", "b").put("x", "y"),
				true, 0, 1, "propertyName", "propertyName[%s]",
				new RequestParameters().put("a", "b").put("x", "y")
			},
			new Object[] {
				Arrays.asList("aa"),
				Arrays.asList("aa"),
				true, 1, 0, "propertyName", null, null
			},
			new Object[] {
				"", "",
				true, 1, 0, "propertyName", null, null
			}
		};
	}
	
}
