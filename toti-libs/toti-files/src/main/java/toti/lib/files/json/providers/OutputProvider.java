package toti.lib.files.json.providers;

import toti.lib.files.json.JsonStreamException;

public interface OutputProvider {

	void write(String json) throws JsonStreamException;
	
	void close() throws JsonStreamException;
	
}
