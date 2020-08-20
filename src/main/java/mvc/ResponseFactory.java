package mvc;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import mvc.templating.ExceptionTemplate;
import mvc.templating.Template;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import socketCommunication.http.server.RestApiServerResponseFactory;

public class ResponseFactory implements RestApiServerResponseFactory {
	
	private final List<String> headers = new LinkedList<>();

	@Override
	public RestApiResponse accept(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			Properties params) throws IOException {
		try {
			
		} catch (Throwable t) {
			return RestApiResponse.textResponse(StatusCode.OK, headers, (bw)->{
				try {
					bw.write(new ExceptionTemplate(t).create(null, null, null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
		}
		return null;
	}

}
