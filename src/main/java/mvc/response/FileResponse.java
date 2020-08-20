package mvc.response;

import java.io.FileInputStream;
import java.util.List;

import core.text.Binary;
import mvc.templating.TemplateFactory;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import translator.Translator;

public class FileResponse implements Response {

	private final String fileName;
	private final StatusCode code;

	public FileResponse(StatusCode code, String fileName) {
		this.fileName = fileName;
		this.code = code;
	}
	
	@Override
	public RestApiResponse getResponse(List<String> header, TemplateFactory templateFactory, Translator translator) {
		return RestApiResponse.binaryResponse(code, header, (bout)->{
			Binary.read((bin)->{
				byte[] b = new byte[2048];
				int len;
				while((len = bin.read(b)) != 0) {
					bout.write(b, 0, len);
				}
			}, new FileInputStream(fileName)); // TODO add www dir ???
		});
	}

}
