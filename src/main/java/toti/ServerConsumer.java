package toti;

import java.util.HashMap;
import java.util.Map;

import ji.socketCommunication.http.RestApiServer;

public class ServerConsumer {

	private final RestApiServer server;
	private final Map<String, String[]> aliases = new HashMap<>();
	
	public ServerConsumer(RestApiServer server) {
		this.server = server;
	}
	
	public void addApplication(ResponseFactory factory, String hostname, String...alias) {
		server.addApplication(factory, hostname, alias);
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
