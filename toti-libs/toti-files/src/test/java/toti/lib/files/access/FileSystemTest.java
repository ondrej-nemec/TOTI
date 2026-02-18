package toti.lib.files.access;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.tests.TestCase;

public class FileSystemTest {

	@ParameterizedTest
	@MethodSource("testGet")
	public void testGet(String message, String folder, boolean recursive, SearchFilter filter, List<FileInfo> expected) throws Exception {
		List<FileInfo> actual = FileSystem.get(folder, recursive, filter);
		TestCase.assertEquals(expected, actual);
	}

	public static Object[] testGet() {
		return new Object[] {
			new Object[] {
				"Full recursive",
				"tests/fileAccess", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					// aJava, cJava bJava not in build
					root(),
					sameJava(), sameJar(), sameRes(), sameExt(),
					aRes(), aExt(), aJar(),
					bRes(), bExt(), bJar(),
					subJar(), subJava(), subRes(), subExt(),
					cRes(), cExt(), cJar(), 
					dJava(), dRes(), dExt(), dJar()
				)
			},
			/*new Object[] {
				"Full Flat",
				"tests/fileAccess", false, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					root(),
					sameJava(), sameJar(), sameRes(), sameExt(),
					aJar(), aRes(), aExt() // aJava not in build
					bJar(), bRes(), bExt(), // bJava not in build
					subJar(), subJava(), aExt(), aJar()
				)
			},
			new Object[] {
				"Files only recursive",
				"tests/fileAccess", true, SearchFilter.FILES_ONLY,
				Arrays.asList(
					sameJava(), sameJar(), sameRes(), sameExt(),
					aJar(), aRes(), aExt() // aJava not in build
					bJar(), bRes(), bExt(), // bJava not in build
					cJar(), cRes(), cExt(), // cJava not in build
					dJava(), dJar(), dRes(), dExt()
				)
			},
			new Object[] {
				"Directories only recursive",
				"tests/fileAccess", true, SearchFilter.DIRECTORY_ONLY,
				Arrays.asList(
					root(),
					subJar(), subJava(), aExt(), aJar()
				)
			},
			////////////////
			new Object[] {
				"Not existing folder",
				"tests/not-existing", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},*/
			////////////////
			new Object[] {
				"Pointing to file 1",
				"tests/fileAccess/a1.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			new Object[] {
				"Pointing to file 2",
				"tests/fileAccess/a2.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(aRes("a2.txt", ""))// search for file give another relative path
			},
			new Object[] {
				"Pointing to file 3",
				"tests/fileAccess/a3.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(aExt("a3.txt", ""))// search for file give another relative path
			},
			new Object[] {
				"Pointing to file 4",
				"tests/fileAccess/a4.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(aJar("a4.txt", ""))// search for file give another relative path
			}/*,
			new Object[] {
				"Pointing to not existing file",
				"tests/fileAccess/not-existing.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			}*/
			// TODO windows style path, folder with / at end, namireno na sub, .. a . v path, Same.java, Same.class
		};
	}

	private static FileInfo root() {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.JAR, "", "",
			"jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!/tests/fileAccess",
			1771361076000L
		);
	}

	private static FileInfo aJava() {
		return null; //"a1.txt"
	}


	private static FileInfo aRes() {
		return aRes("a2.txt", "a2.txt");
	}

	private static FileInfo aRes(String name, String relativePath) {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, name, relativePath,
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/a2.txt",
			1771361530572L
		);
	}

	private static FileInfo aExt() {
		return aExt("a3.txt", "a3.txt");
	}

	private static FileInfo aExt(String name, String relativePath) {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, name, relativePath,
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/a3.txt",
			1761300113000L
		);
	}

	private static FileInfo aJar() {
		return aJar("a4.txt", "a4.txt");
	}

	private static FileInfo aJar(String name, String relativePath) {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, name, relativePath,
			"jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!/tests/fileAccess/a4.txt",
			1761300112000L
		);
	}

	private static FileInfo bJava() {
		return null; //"b1.json"
	}

	private static FileInfo bRes() {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, "b2.json", "b2.json",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/b2.json",
			1771361530564L
		);
	}

	private static FileInfo bExt() {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, "b3.json", "b3.json",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/b3.json",
			1761300122000L
		);
	}

	private static FileInfo bJar() {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, "b4.json", "b4.json",
			"jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!/tests/fileAccess/b4.json",
			1761300122000L
		);
	}

	private static FileInfo sameJava() {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, "Same.class", "Same.class",
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/Same.class",
			1771361530064L
		);
	}

	private static FileInfo sameRes() {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, "Same.java", "Same.java",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/Same.java",
			1771361530572L
		);
	}

	private static FileInfo sameExt() {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, "Same.java", "Same.java",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/Same.java",
			1771361017503L
		);
	}

	private static FileInfo sameJar() {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, "Same.java", "Same.java",
			"jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!/tests/fileAccess/Same.java",
			1771361016000L
		);
	}

	private static FileInfo subJava() {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.RESOURCE, "sub", "sub",
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/sub",
			1771361530060L
		);
	}

	private static FileInfo subRes() {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.RESOURCE, "sub", "sub",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub",
			1771361530572L
		);
	}

	private static FileInfo subExt() {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.EXTERNAL, "sub", "sub",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub",
			1771275800893L
		);
	}

	private static FileInfo subJar() {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.JAR, "sub", "sub",
			"jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!/tests/fileAccess/sub",
			1771275800000L
		);
	}

	private static FileInfo cJava() {
		return null; //"sub/c1.xml",
	}

	private static FileInfo cRes() {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, "c2.xml", "sub/c2.xml",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub/c2.xml",
			1771361530572L
		);
	}

	private static FileInfo cExt() {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, "c3.xml", "sub/c3.xml",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub/c3.xml",
			1761300150000L
		);
	}

	private static FileInfo cJar() {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, "c4.xml", "sub/c4.xml",
			"jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!/tests/fileAccess/sub/c4.xml",
			1761300150000L
		);
	}

	private static FileInfo dJava() {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, "d1.class", "sub/d1.class",
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/sub/d1.class",
			1771361530060L
		);
	}

	private static FileInfo dRes() {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, "d2.java", "sub/d2.java",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub/d2.java",
			1771361530572L
		);
	}

	private static FileInfo dExt() {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, "d3.java", "sub/d3.java",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub/d3.java",
			1771361883533L
		);
	}

	private static FileInfo dJar() {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, "d4.java", "sub/d4.java",
			"jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!/tests/fileAccess/sub/d4.java",
			1771362306000L
		);
	}

}
