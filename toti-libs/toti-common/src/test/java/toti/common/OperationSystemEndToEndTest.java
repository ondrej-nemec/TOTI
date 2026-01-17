package toti.common;

import toti.common.functions.Console;
import toti.common.functions.OperationSystem;

public class OperationSystemEndToEndTest {

	public static void main(String[] args) {
		Console c = new Console();
		c.out("new line: " + OperationSystem.NEW_LINE);
		c.out("cli extension: " + OperationSystem.CLI_EXTENSION);
		c.out("path separator: " + OperationSystem.PATH_SEPARATOR);
		c.out("pre command: " + OperationSystem.PRE_COMMAND);
	}
	
}
