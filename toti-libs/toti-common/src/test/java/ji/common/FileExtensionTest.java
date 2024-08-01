package ji.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.functions.FileExtension;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class FileExtensionTest {
	
	@Test
	@Parameters(method = "dataGetExtensionReturnsCorrectString")
	public void testGetExtensionReturnsCorrectString(String fileName, String extension) {
		FileExtension fe = new FileExtension(fileName);
		assertEquals(extension, fe.getExtension());
	}
	
	public Object[] dataGetExtensionReturnsCorrectString() {
		return new Object[] {
			new Object[] {"no_extension", ""},
			new Object[] {"one.extension", "extension"},
			new Object[] {"extension.more.that", "that"},
			new Object[] {".gitignore", "gitignore"}
		};
	}
	
	@Test
	@Parameters(method = "dataGetJustNameReturnsCorrectName")
	public void testGetJustNameReturnsCorrectName(String fileName, String name) {
		FileExtension fe = new FileExtension(fileName);
		assertEquals(name, fe.getName());
	}
	
	public Object[] dataGetJustNameReturnsCorrectName() {
		return new Object[] {
			new Object[] {"just_name", "just_name"},
			new Object[] {"one.extension", "one"},
			new Object[] {"extension.more.that", "extension.more"},
			new Object[] {".gitignore", ""}
		};
	}

}
