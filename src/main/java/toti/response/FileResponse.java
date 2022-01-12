package toti.response;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import ji.common.exceptions.LogicException;
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
	private final StatusCode code;
	private ByteArrayOutputStream fileContent = null;
	private String fromFile = null;
	
	public FileResponse(StatusCode code, String fileName) {
		this.fileName = fileName;
		this.code = code;
	}

	@Override
	public void addParam(String name, Object value) {}
	
	public FileResponse setFromFile(String fromFile) {
		if (fileContent != null) {
			throw new LogicException("Cannot create response from file and generate at once.");
		}
		this.fromFile = fromFile;
		return this;
	}
	
	public FileResponse addContent(Object data) {
		if (data == null) {
			return this;
		}
		return addContent(data.toString().getBytes());
	}
	
	public FileResponse addContent(byte[] data) {
		if (fromFile != null) {
			throw new LogicException("Cannot create response from file and generate at once.");
		}
		if (fileContent == null) {
			fileContent = new ByteArrayOutputStream();
		}
		try {
			fileContent.write(data);
		} catch (IOException e) {
			// ignored
			e.printStackTrace();
		}
		return this;
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
	//	header.addHeader("Content-Disposition: attachment; filename=\"" + fileName + "\"");
		if (fileContent != null) {
			header.addHeader("Content-Disposition: inline; filename=\"" + fileName + "\"");
			return RestApiResponse.binaryResponse(code, header.getHeaders(), (bout)->{
				bout.write(fileContent.toString().getBytes());
			});
		}
		if (fromFile != null) {
			header.addHeader("Content-Disposition: inline; filename=\"" + fileName + "\"");
			return RestApiResponse.binaryResponse(code, header.getHeaders(), binaryContent(fromFile));
		}
		return RestApiResponse.binaryResponse(code, header.getHeaders(), binaryContent(fileName));
	}
	
	private ThrowingConsumer<BufferedOutputStream, IOException> binaryContent(String name) {
		return (bout)->{
			Binary.get().read((bin)->{
				byte[] b = new byte[2048];
				int len = 0;
				while((len = bin.read(b)) != -1) {
					bout.write(b, 0, len);
				}
			}, InputStreamLoader.createInputStream(getClass(), name));
		};
	}

}
