package toti;

import java.util.HashMap;
import java.util.Map;

import ji.socketCommunication.http.RestApiServer;
import toti.answers.Answer;

public class ServerConsumer {

	private final RestApiServer server;
	private final Map<String, String[]> aliases = new HashMap<>();
	
	public ServerConsumer(RestApiServer server) {
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
