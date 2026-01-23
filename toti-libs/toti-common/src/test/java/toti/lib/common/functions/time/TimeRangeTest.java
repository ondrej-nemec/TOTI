package toti.lib.common.functions.time;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TimeRangeTest {

	@Test
	public void testGetNullFromEmpty() {
		TimeRange<LocalDate> range = new TimeRange<>();
		assertNull(range.get(LocalDate.of(2025, 4, 10)));
	}

	@ParameterizedTest
	@MethodSource("dataGet")
	public void testGet(String message, LocalDate date, Timestone<LocalDate> expected) {
		/*
		2025-03-15
		2025-04-01
		2025-04-10
		2025-04-15
		2025-04-20
		2025-04-25
		*/
		TimeRange<LocalDate> range = TimeRange.create(Arrays.asList(
			LocalDate.of(2025, 4, 1),
			LocalDate.of(2025, 4, 15),
			LocalDate.of(2025, 3, 15),
			LocalDate.of(2025, 4, 10),
			LocalDate.of(2025, 4, 25),
			LocalDate.of(2025, 4, 10), // this one is duplicated
			LocalDate.of(2025, 4, 20)
		), d->d);
		assertEquals(expected, range.get(date), message);
	}

	public static Object[] dataGet() {
		return new Object[] {
			new Object[] {
				"Start interval", LocalDate.of(2025, 4, 1), new Timestone<>(
					ZonedDateTime.of(2025, 4, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
					ZonedDateTime.of(2025, 4, 9, 23, 59, 59, 999_999_999, ZoneId.systemDefault()),
					LocalDate.of(2025, 4, 1)
				)
			},
			new Object[] {
				"End interval", LocalDate.of(2025, 4, 9), new Timestone<>(
					ZonedDateTime.of(2025, 4, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
					ZonedDateTime.of(2025, 4, 9, 23, 59, 59, 999_999_999, ZoneId.systemDefault()),
					LocalDate.of(2025, 4, 1)
				)
			},
			new Object[] {
				"Inside interval", LocalDate.of(2025, 4, 12), new Timestone<>(
					ZonedDateTime.of(2025, 4, 10, 0, 0, 0, 0, ZoneId.systemDefault()),
					ZonedDateTime.of(2025, 4, 14, 23, 59, 59, 999_999_999, ZoneId.systemDefault()),
					LocalDate.of(2025, 4, 10)
				)
			},
			new Object[] {
				"After last", LocalDate.of(2025, 4, 30), new Timestone<>(
					ZonedDateTime.of(2025, 4, 25, 0, 0, 0, 0, ZoneId.systemDefault()),
					null,
					LocalDate.of(2025, 4, 25)
				)
			},
			new Object[] {
				"Before first", LocalDate.of(2025, 3, 14), null
			}
		};
	}

}
