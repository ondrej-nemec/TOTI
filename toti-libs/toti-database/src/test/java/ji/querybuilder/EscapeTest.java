package ji.querybuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

import ji.common.structures.ThrowingSupplier;
import ji.database.Connections;

public class EscapeTest {
	
	private static final Connections CONNECTIONS = Connections.QUERY_BUILDER();

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
			new Object[] { LocalTime.of(14, 47), "'14:47'" },
			new Object[] { LocalTime.of(14, 47, 22), "'14:47:22'" },
			new Object[] { LocalTime.of(14, 47, 22, 150), "'14:47:22.000'" },
			new Object[] { LocalTime.of(14, 47, 22, 150979200), "'14:47:22.150'" },
			new Object[] { LocalDate.of(2025, 9, 22), "'2025-09-22'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18), "'2021-10-23 19:18'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18, 45, 150), "'2021-10-23 19:18:45.000'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18, 45, 150979200), "'2021-10-23 19:18:45.150'" },
			new Object[] {
				ZonedDateTime.of(2021, 10, 23, 19, 18, 45, 150979200, ZoneId.of("+1")),
				"'2021-10-23 19:18:45.150+01:00'"
			},
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

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2EMysql(String name, Object value, String expectedAsString, Object expectedParsed) throws SQLException {
		testE2E(()->CONNECTIONS.mysql(), name, value, expectedAsString, expectedParsed);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2EPostgres(String name, Object value, String expectedAsString, Object expectedParsed) throws SQLException {
		testE2E(()->CONNECTIONS.postgres(), name, value, expectedAsString, expectedParsed);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2ESqlite(String name, Object value, String expectedAsString, Object expectedParsed) throws SQLException {
		testE2E(()->CONNECTIONS.sqlite(), name, value, expectedAsString, expectedParsed);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2ESqlserver(String name, Object value, String expectedAsString, Object expectedParsed) throws SQLException {
		testE2E(()->CONNECTIONS.sqlserver(), name, value, expectedAsString, expectedParsed);
	}

	private void testE2E(
		ThrowingSupplier<Connection, SQLException> getConnection, String name, Object value,
		String expectedAsString, Object expectedParsed
	) throws SQLException {
		try (Connection con = getConnection.get()) {
			try {
				con.setAutoCommit(false);
				try (Statement stmt = con.createStatement()) {
					stmt.execute(
						"insert into escape_table (" + name + ") values (" + Escape.escape(value) + ")"
					);
				}
				try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("select " + name + " from escape_table");) {
					rs.next();
					
					String stringValue = rs.getString(1);
					//assertValue(expectedAsString, stringValue, "");
					Object rawValue = rs.getObject(1);
					//assertValue(expectedRaw, rawValue, "RAW");
					Object parsedValue = Escape.parseValue(rs, 1);
					assertValue(
						value, parsedValue,
						"String: '" + stringValue + "', Raw: "
						+ (rawValue == null ? "NULL" : "'" + rawValue + "'" + rawValue.getClass())
					);
				}
			} finally {
				con.rollback();
			}
		} 
	}

	private void assertValue(Object expected, Object actual, String message) {
		try {
			assertEquals(expected, actual, message);
		} catch (Error e) {
			assertEquals(
				expected == null ? "NULL" : String.format("%s[%s]", expected.getClass(), expected.toString()),
				actual == null ? "NULL" : String.format("%s[%s]", actual.getClass(), actual.toString()),
				message
			);
		}
	}

	public static Object[] dataE2E() {
		// TODO pridat sloupce bez nastaveni (6) a zapsat
		return new Object[] {
			new Object[] {
				"col_time", LocalTime.of(10, 12), "10:12:00", ""
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45), "10:12:45", ""
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 123), "", ""
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 100_000_000), "", ""
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 821_364_000), "", ""
			},
			new Object[] {
				"col_date", LocalDate.of(2021, 8, 20), "", ""
			},
			new Object[] {
				"col_datetime", LocalDateTime.of(2021, 8, 12, 4, 17),
				"", ""
			},
			new Object[] {
				"col_datetime", LocalDateTime.of(2021, 8, 12, 4, 17, 22),
				"", ""
			},
			new Object[] {
				"col_datetime", LocalDateTime.of(2021, 8, 12, 4, 17, 22, 0),
				"", ""
			},
			new Object[] {
				"col_datetime",
				LocalDateTime.of(2021, 8, 12, 4, 17, 22, 4527),
				"", ""
			},
			new Object[] {
				"col_datetime",
				LocalDateTime.of(2021, 8, 12, 4, 17, 22, 100000000),
				"", ""
			},
			new Object[] {
				"col_datetime",
				LocalDateTime.of(2021, 8, 12, 4, 17, 22, 821364000),
				"", ""
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("+1")),
				"", ""
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("-1")),
				"", ""
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneId.of("UTC")),
				"", ""
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 22, 821364000, ZoneOffset.UTC),
				"", ""
			}
		};
	}

}