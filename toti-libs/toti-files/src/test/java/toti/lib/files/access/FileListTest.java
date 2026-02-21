package toti.lib.files.access;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.common.tests.TestCase;

public class FileListTest {

	@ParameterizedTest
	@MethodSource("testGet")
	public void testGet(String message, String folder, boolean recursive, SearchFilter filter, List<FileInfo> expected) throws Exception {
		List<FileInfo> actual = FileList.get(folder, recursive, filter).stream().map(f->create(f)).collect(Collectors.toList());
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
		RuntimeException expected = assertThrows(RuntimeException.class, ()->FileList.get(folder, recursive, filter));
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
		return create(
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
		return aRes("a2.txt");
	}


	private static FileInfo aRes2() {
		return aRes("a2.txt");
	}

	private static FileInfo aRes(String name) {
		return create(
			FileType.FILE, FileMode.RESOURCE, name, "tests/fileAccess/a2.txt",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/a2.txt",
			1771691443673L
		);
	}

	private static FileInfo aExt() {
		return aExt("a3.txt");
	}

	private static FileInfo aExt2() {
		return aExt("a3.txt");
	}

	private static FileInfo aExt(String name) {
		return create(
			FileType.FILE, FileMode.EXTERNAL, name, "tests/fileAccess/a3.txt",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/a3.txt",
			1771689255046L
		);
	}

	private static FileInfo aJar() {
		return aJar("a4.txt");
	}

	private static FileInfo aJar2() {
		return aJar("a4.txt");
	}

	private static FileInfo aJar(String name) {
		return create(
			FileType.FILE, FileMode.JAR, name, "tests/fileAccess/a4.txt",
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
		return create(
			FileType.FILE, FileMode.RESOURCE, "b2.json", "tests/fileAccess/b2.json",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/b2.json",
			1771361530564L
		);
	}

	private static FileInfo bExt() {
		return create(
			FileType.FILE, FileMode.EXTERNAL, "b3.json", "tests/fileAccess/b3.json",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/b3.json",
			1761300122000L
		);
	}

	private static FileInfo bJar() {
		return create(
			FileType.FILE, FileMode.JAR, "b4.json", "tests/fileAccess/b4.json",
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/b4.json",
			1761300122000L
		);
	}

	private static FileInfo sameJava() {
		return sameJava("Same.class");
	}

	private static FileInfo sameJava2() {
		return sameJava("Same.class");
	}

	private static FileInfo sameJava(String name) {
		return create(
			FileType.FILE, FileMode.RESOURCE, name, "tests/fileAccess/Same.class",
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/Same.class",
			1771361530064L
		);
	}

	private static FileInfo sameRes() {
		return sameRes("Same.java");
	}

	private static FileInfo sameRes2() {
		return sameRes("Same.java");
	}

	private static FileInfo sameRes(String name) {
		return create(
			FileType.FILE, FileMode.RESOURCE, name, "tests/fileAccess/Same.java",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/Same.java",
			1771691443673L
		);
	}

	private static FileInfo sameExt() {
		return sameExt("Same.java");
	}

	private static FileInfo sameExt2() {
		return sameExt("Same.java");
	}

	private static FileInfo sameExt(String name) {
		return create(
			FileType.FILE, FileMode.EXTERNAL, name, "tests/fileAccess/Same.java",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/Same.java",
			1771361017503L
		);
	}

	private static FileInfo sameJar() {
		return sameJar("Same.java");
	}

	private static FileInfo sameJar2() {
		return sameJar("Same.java");
	}

	private static FileInfo sameJar(String name) {
		return create(
			FileType.FILE, FileMode.JAR, name, "tests/fileAccess/Same.java",
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/Same.java",
			1771361016000L
		);
	}

	private static FileInfo subJava() {
		return create(
			FileType.DIRECTORY, FileMode.RESOURCE, "sub", "tests/fileAccess/sub",
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/sub",
			1771361530060L
		);
	}

	private static FileInfo subRes() {
		return create(
			FileType.DIRECTORY, FileMode.RESOURCE, "sub", "tests/fileAccess/sub",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub",
			1771361530572L
		);
	}

	private static FileInfo subExt() {
		return create(
			FileType.DIRECTORY, FileMode.EXTERNAL, "sub", "tests/fileAccess/sub",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub",
			1771275800893L
		);
	}

	private static FileInfo subJar() {
		return subJar("sub");
	}

	private static FileInfo subJar2() {
		return subJar("");
	}

	private static FileInfo subJar(String name) {
		return create(
			FileType.DIRECTORY, FileMode.JAR, name, "tests/fileAccess/sub",
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
		return cRes("c2.xml");
	}
	
	private static FileInfo cRes2() {
		return cRes("c2.xml");
	}

	private static FileInfo cRes(String name) {
		return create(
			FileType.FILE, FileMode.RESOURCE, name, "tests/fileAccess/sub/c2.xml",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub/c2.xml",
			1771361530572L
		);
	}

	private static FileInfo cExt() {
		return cExt("c3.xml");
	}

	private static FileInfo cExt2() {
		return cExt("c3.xml");
	}

	private static FileInfo cExt(String name) {
		return create(
			FileType.FILE, FileMode.EXTERNAL, name, "tests/fileAccess/sub/c3.xml",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub/c3.xml",
			1761300150000L
		);
	}

	private static FileInfo cJar() {
		return cJar("c4.xml");
	}

	private static FileInfo cJar2() {
		return cJar("c4.xml");
	}

	private static FileInfo cJar(String name) {
		return create(
			FileType.FILE, FileMode.JAR, name, "tests/fileAccess/sub/c4.xml",
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/sub/c4.xml",
			1761300150000L
		);
	}

	private static FileInfo dJava() {
		return dJava("d1.class");
	}

	private static FileInfo dJava2() {
		return dJava("d1.class");
	}

	private static FileInfo dJava(String name) {
		return create(
			FileType.FILE, FileMode.RESOURCE, name, "tests/fileAccess/sub/d1.class",
			"/home/coder/project/toti-libs/toti-files/build/classes/java/test/tests/fileAccess/sub/d1.class",
			1771361530060L
		);
	}

	private static FileInfo dRes() {
		return dRes("d2.java");
	}

	private static FileInfo dRes2() {
		return dRes("d2.java");
	}

	private static FileInfo dRes(String name) {
		return create(
			FileType.FILE, FileMode.RESOURCE, name, "tests/fileAccess/sub/d2.java",
			"/home/coder/project/toti-libs/toti-files/build/resources/test/tests/fileAccess/sub/d2.java",
			1771361530572L
		);
	}

	private static FileInfo dExt() {
		return dExt("d3.java");
	}

	private static FileInfo dExt2() {
		return dExt("d3.java");
	}

	private static FileInfo dExt(String name) {
		return create(
			FileType.FILE, FileMode.EXTERNAL, name, "tests/fileAccess/sub/d3.java",
			"/home/coder/project/toti-libs/toti-files/tests/fileAccess/sub/d3.java",
			1771361883533L
		);
	}

	private static FileInfo dJar() {
		return dJar("d4.java");
	}

	private static FileInfo dJar2() {
		return dJar("d4.java");
	}

	private static FileInfo dJar(String name) {
		return create(
			FileType.FILE, FileMode.JAR, name, "tests/fileAccess/sub/d4.java",
			// jar:file:/home/coder/project/toti-libs/toti-files/tests/fileAccess.jar!
			"/tests/fileAccess/sub/d4.java",
			1771362306000L
		);
	}

	private static FileInfo create(FileInfo info) {
		return create(info.getType(), info.getMode(), info.getFullName(), info.getRelativePath(), info.getAbsolutePath(), info.getLastModificationTime());
	}


	private static FileInfo create(FileType type, FileMode mode, String name, String relative, String absolute, long lastModified) {
		return new FileInfo(type, mode, name, relative, absolute, -1000000L) {
			@Override public boolean equals(Object obj) {
				return super.partialEquals(obj);
				/*if (super.partialEquals(obj)) {
					FileInfo other = (FileInfo) obj;
					return getLastModificationTime() >= lastModified && other.getLastModificationTime() >= lastModified;
				}
			    return false;*/
			}
		};
	}
}
