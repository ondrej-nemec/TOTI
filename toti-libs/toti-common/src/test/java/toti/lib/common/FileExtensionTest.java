package toti.lib.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.functions.FileExtension;

public class FileExtensionTest {
	
	@ParameterizedTest
	@MethodSource("dataGetExtensionReturnsCorrectString")
	public void testGetExtensionReturnsCorrectString(String fileName, String extension) {
		FileExtension fe = new FileExtension(fileName);
		assertEquals(extension, fe.getExtension());
	}
	
	public static Object[] dataGetExtensionReturnsCorrectString() {
		return new Object[] {
			new Object[] {"no_extension", ""},
			new Object[] {"one.extension", "extension"},
			new Object[] {"extension.more.that", "that"},
			new Object[] {".gitignore", "gitignore"}
		};
	}
	
	@ParameterizedTest
	@MethodSource("dataGetJustNameReturnsCorrectName")
	public void testGetJustNameReturnsCorrectName(String fileName, String name) {
		FileExtension fe = new FileExtension(fileName);
		assertEquals(name, fe.getName());
	}
	
	public static Object[] dataGetJustNameReturnsCorrectName() {
		return new Object[] {
			new Object[] {"just_name", "just_name"},
			new Object[] {"one.extension", "one"},
			new Object[] {"extension.more.that", "extension.more"},
			new Object[] {".gitignore", ""}
		};
	}

}
