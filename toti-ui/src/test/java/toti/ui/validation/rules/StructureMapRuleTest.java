package toti.ui.validation.rules;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapInit;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.answers.request.Request;
import toti.ui.validation.ValidationItem;
import toti.ui.validation.ValidationResult;
import toti.ui.validation.Validator;

@RunWith(JUnitParamsRunner.class)
public class StructureMapRuleTest {
	
	@Test
	@Parameters(method="dataCheck")
	public void testCheck(Object originValue, Object newValue,
			boolean canValidate, int errorCalling, int validatorCalling,
			String propertyName, String format, RequestParameters params) {
		ValidationResult result = mock(ValidationResult.class);
		Translator translator = mock(Translator.class);
		
		ValidationItem item = new ValidationItem("name", originValue, result, translator);
		
		ValidationResult subResult = mock(ValidationResult.class);
		Validator validator = mock(Validator.class);
		when(validator.validate(any(), any(), any(Translator.class))).thenReturn(subResult);
		
		Request request = mock(Request.class);
		
		StructureMapRule rule = new StructureMapRule(validator, (t)->"error");
		rule.check(request, propertyName, "ruleName", item);
		
		assertEquals(newValue, item.getNewValue());
		assertEquals(canValidate, item.canValidationContinue());
		verify(validator, times(validatorCalling)).validate(request, format, params, translator);
		verify(result, times(errorCalling)).addError(propertyName, "error");
		verify(result, times(validatorCalling)).addSubResult(subResult);
	}
	
	public Object[] dataCheck() {
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
