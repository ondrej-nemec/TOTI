package ji.migration.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class MigrationFilesTest {

	@Test
	@Parameters(method = "dataLoadFilesFromDirTreeAndClasspath")
	public void testLoadFilesFromDirTreeAndClasspath(String folder, String dir) throws IOException {
		MigrationFiles f = new MigrationFiles(folder);
		assertEquals(Arrays.asList("First.class" , "Second.sql"), f.getFiles());
		assertNotEquals(-1, f.getDir().toString().indexOf(dir));
	}
	
	public Object[] dataLoadFilesFromDirTreeAndClasspath() {
		return new Object[] {
			new Object[] {
				"migration/files",
				"javainit/ji-database/bin/test/migration/files"
			},
			new Object[] {
				"test/migration/files",
				"test/migration/files"
			}
		};
	}

}
