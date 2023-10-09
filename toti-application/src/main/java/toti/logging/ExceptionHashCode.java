package toti.logging;

import ji.socketCommunication.http.StatusCode;
import toti.application.register.MappedAction;

public class ExceptionHashCode {

	private final StatusCode code;
	private final MappedAction action;
	private final String message;
	
	public ExceptionHashCode(StatusCode code, MappedAction action, Throwable t) {
		this.code = code;
		this.action = action;
		StringBuilder message = new StringBuilder();
		Throwable aux = t;
		do {
			message.append(aux.getClass());
			if (aux.getMessage() != null) {
				message.append(": " + aux.getMessage());
			}
			if (aux.getCause() != null) {
				message.append(", Caused: ");
			}
			aux = aux.getCause();
		} while (aux != null);
		this.message = message.toString(); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.logHashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExceptionHashCode other = (ExceptionHashCode) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.logEquals(other.action)) {
			return false;
		}
		if (code != other.code) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		return true;
	}
	
}
