package toti.answers.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import ji.common.functions.InputStreamLoader;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.Headers;
import toti.security.Identity;

public class FileResponse implements Response {

	private final String fileName;
	private final boolean download;
	private final StatusCode code;
	private final byte[] binaryContent;
	
	public FileResponse(StatusCode code, String fileName) {
		this(code, fileName, binaryContent(fileName), false);
	}
	
	public FileResponse(StatusCode code, String fileName, String source) {
		this(code, fileName, binaryContent(source), true);
	}
	
	public FileResponse(StatusCode code, String fileName, byte[] binaryContent) {
		this(code, fileName, binaryContent, true);
	}
	
	private FileResponse(StatusCode code, String fileName, byte[] binaryContent, boolean download) {
		this.code = code;
		this.fileName = fileName;
		this.binaryContent = binaryContent;
		this.download = download;
	}

	@Override
	public ji.socketCommunication.http.structures.Response getResponse(
			Protocol protocol,
			Headers responseHeader,
			Identity identity,
			ResponseContainer container,
			String charset) {
		setContentType(fileName, charset, responseHeader);
		if (download) {
			//	header.addHeader("Content-Disposition: attachment; filename=\"" + fileName + "\"");
			responseHeader.addHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
		}
		ji.socketCommunication.http.structures.Response response = new  ji.socketCommunication.http.structures.Response(code, protocol);
		response.setHeaders(responseHeader.getHeaders());
		response.setBody(binaryContent);
		return response;
	}
	
	private static byte[] binaryContent(String name) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try (InputStream is = InputStreamLoader.createInputStream(FileResponse.class, name)) {
			byte[] b = new byte[1024];
			int readed;
			while((readed = is.read(b, 0, b.length)) != -1) {
				bos.write(b, 0, readed);
			}
		} catch (IOException e) {
			throw new ResponseException(e);
		}
		return bos.toByteArray();
	}

}
