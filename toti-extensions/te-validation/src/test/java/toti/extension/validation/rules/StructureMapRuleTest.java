package toti.extension.validation.rules;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.validation.results.ValidationCollection;
import toti.lib.common.structures.ListInit;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureMapRuleTest {
	
	@ParameterizedTest
	@MethodSource
	public void testCheck(
		Object originValue, Set<Object> expectedErrors, boolean isMoreValidationPossible,
		Object expectedValue, int expectedValidateCalling,
		RequestParameters fields, String name, String format
	) {
		RuleTest.testStructure(
			(validator, onError)->new StructureMapRule(validator, onError),
			originValue,
			new ValidationCollection(
				MapInit.create().toMap(),
				new ListInit<>().toSet(),
				fields
			),
			expectedErrors, isMoreValidationPossible,
			expectedValue, expectedValidateCalling,
			fields, name, format
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] {
				MapInit.create().append("a", "b").append("x", "y").toMap(), RuleTest.empty(), true,
				new RequestParameters().put("a", "b").put("x", "y"), 1,
				new RequestParameters().put("a", "b").put("x", "y"),
				"propertyName", "propertyName[%s]"
			},
			new Object[] {
				Arrays.asList("aa"), RuleTest.empty(), true,
				new RequestParameters().put("0", "aa"), 1,
				new RequestParameters().put("0", "aa"),
				"propertyName", "propertyName[%s]"
			},
			new Object[] {
				"", RuleTest.filled(), false,
				"parsed-value", 0,
				null, null, null
			}
		};
	}
	
}
