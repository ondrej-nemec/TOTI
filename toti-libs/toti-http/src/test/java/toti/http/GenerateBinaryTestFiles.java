package toti.http;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import ji.common.structures.ThrowingConsumer;

public class GenerateBinaryTestFiles {

	public static void main(String[] args) {
	
		generateBinary("binary/body-binary-req.txt", "POST /some/url HTTP/1.1");
		generateBinary("binary/body-binary-res.txt", "HTTP/1.1 200 OK");
		
		generateFormData("binary/body-multipart-with-file-req.txt", "POST /some/url HTTP/1.1");
		generateFormData("binary/body-multipart-with-file-res.txt", "HTTP/1.1 200 OK");
	}
	
	protected static void generateFormData(String filename, String firstLine) {
		generate(
			filename,
			firstLine,
			l->Arrays.asList(
				"content-length: " + l,
				"content-type: multipart/form-data; boundary=item-separator",
				"some-header: my header value"
			), 
			(os)->{
				os.write("--item-separator\r\n".getBytes());
				os.write("Content-Disposition: form-data; name=\"another\"\r\n".getBytes());
				os.write("\r\n".getBytes());
				os.write("value\r\n".getBytes());
				os.write("--item-separator\r\n".getBytes());
				os.write("Content-Disposition: form-data; name=\"fileinput\"; filename=\"filename.xyz\"\r\n".getBytes());
				os.write("Content-Type: IMG\r\n".getBytes());
				os.write("\r\n".getBytes());
				writeBinaryFile(os);
				os.write("\r\n".getBytes());
				os.write("--item-separator--".getBytes());
			}
		);
	}
	
	protected static void generateBinary(String filename, String firstLine) {
		generate(
			filename,
			firstLine,
			l->Arrays.asList(
				"content-length: " + l,
				"content-type: image/x-icon",
				"some-header: my header value"
			), 
			(os)->writeBinaryFile(os)
		);
	}
	
	private static void generate(
			String filename, String firstLine, Function<Integer, List<String>> headers, 
			ThrowingConsumer<OutputStream, IOException> body) {
		try (OutputStream os = new FileOutputStream(filename)) {

			os.write(firstLine.getBytes());
			os.write('\r');
			os.write('\n');
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			body.accept(bos);
			for (String header : headers.apply(bos.size())) {
				os.write(header.getBytes());
				os.write('\r');
				os.write('\n');
			}
			os.write('\r');
			os.write('\n');
			os.write(bos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// file is 317
	private static void writeBinaryFile(OutputStream os) throws IOException {
		try(InputStream is = new FileInputStream("index/icon.png")) {
			int i;
			while((i = is.read()) != -1) {
				os.write(i);
			}
		}
		
	}
	
}
