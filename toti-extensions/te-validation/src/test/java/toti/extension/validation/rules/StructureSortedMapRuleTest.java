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
		Object originValue, Set<Object> expectedErrors, boolean isMoreValidationPossible,
		Object expectedValue, int expectedValidateCalling,
		RequestParameters fields, String name, String format
	) {
		RuleTest.testStructure(
			(validator, onError)->new StructureSortedMapRule(validator, onError), originValue,
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
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").toMap()
				), RuleTest.empty(), true,
				new SortedMap<>().append("a", "b").append("x", "y"), 1,
				new RequestParameters().put("a", "b").put("x", "y"),
				"propertyName", "propertyName[%s]"
			},
			new Object[] {
				Arrays.asList(
					MapInit.create().toMap(),
					MapInit.create().append("x", "y").toMap()
				), RuleTest.filled(), false,
				"parsed-value", 0,
				null, null, null
			},
			new Object[] {
				Arrays.asList(
					MapInit.create().append("a", "b").toMap(),
					MapInit.create().append("x", "y").append("1", "2").toMap()
				), RuleTest.filled(), false,
				"parsed-value", 0,
				null, null, null
			},
			new Object[] {
				"", RuleTest.filled(), false,
				"parsed-value", 0,
				null, null, null
			}
		};
	}
	
}
