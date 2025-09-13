package ji.querybuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EscapeTest {

	@ParameterizedTest
	@MethodSource("dataEscape")
	public void testEscape(Object sql, String expected) {
		assertEquals(expected, Escape.escape(sql));
	}
	
	public static Object[] dataEscape() {
		return new Object[] {
			new Object[] { null, "null" },
			new Object[] { false, "false" },
			new Object[] { true, "true" },
			new Object[] { 1, "1" },
			new Object[] { 123.4, "123.4" },
			new Object[] { 'c', "'c'" },
			new Object[] { (byte)42, "42" },
			new Object[] { "", "''" },
			new Object[] { "some text", "'some text'" },
			new Object[] { Arrays.asList("a", "b", "c"), "'a','b','c'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18), "'2021-10-23 19:18'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18, 45, 150), "'2021-10-23 19:18:45.000'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18, 45, 150979200), "'2021-10-23 19:18:45.150'" },
			new Object[] { "not-escaped", "'not-escaped'" },
			new Object[] { "single'quote", "'single''quote'" }
		};
	}

	@ParameterizedTest
	@MethodSource("dataParseValueWorksWithDateTime")
	public void testParseValueWorksWithDateTime(Date datetime, String datetimeString, TemporalAccessor expected) throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getObject(anyInt())).thenReturn(datetime);
		when(rs.getString(anyInt())).thenReturn(datetimeString);
		Object actual = Escape.parseValue(rs, 0);
		try {
			assertEquals(expected, actual);
		} catch (Error e) {
			assertEquals(expected.toString(), actual.toString());
			throw e;
		}
	}
	
	public static Object[] dataParseValueWorksWithDateTime() {
		return new Object[] {
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12)), "", LocalTime.of(10, 12)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 45)), "", LocalTime.of(10, 12, 45)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 45, 123)), "", LocalTime.of(10, 12, 45)
			},
			new Object[] {
				java.sql.Date.valueOf(LocalDate.of(2021, 8, 20)), "", LocalDate.of(2021, 8, 20)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17", LocalDateTime.of(2021, 8, 12, 4, 17)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22", LocalDateTime.of(2021, 8, 12, 4, 17, 22)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12T04:17:22", LocalDateTime.of(2021, 8, 12, 4, 17, 22)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.1", LocalDateTime.of(2021, 8, 12, 4, 17, 22, 100000000)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364", LocalDateTime.of(2021, 8, 12, 4, 17, 22, 821364000)
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364+01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12T04:17:22.821364+01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364-01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("-1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364+01",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:22.821364 +01:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1"))
			}
		};
	}

}
