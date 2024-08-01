package ji.common.exceptions;

/**
 * Exception signalizes not finished code
 * 
 * @author Ondřej Němec
 *
 */
public class NotImplementedYet extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NotImplementedYet() {
		super("This method is not implemented yet");
	}

}
