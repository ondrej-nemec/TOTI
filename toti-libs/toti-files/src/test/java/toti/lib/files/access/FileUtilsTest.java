package toti.lib.files.access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class FileUtilsTest {
	
	@ParameterizedTest
	@MethodSource("testParseName")
	public void testParseName(String path, String name, String extension) {
		FileName fe = FileUtils.parseName(path);
		assertEquals(extension, fe.extension());
	}
	
	public static Object[] testParseName() {
		return new Object[] {
			new Object[] {"no_extension", "no_extension", ""},
			new Object[] {"one.extension", "one", "extension"},
			new Object[] {"extension.more.that", "extension.more", "that"},
			new Object[] {".gitignore", "", "gitignore"},
			new Object[] {"just_name", "just_name", ""},
			new Object[] {"", "", ""}
		};
	}

	@ParameterizedTest
	@MethodSource("testCreateInputStream")
	public void testCreateInputStream(String path, String expected) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.createInputStream(path)))) {
			String content = br.readLine();
			assertEquals(expected, content);
		}
	}

	public static Object[] testCreateInputStream() {
		return new Object[] {
			new Object[] { "tests/fileAccess/a2.txt", "Content of a2" },
			new Object[] { "tests/fileAccess/a3.txt", "Content of a3" },
			new Object[] { "tests/fileAccess/a4.txt", "Content of a4" }
		};
	}

}
