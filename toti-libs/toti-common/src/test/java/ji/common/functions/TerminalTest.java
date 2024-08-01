package ji.common.functions;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.logging.log4j.Logger;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TerminalTest {
	
	private final String SYSTEM_ERROR_MESSAGE = "SYSTEM_ERROR_MESSAGE"; 
	
	private String path =
			"src" +
			OperationSystem.PATH_SEPARATOR +
			"test" +
			OperationSystem.PATH_SEPARATOR +
			"resources" +
			OperationSystem.PATH_SEPARATOR +
			"functions" + 
			OperationSystem.PATH_SEPARATOR +
			"terminal" +
			OperationSystem.PATH_SEPARATOR;
	
	private String stdErr = "";
	
	private String stdOut = "";
	
	@Test
	@Parameters
	public void testRunCommandWorks(final String command, int expectedCode, final String expectedOut, final String expectedErr) {
		Terminal terminal = new Terminal(mock(Logger.class));
		
		int code = terminal.runCommand(
				(a)->{stdOut += a;},
				(a)->{stdErr += a;},
				command
			);
		
		assertEquals(expectedCode, code);
		assertEquals(expectedOut, this.stdOut);
		
		if (SYSTEM_ERROR_MESSAGE.equals(expectedErr)) {
			assertTrue(expectedErr.length() > 14);
		} else {
			assertEquals(expectedErr, this.stdErr);
		}		
	}
	
	public Collection<Object[]> parametersForTestRunCommandWorks() {
		return Arrays.asList(
				new Object[]{ //working void command
					"exit", 0, "", ""
				},
				new Object[]{ //working void command
					"echo success", 0, "success", ""
				},
				new Object[]{ // not working command
					"notExisting", 1, "", SYSTEM_ERROR_MESSAGE
				}
			);
	}
	
	@Test
	@Parameters
	public void testRunFileWorks(final String file, int expectedCode, final String expectedOut, final String expectedErr) {
		Terminal terminal = new Terminal(mock(Logger.class));
		
		int code = terminal.runFile(
				(a)->{stdOut += a + " | ";},
				(a)->{stdErr += a + " | ";},
				file
			);
		
		assertEquals(expectedCode, code);
		assertEquals(expectedOut, this.stdOut);
		
		if (SYSTEM_ERROR_MESSAGE.equals(expectedErr)) {
			assertTrue(expectedErr.length() > 7);
		} else {
			assertEquals(expectedErr, this.stdErr);
		}		
	}
	
	public Collection<Object[]> parametersForTestRunFileWorks() {
		String absolutePath = System.getProperty("user.dir");
		return Arrays.asList(
				new Object[]{
					path + "success",
					0,
					" | " + absolutePath + ">echo standart output  | standart output | ",
					""
				},
				new Object[]{
					path + "bad-command", 1, " | " + absolutePath + ">echor | ", SYSTEM_ERROR_MESSAGE
				},
				new Object[]{
					path + "std-err-out",
					0,
					" | " + absolutePath + ">echo std err  1>&2  |  | " + absolutePath + ">echo std out  | std out | ",
					"std err  | "
				},
				new Object[]{
					path + "exit-code-5", 5, " | " + absolutePath + ">exit 5  | ", ""
				}
			);
	}

}
