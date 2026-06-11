package toti.extension.validation.rules;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import toti.extension.validation.Validator;
import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationCollection;
import toti.extension.validation.results.ValidationItem;
import toti.lib.tcpip.structures.RequestParameters;

public interface RuleTest {

	static void test(
		Function<Supplier<String>, Rule> rule, Object originValue, Object parsedValue,
		Set<Object> expectedErrors, boolean isMoreValidationPossible, Object expectedNewValue
	) {
		ValidationItem item = new ValidationItem("Name", "Extended", originValue, parsedValue);

		CheckResult actual = rule.apply(()->"Expected Error").check(item);

		assertEquals(new CheckResult(isMoreValidationPossible), actual);
		assertEquals(expectedErrors, item.getErrors());
		assertEquals(expectedNewValue, item.getParsedValue());
	}

	static void testStructure(
		BiFunction<Validator, Supplier<String>, Rule> rule, Object originValue,
		ValidationCollection subResult,
		Set<Object> expectedErrors, boolean isMoreValidationPossible,
		Object expectedNewValue, int expectedValidateCalling,
		RequestParameters fields, String extendedName, String format
	) {
		Validator validator = mock(Validator.class);
		when(validator._validate(any(), any(), any(), any())).thenReturn(subResult);

		ValidationItem item = new ValidationItem("Name", extendedName, originValue, "parsed-value");

		CheckResult actual = rule.apply(validator, ()->"Expected Error").check(item);

		assertEquals(new CheckResult(isMoreValidationPossible), actual);
		assertEquals(expectedErrors, item.getErrors());
		assertEquals(expectedNewValue, item.getParsedValue());

		verify(validator, times(expectedValidateCalling))._validate(format, fields, "Name", extendedName);
		verifyNoMoreInteractions(validator);
	}

	static Set<Object> empty() {
		return new HashSet<>();
	}

	static Set<Object> filled() {
		return set("Expected Error");
	}

	static Set<Object> set(String error) {
		Set<Object> errors = new HashSet<>();
		errors.add(error);
		return errors;
	}

}
