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
		Object originValue, RequestParameters parsedValueToSub, RequestParameters subResponse, Object expectedValue,
		
		Set<Object> expectedErrors, boolean isMoreValidationPossible,
		int expectedValidateCalling, String name, String format
	) {
		RuleTest.testStructure(
			(validator, onError)->new StructureListRule(validator, onError), originValue,
			new ValidationCollection(
				MapInit.create().toMap(),
				new ListInit<>().toSet(),
				subResponse
			),
			expectedErrors, isMoreValidationPossible, expectedValue, expectedValidateCalling,
			parsedValueToSub, name, format
		);
	}
	
	public static Object[] testCheck() {
		return new Object[] {
			new Object[] {
				Arrays.asList("a", "b"),
				new RequestParameters().put("0", "a").put("1", "b"),
				new RequestParameters().put("0", "a").put("1", "b"),
				Arrays.asList("a", "b"), 
				RuleTest.empty(), true, 1, "%s:propertyName", "%s:propertyName[]"
			},
			new Object[] {
				Arrays.asList("a", "b"),
				new RequestParameters().put("0", "a").put("1", "b"),
				new RequestParameters().put("0", "a").put("1", "b"),
				Arrays.asList("a", "b"),
				RuleTest.empty(), true, 1, "propertyName", "%spropertyName[]"
			},
			new Object[] {
				MapInit.create().append("x", "a").append("y", "b").toMap(),
				new RequestParameters().put("0", "a").put("1", "b"),
				new RequestParameters().put("0", "a").put("1", "b"),
				Arrays.asList("a", "b"),
				RuleTest.empty(), true, 1, "%s:propertyName", "%s:propertyName[]"
			},
			new Object[] {
				"xxx", null, null, "parsed-value",
				RuleTest.filled(), false, 0, "propertyName", "propertyName"
			},
			new Object[] {
				Arrays.asList("a", "b"),
				new RequestParameters().put("0", "a").put("1", "b"),
				// simulate: remove 1 | add 2 | 3 is required but missing
				new RequestParameters().put("2", "c").put("3", null).put("0", "A"),
				Arrays.asList("A", null, "c", null), 
				RuleTest.empty(), true, 1, "%s:propertyName", "%s:propertyName[]"
			},
		};
	}
}
