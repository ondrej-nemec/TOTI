package toti.lib.common.exceptions;

/**
 * Exception wraps Exception throwed during hashing.
 * 
 * @author Ondřej Němec
 *
 */
public class HashException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public HashException(Exception exception) {
		super(exception);
	}

}
