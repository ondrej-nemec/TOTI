package toti.lib.files.access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.files.text.Text;

public class FileInfoTest {

	@ParameterizedTest
	@MethodSource("testCreateInputStream")
	public void testCreateInputStream(String path, String expected) throws IOException {
		FileInfo file = FileList.get(path, false, SearchFilter.FILES_ONLY).get(0);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(file.createInputStream()))) {
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

	@Test
	public void testGetInputStream() throws IOException {
		List<FileInfo> files = FileList.get("tests/fileAccess/Same.java", false, SearchFilter.FILES_ONLY);
	//	files.addAll(FileList.get("tests/fileAccess/Same.class", false, SearchFilter.FILES_ONLY));
		assertEquals(3, files.size());
		for (int i = 0; i < files.size(); i++) {
			FileInfo info = files.get(i);
			assertEquals(switch (i) {
				case 0->"/tests/fileAccess/Same.java";
				case 1->"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/Same.java";
				case 2->"/home/coder/project/toti-libs/toti-files/tests/fileAccess/Same.java";
			//	case 3->"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/Same.class";
				default->"";
			}, info.getAbsolutePath(), "Test item: " + i);
			try (InputStream is = info.createInputStream()) {
				String actual = Text.get().read(br->br.asString(), is);
				assertEquals("""
package tests.fileAccess;

public class Same {
\t
}""", actual);
			}
		}
	}

}
