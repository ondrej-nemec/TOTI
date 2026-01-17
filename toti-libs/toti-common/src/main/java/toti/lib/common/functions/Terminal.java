package toti.lib.common.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class Terminal {
	
	// windows command pre: cmd /c
	public void run(String[] command) throws IOException {
		Runtime.getRuntime().exec(command);
	}
	
	// windows command pre: cmd /c
	public int run(Consumer<String> stdOut, Consumer<String> stdErr, String[] command) throws IOException, InterruptedException {
		Process pr = Runtime.getRuntime().exec(command);
		pr.waitFor();
		
		readsAndApplyConsumer(pr.getInputStream(), stdOut);
		readsAndApplyConsumer(pr.getErrorStream(), stdErr);
		
		int exitValue = pr.exitValue();
		return exitValue;
	}
	
	private void readsAndApplyConsumer(final InputStream stream, final Consumer<String> consumer) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			String line = br.readLine();
			while (line != null) {
				consumer.accept(line);
				line = br.readLine();
			}
		}
	}
}
