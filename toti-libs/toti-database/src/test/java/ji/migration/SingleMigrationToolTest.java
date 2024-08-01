package ji.migration;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.sql.Connection;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.logging.log4j.Logger;
import ji.migration.migrations.JavaMigration;
import ji.migration.migrations.SqlMigration;
import ji.querybuilder.QueryBuilder;
import ji.querybuilder.builders.DeleteBuilder;
import ji.querybuilder.builders.InsertBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class SingleMigrationToolTest {
	
	private final static String MIGRATION_TABLE = "migrations";
	private final static String ALLWAYS_ID = "ALLWAYS";
	private static final String SEPARATOR = "__";

	@Test
	@Parameters(method = "dataTransactionWithWorkingMigration")
	public void testTransactionWithWorkingMigration(
			String name, String extension, int javaCount, int sqlCount, boolean isRevert) throws Exception {
		Connection con = mock(Connection.class);
		
		InsertBuilder insert = mock(InsertBuilder.class);
		when(insert.addValue(any(), any())).thenReturn(insert);
		
		DeleteBuilder delete = mock(DeleteBuilder.class);
		when(delete.where(any(String.class)))
			.thenReturn(delete)
			.thenReturn(delete);
		when(delete.addParameter(any(), any())).thenReturn(delete);
		
		QueryBuilder builder = mock(QueryBuilder.class);
		when(builder.getConnection()).thenReturn(con);
		when(builder.insert(MIGRATION_TABLE)).thenReturn(insert);
		when(builder.delete(MIGRATION_TABLE)).thenReturn(delete);
		
		SqlMigration sql = mock(SqlMigration.class);
		JavaMigration java = mock(JavaMigration.class);
		
		SingleMigrationTool tool = new SingleMigrationTool("module", MIGRATION_TABLE, ALLWAYS_ID, SEPARATOR, java, sql, mock(Logger.class));
		tool.transaction(name + "." + extension, builder, isRevert);
		if (!isRevert) {
    		verify(insert, times(4)).addValue(any(), any());
    		verify(insert, times(1)).execute();
		} else {
    		verify(delete, times(2)).where(any(String.class));
    		verify(delete, times(2)).addParameter(any(), any());
    		verify(delete, times(1)).execute();
		}
    	verifyNoMoreInteractions(insert);
    	verifyNoMoreInteractions(delete);
		
		verify(builder, times(1)).getConnection();
		verify(builder, times(isRevert ? 1 : 0)).delete(MIGRATION_TABLE);
		verify(builder, times(isRevert ? 0 : 1)).insert(MIGRATION_TABLE);
		verifyNoMoreInteractions(builder);
		
		verify(sql, times(sqlCount)).migrate(name + ".sql", builder);
		verifyNoMoreInteractions(sql);
		
		verify(java, times(javaCount)).migrate(name, builder);
		verifyNoMoreInteractions(java);
		
		verify(con, times(1)).setAutoCommit(false);
		verify(con, times(1)).commit();
		verifyNoMoreInteractions(con);
	}

	public Object[] dataTransactionWithWorkingMigration() {
		return new Object[] {
			new Object[] {"Id__desc", "sql", 0, 1, false},
			new Object[] {"Id__desc", "java", 1, 0, false},
			new Object[] {"Id__desc", "sql", 0, 1, true},
			new Object[] {"Id__desc", "java", 1, 0, true}
		};
	}
	
	@Test
	@Parameters(method = "dataTransactionWithThrowingMigration")
	public void testTransactionWithThrowingMigration(String file, int javaCount, int sqlCount, String name, boolean isRevert) throws Exception {
		Connection con = mock(Connection.class);
		
		QueryBuilder builder = mock(QueryBuilder.class);
		when(builder.getConnection()).thenReturn(con);
		
		SingleMigrationTool tool = new SingleMigrationTool("module", MIGRATION_TABLE, ALLWAYS_ID, SEPARATOR, null, null, mock(Logger.class)); // java, sql null for throwing exceptin
		try {
			tool.transaction(file, builder, isRevert);
			fail("NullPointerException required");
		} catch (NullPointerException e) {
			// expected - simulate throwing migration
		}
		
		verify(builder, times(1)).getConnection();
		verifyNoMoreInteractions(builder);
		
		verify(con, times(1)).setAutoCommit(false);
		verify(con, times(1)).rollback();
		verifyNoMoreInteractions(con);
	}

	public Object[] dataTransactionWithThrowingMigration() {
		return new Object[] {
			new Object[] {"Id__desc.sql", 0, 1, "Id", false},
			new Object[] {"Id__desc.java", 1, 0, "Id", false},
			new Object[] {"Id__desc.sql", 0, 1, "Id", true},
			new Object[] {"Id__desc.java", 1, 0, "Id", true}
		};
	}
	
	@Test
	public void testTransactionWithAllwaysMigration() throws Exception {
		Connection con = mock(Connection.class);
		
		QueryBuilder builder = mock(QueryBuilder.class);
		when(builder.getConnection()).thenReturn(con);
		
		SqlMigration sql = mock(SqlMigration.class);
		JavaMigration java = mock(JavaMigration.class);
		
		SingleMigrationTool tool = new SingleMigrationTool("module", MIGRATION_TABLE, ALLWAYS_ID, SEPARATOR, java, sql, mock(Logger.class));
		tool.transaction("ALLWAYS_1__desc.java", builder, false);
		tool.transaction("ALLWAYS_1__desc.sql", builder, false);
		
		verify(builder, times(2)).getConnection();
		verifyNoMoreInteractions(builder);
		
		verify(con, times(2)).setAutoCommit(false);
		verify(con, times(2)).commit();
		verifyNoMoreInteractions(con);
	}
}
