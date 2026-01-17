package toti.lib.tcpip.client;

public class Protocol {

	public static final Protocol HTTP_1_1 = new Protocol("HTTP/1.1");
	public static final Protocol HTTP_2 = new Protocol("HTTP/2");
	
	public static Protocol valueOf(String protocol) {
		switch (protocol) {
			case "HTTP/1.1": return HTTP_1_1;
			case "HTTP/2": return HTTP_2;
			default:
				throw new RuntimeException("Unsupported protocol '" + protocol + "'");
		}
	}
	
	public static String getAll() {
		return HTTP_1_1.toString() /* + ", " + HTTP_2.toString() */;
	}
	
	private final String protocol;
	
	private Protocol(String protocol) {
		this.protocol = protocol;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof Protocol)) {
			return false;
		}
		return protocol.equals(((Protocol)obj).protocol);
	}
	
	@Override
	public String toString() {
		return protocol;
	}
	
}
