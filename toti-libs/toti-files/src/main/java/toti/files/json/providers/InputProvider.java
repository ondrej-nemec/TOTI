package toti.files.json.providers;

import toti.files.json.JsonStreamException;

public interface InputProvider {
	
	public char getNext() throws JsonStreamException;
	
	public void close() throws JsonStreamException;
	
}
