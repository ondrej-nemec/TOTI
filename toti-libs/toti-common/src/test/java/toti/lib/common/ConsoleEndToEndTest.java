package toti.lib.common;

import toti.lib.common.functions.Console;

public class ConsoleEndToEndTest {

	public static void main(String[] args) {
		Console c = new Console();
		c.out("Text printed by console");
		c.out();
		c.out("Text after new line");
		
		Object[] array = new String[] {"Printed", "array"};
		c.out(array);
		
		c.out("Write some text:");
		String a = c.in();
		c.out("Your input is: " + a);
	}
}
