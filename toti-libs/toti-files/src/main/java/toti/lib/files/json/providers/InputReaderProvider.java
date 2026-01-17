package toti.lib.files.json.providers;

import java.io.IOException;
import java.io.Reader;

import toti.lib.files.json.JsonStreamException;

public class InputReaderProvider implements InputProvider {

	private final Reader reader;
	
	public InputReaderProvider(Reader reader) {
		this.reader = reader;
	}

	@Override
	public char getNext() throws JsonStreamException {
		try {
			return (char)reader.read();
		} catch (IOException e) {
			throw new JsonStreamException(e);
		}
	}

	@Override
	public void close() throws JsonStreamException {
		try {
			reader.close();
		} catch (IOException e) {
			throw new JsonStreamException(e);
		}
	}
	
}
