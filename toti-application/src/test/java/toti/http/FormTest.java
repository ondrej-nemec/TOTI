package toti.http;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class FormTest {

	@Test
	@Parameters(method="dataRead")
	public void testRead(String file, int length, String type) throws IOException {
		Form form = new Form(new Payload(), new StreamReader(null));
		try (BufferedInputStream is = new BufferedInputStream(getClass().getResourceAsStream("/parser/form/" + file))) {
			RequestParameters params = form.read(type, length, is);
			assertEquals(2, params.size());
			assertEquals("admin", params.getString("username"));
			assertEquals("password", params.getString("password"));
		}
	}
	
	public Object[] dataRead() {
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
