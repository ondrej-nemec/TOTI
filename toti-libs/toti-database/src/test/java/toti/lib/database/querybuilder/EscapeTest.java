package toti.lib.database.querybuilder;

import java.sql.Connection;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import toti.lib.common.structures.ThrowingSupplier;
import toti.lib.database.base.Connections;

public class EscapeTest {
	
	private static final Connections CONNECTIONS = Connections.QUERY_BUILDER();

	@ParameterizedTest
	@MethodSource("dataEscape")
	public void testEscape(Object sql, String expected) {
		Escape escape = new Escape();
		assertEquals(expected, escape.escape(sql));
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
			new Object[] { LocalTime.of(14, 47, 22, 123), "'14:47:22.000000123'" },
			new Object[] { LocalTime.of(14, 47, 22, 123456), "'14:47:22.000123456'" },
			new Object[] { LocalTime.of(14, 47, 22, 123456789), "'14:47:22.123456789'" },
			new Object[] { LocalDate.of(2025, 9, 22), "'2025-09-22'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18), "'2021-10-23 19:18'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18, 45, 150), "'2021-10-23 19:18:45.000000150'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18, 45, 123456789), "'2021-10-23 19:18:45.123456789'" },
			new Object[] {
				ZonedDateTime.of(2021, 10, 23, 19, 18, 45, 123456789, ZoneId.of("+1")),
				"'2021-10-23 19:18:45.123456789+01:00'"
			},
			new Object[] { "not-escaped", "'not-escaped'" },
			new Object[] { "single'quote", "'single''quote'" }
		};
	}

	/*@Test
	public void testEscapePrimitives() {
		// parametrized is always Object
		Escape escape = new Escape();
		assertEquals("null", escape.escape(null));
		assertEquals("false", escape.escape(false));
		assertEquals("true", escape.escape(true));
		assertEquals("42", escape.escape((byte)42));
		assertEquals("42", escape.escape((short)42));
		assertEquals("42", escape.escape(42));
		assertEquals("42", escape.escape(42L));
		assertEquals("123.4", escape.escape(123.4));
		assertEquals("'c'", escape.escape('c'));
		assertEquals("''", escape.escape(""));
		assertEquals("'some text'", escape.escape("some text"));
	}*/
	
	@ParameterizedTest
	@MethodSource("dataParseValue")
	public void testParseValue(Object firstRead, String stringRead, Object expected) throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getObject(anyInt())).thenReturn(firstRead);
		when(rs.getString(anyInt())).thenReturn(stringRead);
		Object actual = Escape.parseValue(rs, 0);
		assertValue(expected, actual, "");
	}
	
	public static Object[] dataParseValue() {
		return new Object[] {
			new Object[] { null, "null", null },
			new Object[] { false, "false", false },
			new Object[] { true, "true", true },
			new Object[] { 1, "1", 1 },
			new Object[] { 123.4, "123.4", 123.4 },
			new Object[] { 'c', "'c'", 'c' },
			new Object[] { (byte)42, "42", (byte)42 },
			new Object[] { "", "''", "" },
			new Object[] { "some text", "'some text'", "some text" },
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 45, 123)), "10:12:45.123456789", "10:12:45.123456789"
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 45)), "10:12:45.123456", "10:12:45.123456000"
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 0)), "10:12:45.123", "10:12:45.123000000"
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 0, 0)), "10:12:45", "10:12:45"
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 0, 0)), "10:12", "10:12"
			},
			new Object[] {
				java.sql.Date.valueOf(LocalDate.of(2021, 8, 20)), "2021-08-20", "2021-08-20"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123456789", "2021-08-12T04:17:45.123456789"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123456", "2021-08-12T04:17:45.123456000"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123", "2021-08-12T04:17:45.123000000"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45", "2021-08-12T04:17:45"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17", "2021-08-12T04:17"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123456789+05:00", "2021-08-12T04:17:45.123456789+05:00"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123456+05:00", "2021-08-12T04:17:45.123456000+05:00"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123+05:00", "2021-08-12T04:17:45.123000000+05:00"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45+05:00", "2021-08-12T04:17:45+05:00"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17+05:00", "2021-08-12T04:17+05:00"
			},
			/*new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123456789Z", "2021-08-12T04:17:45.123456789+00:00"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123456789[UTC]", "2021-08-12T04:17:45.123456789+00:00"
			},
			new Object[] {
				new Timestamp(0), "2021-08-12 04:17:45.123456789Z[UTC]", "2021-08-12T04:17:45.123456789+00:00"
			}*/
		};
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2EMysql(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(()->CONNECTIONS.mysql(), name, value, mysql);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2EPostgres(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(()->CONNECTIONS.postgres(), name, value, postgres);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2ESqlite(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(()->CONNECTIONS.sqlite(), name, value, sqlite);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2ESqlserver(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(()->CONNECTIONS.sqlserver(), name, value, sqlServer);
	}

	private void testE2E(
		ThrowingSupplier<Connection, SQLException> getConnection, String name, Object value, Object expectedValue
	) throws SQLException {
		try (Connection con = getConnection.get()) {
			try {
				Escape escape = new Escape();
				con.setAutoCommit(false);
				try (Statement stmt = con.createStatement()) {
					stmt.execute(
						"insert into escape_table (" + name + ") values (" + escape.escape(value) + ")"
					);
				} catch (Exception e) {
					System.out.println(name + " '" + value + "' " + escape.escape(value));
					throw e;
				}
				try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery("select " + name + " from escape_table");) {
					rs.next();
					
					String stringValue = rs.getString(1);
					//assertValue(expectedAsString, stringValue, "");
					Object rawValue = rs.getObject(1);
					//assertValue(expectedRaw, rawValue, "RAW");
					Object parsedValue = Escape.parseValue(rs, 1);

					assertValue(
						expectedValue, parsedValue,
						"String: '" + stringValue + "', Raw: "
						+ (rawValue == null ? "NULL" : "'" + rawValue + "'" + rawValue.getClass())
						+ "\n"
					);
				}
			} finally {
				con.rollback();
			}
		} 
	}

	public static Object[] dataE2E() {
		return new Object[] {
			/*new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 123),
				"10:12:45.000000000", // mysql
				"10:12:45.000000000", // postgres
				"10:12:45.000000000", // sqlServer
				"10:12:45.000000123" // sqlite
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 123_456),
				"10:12:45.000124000", // mysql
				"10:12:45.000123000", // postgres
				"10:12:45.000123000", // sqlServer
				"10:12:45.000123456" // sqlite
			},*/
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 123_456_789),
				"10:12:45.123457000", // mysql
				"10:12:45.123457000", // postgres
				"10:12:45.123457000", // sqlServer
				"10:12:45.123456789" // sqlite
			},
			new Object[] {
				"col_date", LocalDate.of(2021, 8, 20),
				"2021-08-20", // mysql
				"2021-08-20", // postgres
				"2021-08-20", // sqlServer
				"2021-08-20" // sqlite
			},
			new Object[] {
				"col_datetime",
				LocalDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789),
				"2021-08-20T10:12:45.123457000", // mysql
				"2021-08-20T10:12:45.123457000", // postgres
				"2021-08-20T10:12:45.123457000", // sqlServer
				"2021-08-20 10:12:45.123456789" // sqlite
			},
			/*new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123, ZoneId.of("+5")),
				"2021-08-20T05:12:45.000000000", // mysql
				"2021-08-20T07:12:45.000000000+02", // postgres
				"2021-08-20T10:12:45.000000000+05:00", // sqlServer
				"2021-08-20 10:12:45.000000123+05:00" // sqlite 
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456, ZoneId.of("+5")),
				"2021-08-20T05:12:45.000123000", // mysql
				"2021-08-20T07:12:45.000123000+02", // postgres
				"2021-08-20T10:12:45.000123000+05:00", // sqlServer
				"2021-08-20 10:12:45.000123456+05:00" // sqlite 
			},*/
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("+5")),
				"2021-08-20T05:12:45.123457000", // mysql
				"2021-08-20T05:12:45.123457000+00", // postgres
				"2021-08-20T10:12:45.123457000+05:00", // sqlServer
				"2021-08-20 10:12:45.123456789+05:00" // sqlite 
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("+0")),
				"2021-08-20T10:12:45.123457000", // mysql
				"2021-08-20T10:12:45.123457000+00", // postgres
				"2021-08-20T10:12:45.123457000+00:00", // sqlServer
				"2021-08-20 10:12:45.123456789+00:00" // sqlite 
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("UTC")),
				"2021-08-20T10:12:45.123457000", // mysql
				"2021-08-20T10:12:45.123457000+00", // postgres
				"2021-08-20T10:12:45.123457000+00:00", // sqlServer
				"2021-08-20 10:12:45.123456789+00:00" // sqlite 
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneOffset.UTC),
				"2021-08-20T10:12:45.123457000", // mysql
				"2021-08-20T10:12:45.123457000+00", // postgres
				"2021-08-20T10:12:45.123457000+00:00", // sqlServer
				"2021-08-20 10:12:45.123456789+00:00" // sqlite 
			}
		};
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

}