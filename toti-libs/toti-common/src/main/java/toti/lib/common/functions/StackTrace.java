package toti.lib.common.functions;

import toti.lib.common.structures.ThrowingFunction;

/**
 * Class simplify searching in stack trace
 * 
 * @author Ondřej Němec
 *
 */
public class StackTrace {

	/**
	 * Search in stack trace of current thread
	 * <p>
	 * Iterate current stack trace. Apply given function on each element until function returns true.
	 * In this case method returns name of class from the element.
	 * 
	 * @param function
	 * @return String class name or null
	 */
	public static String classParent(ThrowingFunction<StackTraceElement, Boolean, Exception> function) {
		String className = null;
		try {
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				if (function.apply(ste)) {
					className = ste.getClassName();
					break;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return className;
	}
	
}
