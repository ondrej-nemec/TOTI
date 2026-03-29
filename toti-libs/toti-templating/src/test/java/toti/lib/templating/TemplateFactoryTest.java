package toti.lib.templating;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import toti.lib.common.functions.compiling.Compiler;
import toti.lib.common.tests.TestCase;
import toti.lib.templating.parsing.TemplateParser;
import toti.lib.templating.structures.TemplateFile;

public class TemplateFactoryTest {

	@ParameterizedTest
	@MethodSource("testCreateFileThrows")
	public void testCreateFileThrows(String moduleName, String fileRelativePath, String expectedMessage) {
		Map<String, String> modules = new HashMap<>();
		modules.put("module-a", "toti/templating/templateFactoryTest/module-a");
		TemplateFactory factory = new TemplateFactory(null, modules, null, null, null);
		TemplateException expected = assertThrows(TemplateException.class, ()->{
			factory.createTemplateFile(moduleName, fileRelativePath);
		});
		assertEquals(expectedMessage, expected.getMessage());
	}

	public static Object[] testCreateFileThrows() {
		return new Object[] {
			// fileRelativePath is null
			new Object[] { "module-a", null, "No template filename was given" },
			// fileRelativePath is empty
			new Object[] { "module-a", "", "No template filename was given" },
			// not existing module name
			new Object[] { "module-b", "index.jsp", "No module path found for 'module-b'" },
			// pointing to empty folder
			new Object[] { "module-a", "empty", "No file found for folder='toti/templating/templateFactoryTest/module-a' and name='empty'" }
		};
	}

	@ParameterizedTest
	@MethodSource("testCreateFileReturnsFile")
	public void testCreateFileReturnsFile(String moduleName, String fileRelativePath, TemplateFile expected) throws Exception {
		Map<String, String> modules = new HashMap<>();
		modules.put("", "toti/templating/templateFactoryTest/module-main");
		modules.put("module-a", "toti/templating/templateFactoryTest/module-a");
		modules.put("module-b", "toti/templating/templateFactoryTest/module-b");
		TemplateFactory factory = new TemplateFactory(null, modules, null, null, null);
		TemplateFile actual = factory.createTemplateFile(moduleName, fileRelativePath);
		TestCase.assertEquals(expected, actual);
	}

	public static Object[] testCreateFileReturnsFile() {
		return new Object[] {
			new Object[] {
				"module-a", "index.jsp", new TemplateFile(
					"module-a",
					"index",
					"toti_templating_templateFactoryTest_module_a",
					"toti_templating_templateFactoryTest_module_a.index",
					0L,
					"toti_templating_templateFactoryTest_module_a",
					"toti/templating/templateFactoryTest/module-a/index.jsp"
				)
			},
			new Object[] {
				"module-a", "/index.jsp", new TemplateFile(
					"module-a",
					"index",
					"toti_templating_templateFactoryTest_module_a",
					"toti_templating_templateFactoryTest_module_a.index",
					0L,
					"toti_templating_templateFactoryTest_module_a",
					"toti/templating/templateFactoryTest/module-a/index.jsp"
				)
			},
			new Object[] {
				"module-a", "entities/sub.jsp", new TemplateFile(
					"module-a",
					"sub",
					"toti_templating_templateFactoryTest_module_a.entities",
					"toti_templating_templateFactoryTest_module_a.entities.sub",
					0L,
					"toti_templating_templateFactoryTest_module_a/entities",
					"toti/templating/templateFactoryTest/module-a/entities/sub.jsp"
				)
			},
			new Object[] {
				"module-a", "/entities/sub.jsp", new TemplateFile(
					"module-a",
					"sub",
					"toti_templating_templateFactoryTest_module_a.entities",
					"toti_templating_templateFactoryTest_module_a.entities.sub",
					0L,
					"toti_templating_templateFactoryTest_module_a/entities",
					"toti/templating/templateFactoryTest/module-a/entities/sub.jsp"
				)
			},
			new Object[] {
				"module-b", "index.jsp", new TemplateFile(
					"module-b",
					"index",
					"toti_templating_templateFactoryTest_module_b",
					"toti_templating_templateFactoryTest_module_b.index",
					0L,
					"toti_templating_templateFactoryTest_module_b",
					"toti/templating/templateFactoryTest/module-b/index.jsp"
				)
			},
			new Object[] {
				"", "index.jsp", new TemplateFile(
					"",
					"index",
					"toti_templating_templateFactoryTest_module_main",
					"toti_templating_templateFactoryTest_module_main.index",
					0L,
					"toti_templating_templateFactoryTest_module_main",
					"toti/templating/templateFactoryTest/module-main/index.jsp"
				)
			}
		};
	}

	@ParameterizedTest
	@MethodSource("testClear")
	public void testClear(String path, String expected) {
		TemplateFactory factory = new TemplateFactory(null, new HashMap<>(), null, null, null);
		TestCase.assertEquals(expected, factory.clear(path));
	}

	public static Object[] testClear() {
		return new Object[] {
			new Object[] { "some-path-without-slash", "some-path-without-slash" },

			new Object[] { "some/path/with/slash", "some/path/with/slash" },
			new Object[] { "/some/path/starts/with/slash", "some/path/starts/with/slash" },
			new Object[] { "some/path/ends/with/slash/", "some/path/ends/with/slash" },
			new Object[] { "/some/path/with/both/", "some/path/with/both" },
			
			new Object[] { "some\\path\\with\\slash", "some/path/with/slash" },
			new Object[] { "\\some\\path\\starts\\with\\slash", "some/path/starts/with/slash" },
			new Object[] { "some\\path\\ends\\with\\slash\\", "some/path/ends/with/slash" },
			new Object[] { "\\some\\path\\with\\both\\", "some/path/with/both" }
		};
	}

	@Test
	public void testCompileNewCache() throws Exception {
		Map<String, String> modules = new HashMap<>();
		modules.put("module-a", "toti/templating/templateFactoryTest/module-a");

		TemplateParser parser = mock(TemplateParser.class);
		when(parser.createTempCache(any(), any())).thenReturn("path/to/prepared/file");
		Compiler compiler = mock(Compiler.class);
		when(compiler.compile(any(), any(), any())).thenReturn(Optional.empty());

		TemplateFile file = new TemplateFile(
			"module-a",
			"sub",
			"toti_templating_templateFactoryTest_module_a.entities",
			"toti_templating_templateFactoryTest_module_a.entities.sub",
			123456789L,
			"toti_templating_templateFactoryTest_module_a/entities",
			"toti/templating/templateFactoryTest/module-a/entities/sub.jsp"
		);

		TemplateFactory factory = new TemplateFactory(
			"temp", modules, false, false, null, null, compiler, null
		);
		factory.compileNewCache(file, parser);

		verify(compiler, times(1)).compile(
			new File("path/to/prepared/file"),
			"toti_templating_templateFactoryTest_module_a.entities",
			"toti/templating/templateFactoryTest/module-a/entities/sub.jsp"
		);
		verify(parser, times(1)).createTempCache(file, "temp/cache");
	}

}
