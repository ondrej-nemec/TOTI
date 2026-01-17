package toti.common.functions;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.common.functions.FilesList;

public class FilesListTest {

	// TODO test when path is pointed to file (4 cases)

	@Test
	public void test() throws Exception {
		List<String> expected = Arrays.asList(
			"a1.txt", "a2.txt", "a3.txt", "a4.txt",
			"b1.json", "b2.json", "b3.json", "b4.json",
			"sub/c1.xml", "sub/c2.xml", "sub/c3.xml", "sub/c4.xml",
			"sub/d1.class", "sub/d2.class", "sub/d3.java", "sub/d4.java"
		);
		List<String> actual = FilesList.get("tests/filesList", true).getFiles();
		try {
			assertEquals(expected, actual);
		} catch (Error e) {
			assertEquals(expected.toString(), actual.toString());
			throw e;
		}
	}
	
	@ParameterizedTest
	@MethodSource("dataPointedToFile")
	public void testPointedToFile(String path) throws Exception {
		List<String> expected = Arrays.asList();
		List<String> actual = FilesList.get(path, true).getFiles();
		try {
			assertEquals(expected, actual);
		} catch (Error e) {
			assertEquals(expected.toString(), actual.toString());
			throw e;
		}
	}
	
	public static Object[] dataPointedToFile() {
		return new Object[] {
			new Object[] {
				"tests/filesList/a1.txt"
			},
			/*new Object[] {
				"/tests/filesList/a1.txt"
			},*/
			
			new Object[] {
				"tests/filesList/a2.txt"
			},
			/*new Object[] {
				"/tests/filesList/a2.txt"
			},*/
			
			new Object[] {
				"tests/filesList/a3.txt"
			},
			/*new Object[] {
				"/tests/filesList/a3.txt"
			},*/
			
			new Object[] {
				"tests/filesList/a4.txt"
			},
			/*new Object[] {
				"/tests/filesList/a4.txt"
			}*/
		};
	}

}