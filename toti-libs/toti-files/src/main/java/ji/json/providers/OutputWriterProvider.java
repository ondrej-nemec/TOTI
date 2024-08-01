package ji.json.providers;

import java.io.IOException;
import java.io.Writer;

import ji.json.JsonStreamException;

public class OutputWriterProvider implements OutputProvider {

	private final Writer writer;
	
	public OutputWriterProvider(Writer writer) {
		this.writer = writer;
	}
	
	@Override
	public void write(String json) throws JsonStreamException {
		try {
			writer.write(json);
		} catch (IOException e) {
			throw new JsonStreamException(e);
		}
	}

	@Override
	public void close() throws JsonStreamException {
		try {
			writer.close();
		} catch (IOException e) {
			throw new JsonStreamException(e);
		}
	}

}
