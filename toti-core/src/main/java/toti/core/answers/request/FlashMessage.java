package toti.core.answers.request;

public record FlashMessage(String severity, String message) {

	@Override
	public final String toString() {
		return "FlashMessage(severity=" + severity + ", message" + message + ")";
	}

}
