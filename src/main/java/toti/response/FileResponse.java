package toti.response;

import java.io.BufferedOutputStream;
import java.io.IOException;

import ji.common.functions.InputStreamLoader;
import ji.common.structures.ThrowingConsumer;
import ji.files.text.Binary;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RestApiResponse;
import toti.ResponseHeaders;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import ji.translator.Translator;

public class FileResponse implements Response {

	private final String fileName;
	private final boolean download;
	private final StatusCode code;
	private final ThrowingConsumer<BufferedOutputStream, IOException> binaryContent;
	
	public FileResponse(StatusCode code, String fileName) {
		this(code, fileName, binaryContent(fileName), false);
	}
	
	public FileResponse(StatusCode code, String fileName, String source) {
		this(code, fileName, binaryContent(source), true);
	}
	
	public FileResponse(StatusCode code, String fileName, ThrowingConsumer<BufferedOutputStream, IOException> binaryContent) {
		this(code, fileName, binaryContent, true);
	}
	
	private FileResponse(StatusCode code, String fileName, ThrowingConsumer<BufferedOutputStream, IOException> binaryContent, boolean download) {
		this.code = code;
		this.fileName = fileName;
		this.binaryContent = binaryContent;
		this.download = download;
	}

	@Override
	public RestApiResponse getResponse(
			ResponseHeaders header,
			TemplateFactory templateFactory, 
			Translator translator, 
			Authorizator authorizator,
			Identity identity, MappedUrl current,
			String charset) {
		String head = getContentType(fileName, charset);
		if (head != null) {
			header.addHeader(head);
		}
		if (download) {
			//	header.addHeader("Content-Disposition: attachment; filename=\"" + fileName + "\"");
			header.addHeader("Content-Disposition: inline; filename=\"" + fileName + "\"");
		}
		return RestApiResponse.binaryResponse(code, header.getHeaders(), binaryContent);
	}
	
	private static ThrowingConsumer<BufferedOutputStream, IOException> binaryContent(String name) {
		return (bout)->{
			Binary.get().read((bin)->{
				byte[] b = new byte[2048];
				int len = 0;
				while((len = bin.read(b)) != -1) {
					bout.write(b, 0, len);
				}
			}, InputStreamLoader.createInputStream(FileResponse.class, name));
		};
	}

}
