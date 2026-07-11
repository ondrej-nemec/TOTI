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

import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.ListDictionary;
import toti.lib.common.structures.ThrowingSupplier;
import toti.lib.database.base.Connections;
import toti.lib.database.querybuilder.instances.MySqlQueryBuilder;
import toti.lib.database.querybuilder.instances.PostgreSqlQueryBuilder;
import toti.lib.database.querybuilder.instances.SqLiteQueryBuilder;
import toti.lib.database.querybuilder.instances.SqlServerQueryBuilder;

public class EscapeTest {
	
	private static final Connections CONNECTIONS = Connections.QUERY_BUILDER();

	@ParameterizedTest
	@MethodSource("dataEscape")
	public void testEscape(Object sql, String expected) {
		assertEquals(expected, new Escape().escape(sql));
	}
	
	public static Object[] dataEscape() {
		return new Object[] {
			new Object[] { null, "null" },
			new Object[] { true, "true" },
			new Object[] { false, "false" },
			new Object[] { 1, "1" },
			new Object[] { 123.4, "123.4" },
			new Object[] { 'c', "'c'" },
			new Object[] { (byte)42, "42" },
			new Object[] { "", "''" },
			new Object[] { "some text", "'some text'" },
			new Object[] { "not-escaped", "'not-escaped'" },
			new Object[] { "single'quote", "'single''quote'" },
			new Object[] { new DictionaryValue("some text"), "'some text'" },
			new Object[] { Arrays.asList("a", "1", 1, true, null), "'a','1',1,true,null" },
			new Object[] { new Object[] {"a", "1", 1, true, null}, "'a','1',1,true,null" },
			new Object[] { new ListDictionary(Arrays.asList("a", "1", 1, true, null)), "'a','1',1,true,null" },
			new Object[] { LocalTime.of(14, 47), "'14:47'" },
			new Object[] { LocalTime.of(14, 47, 22), "'14:47:22'" },
			new Object[] { LocalTime.of(14, 47, 22, 123), "'14:47:22.000000123'" },
			new Object[] { LocalTime.of(14, 47, 22, 123456), "'14:47:22.000123456'" },
			new Object[] { LocalTime.of(14, 47, 22, 123456789), "'14:47:22.123456789'" },
			new Object[] { LocalDate.of(2025, 9, 22), "'2025-09-22'" },
			new Object[] { LocalDateTime.of(2021, 10, 23, 19, 18), "'2021-10-23T19:18'" },
			new Object[] {
				LocalDateTime.of(2021, 10, 23, 19, 18, 45, 150),
				"'2021-10-23T19:18:45.000000150'"
			},
			new Object[] {
				LocalDateTime.of(2021, 10, 23, 19, 18, 45, 123456789),
				"'2021-10-23T19:18:45.123456789'"
			},
			new Object[] {
				ZonedDateTime.of(2021, 8, 23, 19, 18, 45, 123456789, ZoneId.of("+1")),
				"'2021-08-23T18:18:45.123456789Z'"
			},
			new Object[] {
				ZonedDateTime.of(2021, 8, 23, 19, 18, 45, 123456789, ZoneId.of("Europe/Prague")),
				"'2021-08-23T17:18:45.123456789Z'"
			},
			new Object[] {
				ZonedDateTime.of(2021, 8, 23, 19, 18, 45, 123456789, ZoneOffset.UTC),
				"'2021-08-23T19:18:45.123456789Z'"
			}
		};
	}
	
	@ParameterizedTest
	@MethodSource("dataParseValue")
	public void testParseValue(Object firstRead, String stringRead, Object expected) throws SQLException {
		ResultSet rs = mock(ResultSet.class);
		when(rs.getObject(anyInt())).thenReturn(firstRead);
		when(rs.getString(anyInt())).thenReturn(stringRead);
		Object actual = new Escape().parseValue(rs, 0);
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
				Time.valueOf(LocalTime.of(10, 12, 45, 123)),
				"10:12:45.123456789",
				LocalTime.of(10, 12, 45, 123456789)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 45)),
				"10:12:45.123456",
				LocalTime.of(10, 12, 45, 123456000)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 12, 0)),
				"10:12:45.123",
				LocalTime.of(10, 12, 45, 123000000)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 0, 0)),
				"10:12:45",
				LocalTime.of(10, 12, 45)
			},
			new Object[] {
				Time.valueOf(LocalTime.of(10, 0, 0)),
				"10:12",
				LocalTime.of(10, 12)
			},
			new Object[] {
				java.sql.Date.valueOf(LocalDate.of(2021, 8, 20)),
				"2021-08-20",
				LocalDate.of(2021, 8, 20)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45.123456789",
				LocalDateTime.of(2021, 8, 12, 4, 17, 45, 123456789)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45.123456",
				LocalDateTime.of(2021, 8, 12, 4, 17, 45, 123456000)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45.123",
				LocalDateTime.of(2021, 8, 12, 4, 17, 45, 123000000)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45",
				LocalDateTime.of(2021, 8, 12, 4, 17, 45)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17",
				LocalDateTime.of(2021, 8, 12, 4, 17)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45.123456789+00:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 45, 123456789, ZoneOffset.UTC)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45.123456+00:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 45, 123456000, ZoneOffset.UTC)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45.123+00:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 45, 123000000, ZoneOffset.UTC)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17:45+00:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 45, 0, ZoneOffset.UTC)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17+00:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 0, 0, ZoneOffset.UTC)
			},
			new Object[] {
				new Timestamp(0),
				"2021-08-12 04:17+02:00",
				ZonedDateTime.of(2021, 8, 12, 4, 17, 0, 0, ZoneId.of("+02"))
			}
		};
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2EMysql(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(new MySqlQueryBuilder(), ()->CONNECTIONS.mysql(), name, value, mysql);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2EPostgres(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(new PostgreSqlQueryBuilder(), ()->CONNECTIONS.postgres(), name, value, postgres);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2ESqlite(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(new SqLiteQueryBuilder(), ()->CONNECTIONS.sqlite(), name, value, sqlite);
	}

	@ParameterizedTest
	@MethodSource("dataE2E")
	public void testE2ESqlserver(
		String name, Object value,
		Object mysql, Object postgres, Object sqlServer, Object sqlite
	) throws SQLException {
		testE2E(new SqlServerQueryBuilder(), ()->CONNECTIONS.sqlserver(), name, value, sqlServer);
	}

	private void testE2E(
		DbInstance instance,
		ThrowingSupplier<Connection, SQLException> getConnection, String name, Object value, Object expectedValue
	) throws SQLException {
		try (Connection con = getConnection.get()) {
			try {
				Escape escape = instance.getEscape();
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
					Object parsedValue = escape.parseValue(rs, 1);

					assertValue(
						expectedValue, parsedValue,
						"String: '" + stringValue + "'\n Raw: "
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
			new Object[] {
				"col_bool", true,
				true, // mysql
				true, // postgres
				true, // sqlServer
				1 // sqlite
			},
			new Object[] {
				"col_int", 42,
				42, // mysql
				42, // postgres
				42, // sqlServer
				42 // sqlite
			},
			new Object[] {
				"col_float", 4.2,
				4.2f, // mysql
				4.2, // postgres
				4.2, // sqlServer
				4.2 // sqlite
			},
			new Object[] {
				"col_char", 'X',
				"X", // mysql
				"X  ", // postgres
				"X  ", // sqlServer
				"X" // sqlite
			},
			new Object[] {
				"col_string", "TOTI",
				"TOTI", // mysql
				"TOTI", // postgres
				"TOTI", // sqlServer
				"TOTI" // sqlite
			},
			new Object[] {
				"col_text", "TOTI",
				"TOTI", // mysql
				"TOTI", // postgres
				"TOTI", // sqlServer
				"TOTI" // sqlite
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12),
				LocalTime.of(10, 12), // mysql
				LocalTime.of(10, 12), // postgres
				LocalTime.of(10, 12), // sqlServer
				"10:12" // sqlite
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45),
				LocalTime.of(10, 12, 45), // mysql
				LocalTime.of(10, 12, 45), // postgres
				LocalTime.of(10, 12, 45), // sqlServer
				"10:12:45" // sqlite
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 123_456),
				LocalTime.of(10, 12, 45, 124_000), // mysql
				LocalTime.of(10, 12, 45, 123_000), // postgres
				LocalTime.of(10, 12, 45, 123_000), // sqlServer
				"10:12:45.000123456" // sqlite
			},
			new Object[] {
				"col_time", LocalTime.of(10, 12, 45, 123_456_789),
				LocalTime.of(10, 12, 45, 123_457_000), // mysql
				LocalTime.of(10, 12, 45, 123_457_000), // postgres
				LocalTime.of(10, 12, 45, 123_457_000), // sqlServer
				"10:12:45.123456789" // sqlite
			},
			new Object[] {
				"col_date", LocalDate.of(2021, 8, 20),
				LocalDate.of(2021, 8, 20), // mysql
				LocalDate.of(2021, 8, 20), // postgres
				LocalDate.of(2021, 8, 20), // sqlServer
				"2021-08-20" // sqlite
			},
			new Object[] {
				"col_datetime",
				LocalDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789),
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // mysql
				LocalDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000), // postgres
				LocalDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000), // sqlServer
				"2021-08-20T10:12:45.123456789" // sqlite
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("+5")),
				ZonedDateTime.of(2021, 8, 20, 5, 12, 45, 123_457_000, ZoneOffset.UTC), // mysql
				ZonedDateTime.of(2021, 8, 20, 5, 12, 45, 123_457_000, ZoneOffset.UTC), // postgres
				ZonedDateTime.of(2021, 8, 20, 5, 12, 45, 123_457_000, ZoneOffset.UTC), // sqlServer
				"2021-08-20T05:12:45.123456789Z" // sqlite 
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("+0")),
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // mysql
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // postgres
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // sqlServer
				"2021-08-20T10:12:45.123456789Z" // sqlite 
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("UTC")),
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // mysql
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // postgres
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // sqlServer
				"2021-08-20T10:12:45.123456789Z" // sqlite 
			},
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneOffset.UTC),
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // mysql
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // postgres
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_457_000, ZoneOffset.UTC), // sqlServer
				"2021-08-20T10:12:45.123456789Z" // sqlite 
			},
			// same timezone as connection
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("Europe/Prague")),
				ZonedDateTime.of(2021, 8, 20, 8, 12, 45, 123_457_000, ZoneOffset.UTC), // mysql
				ZonedDateTime.of(2021, 8, 20, 8, 12, 45, 123_457_000, ZoneOffset.UTC), // postgres
				ZonedDateTime.of(2021, 8, 20, 8, 12, 45, 123_457_000, ZoneOffset.UTC), // sqlServer
				"2021-08-20T08:12:45.123456789Z" // sqlite 
			},
			// timezone is different from connenction
			new Object[] {
				"col_datetime_zoned",
				ZonedDateTime.of(2021, 8, 20, 10, 12, 45, 123_456_789, ZoneId.of("Europe/Kyiv")),
				ZonedDateTime.of(2021, 8, 20, 7, 12, 45, 123_457_000, ZoneOffset.UTC), // mysql
				ZonedDateTime.of(2021, 8, 20, 7, 12, 45, 123_457_000, ZoneOffset.UTC), // postgres
				ZonedDateTime.of(2021, 8, 20, 7, 12, 45, 123_457_000, ZoneOffset.UTC), // sqlServer
				"2021-08-20T07:12:45.123456789Z" // sqlite 
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

	@ParameterizedTest
	@MethodSource
	// this test is for verifying behaviour of ZonedDateTime
	public void testZonedDateTimeCompare(String message, ZonedDateTime a, ZonedDateTime b, int expectedCompare, int expectedInstant) {
		//System.out.println(message);
		//System.out.println(a);
		//System.out.println(a.toInstant());
		//System.out.println(b);
		//System.out.println(b.toInstant());
		//System.out.println();
		assertEquals(expectedCompare, a.compareTo(b), message + " | Zoned");
		assertEquals(expectedInstant, a.toInstant().compareTo(b.toInstant()), message + " | Instant");
	}

	public static Object[] testZonedDateTimeCompare() {
		return new Object[] {
			new Object[] {
				"Both UTC | same hour",
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneOffset.UTC),
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneOffset.UTC),
				0, 0
			},
			new Object[] {
				"Both UTC | first is early",
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneOffset.UTC),
				ZonedDateTime.of(2026, 7, 8, 13, 0, 0, 0, ZoneOffset.UTC),
				-1, -1
			},
			new Object[] {
				"Both UTC | first is later",
				ZonedDateTime.of(2026, 7, 8, 13, 0, 0, 0, ZoneOffset.UTC),
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneOffset.UTC),
				1, 1
			},
			new Object[] {
				"Zone diff | same time",
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneId.of("UTC")),
				ZonedDateTime.of(2026, 7, 8, 13, 0, 0, 0, ZoneId.of("+01")),
				-1, 0
			},
			new Object[] {
				"Zone diff | same hour -> first is later",
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneId.of("UTC")),
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneId.of("+01")),
				1, 1
			},
			new Object[] {
				"Zone diff | first is early",
				ZonedDateTime.of(2026, 7, 8, 12, 0, 0, 0, ZoneId.of("UTC")),
				ZonedDateTime.of(2026, 7, 8, 14, 0, 0, 0, ZoneId.of("+01")),
				-1, -1
			}
		};
	}

}