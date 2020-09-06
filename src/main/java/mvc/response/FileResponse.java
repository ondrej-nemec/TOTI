package mvc.response;

import java.io.FileInputStream;
import java.util.List;

import common.FileExtension;
import core.text.Binary;
import mvc.ResponseHeaders;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class FileResponse implements Response {

	private final String fileName;
	private final StatusCode code;
	/*private final String charset;*/
	
	public FileResponse(StatusCode code, String fileName/*, String charset*/) {
		this.fileName = fileName;
		this.code = code;
	//	this.charset = charset;
	}

	@Override
	public void addParam(String name, Object value) {}
	
	@Override
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset) {
		List<String> h = header.getHeaders();
		String head = getContentType(fileName, charset);
		if (head != null) {
			h.add(head);
		}
		return RestApiResponse.binaryResponse(code, h, (bout)->{
			Binary.read((bin)->{
				byte[] b = new byte[2048];
				int len = 0;
				while((len = bin.read(b)) != -1) {
					bout.write(b, 0, len);
				}
			}, new FileInputStream(fileName)); // TODO add www dir ???
		});
	}
	
	private String getContentType(String fileName, String charset) {
		String ext = new FileExtension(fileName).getExtension();
		// https://stackoverflow.com/a/48704300
		switch (ext) {
			case "html": return "Content-Type: text/html; charset=" + charset;
			case "css": return "Content-Type: text/css; charset=" + charset;
			case "js": return "Content-Type: text/javascript; charset=" + charset;
			case "json": return "Content-Type: application/json; charset=" + charset;
			case "ico": return "Content-Type: image/ico";
			case "jpeg":
			case "jpg": return "Content-Type: image/jpeg";
			case "png": return "Content-Type: image/png";
			case "giff": return "Content-Type: image/giff";
			case "gif": return "Content-Type: image/gif";
			case "txt": return "Content-Type: text/plain; charset=" + charset;
			default: return null;
		}
	}

}
