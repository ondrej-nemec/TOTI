package ji.common.functions.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TimeRangeCollectionTest {

	@Test
	public void testGetNullFromEmpty() {
		TimeRangeCollection<LocalDate> range = new TimeRangeCollection<>(d->d);
		assertNull(range.get(LocalDate.of(2025, 4, 10)));
	}

	@Test
	@Parameters(method="dataGet")
	public void testGet(String message, LocalDate date, TimeRange<LocalDate> expected) {
		/*
		2025-03-15
		2025-04-01
		2025-04-10
		2025-04-15
		2025-04-20
		2025-04-25
		*/
		TimeRangeCollection<LocalDate> range = new TimeRangeCollection<>(Arrays.asList(
			LocalDate.of(2025, 4, 1),
			LocalDate.of(2025, 4, 15),
			LocalDate.of(2025, 3, 15),
			LocalDate.of(2025, 4, 10),
			LocalDate.of(2025, 4, 25),
			LocalDate.of(2025, 4, 10), // this one is duplicated
			LocalDate.of(2025, 4, 20)
		), d->d);
		assertEquals(message, expected, range.get(date));
	}

	public Object[] dataGet() {
		return new Object[] {
			new Object[] {
				"Start interval", LocalDate.of(2025, 4, 1), new TimeRange<>(
					ZonedDateTime.of(2025, 4, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
					ZonedDateTime.of(2025, 4, 9, 23, 59, 59, 999_999_999, ZoneId.systemDefault()),
					LocalDate.of(2025, 4, 1)
				)
			},
			new Object[] {
				"End interval", LocalDate.of(2025, 4, 9), new TimeRange<>(
					ZonedDateTime.of(2025, 4, 1, 0, 0, 0, 0, ZoneId.systemDefault()),
					ZonedDateTime.of(2025, 4, 9, 23, 59, 59, 999_999_999, ZoneId.systemDefault()),
					LocalDate.of(2025, 4, 1)
				)
			},
			new Object[] {
				"Inside interval", LocalDate.of(2025, 4, 12), new TimeRange<>(
					ZonedDateTime.of(2025, 4, 10, 0, 0, 0, 0, ZoneId.systemDefault()),
					ZonedDateTime.of(2025, 4, 14, 23, 59, 59, 999_999_999, ZoneId.systemDefault()),
					LocalDate.of(2025, 4, 10)
				)
			},
			new Object[] {
				"After last", LocalDate.of(2025, 4, 30), new TimeRange<>(
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
