package toti;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.Logger;
import ji.files.text.Text;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Request;
import ji.translator.Translator;
import toti.response.Response;
import toti.response.TemplateResponse;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;

public class ResponseFactoryExceptions {
	
	private final List<String> developIps;
	private final Logger logger;
	private final TemplateFactory templateFactory;
	private final Translator translator;
	private final String charset;
	private final Headers responseHeaders;
	private final CustomExceptionResponse customExceptionResponse;
	private final String logsPath;
	
	public ResponseFactoryExceptions(
			Translator translator, TemplateFactory templateFactory, Headers responseHeaders,
			CustomExceptionResponse customExceptionResponse, String charset, String logsPath,
			List<String> developIps, Logger logger) {
		this.developIps = developIps;
		this.logsPath = logsPath;
		this.logger = logger;
		this.templateFactory = templateFactory;
		this.translator = translator;
		this.charset = charset;
		this.responseHeaders = responseHeaders;
		this.customExceptionResponse = customExceptionResponse;
	}
	
	public ji.socketCommunication.http.structures.Response getExceptionResponse(
			StatusCode responseCode, 
			Request request,
			Identity identity, 
			MappedUrl mappedUrl, // can be null
			Translator translator,
			Throwable t) {
		return getException(responseCode, request, identity, mappedUrl, translator, t)
			.getResponse(
				request.getProtocol(), responseHeaders, templateFactory, translator.withLocale(identity.getLocale()),
				null /*authorizator*/, identity, mappedUrl, charset
			);
	}
	
	private Response getException(
			StatusCode status, Request request, Identity identity, 
			MappedUrl mappedUrl, Translator translator, Throwable t) {
		logger.error(String.format("Exception occured %s URL: %s", status, request.getUri()), t);
				
		TemplateResponse response = getTemplate(status, request, identity, mappedUrl, t);
		boolean isDevelopResponseAllowed = developIps.contains(identity.getIP());
		boolean isAsyncRequest = identity.isAsyncRequest(); // probably js request
		if (customExceptionResponse != null) {
			try {
				return customExceptionResponse.catchException(
					status, request, identity, mappedUrl, t,
					translator, isDevelopResponseAllowed, isAsyncRequest
				);
			} catch (Throwable t1) {
				logger.error("CustomExceptionResponse fail, default implementation continue", t1);
			}
		}
		if (isAsyncRequest) {
			saveToFile(response);
			if (isDevelopResponseAllowed) {
				return Response.getText(status, t.getClass() + ": " + t.getMessage());
			}
			return Response.getText(status, status.getDescription());
		}
		if (isDevelopResponseAllowed) {
			return response;
		}
		return getSyncException(status, response);
	}
	
	private Response getSyncException(StatusCode code, TemplateResponse response) {
		saveToFile(response);
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", code);
		return Response.getTemplate(code, "/errors/error.jsp", variables);
	}
	
	private String saveToFile(TemplateResponse response) {
		if (logsPath == null || logsPath.isEmpty()) {
			return "-- log exception detail is disabled --";
		}	
		try {
			String dirName = logsPath + (logsPath.endsWith("/") ? "" : "/");
            File dir = new File(dirName);
            dir.setExecutable(true, false);
            dir.setReadable(true, false);
            dir.setWritable(true, false);
            
			String fileName =
					dirName
					+ "/exception-"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) 
					+ "__"
					+ new Random().nextInt()
					+ ".html";
			Text.get().write((bw)->{
				bw.write(
					response.createResponse(templateFactory, translator, null, null)
				);
			}, fileName,  charset, false);
			File file = new File(fileName);
			file.setExecutable(true, false);
			file.setReadable(true, false);
			file.setWritable(true, false);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			return "-- file not saved -- (" + e.getMessage() + ")";
		}
	}
	
	private TemplateResponse getTemplate(
			StatusCode code, 
			Request request,
			Identity identity,
			MappedUrl mappedUrl,
			Throwable t) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", code);
		variables.put("url", request.getPlainUri());
		variables.put("fullUrl", request.getUri());
		variables.put("method", request.getMethod());
		variables.put("protocol", request.getProtocol());
		variables.put("headers", identity.getHeaders());
		variables.put("urlParameters", request.getUrlParameters());
		variables.put("bodyParameters", request.getBodyInParameters());
		variables.put("body", request.getBody());
		variables.put("identity", identity);
		variables.put("mappedUrl", mappedUrl);
		variables.put("t", t);
		return new TemplateResponse(code, "/errors/exception.jsp", variables);
	}
}
