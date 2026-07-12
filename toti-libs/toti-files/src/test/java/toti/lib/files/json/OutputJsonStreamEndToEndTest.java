package toti.lib.files.json;

import toti.lib.files.json.providers.OutputStringProvider;

public class OutputJsonStreamEndToEndTest {
	
	public void formatted() throws JsonStreamException {
		OutputStringProvider provider = new OutputStringProvider();
		OutputJsonStream stream = new OutputJsonStream(provider, true);
		createJson(stream);
		System.out.println(provider.getJson());
	}
	
	public void unFormatted() throws JsonStreamException {
		OutputStringProvider provider = new OutputStringProvider();
		OutputJsonStream stream = new OutputJsonStream(provider, false);
		createJson(stream);
		System.out.println(provider.getJson());
	}
	
	public void createJson(OutputJsonStream stream) throws JsonStreamException {
		// stream.startDocument();
		stream.writeObjectStart();
		stream.writeObjectValue("name1", "value1");
		stream.writeObjectValue("with-quotes", "\"");
		stream.writeListStart("list1");
		stream.writeListValue("list-value1");
		stream.writeObjectStart();
		stream.writeObjectValue("name2", "value2");
		stream.writeObjectEnd();
		stream.writeListEnd();
		
		stream.writeObjectEnd();
		// stream.endDocument();
	}

	@SuppressWarnings("CallToPrintStackTrace")
	public static void main(String[] args) {
		OutputJsonStreamEndToEndTest test = new OutputJsonStreamEndToEndTest();
		try {
			test.formatted();
			System.out.println();
			test.unFormatted();
		} catch (JsonStreamException e) {
			e.printStackTrace();
		}

	}

}
