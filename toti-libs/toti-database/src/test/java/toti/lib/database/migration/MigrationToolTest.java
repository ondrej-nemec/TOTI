package toti.lib.database.migration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;

import toti.lib.common.functions.Implode;
import toti.lib.common.structures.SortedMap;
import toti.lib.database.migration.migrations.JavaMigrationFile;
import toti.lib.database.migration.migrations.MigrationFile;
import toti.lib.database.migration.migrations.MigrationInternal;
import toti.lib.database.migration.migrations.SqlMigrationFile;
import toti.lib.database.querybuilder.QueryBuilder;
import toti.lib.files.access.FileInfo;

public class MigrationToolTest {
	
	class TestMigrationFile implements MigrationFile {

		@Override
		public void migrate(QueryBuilder builder, boolean isRevert) throws Exception {}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof TestMigrationFile;
		}

        @Override public int hashCode() { return super.hashCode(); }
		
		@Override
		public String toString() {
			return "TestMigrationFile";
		}
	}
	
	@ParameterizedTest
	@MethodSource("dataGetFowardMigrationsValidState")
	public void testGetFowardMigrationsValidState(
		String message,
		SortedMap<String, MigrationInternal> filesMigrations,
		List<String> savedMirations,
		List<MigrationInternal> expected
	) throws MigrationException {
		MigrationTool tool = new MigrationTool(null, null, null);
		List<MigrationInternal> actual = tool.getFowardMigrations(filesMigrations, savedMirations);
		try {
			assertEquals(expected, actual, message);
		} catch(Error e) {
			assertEquals(Implode.implode("\n", expected), Implode.implode("\n", actual), message);
			throw e;
		}
	}
	
	public static Object[] dataGetFowardMigrationsValidState() {
		return new Object[] {
			new Object[] {
				"Empty data",
				new SortedMap<>(),
				Arrays.asList(),
				Arrays.asList()
			},
			new Object[] {
				"All migrated",
				new SortedMap<>()
				.append("M1", new MigrationInternal("M1", "", ""))
				.append("M2", new MigrationInternal("M2", "", ""))
				.append("M3", new MigrationInternal("M3", "", "")),
				Arrays.asList("M1", "M2", "M3"),
				Arrays.asList()
			},
			new Object[] {
				"Some need migrate",
				new SortedMap<>()
				.append("M1", new MigrationInternal("M1", "", ""))
				.append("M2", new MigrationInternal("M2", "", ""))
				.append("M3", new MigrationInternal("M3", "", ""))
				.append("M4", new MigrationInternal("M4", "", "")),
				Arrays.asList("M1", "M2"),
				Arrays.asList(
					new MigrationInternal("M3", "", ""),
					new MigrationInternal("M4", "", "")
				)
			},
			new Object[] {
				"All migrated with always before",
				new SortedMap<>()
				.append("ALWAYS_1", new MigrationInternal("ALWAYS_1", "", ""))
				.append("M1", new MigrationInternal("M1", "", ""))
				.append("M2", new MigrationInternal("M2", "", ""))
				.append("M3", new MigrationInternal("M3", "", "")),
				Arrays.asList("M1", "M2", "M3"),
				Arrays.asList(new MigrationInternal("ALWAYS_1", "", ""))
			},
			new Object[] {
				"All migrated with always after",
				new SortedMap<>()
				.append("M1", new MigrationInternal("M1", "", ""))
				.append("M2", new MigrationInternal("M2", "", ""))
				.append("M3", new MigrationInternal("M3", "", ""))
				.append("ALWAYS_1", new MigrationInternal("ALWAYS_1", "", "")),
				Arrays.asList("M1", "M2", "M3"),
				Arrays.asList(new MigrationInternal("ALWAYS_1", "", ""))
			},
			new Object[] {
				"Some need migrate with always",
				new SortedMap<>()
				.append("M1", new MigrationInternal("M1", "", ""))
				.append("M2", new MigrationInternal("M2", "", ""))
				.append("ALWAYS_1", new MigrationInternal("ALWAYS_1", "", ""))
				.append("M3", new MigrationInternal("M3", "", ""))
				.append("M4", new MigrationInternal("M4", "", "")),
				Arrays.asList("M1", "M2"),
				Arrays.asList(
					new MigrationInternal("ALWAYS_1", "", ""),
					new MigrationInternal("M3", "", ""),
					new MigrationInternal("M4", "", "")
				)
			},
		};
	}

	@ParameterizedTest
	@MethodSource("dataGetFowardMigrationsInvalidState")
	public void testGetFowardMigrationsInvalidState(
		String message,
		SortedMap<String, MigrationInternal> filesMigrations,
		List<String> savedMirations
	) throws MigrationException {
		MigrationException e = assertThrows(MigrationException.class, ()->{
			MigrationTool tool = new MigrationTool(null, null, null);
			tool.getFowardMigrations(filesMigrations, savedMirations);
		});
		assertNotNull(e);
	}
	
	public static Object[] dataGetFowardMigrationsInvalidState() {
		return new Object[] {
			new Object[] {
				"Empty files, database not empty",
				new SortedMap<>(),
				Arrays.asList("M1")
			},
			new Object[] {
				"Missing migration",
				new SortedMap<>()
				.append("M1", new MigrationInternal("", "", ""))
				.append("M2", new MigrationInternal("", "", "")),
				Arrays.asList("M1", "M2", "M3")
			},
			new Object[] {
				"Order not match",
				new SortedMap<>()
				.append("M2", new MigrationInternal("", "", ""))
				.append("M1", new MigrationInternal("", "", "")),
				Arrays.asList("M1", "M2")
			}
		};
	}
	
	@Test
	public void testProcess() throws MigrationException {
		new File("test/migrations/Java_not_compiled__in_dir.class").delete();
		
		ClassLoader loader = mock(ClassLoader.class);
		MigrationTool tool = new MigrationTool(null, null, null);
		
		SortedMap<String, MigrationInternal> expected = new SortedMap<String, MigrationInternal>()
		.append(
			"Java_compiled",
			new MigrationInternal("Java_compiled", "in_dir", "test/migrations")
			.setFile(new JavaMigrationFile("test.migrations", "Java_compiled__in_dir", loader))
		)
		.append(
			"Java_not_compiled",
			new MigrationInternal("Java_not_compiled", "in_dir", "test/migrations")
			.setFile(new JavaMigrationFile("test.migrations", "Java_not_compiled__in_dir", loader))
		)
		.append(
			"sql",
			new MigrationInternal("sql", "in_dir", "test/migrations")
			.setFile(new SqlMigrationFile(new FileInfo(
				null, null, "sql__in_dir.sql", "test/migrations/sql__in_dir.sql", null, 0
			)))
		)
		;
		SortedMap<String, MigrationInternal> actual = tool.processFiles("test/migrations", Arrays.asList(
			new FileInfo(
				null, null,
				"Java_compiled__in_dir.class",
				"test/migrations/Java_compiled__in_dir.class",
				null, 0
			),
			new FileInfo(
				null, null,
				"Java_compiled__in_dir.java",
				"test/migrations/Java_compiled__in_dir.java",
				null, 0
			),
			new FileInfo(
				null, null,
				"Java_not_compiled__in_dir.java",
				"test/migrations/Java_not_compiled__in_dir.java",
				null, 0
			),
			new FileInfo(
				null, null,
				"not__a-migration.txt",
				"test/migrations/not__a-migration.txt",
				null, 0
			),
			new FileInfo(
				null, null,
				"sql__in_dir.sql",
				"test/migrations/sql__in_dir.sql",
				null, 0
			)
		), loader);
		
		try {
			assertEquals(expected, actual);
		} catch(Error e) {
			assertEquals(
				Implode.implode("\n", ": ", expected.toMap()),
				Implode.implode("\n", ": ", actual.toMap())
			);
			throw e;
		}
		assertTrue(new File("test/migrations/Java_not_compiled__in_dir.class").exists());
		assertTrue(new File("test/migrations/Java_compiled__in_dir.class").exists());
		new File("test/migrations/Java_not_compiled__in_dir.class").delete();
	}
	
	@Test
	public void testProcessFailsIfCompilationFails() throws MigrationException {
		ClassLoader loader = mock(ClassLoader.class);
		MigrationTool tool = new MigrationTool(null, null, null);
		
		MigrationException e = assertThrows(MigrationException.class, ()->{
			tool.processFiles("test/migrations/subdir", Arrays.asList(new FileInfo(
				null, null, "Java__compilation_error.java",
				"test/migrations/subdir/Java__compilation_error.java", null, 0
		)), loader);
		});
		assertNotNull(e);
	}
	
	
	
	@Test
	public void testCreateMigrationReturnCorrectResult() throws MigrationException {
		MigrationTool tool = new MigrationTool(null, null, null);
		
		MigrationInternal expected = new MigrationInternal("some_id", "comment_or_description", "module-name");
		MigrationInternal actual = tool.createMigration(
			new FileInfo(null, null, "some_id__comment_or_description", null, null, 0),
			"module-name"
		);
		try {
			assertEquals(expected, actual);
		} catch(Error e) {
			assertEquals(expected.toString(), actual.toString());
			throw e;
		}
	}
	
	@Test
	public void testCreateMigrationThrowsWhenNoSeparator() throws MigrationException {
		MigrationException e = assertThrows(MigrationException.class, ()->{
			MigrationTool tool = new MigrationTool(null, null, null);
			tool.createMigration(
				new FileInfo(null, null, "some_id", null, null, 0),
				"module-name"
			);
		});
		assertNotNull(e);
	}
	
	@Test
	public void testCreateMigrationThrowsWhenMoreSeparators() throws MigrationException {
		MigrationException e = assertThrows(MigrationException.class, ()->{
			MigrationTool tool = new MigrationTool(null, null, null);
			tool.createMigration(
				new FileInfo(null, null, "some_id__comment__description", null, null, 0),
				"module-name"
			);
		});
		assertNotNull(e);
	}
	
	@ParameterizedTest
	@MethodSource("dataParseJavaPath")
	public void testParseJavaPath(String path, String expected) {
		MigrationTool tool = new MigrationTool(null, null, null);
		assertEquals(expected, tool.parseJavaPath(path));
	}
	
	public static Object[] dataParseJavaPath() {
		return new Object[] {
			new Object[] { "", "" },
			new Object[] { "a-b-c", "a-b-c" },
			new Object[] { "a\\b\\c", "a.b.c" },
			new Object[] { "a/b/c", "a.b.c" },
			new Object[] { "a\\b/c", "a.b.c" },
			new Object[] { "a.b.c", "a.b.c" }
		};
	}
}
