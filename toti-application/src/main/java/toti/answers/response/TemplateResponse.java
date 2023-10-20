package toti.answers.response;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.templating.Template;

public class TemplateResponse implements Response {
	
	private final Map<String, Object> params;
	private final StatusCode code;
	private final String fileName;
	private final Headers headers;

	public TemplateResponse(StatusCode code, Headers headers, String fileName, Map<String, Object> params) {
		this.params = params;
		this.code = code;
		this.fileName = fileName;
		this.headers = headers;
	}

	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers header, 
			Identity identity, 
			ResponseContainer container,
			String charset) {
		String nonce = RandomStringUtils.randomAlphanumeric(50);
		params.put("nonce", nonce);
		params.put("totiIdentity", identity);
		
		/* Headers resHeaders = new Headers(new HashMap<>());
		header.getHeaders().forEach((n, l)->{
			l.forEach(v->{
				if (v != null && v instanceof String) {
					resHeaders.addHeader(n, v.toString().replace("{nonce}", nonce));
				} else {
					resHeaders.addHeader(n, v);
				}
			});
		});
		setContentType(fileName, charset, resHeaders);*/
		ji.socketCommunication.http.structures.Response response = new ji.socketCommunication.http.structures.Response(code, protocol);
		
		// response.setHeaders(resHeaders.getHeaders());
		header.getHeaders().forEach((n, l)->{
			l.forEach(v->{
				if (v != null && v instanceof String) {
					response.addHeader(n, v.toString().replace("{nonce}", nonce));
				} else {
					response.addHeader(n, v);
				}
			});
		});
		response.addHeader("Content-Type", getContentType(fileName, charset));
		response.setHeaders(this.headers.getHeaders());
		
		
		response.setBody(createResponse(container).getBytes());
		return response;
	}
	
	public String createResponse(ResponseContainer container) {
		try {
			Template template = container.getTemplateFactory().getTemplate(fileName);
			return template.create(container.getTemplateFactory(), params, container);
		} catch (Exception e) {
			throw new ResponseException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
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
		TemplateResponse other = (TemplateResponse) obj;
		if (code != other.code) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (headers == null) {
			if (other.headers != null) {
				return false;
			}
		} else if (!headers.equals(other.headers)) {
			return false;
		}
		if (params == null) {
			if (other.params != null) {
				return false;
			}
		} else if (!params.equals(other.params)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TemplateResponse [params=" + params + ", code=" + code + ", fileName=" + fileName + ", headers="
				+ headers + "]";
	}
}
