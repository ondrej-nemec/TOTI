package toti.lib.common.functions.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.functions.time.TimeRange;

public class TimeRangeTest {

	@ParameterizedTest
	@MethodSource("dataIncludes")
	public void testIncludes(String message, Temporal temporal, boolean expected) {
		TimeRange<Integer> timeRange = new TimeRange<>(
			ZonedDateTime.of(2025, 5, 2, 19, 40, 0 , 0, ZoneId.systemDefault()),
			ZonedDateTime.of(2025, 5, 10, 10, 20, 0 , 0, ZoneId.systemDefault()),
			null
		);
		assertEquals(expected, timeRange.includes(temporal), message);
	}

	public static Object[] dataIncludes() {
		return new Object[] {
			new Object[] { "", LocalDate.of(2025, 5, 2), false },
			new Object[] { "", LocalDate.of(2025, 5, 3), true },
			new Object[] { "", LocalDate.of(2025, 5, 10), true },
			new Object[] { "", LocalDate.of(2025, 5, 11), false },
			new Object[] { "", ZonedDateTime.of(2025, 5, 2, 19, 39, 0 , 0, ZoneId.systemDefault()), false },
			new Object[] { "", ZonedDateTime.of(2025, 5, 2, 19, 40, 0 , 0, ZoneId.systemDefault()), true },
			new Object[] { "", ZonedDateTime.of(2025, 5, 10, 10, 20, 0 , 0, ZoneId.systemDefault()), true },
			new Object[] { "", ZonedDateTime.of(2025, 5, 10, 10, 20, 0 , 1, ZoneId.systemDefault()), false }
		};
	}

}
