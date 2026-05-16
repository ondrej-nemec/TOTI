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

public class StructureListRuleTest {

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
		
		StructureListRule rule = new StructureListRule(validator, (t)->"error");
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
				Arrays.asList("a", "b"), Arrays.asList("a", "b"),
				true, 0, 1, "propertyName", "%s:propertyName[]",
				new RequestParameters().put("0", "a").put("1", "b")
			},
			new Object[] {
				Arrays.asList("a", "b"), Arrays.asList("a", "b"),
				true, 0, 1, "%s:propertyName", "%s:propertyName[]",
				new RequestParameters().put("0", "a").put("1", "b")
			},
			new Object[] {
				MapInit.create().append("x", "a").append("y", "b").toMap(), Arrays.asList("a", "b"),
				true, 0, 1, "propertyName", "%s:propertyName[]",
				new RequestParameters().put("0", "a").put("1", "b")
			},
			new Object[] {
				"xxx", "xxx",
				true, 1, 0, "propertyName", null, null
			},
		};
	}
}
