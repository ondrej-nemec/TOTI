package toti.core.hosts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toti.core.answers.Answer;

public class Hosts {

	private final Map<String, Host> hosts = new HashMap<>();
	private final Host defHost;
	
	public Hosts() {
		this.defHost = new Host();
	}

	public void add(Answer answer, List<String> hostnames, List<String> paths) {
		if (hostnames == null) {
			defHost.add(paths, answer);
			return;
		}
		hostnames.forEach(hostname->{
			if (!hosts.containsKey(hostname)) {
				hosts.put(hostname, new Host());
			}
			hosts.get(hostname).add(paths, answer);
		});
	}

	public void remove(List<String> hostnames, List<String> paths) {
		if (hostnames == null) {
			defHost.remove(paths);
			return;
		}
		hostnames.forEach(hostname->{
			Host host = hosts.get(hostname);
			if (host == null) {
				return;
			}
			host.remove(paths);
		});
	}

	public AnswerWrapper get(String host, String path) {
		if (host != null && host.length() > 0 &&  hosts.containsKey(host)) {
			return hosts.get(host).get(path);
		}
		return defHost.get(path);
	}

}
