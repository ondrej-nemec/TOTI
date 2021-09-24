package toti.annotations;

import socketCommunication.http.HttpMethod;

public class UrlPart {

	private final Object url;
	private final boolean isRegex;
	
	public UrlPart(String url, boolean isRegex) {
		this.url = url;
		this.isRegex = isRegex;
	}
	
	public UrlPart(HttpMethod method) {
		this.url = method;
		this.isRegex = false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isRegex ? 1231 : 1237);
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}			
		if (obj == null) {
			return false;
		}			
		if (getClass() != obj.getClass()) {
			return false;
		}
		UrlPart other = (UrlPart) obj;
		if (url == null && other.url != null) {
			return false;
		}
		if (isRegex) {
			/*Matcher m = Pattern.compile(String.format("(%s)", url)).matcher(url);	
			return m.find();*/
			return true;
		}
		return url.equals(other.url);
	}

	@Override
	public String toString() {
	//	return "UrlPart [url=" + url + ", isRegex=" + isRegex + "]";
		return url.toString();
	}
}
