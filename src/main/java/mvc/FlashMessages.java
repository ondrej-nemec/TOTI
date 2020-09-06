package mvc;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FlashMessages implements Serializable, Iterable<FlashMessages.Message>, Iterator<FlashMessages.Message> {

	private static final long serialVersionUID = 123L;
	
	public class Message implements Serializable {
		private static final long serialVersionUID = 124L;
		private final String message;
		private final String severity;
		public Message(String message, String severity) {
			this.message = message;
			this.severity = severity;
		}
		public String getMessage() {
			return message;
		}
		public String getSeverity() {
			return severity;
		}
	}
	
	private final List<Message> messages = new LinkedList<>();
	
	private Iterator<Message> iterator;
		
	public void addMessage(String message, String severity) {
		messages.add(new Message(message, severity));
	}

	protected List<Message> getMessages() {
		return messages;
	}

	@Override
	public boolean hasNext() {
		if (iterator == null) {
			this.iterator = messages.iterator();
		}
		return iterator.hasNext();
	}

	@Override
	public Message next() {
		if (iterator == null) {
			this.iterator = messages.iterator();
		}
		Message message = iterator.next();
		iterator.remove();
		return message;
	}

	@Override
	public Iterator<Message> iterator() {
		return this;
	}
	
}
