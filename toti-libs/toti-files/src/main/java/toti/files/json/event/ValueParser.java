package toti.files.json.event;

public class ValueParser {
	
	public static Value<?> parse(String value, boolean isValueQuoted) {
		if (!isValueQuoted && value.isEmpty()) {
			// valid state for start and end events
			// invalid state "key": ,
			return new Value<>(null, ValueType.NULL);
		}
		if (isValueQuoted) {
			return new Value<>(
				value.toString()
					.replace("\\\\\\", "\\\\") // \\\ to \\
					.replace("\\n", "\n") // \\n to \n
					.replace("\\\"", "\"")
				,
				ValueType.STRING
			);
		}
		if ("null".equals(value.toLowerCase())) {
			return new Value<>(null, ValueType.NULL);
		}
		if ("false".equals(value.toLowerCase()) || "true".equals(value.toLowerCase())) {
			return new Value<>("true".equals(value.toLowerCase()), ValueType.BOOLEAN);
		}
		if (value.contains(".")) {
			return new Value<>(Double.parseDouble(value), ValueType.DOUBLE);
		}
		try {
			return new Value<>(Integer.parseInt(value), ValueType.INTEGER);
		} catch (NumberFormatException e) {
			return new Value<>(Long.parseLong(value), ValueType.INTEGER);
		}
	}
}
