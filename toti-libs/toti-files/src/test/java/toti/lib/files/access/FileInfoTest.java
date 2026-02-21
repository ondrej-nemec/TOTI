package toti.lib.files.access;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import toti.lib.files.text.Text;

public class FileInfoTest {

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
