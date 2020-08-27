package mvc;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import mvc.response.Response;
import mvc.templating.DirectoryTemplate;
import mvc.templating.ExceptionTemplate;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import socketCommunication.http.server.RestApiServerResponseFactory;
import socketCommunication.http.server.session.Session;

public class ResponseFactory implements RestApiServerResponseFactory {
	
	private final List<String> headers = new LinkedList<>();
	
	private final String resourcesDir;
	private final String charset;
	
	public ResponseFactory(String resourcesDir, String charset) {
		this.resourcesDir = resourcesDir;
		this.charset = charset;
	}
	
	public void addHeader(String header) {
		headers.add(header);
	}

	@Override
	public RestApiResponse accept(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			Properties params,
			Session session) throws IOException {
		// TODO mapping
		File file = new File(resourcesDir + url);
		if (file.isDirectory()) {
			return getDirResponse(file.listFiles(), url);
		}
		return Response.getFile(resourcesDir + url, charset).getResponse(headers, null, null);
	}
	
	private RestApiResponse getDirResponse(File[] files, String path) {
		List<String> h = new LinkedList<>(headers);
		h.add("Content-Type: text/html; charset=" + charset);
		return RestApiResponse.textResponse(StatusCode.OK, h, (bw)->{
			try {
				bw.write(new DirectoryTemplate(files, path).create(null, null, null));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	@Override
	public RestApiResponse onException(HttpMethod method, String url, String fullUrl, String protocol,
			Properties header, Properties params, Session session, Throwable t) throws IOException {
		List<String> h = new LinkedList<>(headers);
		h.add("Content-Type: text/html; charset=" + charset);
		return RestApiResponse.textResponse(StatusCode.OK, h, (bw)->{
			try {
				bw.write(new ExceptionTemplate(t).create(null, null, null));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

}
