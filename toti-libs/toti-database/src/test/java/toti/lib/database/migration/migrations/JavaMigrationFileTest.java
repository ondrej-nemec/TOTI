package toti.lib.database.migration.migrations;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import toti.lib.database.migration.Migration;
import toti.lib.database.querybuilder.QueryBuilder;
import toti.lib.common.structures.ThrowingBiFunction;

public class JavaMigrationFileTest {
	
	@Test
	public void testMigrateRevert() throws SQLException {
		Migration migration = mock(Migration.class);
		QueryBuilder builder = mock(QueryBuilder.class);
		
		JavaMigrationFile file = new JavaMigrationFile(null, null, null);
		file.migrate(builder, true, migration);
		
		verify(migration, times(1)).revert(builder);
		verifyNoMoreInteractions(migration, builder);
	}
	
	@Test
	public void testMigrateFoward() throws SQLException {
		Migration migration = mock(Migration.class);
		QueryBuilder builder = mock(QueryBuilder.class);
		
		JavaMigrationFile file = new JavaMigrationFile(null, null, null);
		file.migrate(builder, false, migration);
		
		verify(migration, times(1)).migrate(builder);
		verifyNoMoreInteractions(migration, builder);
	}

	@Test
	public void testGetMigrationInClasspath() throws Exception {
		Migration migration = mock(Migration.class);
		ClassLoader loader = mock(ClassLoader.class);
		
		@SuppressWarnings("unchecked")
		ThrowingBiFunction<String, ClassLoader, Object, Exception> load = mock(ThrowingBiFunction.class);
		when(load.apply(any(), any())).thenReturn(migration);

		JavaMigrationFile file = new JavaMigrationFile("toti.lib.database.migration.migrations", "Java__in_classpath", loader);
		assertEquals(migration, file.getMigration(load));
		
		verify(load, times(1)).apply("toti.lib.database.migration.migrations.Java__in_classpath", loader);
		verifyNoMoreInteractions(load, loader);
	}

	@Test
	public void testGetMigrationInDir() throws Exception {
		Migration migration = mock(Migration.class);
		ClassLoader loader = mock(ClassLoader.class);
		
		@SuppressWarnings("unchecked")
		ThrowingBiFunction<String, ClassLoader, Object, Exception> load = mock(ThrowingBiFunction.class);
		when(load.apply(any(), any()))
		.thenThrow(new ClassNotFoundException())
		.thenReturn(migration);

		JavaMigrationFile file = new JavaMigrationFile("test.migrationFiles", "Java_compiled__in_dir", loader);
		assertEquals(migration, file.getMigration(load));
		
		verify(load, times(1)).apply("test.migrationFiles.Java_compiled__in_dir", loader);
		verify(load, times(1)).apply("Java_compiled__in_dir", loader);
		verifyNoMoreInteractions(load, loader);
	}

	@Test
	public void testGetMigrationNotExisting() throws Exception {
		ClassLoader loader = mock(ClassLoader.class);
		
		@SuppressWarnings("unchecked")
		ThrowingBiFunction<String, ClassLoader, Object, Exception> load = mock(ThrowingBiFunction.class);
		when(load.apply(any(), any()))
		.thenThrow(new ClassNotFoundException())
		.thenThrow(new ClassNotFoundException());

		JavaMigrationFile file = new JavaMigrationFile("some.path", "Not_existing", loader);
		ClassNotFoundException e = assertThrows(ClassNotFoundException.class, ()->{
			file.getMigration(load);
		});
		assertNotNull(e);
		
		/*verify(load, times(1)).apply("some.path.Not_existing", loader);
		verify(load, times(1)).apply("Not_existing", loader);
		verifyNoMoreInteractions(load, loader);*/
	}
	
}
