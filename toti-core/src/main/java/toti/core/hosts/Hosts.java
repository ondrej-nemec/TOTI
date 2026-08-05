package toti.core.hosts;

import java.util.HashMap;
import java.util.Map;

import toti.core.answers.Answer;

public class Hosts {

	private final Map<String, Host> hosts = new HashMap<>();
	private final Host defHost;
	
	public Hosts() {
		this.defHost = new Host();
	}

	public void add(Answer answer, String hostname, String path) {
		if (hostname == null) {
			defHost.add(path, answer);
			return;
		}
		if (!hosts.containsKey(hostname)) {
			hosts.put(hostname, new Host());
		}
		hosts.get(hostname).add(path, answer);
	}

	public void remove(String hostname, String path) {
		if (hostname == null) {
			defHost.remove(path);
			return;
		}
		Host host = hosts.get(hostname);
		if (host == null) {
			return;
		}
		host.remove(path);
	}

	public AnswerWrapper get(String host, String path) {
		if (host != null && host.length() > 0 &&  hosts.containsKey(host)) {
			return hosts.get(host).get(path);
		}
		return defHost.get(path);
	}

}
