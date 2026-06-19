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
		Object originValue, RequestParameters parsedValueToSub, RequestParameters subResponse, Object expectedValue,
		Set<Object> expectedErrors, boolean isMoreValidationPossible,
		int expectedValidateCalling, String name, String format
	) {
		RuleTest.testStructure(
			(validator, onError)->new StructureMapRule(validator, onError),
			originValue,
			new ValidationCollection(
				MapInit.create().toMap(),
				new ListInit<>().toSet(),
				subResponse
			),
			expectedErrors, isMoreValidationPossible,
			expectedValue, expectedValidateCalling,
			parsedValueToSub, name, format
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] {
				MapInit.create().append("a", "b").append("x", "y").toMap(),
				new RequestParameters().put("a", "b").put("x", "y"),
				new RequestParameters().put("a", "b").put("x", "y"),
				new RequestParameters().put("a", "b").put("x", "y"),
				RuleTest.empty(), true, 1, "propertyName", "propertyName[%s]"
			},
			new Object[] {
				Arrays.asList("aa"),
				new RequestParameters().put("0", "aa"),
				new RequestParameters().put("0", "aa"),
				new RequestParameters().put("0", "aa"),
				RuleTest.empty(), true, 1, "propertyName", "propertyName[%s]"
			},
			new Object[] {
				"", null, null, "parsed-value",
				RuleTest.filled(), false, 0, null, null
			},
			new Object[] {
				MapInit.create().append("a", "A").append("b", "B").toMap(),
				new RequestParameters().put("a", "A").put("b", "B"),
				// simulate: remove b | add c | d is required but missing
				new RequestParameters().put("a", "a").put("c", "C").put("d", null),
				new RequestParameters().put("a", "a").put("c", "C").put("d", null),
				RuleTest.empty(), true, 1, "propertyName", "propertyName[%s]"
			},
		};
	}
	
}
