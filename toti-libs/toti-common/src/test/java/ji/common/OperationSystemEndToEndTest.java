package ji.common;

import ji.common.functions.Console;
import ji.common.functions.OperationSystem;

public class OperationSystemEndToEndTest {

	public static void main(String[] args) {
		Console c = new Console();
		c.out("new line: " + OperationSystem.NEW_LINE);
		c.out("cli extension: " + OperationSystem.CLI_EXTENSION);
		c.out("path separator: " + OperationSystem.PATH_SEPARATOR);
		c.out("pre command: " + OperationSystem.PRE_COMMAND);
	}
	
}
