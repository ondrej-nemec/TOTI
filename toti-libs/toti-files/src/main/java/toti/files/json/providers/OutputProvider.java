package toti.files.json.providers;

import toti.files.json.JsonStreamException;

public interface OutputProvider {

	void write(String json) throws JsonStreamException;
	
	void close() throws JsonStreamException;
	
}
