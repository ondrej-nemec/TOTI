package ji.common.functions;

import java.util.Scanner;

/**
 * Communication with system console.
 * Read user inputs and write to stout and sterr.
 * 
 * @author Ondřej Němec
 *
 */
public class Console {

	private final Scanner scanner;	
	
	public Console() {
		this.scanner = new Scanner(System.in);
	}
	
	/**
	 * Write to System.out
	 * 
	 * @param message Object what will be printed in console
	 */
	public synchronized void out(final Object message) {
		System.out.println(message);
	}
	
	/**
	 * Write empty row to System.out
	 */
	public synchronized void out() {
		out("");
	}
	
	/**
	 * Write mesages to System.out. One message - one row
	 * 
	 * @param messages array of {@link Object} what will be printed
	 */
	public synchronized void out(final Object... messages) {
		for(Object message : messages) {
			out(message);
		}
	}
	
	/**
	 * Write to System.err
	 * 
	 * @param message Object what will be printed in console
	 */
	public synchronized void err(final Object message) {
		System.err.println(message);
	}
	
	/**
	 * Read line from console
	 * 
	 * @return String readed line
	 */
	public synchronized String in() {
		return scanner.nextLine();
	}
	
	/**
	 * Write question for input to System.out and read user answer
	 * 
	 * @param query String question
	 * @return String user answer
	 */
	public synchronized String in(String query) {
		out(query);
		return in();
	}

	/**
	 * Write question for input to System.out and read user answer
	 * 
	 * @param message String question
	 * @return String user answer
	 */
	public String ask(String message) {
		out(message);
		return in();
	}
	
	/**
	 * Clear console
	 */
	public synchronized void clear() {
		System.out.print("\033[H\033[2J");  
		System.out.flush();
	}
}
