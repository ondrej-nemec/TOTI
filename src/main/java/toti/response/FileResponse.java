package toti.response;

import core.text.Binary;
import core.text.InputStreamLoader;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.templating.TemplateFactory;
import translator.Translator;

public class FileResponse implements Response {

	private final String fileName;
	private final StatusCode code;
	
	public FileResponse(StatusCode code, String fileName) {
		this.fileName = fileName;
		this.code = code;
	}

	@Override
	public void addParam(String name, Object value) {}
	
	@Override
	public RestApiResponse getResponse(ResponseHeaders header, TemplateFactory templateFactory, Translator translator, String charset) {
		String head = getContentType(fileName, charset);
		if (head != null) {
			header.addHeader(head);
		}
		return RestApiResponse.binaryResponse(code, header.getHeaders(), (bout)->{
			Binary.read((bin)->{
				byte[] b = new byte[2048];
				int len = 0;
				while((len = bin.read(b)) != -1) {
					bout.write(b, 0, len);
				}
			}, InputStreamLoader.createInputStream(getClass(), fileName));
		});
	}

}
