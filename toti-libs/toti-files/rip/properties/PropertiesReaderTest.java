package ji.properties;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesReaderTest {

	public void testReadProperties() {
		Map<String, Object> expected = new HashMap<>();
		expected.put("website", "https://en.wikipedia.org/");
		expected.put("language", "English");
		expected.put("topic", ".properties files");
		expected.put("hello", "hello");
		expected.put("duplicateKey", "second");
		expected.put("delimiterCharacters:= ", "This is the value for the key \"delimiterCharacters:= \"");
		expected.put("multiline", "This line continues");
		expected.put("path", "c:\\wiki\\templates");
		expected.put("valueWithEscapes", "This is a newline\n and a carriage return\r and a tab\t.");
		expected.put("empty", "");
		expected.put("encodedHelloInJapanese", "こんにちは");
		expected.put("evenKey", "This is on one line\\\\");
		expected.put("welcome", "Welcome to Wikipedia!");
		expected.put("oddKey", "This is line one and\\# This is line two");
		expected.put("helloInJapanese", "こんにちは");
		expected.put("more-escape", "escapemore");
		
		PropertiesReader reader = new PropertiesReader();		
		assertEquals(expected, reader.read(getClass().getResourceAsStream("/ji/properties/test.properties")));
	}
	
	public static void main(String[] args) {
		Properties prop = new Properties();
		try {
			prop.load(new InputStreamReader(PropertiesReaderTest.class.getResourceAsStream(
					"/ji/properties/test.properties"), "utf-8"));
			prop.forEach((k,v)->{
				System.out.println(k + ": " + v);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
