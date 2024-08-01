package ji.json;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.ThrowingConsumer;
import ji.json.providers.OutputStringProvider;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class OutputJsonStreamTest {

	@Test
	@Parameters(method = "dataOutputJsonStreamReturnsCorrectJson")
	public void testOutputJsonStreamReturnsCorrectJson(
			String json,
			ThrowingConsumer<OutputJsonStream, JsonStreamException> createStream,
			boolean formated) throws JsonStreamException {
		OutputStringProvider provider = new OutputStringProvider();
		OutputJsonStream stream = new OutputJsonStream(provider, formated);
		createStream.accept(stream);
		assertEquals(json, provider.getJson());
	}
	
	public Object[] dataOutputJsonStreamReturnsCorrectJson() {
		return new Object[] {
				new Object[] {
						"{}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectEnd();
						}),
						false
				},
				new Object[] {
						"{\"name\":\"value\"}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectValue("name", "value");
							stream.writeObjectEnd();
						}),
						false
				},
				new Object[] {
						"{\"quotes\":\"--\\\"--\"}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectValue("quotes", "--\"--");
							stream.writeObjectEnd();
						}),
						false
				},
				new Object[] {
						"{\"name\":\"value\",\"int\":12,\"special\":null,\"name2\":true}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectValue("name", "value");
							stream.writeObjectValue("int", 12);
							stream.writeObjectValue("special", null);
							stream.writeObjectValue("name2", true);
							stream.writeObjectEnd();
						}),
						false
				},
				new Object[] {
						"{\"list\":[\"value1\",\"value2\"]}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeListStart("list");
							stream.writeListValue("value1");
							stream.writeListValue("value2");
							stream.writeListEnd();
							stream.writeObjectEnd();
						}),
						false
				},
				new Object[] {
						"{\"object\":{\"name\":\"value\"}}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectStart("object");
							stream.writeObjectValue("name", "value");
							stream.writeObjectEnd();
							stream.writeObjectEnd();
						}),
						false
				},
				new Object[] {
						"{\"object\":{\"name\":\"value\",\"object2\":{\"list\":[12,false,123.4]}}}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectStart("object");
							stream.writeObjectValue("name", "value");
							stream.writeObjectStart("object2");
							stream.writeListStart("list");
							stream.writeListValue(12);
							stream.writeListValue(false);
							stream.writeListValue(123.4);
							stream.writeListEnd();
							stream.writeObjectEnd();
							stream.writeObjectEnd();
							stream.writeObjectEnd();
						}),
						false
				},
				
				new Object[] {
						"{\"name\":\"some \\n text \\t with \\r escaped \\\" chars \\\\ like \\b and \\f here\"}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectValue(
								"name",
								"some \n text \t with \r escaped \" chars \\ like \b and \f here"
							);
							stream.writeObjectEnd();
						}),
						false
				},

				new Object[] {
						"{\n"
						+ "}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectEnd();
						}),
						true
				},
				new Object[] {
						"{\n"
						+ "  \"name\": \"value\"\n"
						+ "}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectValue("name", "value");
							stream.writeObjectEnd();
						}),
						true
				},
				new Object[] {
						"{\n"
						+ "  \"name\": \"value\",\n"
						+ "  \"int\": 12,\n"
						+ "  \"special\": null,\n"
						+ "  \"name2\": true\n"
						+ "}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectValue("name", "value");
							stream.writeObjectValue("int", 12);
							stream.writeObjectValue("special", null);
							stream.writeObjectValue("name2", true);
							stream.writeObjectEnd();
						}),
						true
				},
				new Object[] {
						"{\n"
						+ "  \"list\": [\n"
						+ "    \"value1\",\n"
						+ "    \"value2\"\n"
						+ "  ]\n"
						+ "}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeListStart("list");
							stream.writeListValue("value1");
							stream.writeListValue("value2");
							stream.writeListEnd();
							stream.writeObjectEnd();
						}),
						true
				},
				new Object[] {
						"{\n"
						+ "  \"object\": {\n"
						+ "    \"name\": \"value\"\n"
						+ "  }\n"
						+ "}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectStart("object");
							stream.writeObjectValue("name", "value");
							stream.writeObjectEnd();
							stream.writeObjectEnd();
						}),
						true
				},
				new Object[] {
						"{\n"
						+ "  \"object\": {\n"
						+ "    \"name\": \"value\",\n"
						+ "    \"object2\": {\n"
						+ "      \"list\": [\n"
						+ "        12,\n"
						+ "        false,\n"
						+ "        123.4\n"
						+ "      ]\n"
						+ "    }\n"
						+ "  }\n"
						+ "}",
						c((stream)->{
							stream.writeObjectStart();
							stream.writeObjectStart("object");
							stream.writeObjectValue("name", "value");
							stream.writeObjectStart("object2");
							stream.writeListStart("list");
							stream.writeListValue(12);
							stream.writeListValue(false);
							stream.writeListValue(123.4);
							stream.writeListEnd();
							stream.writeObjectEnd();
							stream.writeObjectEnd();
							stream.writeObjectEnd();
						}),
						true
				},
				new Object[] {
					"{\"escape\":\" escape \\\\ and \\\" for safety \"}",
					c((stream)->{
						stream.writeObjectStart();
						stream.writeObjectValue("escape", " escape \\ and \" for safety ");
						stream.writeObjectEnd();
					}),
					false
				},
				new Object[] {
					"{\"escape\":\" escape \\n for safety \"}",
					c((stream)->{
						stream.writeObjectStart();
						stream.writeObjectValue("escape", " escape \n for safety ");
						stream.writeObjectEnd();
					}),
					false
				},
		};
	}
	
	private ThrowingConsumer<OutputJsonStream, JsonStreamException> c(ThrowingConsumer<OutputJsonStream, JsonStreamException> createStream) {
		return createStream;
	}
	
}
