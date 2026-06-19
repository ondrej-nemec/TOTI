package toti.extension.validation.rules;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.extension.validation.results.ValidationCollection;
import toti.lib.common.structures.ListInit;
import toti.lib.common.structures.MapInit;
import toti.lib.common.structures.SortedMap;
import toti.lib.tcpip.structures.RequestParameters;

public class StructureSortedMapRuleTest {

	@ParameterizedTest
	@MethodSource
	public void testCheck(
		Object originValue, RequestParameters parsedValueToSub, RequestParameters subResponse, Object expectedValue,
		Set<Object> expectedErrors, boolean isMoreValidationPossible,
		int expectedValidateCalling, String name, String format
	) {
		RuleTest.testStructure(
			(validator, onError)->new StructureSortedMapRule(validator, onError), originValue,
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
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").toMap()
				),
				new RequestParameters().put("a", "b").put("x", "y"),
				new RequestParameters().put("a", "b").put("x", "y"),
				new SortedMap<>().append("a", "b").append("x", "y"),
				RuleTest.empty(), true, 1, "propertyName", "propertyName[%s]"
			},
			new Object[] {
				Arrays.asList(
					MapInit.create().toMap(),
					MapInit.create().append("x", "y").toMap()
				),
				null,
				null,
				"parsed-value",
				RuleTest.filled(), false, 0, null, null
			},
			new Object[] {
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").append("1", "2").toMap()
				),
				null,
				null,
				"parsed-value",
				RuleTest.filled(), false, 0, null, null
			},
			new Object[] {
				"",
				null,
				null,
				"parsed-value",
				RuleTest.filled(), false, 0, null, null
			},
			new Object[] {
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").toMap()
				),
				new RequestParameters().put("a", "b").put("x", "y"),
				new RequestParameters().put("a", "B").put("c", "C"),
				new SortedMap<>().append("a", "B").append("c", "C"),
				RuleTest.empty(), true, 1, "propertyName", "propertyName[%s]"
			},
		};
	}
	
}
