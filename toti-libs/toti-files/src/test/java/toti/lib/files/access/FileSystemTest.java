package toti.lib.files.access;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
					// aJava, cJava, bJava not in build
					//root(),
					sameJava(), sameJar(), sameRes(), sameExt(),
					aRes(), aExt(), aJar(),
					bRes(), bExt(), bJar(),
					subJar(), subJava(), subRes(), subExt(),
					cRes(), cExt(), cJar(), 
					dJava(), dRes(), dExt(), dJar()
				)
			},
			new Object[] {
				"Full Flat",
				"tests/fileAccess", false, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					// aJava, cJava, bJava not in build
					//root(),
					sameJava(), sameJar(), sameRes(), sameExt(),
					aRes(), aExt(), aJar(),
					bRes(), bExt(), bJar(),
					subJar(), subJava(), subRes(), subExt()
				)
			},
			new Object[] {
				"Files only recursive",
				"tests/fileAccess", true, SearchFilter.FILES_ONLY,
				Arrays.asList(
					// aJava, cJava, bJava not in build
					sameJava(), sameJar(), sameRes(), sameExt(),
					aRes(), aExt(), aJar(),
					bRes(), bExt(), bJar(),
					cRes(), cExt(), cJar(), 
					dJava(), dRes(), dExt(), dJar()
				)
			},
			new Object[] {
				"Directories only recursive",
				"tests/fileAccess", true, SearchFilter.DIRECTORY_ONLY,
				Arrays.asList(
					// aJava, cJava, bJava not in build
					//root(),
					subJar(), subJava(), subRes(), subExt()
				)
			},
			////////////////
			new Object[] {
				"Not existing folder",
				"tests/not-existing", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			////////////////
			new Object[] {
				"Pointing to file 1",
				"tests/fileAccess/a1.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			new Object[] {
				"Pointing to file 2",
				"tests/fileAccess/a2.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(aRes2())
			},
			new Object[] {
				"Pointing to file 3",
				"tests/fileAccess/a3.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(aExt2())
			},
			new Object[] {
				"Pointing to file 4",
				"tests/fileAccess/a4.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(aJar2())
			},
			new Object[] {
				"Pointing to not existing file",
				"tests/fileAccess/not-existing.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			////////////////////
			new Object[] {
				"Pointing to folder",
				"tests/fileAccess/sub", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					//subJar2(),
					cRes2(), cExt2(), cJar2(), 
					dJava2(), dRes2(), dExt2(), dJar2()
				)
			},
			////////////////
			new Object[] {
				"Folder ends with /",
				"tests/fileAccess/sub/", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					//subJar2(),
					cRes2(), cExt2(), cJar2(), 
					dJava2(), dRes2(), dExt2(), dJar2()
				)
			},
			////////////////////////
			new Object[] {
				"Pointing to upper folder",
				"tests/fileAccess/sub/../a4.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			////////////////
			new Object[] {
				"Folder in windows style",
				"tests\\fileAccess", true, SearchFilter.FILES_ONLY,
				Arrays.asList(
					// aJava, cJava, bJava not in build
					sameJava(), sameJar(), sameRes(), sameExt(),
					aRes(), aExt(), aJar(),
					bRes(), bExt(), bJar(),
					cRes(), cExt(), cJar(), 
					dJava(), dRes(), dExt(), dJar()
				)
			},
			new Object[] {
				"Pointing to file in windows style",
				"tests\\fileAccess\\a4.txt", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(aJar2())
			},
			new Object[] {
				"Folder ends with \\",
				"tests\\fileAccess\\sub\\", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					//subJar2(),
					cRes2(), cExt2(), cJar2(), 
					dJava2(), dRes2(), dExt2(), dJar2()
				)
			},
			/////////////////////////////////
			new Object[] {
				"Pointing to same 1",
				"tests/fileAccess/Same.java", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					sameJar2(), sameRes2(), sameExt2()
				)
			},
			new Object[] {
				"Pointing to same 2",
				"tests/fileAccess/Same.class", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					sameJava2()
				)
			}
		};
	}

	@ParameterizedTest
	@MethodSource("testGetThrowsWithWrongInput")
	public void testGetThrowsWithWrongInput(String message, String folder, boolean recursive, SearchFilter filter) throws Exception {
		RuntimeException expected = assertThrows(RuntimeException.class, ()->FileSystem.get(folder, recursive, filter));
		assertNotNull(expected);
	}

	public static Object[] testGetThrowsWithWrongInput () {
		return new Object[] {
			new Object[] {
				"Null search",
				null, true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			new Object[] {
				"Empty search",
				"", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			new Object[] {
				"Search is only /",
				"/", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			new Object[] {
				"Search is only \\",
				"\\", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList()
			},
			new Object[] {
				"Folder starts with /",
				"/tests/fileAccess/sub", true, SearchFilter.FILES_AND_DIRECTORY,
				Arrays.asList(
					subJar2(),
					cRes2(), cExt2(), cJar2(), 
					dJava2(), dRes2(), dExt2(), dJar2()
				)
			},
			new Object[] {
				"Folder starts with \\",
				"\\tests\\fileAccess\\sub", true, SearchFilter.FILES_AND_DIRECTORY
			}
		};
	}
/*
	private static FileInfo root() {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.JAR, "", "",
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess",
			1771361076000L
		);
	}
*/
/*
	private static FileInfo aJava() {
		return null; //"a1.txt"
	}
*/
	private static FileInfo aRes() {
		return aRes("a2.txt", "a2.txt");
	}


	private static FileInfo aRes2() {
		return aRes("a2.txt", "");
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

	private static FileInfo aExt2() {
		return aExt("a3.txt", "");
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

	private static FileInfo aJar2() {
		return aJar("a4.txt", "");
	}

	private static FileInfo aJar(String name, String relativePath) {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, name, relativePath,
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/a4.txt",
			1761300112000L
		);
	}
/*
	private static FileInfo bJava() {
		return null; //"b1.json"
	}
*/
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
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/b4.json",
			1761300122000L
		);
	}

	private static FileInfo sameJava() {
		return sameJava("Same.class", "Same.class");
	}

	private static FileInfo sameJava2() {
		return sameJava("Same.class", "");
	}

	private static FileInfo sameJava(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, name, relative,
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/Same.class",
			1771361530064L
		);
	}

	private static FileInfo sameRes() {
		return sameRes("Same.java", "Same.java");
	}

	private static FileInfo sameRes2() {
		return sameRes("Same.java", "");
	}

	private static FileInfo sameRes(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, name, relative,
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/Same.java",
			1771361530572L
		);
	}

	private static FileInfo sameExt() {
		return sameExt("Same.java", "Same.java");
	}

	private static FileInfo sameExt2() {
		return sameExt("Same.java", "");
	}

	private static FileInfo sameExt(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, name, relative,
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/Same.java",
			1771361017503L
		);
	}

	private static FileInfo sameJar() {
		return sameJar("Same.java", "Same.java");
	}

	private static FileInfo sameJar2() {
		return sameJar("Same.java", "");
	}

	private static FileInfo sameJar(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, name, relative,
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/Same.java",
			1771361016000L
		);
	}

	private static FileInfo subJava() {
		return subJava("sub", "sub");
	}
/*
	private static FileInfo subJava2() {
		return subJava("", "");
	}
*/
	private static FileInfo subJava(String name, String relative) {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.RESOURCE, name, relative,
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/sub",
			1771361530060L
		);
	}

	private static FileInfo subRes() {
		return subRes("sub", "sub");
	}
/*
	private static FileInfo subRes2() {
		return subRes("", "");
	}
*/
	private static FileInfo subRes(String name, String relative) {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.RESOURCE, name, relative,
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub",
			1771361530572L
		);
	}

	private static FileInfo subExt() {
		return subExt("sub", "sub");
	}
/*
	private static FileInfo subExt2() {
		return subExt("", "");
	}
*/
	private static FileInfo subExt(String name, String relative) {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.EXTERNAL, name, relative,
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub",
			1771275800893L
		);
	}

	private static FileInfo subJar() {
		return subJar("sub", "sub");
	}

	private static FileInfo subJar2() {
		return subJar("", "");
	}

	private static FileInfo subJar(String name, String relative) {
		return new FileInfo(
			FileType.DIRECTORY, FileMode.JAR, name, relative,
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/sub",
			1771275800000L
		);
	}
/*
	private static FileInfo cJava() {
		return null; //"sub/c1.xml",
	}
*/	
	private static FileInfo cRes() {
		return cRes("c2.xml", "sub/c2.xml");
	}
	
	private static FileInfo cRes2() {
		return cRes("c2.xml", "c2.xml");
	}

	private static FileInfo cRes(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, name, relative,
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub/c2.xml",
			1771361530572L
		);
	}

	private static FileInfo cExt() {
		return cExt("c3.xml", "sub/c3.xml");
	}

	private static FileInfo cExt2() {
		return cExt("c3.xml", "c3.xml");
	}

	private static FileInfo cExt(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, name, relative,
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub/c3.xml",
			1761300150000L
		);
	}

	private static FileInfo cJar() {
		return cJar("c4.xml", "sub/c4.xml");
	}

	private static FileInfo cJar2() {
		return cJar("c4.xml", "c4.xml");
	}

	private static FileInfo cJar(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, name, relative,
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/sub/c4.xml",
			1761300150000L
		);
	}

	private static FileInfo dJava() {
		return dJava("d1.class", "sub/d1.class");
	}

	private static FileInfo dJava2() {
		return dJava("d1.class", "d1.class");
	}

	private static FileInfo dJava(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, name, relative,
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/sub/d1.class",
			1771361530060L
		);
	}

	private static FileInfo dRes() {
		return dRes("d2.java", "sub/d2.java");
	}

	private static FileInfo dRes2() {
		return dRes("d2.java", "d2.java");
	}

	private static FileInfo dRes(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.RESOURCE, name, relative,
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub/d2.java",
			1771361530572L
		);
	}

	private static FileInfo dExt() {
		return dExt("d3.java", "sub/d3.java");
	}

	private static FileInfo dExt2() {
		return dExt("d3.java", "d3.java");
	}

	private static FileInfo dExt(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.EXTERNAL, name, relative,
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub/d3.java",
			1771361883533L
		);
	}

	private static FileInfo dJar() {
		return dJar("d4.java", "sub/d4.java");
	}

	private static FileInfo dJar2() {
		return dJar("d4.java", "d4.java");
	}

	private static FileInfo dJar(String name, String relative) {
		return new FileInfo(
			FileType.FILE, FileMode.JAR, name, relative,
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/sub/d4.java",
			1771362306000L
		);
	}
}
