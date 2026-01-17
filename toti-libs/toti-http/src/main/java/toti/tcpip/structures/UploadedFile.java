package toti.tcpip.structures;

import java.io.IOException;
import java.util.Arrays;

import toti.lib.files.text.Binary;

public class UploadedFile {

	private final String fileName;
	private final String contentType;
	private final String bom;
	private final byte[] content;
	private final Binary binary;
	
	public UploadedFile(String fileName, String contentType, String bom, byte[] content) {
		this(fileName, contentType, bom, content, Binary.get());
	}
	
	protected UploadedFile(String fileName, String contentType, String bom, byte[] content, Binary binary) {
		this.fileName = fileName;
		this.contentType = contentType;
		this.content = content;
		this.binary = binary;
		this.bom = bom;
	}

	public String getFileName() {
		return fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public String getFileBom() {
		return bom;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	/**
	 * Save file on given path. The filename will be same as uploaded name.
	 * @param path
	 * @throws IOException
	 */
	public void save(String path) throws IOException {
		save(path, fileName);
	}
	
	/**
	 * Save file to given path. The name will be the given name.
	 * @param path
	 * @param name
	 * @throws IOException
	 */
	public void save(String path, String name) throws IOException {
		if (!path.endsWith("/")) {
			path += "/";
		}
		binary.write((stream)->{
			stream.write(content);
			stream.flush();
		}, path + name);
	}
	
	@Override
	public String toString() {
		return String.format("File[name: %s, contentType: %s, bom: %s, size: %sb]", fileName, contentType, bom, content.length);
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
		UploadedFile other = (UploadedFile) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!Arrays.equals(content, other.content)) {
			return false;
		}
		if (contentType == null) {
			if (other.contentType != null) {
				return false;
			}
		} else if (!contentType.equals(other.contentType)) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			return false;
		}
		return true;
	}
	
}
