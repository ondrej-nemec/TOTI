package ji.json.providers;

import ji.json.JsonStreamException;

public interface InputProvider {
	
	public char getNext() throws JsonStreamException;
	
	public void close() throws JsonStreamException;
	
}
