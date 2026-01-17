package toti.lib.files.json.providers;

import toti.lib.files.json.JsonStreamException;

public class OutputStringProvider implements OutputProvider {

	private final StringBuilder json = new StringBuilder();
	
	@Override
	public void write(String json) throws JsonStreamException {
		this.json.append(json);
	}
	
	public String getJson() {
		return json.toString();
	}

	@Override
	public void close() throws JsonStreamException {
		// not implemented
	}

}
