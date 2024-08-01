package ji.files.text.basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ReadText {
	
	private final BufferedReader br;
	
	public ReadText(BufferedReader br) {
		this.br = br;
	}

	public BufferedReader getBufferedReader() {
		return br;
	}
	
	public void perLine(Consumer<String> consumer) throws IOException {
		String line = br.readLine();
		while (line != null) {
			consumer.accept(line);
			line = br.readLine();
		}
	}
	
	public String asString() throws IOException {
		StringBuilder result = new StringBuilder();
		String line = br.readLine();
		if(line != null){
			result.append(line);
			line = br.readLine();
		}
		while(line!=null){
			result.append("\n");
			result.append(line);
			line = br.readLine();
		}
		return result.toString();
	}
	
	public List<String> asList() throws IOException {
		List<String> result = new ArrayList<>();
		String line = br.readLine();
		while(line!=null){
			result.add(line);
			line = br.readLine();
		}
		return result;
	}
	
	public Collection<List<String>> asTable(final String delimeter) throws IOException {
		List<List<String>> result = new ArrayList<>();
		String line = br.readLine();
		while(line != null){
			String[] cels = line.split(delimeter);
			List<String> row = new ArrayList<>();
			for(int i = 0; i < cels.length; i++){
				row.add(cels[i]);
			}
			result.add(row);
			line = br.readLine();
		}
		return result;
	}
}
