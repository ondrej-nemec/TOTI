package ji.common.functions;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TerminalTest {
	
	private static final String SYSTEM_ERROR_MESSAGE = "SYSTEM_ERROR_MESSAGE";
	
	@ParameterizedTest
	@MethodSource("dataRunSyncWorkingCommands")
	public void testRunSyncWorkingCommands(String[] command, int expectedCode, String expectedOut, String expectedErr) throws IOException, InterruptedException {
		Terminal terminal = new Terminal();
		
		StringBuilder stdOut = new StringBuilder();
		StringBuilder stdErr = new StringBuilder();
		int code = terminal.run(
			a->stdOut.append(a),
			a->stdErr.append(a),
			command
		);
		System.out.println(stdOut);
		System.out.println(stdErr);
		System.out.println(code);
		System.out.println("----------------");
		
		assertEquals(expectedCode, code);
		assertEquals(expectedOut, stdOut.toString());
		
		if (SYSTEM_ERROR_MESSAGE.equals(expectedErr)) {
			assertTrue(expectedErr.length() > 14);
		} else {
			assertEquals(expectedErr, stdErr.toString());
		}
	}
	
	public static Object[] dataRunSyncWorkingCommands() {
		String filesPath = "./src/test/resources/functions/terminal/";
		return new Object[] {
			new Object[] { //working void command
				new String[] { "touch", "tmp/aaa" }, 0, "", "" // "echo \"\""
			},
			new Object[] { //working void command
				new String[] { "echo", "success" }, 0, "success", ""
			},
			new Object[] {
				new String[] { filesPath + "success" }, 0, "standart output", ""
			},
			new Object[] {
				new String[] { filesPath + "bad-command" }, 127, "", SYSTEM_ERROR_MESSAGE
			},
			new Object[] {
				new String[] { filesPath + "std-err-out" }, 0, "std out", "std err"
			},
			new Object[] {
				new String[] { filesPath + "exit-code-5" }, 5, "", ""
			}
		};
	}
	
	@ParameterizedTest
	@MethodSource("dataRunSyncThrowsOnWrongCommand")
	public void testRunSyncThrowsOnWrongCommand(String[] command) throws IOException, InterruptedException {
		Terminal terminal = new Terminal();
		
		StringBuilder stdOut = new StringBuilder();
		StringBuilder stdErr = new StringBuilder();
		IOException expected = assertThrows(IOException.class, ()->{
			terminal.run(
				a->stdOut.append(a),
				a->stdErr.append(a),
				command
			);
		});
		assertNotNull(expected);
		assertEquals("", stdOut.toString());
		assertEquals("", stdErr.toString());
	}
	
	public static Object[] dataRunSyncThrowsOnWrongCommand() {
		// String absolutePath = System.getProperty("user.dir");
		// String filesPath = "./src/test/resources/functions/terminal/";
		return new Object[] {
			new Object[] { // not working command
				new String[] { "notExisting" }, -1, "", SYSTEM_ERROR_MESSAGE
			}/*,
			new Object[] {
				new String[] { filesPath + "bad-command" },
				1,
				" | " + absolutePath + ">echor | ", SYSTEM_ERROR_MESSAGE
			},
			new Object[] {
				new String[] { filesPath + "std-err-out" },
				0,
				" | " + absolutePath + ">echo std err  1>&2  |  | " + absolutePath + ">echo std out  | std out | ",
				"std err  | "
			},
			new Object[] {
				new String[] { filesPath + "exit-code-5" },
				5,
				" | " + absolutePath + ">exit 5  | ", ""
			}*/
		};
	}

}
