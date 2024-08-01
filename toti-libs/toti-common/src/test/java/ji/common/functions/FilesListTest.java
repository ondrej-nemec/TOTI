package ji.common.functions;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilesListTest {

	@Test
	public void testGetFolderContentFromClasspathDir() throws Exception {
		List<String> folders = Arrays.asList(
			"app.properties",
			"Empty.class",
			"subDir/env.properties"
		);
		assertEquals(folders, FilesList.get("functions/filesList", true).getFiles());
	}

	@Test
	public void testGetFolderContentFromDir() throws Exception {
		List<String> folders = Arrays.asList(
			"app.properties",
			"Empty.java",
			"subDir/env.properties"
		);
		assertEquals(folders, FilesList.get("tests/functions/filesList", true).getFiles());
	}
	
	// TODO add test for jar
	
}
