package toti.extension.validation.rules;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.validation.results.ValidationCollection;
import toti.lib.common.structures.ListInit;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureListRuleTest {

	@ParameterizedTest
	@MethodSource
	public void testCheck(
		Object originValue, Set<Object> expectedErrors, boolean isMoreValidationPossible,
		Object expectedValue, int expectedValidateCalling,
		RequestParameters fields, String name, String format
	) {
		RuleTest.testStructure(
			(validator, onError)->new StructureListRule(validator, onError), originValue,
			new ValidationCollection(
				MapInit.create().toMap(),
				new ListInit<>().toSet(),
				fields
			),
			expectedErrors, isMoreValidationPossible, expectedValue, expectedValidateCalling,
			fields, name, format
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] {
				Arrays.asList("a", "b"), RuleTest.empty(), true,
				Arrays.asList("a", "b"), 1,
				new RequestParameters().put("0", "a").put("1", "b"),
				"%s:propertyName", "%s:propertyName[]"
			},
			new Object[] {
				Arrays.asList("a", "b"), RuleTest.empty(), true,
				Arrays.asList("a", "b"), 1,
				new RequestParameters().put("0", "a").put("1", "b"),
				"propertyName", "%spropertyName[]"
			},
			new Object[] {
				MapInit.create().append("x", "a").append("y", "b").toMap(), RuleTest.empty(), true,
				Arrays.asList("a", "b"), 1,
				new RequestParameters().put("0", "a").put("1", "b"),
				"%s:propertyName", "%s:propertyName[]"
			},
			new Object[] {
				"xxx", RuleTest.filled(), false,
				"parsed-value", 0,
				null, "propertyName", "propertyName"
			},
		};
	}
}
