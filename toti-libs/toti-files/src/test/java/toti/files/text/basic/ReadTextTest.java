package toti.files.text.basic;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class ReadTextTest {
	
	@Test
	public void testReadPerLineWorks() throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader br = mock(BufferedReader.class);
		when(br.readLine())
			.thenReturn("This")
			.thenReturn("a")
			.thenReturn("eee")
			.thenReturn("eeeeeee")
			.thenReturn(" is ")
			.thenReturn("")
			.thenReturn("Java")
			.thenReturn("1")
			.thenReturn(null);
		new ReadText(br).perLine((line)->{
			if (line.length() == 4) {
				builder.append(line);
			}
		});
		assertEquals("This is Java", builder.toString());
	}
	
	@Test
	public void testReadAsStringWorks() throws IOException {
		BufferedReader br = mock(BufferedReader.class);
		when(br.readLine())
			.thenReturn("This is text of success reading of file.")
			.thenReturn("File has two rows;")
			.thenReturn(null);
		assertEquals(
			"This is text of success reading of file.\nFile has two rows;",
			new ReadText(br).asString()
		);
	}
	
	@Test
	public void testReadAsListWorks() throws IOException {
		BufferedReader br = mock(BufferedReader.class);
		when(br.readLine())
			.thenReturn("First line")
			.thenReturn("Second line")
			.thenReturn("Third line")
			.thenReturn(null);
		assertEquals(
			Arrays.asList("First line", "Second line", "Third line"),
			new ReadText(br).asList()
		);
	}
	
	@Test
	public void testReadAsTableWorks() throws IOException {
		BufferedReader br = mock(BufferedReader.class);
		when(br.readLine())
			.thenReturn("key-message")
			.thenReturn("second-mess")
			.thenReturn("grid-here")
			.thenReturn(null);
		assertEquals(
			Arrays.asList(
				Arrays.asList("key", "message"),
				Arrays.asList("second", "mess"),
				Arrays.asList("grid", "here")
			),
			new ReadText(br).asTable("-")
		);
	}
}
