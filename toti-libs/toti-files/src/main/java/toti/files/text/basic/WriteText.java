package toti.files.text.basic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import toti.lib.common.functions.Implode;

public class WriteText {
	
	private final BufferedWriter bw;
	
	public WriteText(BufferedWriter bw) {
		this.bw = bw;
	}
	
	public BufferedWriter getBufferedWriter() {
		return bw;
	}
	
	public void write(final String string) throws IOException {
		bw.write(string);
		bw.newLine();
		bw.flush();
	}
	
	public void write(final List<String> list) throws IOException {
		for (String line : list) {
			bw.write(line);
			bw.newLine();
		}
		bw.flush();
	}
	
	public void write(final List<List<String>> table, final String delimeter) throws IOException {
		for(List<String> line : table) {
			bw.write(Implode.implode(delimeter, line));			
			bw.newLine();
		}
		bw.flush();
	}
}
