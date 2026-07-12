package toti.lib.tcpip.parsers;

import java.io.BufferedInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import toti.lib.tcpip.structures.RequestParameters;

public class FormTest {

	@ParameterizedTest
	@MethodSource("dataRead")
	public void testRead(String file, int length, String type) throws IOException {
		Form form = new Form(new Payload(), new StreamReader(null));
		try (BufferedInputStream is = new BufferedInputStream(getClass().getResourceAsStream("/parser/form/" + file))) {
			RequestParameters params = form.read(type, length, is);
			assertEquals(2, params.size());
			assertEquals("admin", params.getString("username"));
			assertEquals("password", params.getString("password"));
		}
	}
	
	public static Object[] dataRead() {
		return new Object[] {
			new Object[] {
				"postman.txt", 277,
				"multipart/form-data; boundary=--------------------------874535426331349754764480"
			},
			new Object[] {
				"browser.txt", 285,
				"multipart/form-data; boundary=--------------------------874535426331349754764480"
			},
			new Object[] {
				"nginx.txt", 245,
				"multipart/form-data; boundary=----WebKitFormBoundaryBcAD30uSggyegBp6"
			}
		};
	}
	
}
