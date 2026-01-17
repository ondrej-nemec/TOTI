package toti.common.exceptions;

/**
 * Exception that represents error in the program logic.
 * 
 * @author Ondřej Němec
 *
 */
public class LogicException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public LogicException(String message) {
		super(message);
	}

}
