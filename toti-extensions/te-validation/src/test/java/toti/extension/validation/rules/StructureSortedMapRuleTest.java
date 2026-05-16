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
import toti.lib.common.structures.SortedMap;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureSortedMapRuleTest {

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

		StructureSortedMapRule rule = new StructureSortedMapRule(validator, (t)->"error");
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
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").toMap()
				),
				new SortedMap<>().append("a", "b").append("x", "y"),
				true, 0, 1, "propertyName", "propertyName[%s]",
				new RequestParameters().put("a", "b").put("x", "y")
			},
			new Object[] {
				Arrays.asList(
					MapInit.create().toMap(),
					MapInit.create().append("x", "y").toMap()
				),
				Arrays.asList(
					MapInit.create().toMap(),
					MapInit.create().append("x", "y").toMap()
				),
				true, 1, 0, "propertyName", null,
				new RequestParameters().put("a", "b").put("x", "y")
			},
			new Object[] {
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").append("1", "2").toMap()
				),
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").append("1", "2").toMap()
				),
				true, 1, 0, "propertyName", null,
				new RequestParameters().put("a", "b").put("x", "y")
			},
			new Object[] {
				"", "", true, 1, 0, null, null, null
			}
		};
	}
	
}
