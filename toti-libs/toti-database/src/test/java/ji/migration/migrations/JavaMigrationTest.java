package ji.migration.migrations;

import static org.mockito.Mockito.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.querybuilder.QueryBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class JavaMigrationTest {

	@Test
	@Parameters(method = "dataMigrateFindCorectFile")
	public void testMigrateFindCorectFile(
			String path,
			ClassLoader loader, 
			boolean isInClasspath,
			boolean isRevert,
			String selectText
		) throws Exception {
		QueryBuilder builder = mock(QueryBuilder.class);
		
		JavaMigration m = new JavaMigration(path, loader, isRevert/*, isInClasspath*/);
		m.migrate("Java", builder);
		
		verify(builder, times(1)).select(selectText);
		verifyNoMoreInteractions(builder);
	}
	
	public Object[] dataMigrateFindCorectFile() throws MalformedURLException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null, new File("test/migration/per_type/Java.java").getPath());
		
		File dir = new File("test/migration/per_type");
		URLClassLoader loader = new URLClassLoader( new URL[]{dir.toURI().toURL()});
		
		return new Object[] {
			new Object[] {"migration/per_type", getClass().getClassLoader(), true, false, "foward"}, // in classpath foward
			new Object[] {"migration/per_type", getClass().getClassLoader(), true, true, "revert"}, // in classpath revert
			new Object[] {"test/migration/per_type", loader, false, false, "foward"}, // external foward
			new Object[] {"test/migration/per_type", loader, false, true, "revert"}, // external revert
		};
	}
	
	@Test(expected = ClassNotFoundException.class)
	public void testMigrateThrowsClassNotFoundOnNotExistingMigration() throws Exception {
		QueryBuilder builder = mock(QueryBuilder.class);
		
		JavaMigration m = new JavaMigration("path", getClass().getClassLoader(), false);
		m.migrate("not-existing-class", builder);
	}
	
}
