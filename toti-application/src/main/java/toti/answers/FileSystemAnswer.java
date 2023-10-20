package toti.answers;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import toti.ServerException;
import toti.answers.response.Response;
import toti.templating.DirectoryTemplate;

public class FileSystemAnswer {

	private final String resourcesDir;
	private final boolean dirResponseAllowed;
	private final String dirDefaultFile;
	private final Logger logger;
	
	public FileSystemAnswer(String resourcesDir,
			boolean dirResponseAllowed,
			String dirDefaultFile,
			Logger logger) {
		this.resourcesDir = resourcesDir;
		this.logger = logger;
		this.dirResponseAllowed = dirResponseAllowed;
		this.dirDefaultFile = dirDefaultFile;
	}
	
	public  ji.socketCommunication.http.structures.Response answer(
			 ji.socketCommunication.http.structures.Request request, Headers responseHeaders, String charset
			) throws ServerException {
		return answer(request.getPlainUri(), request.getMethod(), responseHeaders, charset)
				.getResponse(request.getProtocol(), responseHeaders, charset);
	}
	
	private Response answer(String url, HttpMethod method, Headers responseHeaders, String charset) throws ServerException {
		File file = new File(resourcesDir + url);
		try {
			File resDir = new File(resourcesDir);
			if (!file.getCanonicalFile().toString().startsWith(resDir.getCanonicalFile().toString())) {
				throw new ServerException(StatusCode.NOT_FOUND, String.format("URL not fouded: %s (%s)", url, method));
			}
		} catch (IOException e) {
			logger.warn("Cannot validate URL: " + url, e);
			throw new ServerException(StatusCode.NOT_FOUND, String.format("URL not fouded: %s (%s)", url, method));
		}
		
		if (!file.exists() || (file.isDirectory() && !dirResponseAllowed)) {
			if (file.isDirectory() && dirDefaultFile != null && new File(resourcesDir + url + "/" + dirDefaultFile).exists()) {
                return Response.create(StatusCode.OK).getFile(resourcesDir + url + "/" + dirDefaultFile);
			}
			throw new ServerException(StatusCode.NOT_FOUND, String.format("URL not fouded: %s (%s)", url, method));
		}
		if (file.isDirectory()) {
			return getDirResponse(file.listFiles(), url, responseHeaders, charset);
		}
		return Response.create(StatusCode.OK).getFile(resourcesDir + url);
	}
	
	private Response getDirResponse(File[] files, String path, Headers responseHeaders, String charset) throws ServerException {
		try {
			responseHeaders.addHeader("Content-Type", "text/html; charset=" + charset);
			return Response.create(StatusCode.OK).getText(new DirectoryTemplate(files, path).create(null, null, null));
		} catch (Exception e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, null, "Directory list fail: " + path);
		}
	}
	
}
