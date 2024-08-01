package ji.json.providers;

import ji.json.JsonStreamException;

public class InputStringProvider implements InputProvider {

	private String json;
	
	private int index = 0;
	
	public InputStringProvider(String json) {
		this.json = json;
	}
	
	@Override
	public char getNext() throws JsonStreamException {
		if (index < json.length()) {
			return json.charAt(index++);
		}
		return (char)-1;
	}

	@Override
	public void close() throws JsonStreamException {
		// not required
	}
	
}
