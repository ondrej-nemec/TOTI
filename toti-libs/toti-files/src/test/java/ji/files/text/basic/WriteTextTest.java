package ji.files.text.basic;

import static org.mockito.Mockito.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class WriteTextTest {

	@Test
	public void testWriteStringWorks() throws IOException {
		String string = "This is test data\nNew line\tafter tab";
		BufferedWriter bw = mock(BufferedWriter.class);
		new WriteText(bw).write(string);
		
		verify(bw, times(1)).write(string);
		verify(bw, times(1)).newLine();
		verify(bw, times(1)).flush();
		verifyNoMoreInteractions(bw);
	}
	
	@Test
	public void testWriteLinesWorks() throws IOException {
		List<String> list = Arrays.asList("First line","Second line","Third line");
		BufferedWriter bw = mock(BufferedWriter.class);
		new WriteText(bw).write(list);
		
		for(String line : list){
			verify(bw).write(line);
		}
		verify(bw, times(3)).newLine();
		verify(bw, times(1)).flush();
		verifyNoMoreInteractions(bw);
	}
	
	@Test
	public void testWriteTableWorks() throws IOException {
		List<List<String>> table = Arrays.asList(
			Arrays.asList("a-a", "a-b", "a-c"),
			Arrays.asList("b-a", "b-b", "b-c"),
			Arrays.asList("c-a", "c-b", "c-c")
		);
		BufferedWriter bw = mock(BufferedWriter.class);
		new WriteText(bw).write(table, ";");
		
		for(List<String> line : table){
			verify(bw).write(line.get(0) + ";" + line.get(1) + ";" + line.get(2));
		}
		verify(bw, times(3)).newLine();
		verify(bw, times(1)).flush();
		verifyNoMoreInteractions(bw);
	}
	
}
