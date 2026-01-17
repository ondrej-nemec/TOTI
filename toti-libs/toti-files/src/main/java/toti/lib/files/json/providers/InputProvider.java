package toti.lib.files.json.providers;

import toti.lib.files.json.JsonStreamException;

public interface InputProvider {
	
	public char getNext() throws JsonStreamException;
	
	public void close() throws JsonStreamException;
	
}
