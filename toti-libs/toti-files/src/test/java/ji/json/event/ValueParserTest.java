package ji.json.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ValueParserTest {

	@ParameterizedTest
	@MethodSource("dataGetTypeWorks")
	public void testGetTypeWorks(ValueType expected, String value, boolean isValueQuoted) {
		assertEquals(expected, ValueParser.parse(value, isValueQuoted).getType());
	}
	
	public static Object[] dataGetTypeWorks() {
		return new Object[] {
			new Object[] {ValueType.STRING, "string", true},
			new Object[] {ValueType.STRING, "123", true},
			new Object[] {ValueType.STRING, "1.23", true},
			new Object[] {ValueType.DOUBLE, "1.23", false},
			new Object[] {ValueType.INTEGER, "123", false},
			new Object[] {ValueType.BOOLEAN, "false", false},
			new Object[] {ValueType.BOOLEAN, "true", false},
			new Object[] {ValueType.NULL, "null", false},
			new Object[] {ValueType.NULL, "", false},
		};
	}
	
}
