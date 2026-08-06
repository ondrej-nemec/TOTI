package toti.core.answers;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

import toti.core.ServerException;
import toti.core.answers.request.Request;
import toti.core.answers.response.FinalResponse;
import toti.core.answers.response.Response;
import toti.core.answers.response.ResponseContainer;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;

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
	
	public FinalResponse answer(
			Request request, Headers responseHeaders, String charset
			) throws ServerException {
		return answer(request.getUri(), request.getMethod(), responseHeaders, charset)
			.prepare(new ResponseContainer(charset, responseHeaders, null, null, null, null));
	}
	
	private Response answer(String url, HttpMethod method, Headers responseHeaders, String charset) throws ServerException {
		File file = new File(resourcesDir + url);
		System.out.println(resourcesDir);
		System.out.println(url);
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
			String path = resourcesDir + url;
			if (!path.endsWith("/")) {
				path += "/";
			}
			path += dirDefaultFile;
			if (file.isDirectory() && dirDefaultFile != null && new File(path).exists()) {
				return Response.create(StatusCode.OK).getFile(path);
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
			return Response.create(StatusCode.OK).getText(createFolderHtml(files, path));
		} catch (Exception e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, null, "Directory list fail: " + path);
		}
	}
	
	private String createFolderHtml(File[] files, String path) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Folder: <br>");
		
		String filePath = path;
		if (!path.endsWith("/")) {
			filePath += "/";
		}
		if (!path.equals("/")) { // root
			builder.append(String.format("<a href='%s..'>..</a>", filePath));			
			builder.append("<br>");
		}
		
		for(File file : files) {
			builder.append(String.format("<a href='%s'>%s</a>", filePath + file.getName(), file.getName()));			
			builder.append("<br>");
		}
		
		return builder.toString();
	}
	
}
