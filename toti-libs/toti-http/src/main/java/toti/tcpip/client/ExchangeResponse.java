package toti.tcpip.client;

import toti.tcpip.enums.StatusCode;

public class ExchangeResponse extends Exchange {

	private final Protocol protocol;
	private final StatusCode code;
	
	public ExchangeResponse(StatusCode code, Protocol protocol) {
		super();
		this.protocol = protocol;
		this.code = code;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public StatusCode getCode() {
		return code;
	}

	@Override
	public String getFirstLine() {
		return String.format("%s %s %s", protocol, code.getCode(), code.getDescription());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExchangeResponse other = (ExchangeResponse) obj;
		if (code != other.code) {
			return false;
		}
		if (protocol == null) {
			if (other.protocol != null) {
				return false;
			}
		} else if (!protocol.equals(other.protocol)) {
			return false;
		}
		return true;
	}
	
}
