package toti.answers.response;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import toti.answers.Headers;
import toti.answers.request.Identity;
import toti.http.enums.StatusCode;

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
	public FinalResponse prepare(
			Headers headers, 
			Identity identity, 
			ResponseContainer container,
			String charset) {
		String nonce = RandomStringUtils.randomAlphanumeric(50);
		params.put("nonce", nonce);
		params.put("totiIdentity", identity);
		
		Headers resHeaders = new Headers(new HashMap<>());
		resHeaders.setHeaders(this.headers.getHeaders());
		headers.getHeaders().forEach((n, l)->{
			l.forEach(v->{
				if (v != null && v instanceof String) {
					resHeaders.addHeader(n, v.toString().replace("{nonce}", nonce));
				} else {
					resHeaders.addHeader(n, v);
				}
			});
		});
		setContentType(fileName, charset, resHeaders);
		
		return new FinalResponse(code, resHeaders, createResponse(container).getBytes());
	}
	
	public String createResponse(ResponseContainer container) {
		try {
			String module = null;
			if (container.getCurrent() != null) {
				module = container.getCurrent().getModuleName();
			}
			return container.getTemplateExtension().getTemplate(module, fileName, params, container);
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
