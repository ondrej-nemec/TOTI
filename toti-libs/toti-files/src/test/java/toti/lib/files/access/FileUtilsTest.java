package toti.lib.files.access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FileUtilsTest {
	
	@ParameterizedTest
	@MethodSource("dataGetExtensionReturnsCorrectString")
	public void testGetExtensionReturnsCorrectString(String fileName, String extension) {
		FileName fe = FileUtils.parseName(fileName);
		assertEquals(extension, fe.extension());
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
		FileName fe = FileUtils.parseName(fileName);
		assertEquals(name, fe.name());
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
