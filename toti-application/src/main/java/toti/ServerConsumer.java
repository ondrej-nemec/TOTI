package toti;

import java.util.HashMap;
import java.util.Map;

import toti.answers.Answer;
import toti.http.http.HttpServer;

public class ServerConsumer {

	private final HttpServer server;
	private final Map<String, String[]> aliases = new HashMap<>();
	
	public ServerConsumer(HttpServer server) {
		this.server = server;
	}
	
	public void addApplication(Answer answer, String hostname, String...alias) {
		server.addApplication(answer, hostname, alias);
		aliases.put(hostname, alias);
	}
	
	public void removeApplication(String hostname) {
		server.removeApplication(hostname);
		if (aliases.get(hostname) != null) {
			for (String alias : aliases.get(hostname)) {
				server.removeApplication(alias);
			}
		}
	}
	
}
