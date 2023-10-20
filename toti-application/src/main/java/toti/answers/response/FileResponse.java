package toti.answers.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import ji.common.functions.InputStreamLoader;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import toti.answers.Headers;
import toti.answers.request.Identity;

public class FileResponse implements Response {

	private final String fileName;
	private final boolean download;
	private final StatusCode code;
	private final byte[] binaryContent;
	private final Headers headers;
	
	public FileResponse(StatusCode code, Headers headers, String fileName) {
		this(code, headers, fileName, binaryContent(fileName), false);
	}
	
	public FileResponse(StatusCode code, Headers headers, String fileName, String source) {
		this(code, headers, fileName, binaryContent(source), true);
	}
	
	public FileResponse(StatusCode code, Headers headers, String fileName, byte[] binaryContent) {
		this(code, headers, fileName, binaryContent, true);
	}
	
	private FileResponse(StatusCode code, Headers headers, String fileName, byte[] binaryContent, boolean download) {
		this.code = code;
		this.fileName = fileName;
		this.binaryContent = binaryContent;
		this.download = download;
		this.headers = headers;
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
		response.setHeaders(this.headers.getHeaders());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(binaryContent);
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (download ? 1231 : 1237);
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
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
		FileResponse other = (FileResponse) obj;
		if (!Arrays.equals(binaryContent, other.binaryContent)) {
			return false;
		}
		if (code != other.code) {
			return false;
		}
		if (download != other.download) {
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
		return true;
	}

	@Override
	public String toString() {
		return "FileResponse [fileName=" + fileName + ", download=" + download + ", code=" + code + ", binaryContent="
				+ Arrays.toString(binaryContent) + ", headers=" + headers + "]";
	}

}
