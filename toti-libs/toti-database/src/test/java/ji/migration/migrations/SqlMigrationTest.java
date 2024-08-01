package ji.migration.migrations;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.files.text.Text;
import ji.querybuilder.QueryBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class SqlMigrationTest {

	@Test
	@Parameters(method = "dataMigrateLoadFileAndMigrate")
	public void testMigrateLoadFileAndMigrate(
			String path,
			boolean isRevert,
			boolean isInclasspath,
			String selectText) throws Exception {
		
		Statement stat = mock(Statement.class);
		
		Connection con = mock(Connection.class);
		when(con.createStatement()).thenReturn(stat);
		
		QueryBuilder builder = mock(QueryBuilder.class);
		when(builder.getConnection()).thenReturn(con);
		
		SqlMigration m = new SqlMigration(path, isRevert, Text.get()); // TODO use mock
		m.migrate("Sql.sql", builder);
		
		verify(stat, times(1)).addBatch("select '" + selectText + "1'");
		verify(stat, times(1)).addBatch("select '" + selectText + "2'");
		verify(stat, times(1)).executeBatch();
		verify(stat, times(1)).close();
		verifyNoMoreInteractions(stat);
		
		verify(con, times(1)).createStatement();
		verifyNoMoreInteractions(con);
		
		verify(builder, times(1)).getConnection();
		verifyNoMoreInteractions(builder);
		
	}
	
	public Object[] dataMigrateLoadFileAndMigrate() {
		return new Object[] {
			new Object[] {"migration/per_type", false, true, "foward"},
			new Object[] {"migration/per_type", true, true, "revert"},
			new Object[] {"test/migration/per_type", false, false, "foward"},
			new Object[] {"test/migration/per_type", true, false, "revert"}
		};
	}
	
}
