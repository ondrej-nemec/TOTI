package ji.migration.migrations;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import ji.querybuilder.QueryBuilder;

public class SqlMigrationFileTest {

	@ParameterizedTest
	@MethodSource("dataMigrate")
	public void testMigrate(String loadedText, List<String> batchs) throws SQLException {
		Statement stmt = mock(Statement.class);
		Connection conn = mock(Connection.class);
		when(conn.createStatement()).thenReturn(stmt);
		QueryBuilder builder = mock(QueryBuilder.class);
		when(builder.getConnection()).thenReturn(conn);
		
		SqlMigrationFile file = new SqlMigrationFile(null);
		file.migrate(builder, loadedText);
		
		verify(conn, times(1)).createStatement();
		verifyNoMoreInteractions(conn);
		
		verify(builder, times(1)).getConnection();
		verifyNoMoreInteractions(builder);
		
		verify(stmt, times(1)).executeBatch();
		verify(stmt, times(1)).close();
		for (String batch : batchs) {
			verify(stmt, times(1)).addBatch(batch);
		}
		verifyNoMoreInteractions(stmt);
	}
	
	public static Object[] dataMigrate() {
		return new Object[] {
			new Object[] {
				"",
				Arrays.asList()
			},
			new Object[] {
				"one select",
				Arrays.asList("one select")
			},
			new Object[] {
				"\nsome \ntext ",
				Arrays.asList("some \ntext")
			},
			new Object[] {
				"more;sqls",
				Arrays.asList("more", "sqls")
			},
			new Object[] {
				"some sql; another",
				Arrays.asList("some sql", "another")
			}
		};
	}

	@ParameterizedTest
	@MethodSource("dataLoadContent")
	public void testLoadContent(String path, boolean isRevert, String expected) throws IOException {
		SqlMigrationFile file = new SqlMigrationFile(path);
		assertEquals(expected, file.loadContent(isRevert));
	}
	
	public static Object[] dataLoadContent() {
		return new Object[] {
			new Object[] {
				"test/migrationFiles/sql__in_dir.sql",
				false,
				"select * from dir"
			},
			new Object[] {
				"ji/migration/migrations/sql__in_classpath.sql",
				false,
				"select * from classpath"
			},
			new Object[] {
				"ji/migration/migrations/sql__only_foward.sql",
				false,
				"select * from Foward"
			},
			new Object[] {
				"ji/migration/migrations/sql__only_revert.sql",
				true,
				"select * from Revert"
			},
			new Object[] {
				"ji/migration/migrations/sql__only_revert.sql",
				false,
				""
			},
			new Object[] {
				"ji/migration/migrations/sql__foward_and_revert.sql",
				false,
				"select * from Foward"
			},
			new Object[] {
				"ji/migration/migrations/sql__foward_and_revert.sql",
				true,
				"select * from Revert"
			}
		};
	}

	@Test
	// (expected = RuntimeException.class)
	public void testLoadContentThrowsIfMissingRequiredRevertPart() throws IOException {
		SqlMigrationFile file = new SqlMigrationFile("ji/migration/migrations/sql__only_foward.sql");
		RuntimeException e = assertThrows(RuntimeException.class, ()->{
			file.loadContent(true);
		});
		assertNotNull(e);
	}

}
