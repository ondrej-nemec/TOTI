package toti.answers.response;

import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.files.json.JsonWritter;
import toti.http.enums.StatusCode;

public class JsonResponse implements Response {
	
	private final Object json;
	private final StatusCode code;
	private final Headers headers;

	public JsonResponse(StatusCode code, Headers headers, Object json) {
		this.json = json;
		this.code = code;
		this.headers = headers;
	}

	@Override
	public FinalResponse prepare(
			Headers headers,
			Identity identity,
			ResponseContainer container,
			String charset) {
		headers.addHeader("Content-Type", "application/json; charset=" + charset);
		headers.setHeaders(this.headers.getHeaders());
		return new FinalResponse(code, headers, createResponse().getBytes());
	}
	
	private String createResponse() {
		try {
			JsonWritter writter = new JsonWritter();
			return writter.write(json);
		} catch (Exception e) {
			throw new ResponseException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((json == null) ? 0 : json.hashCode());
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
		JsonResponse other = (JsonResponse) obj;
		if (code != other.code) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (json == null) {
			if (other.json != null) {
				return false;
			}
		} else if (!json.equals(other.json)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "JsonResponse [json=" + json + ", code=" + code + ", headers=" + headers + "]";
	}
	
}
