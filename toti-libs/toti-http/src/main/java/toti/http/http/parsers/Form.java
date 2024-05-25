package toti.http.http.parsers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toti.http.http.structures.RequestParameters;
import toti.http.http.structures.UploadedFile;

public class Form {
	
	private final Payload payload;
	private final StreamReader stream;
	
	public Form(Payload payload, StreamReader stream) {
		this.payload = payload;
		this.stream = stream;
	}

	public ByteArrayOutputStream write(String boundary, RequestParameters map) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		map.forEach((name, value)->{
			payload.writeItem((n, v)->{
				try {
					byte[] bytes = String.format("--%s", boundary).getBytes();
					byte[] newLine = stream.getNewLine();

					stream.write(bytes, bos);
					stream.write(newLine, bos);
					
					if (v instanceof UploadedFile) {
						UploadedFile file = UploadedFile.class.cast(v);
						bytes = String.format(
							"Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"",
							n, file.getFileName()
						).getBytes();
						stream.write(bytes, bos);
						stream.write(newLine, bos);
						
						bytes = String.format("Content-Type: %s", file.getContentType()).getBytes();
						stream.write(bytes, bos);
						stream.write(newLine, bos);
						
						stream.write(newLine, bos);
						
						stream.write(file.getContent(), bos);
					} else {
						bytes = String.format("Content-Disposition: form-data; name=\"%s\"", n).getBytes();
						stream.write(bytes, bos);
						stream.write(newLine, bos);

						stream.write(newLine, bos);

						bytes = v.toString().getBytes();
						stream.write(bytes, bos);
					}
					
					stream.write(newLine, bos);
				} catch (IOException e) {
					throw new ExceptionWrapper(e);
				}
			}, name, value == null ? null : value.getValue());
		});
		byte[] bytes = String.format("--%s--", boundary).getBytes();
		stream.write(bytes, bos);
		return bos;
	}
	
	/******************************/

	public RequestParameters read(String type, int contentLength, InputStream bis) throws IOException {
		String boundary = "--" + type.trim().substring("multipart/form-data;".length()).trim().substring("boundary=".length()).trim();
		// String boundary = "--" + type.toLowerCase().replace("multipart/form-data;", "").trim().replace("boundary=", "").trim();

		int readed = 0;
		RequestParameters data = new RequestParameters();
		
		String elementName = null;
		String elementValue = null;
		String filename = null;
		String contentType = null;
	//	String bom = null;
		boolean isValue = false;
		ByteArrayOutputStream fileContent = null;
		Boolean useRN = null;

		while(readed < contentLength) {
			byte[] readedData = stream.readData(contentLength, bis, readed, actual->actual == '\n', false);
			readed += readedData.length;
			String requestLine = new String(readedData);
			if (useRN == null) {
				useRN = requestLine.endsWith("\r\n");
			}
			requestLine = requestLine.replace("\r", "").replace("\n", "");

			if (requestLine.startsWith(boundary)) { // start element
				if (elementName != null && fileContent != null) {
					String bom = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(
						fileContent.toByteArray()
					));
					// URLConnection.guessContentTypeFromName(filename);
					data.put(elementName, new UploadedFile(
						filename, contentType, bom,
						Arrays.copyOfRange(fileContent.toByteArray(), 0, fileContent.size() - (useRN ? 2 : 1)))
					);
				} else if (elementName != null && elementValue != null) {
					payload.readItem(
						(name, value)->data.put(name, value),
						name->data.get(name),
						elementName, 
						elementValue
					);
				}
				elementName = null;
				filename = null;
				contentType = null;
				isValue = false;
				fileContent = null;
				elementValue = null;
			} else if (requestLine.toLowerCase().startsWith("content-disposition: form-data; name=") && !isValue) {
				Matcher m = Pattern.compile(" name=\\\"(([^\\\"])+)\\\"(; (filename=\\\"(([^\\\"])+)\\\")?)?")
						.matcher(requestLine);
				m.find();
				elementName = m.group(1);
				filename = m.group(5);
				if (filename != null && (filename.contains("..") || filename.contains("/") || filename.contains("\\"))) {
					throw new IOException("Filename is probably corrupted " + filename);
				}
			} else if (requestLine.toLowerCase().startsWith("content-type: ") && !isValue) {
				contentType = requestLine.substring("Content-Type: ".length());
				//if (allowedFileTypes.isPresent() && !allowedFileTypes.get().contains(contentType)) {
				//	throw new IOException("Uploading file content type is not allowed " + contentType);
				//}
			//} else if (requestLine.equals(boundary + "--")) { // close
			} else {
				isValue = true;
				if (filename == null && requestLine.isEmpty()) {
				} else if (filename == null) {
					if (elementValue == null) {
						elementValue = "";
					} else {
						elementValue += "\n";
					}
					elementValue += requestLine;
				} else if (fileContent == null && requestLine.isEmpty()) {
					// ignore
				} else {
					if (fileContent == null) {
						fileContent = new ByteArrayOutputStream();
						//bom = requestLine.replace("‰", "");
						/*if (requestLine.startsWith("‰")) {
							contentType = requestLine.replace("‰", "");
						}*/
						/*byte[] lineBytes = requestLine.getBytes();
						// UTF-8:    0xEF 0xBB 0xBF
						// UTF-16 1: 0xFE 0xFF
						// UTF-16 2: 0xFF 0xFE
	                    if (lineBytes.length > 3) {
	                    	System.err.println(lineBytes[0] + " " + ((byte)lineBytes[0]) + " " + (byte)0xEF);
	                    	System.err.println(lineBytes[1] + " " + ((byte)lineBytes[1]) + " " + (byte)0xBB);
	                    	System.err.println(lineBytes[2] + " " + ((byte)lineBytes[2]) + " " + (byte)0xBF);
	                        //if (lineBytes[0] == (byte)0xEF && lineBytes[1] == (byte)0xBB && lineBytes[2] == (byte)0xBF) {
	                             byte[] n = Arrays.copyOfRange(lineBytes, 3, lineBytes.length);
	                             bom = new String(Arrays.copyOfRange(lineBytes, 3, lineBytes.length));
	                        //}
	                    }*/
					}
					//if (readedData.length + fileContent.size() > maxUploadFileSize) {
					//	throw new IOException("Maximal upload file size overflow " + maxUploadFileSize);
					//}
					fileContent.write(readedData);
					fileContent.flush();
				}
			}
		}
		return data;
	}
}
