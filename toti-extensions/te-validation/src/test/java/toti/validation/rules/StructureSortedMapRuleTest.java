package toti.validation.rules;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.common.structures.MapInit;
import toti.common.structures.SortedMap;
import toti.extensions.Translator;
import toti.tcpip.structures.RequestParameters;
import toti.validation.ValidationItem;
import toti.validation.ValidationResult;
import toti.validation.Validator;

public class StructureSortedMapRuleTest {

	@ParameterizedTest
	@MethodSource("dataCheck")
	public void testCheck(Object originValue, Object newValue,
			boolean canValidate, int errorCalling, int validatorCalling,
			String propertyName, String format, RequestParameters params) {
		ValidationResult result = mock(ValidationResult.class);
		Translator translator = mock(Translator.class);
		Identity identity = mock(Identity.class);
		
		ValidationItem item = new ValidationItem("name", originValue, result, translator, identity);
		
		ValidationResult subResult = mock(ValidationResult.class);
		Validator validator = mock(Validator.class);
		when(validator.validate(any(), any(), any(), any(Translator.class), any())).thenReturn(subResult);
		
		Request request = mock(Request.class);
		
		StructureSortedMapRule rule = new StructureSortedMapRule(validator, (t)->"error");
		rule.check(request, propertyName, "ruleName", item);
		
		assertEquals(newValue, item.getNewValue());
		assertEquals(canValidate, item.canValidationContinue());
		verify(validator, times(validatorCalling)).validate(request, format, params, translator, identity);
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
