package toti.answers;

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
import ji.translator.Translator;
import toti.Headers;
import toti.answers.response.Response;
import toti.answers.response.ResponseContainer;
import toti.answers.response.TemplateResponse;
import toti.security.Identity;
import toti.templating.TemplateFactory;
import toti.url.MappedAction;

public class ExceptionAnswer {

	private final List<String> developIps;
	private final Logger logger;
	private final String logsPath;
	private final Translator translator;
	private final TemplateFactory totiTemplateFactory;
	
	public ExceptionAnswer(List<String> developIps, TemplateFactory totiTemplateFactory, String logsPath, Translator translator, Logger logger) {
		this.logger = logger;
		this.developIps = developIps;
		this.logsPath = logsPath;
		this.translator = translator;
		this.totiTemplateFactory = totiTemplateFactory;
	}

	public ji.socketCommunication.http.structures.Response answer(
			ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders,
			StatusCode status, Throwable t,
			Identity identity, MappedAction mappedAction,
			Headers responseHeaders,
			String charset
		) {
		return getResponse(request, requestHeaders, status, t, identity, mappedAction, responseHeaders, charset)
			.getResponse(
				request.getProtocol(), responseHeaders, identity,
				new ResponseContainer(
					translator.withLocale(identity.getLocale()), null, mappedAction, totiTemplateFactory, null
				),
				charset
			);
	}

	public Response getResponse(
			ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders,
			StatusCode status, Throwable t,
			Identity identity, MappedAction mappedAction,
			Headers responseHeaders,
			String charset
		) {
		logger.error(String.format("Exception occured %s URL: %s", status, request.getUri()), t);
		boolean isDevelopResponseAllowed = developIps.contains(identity.getIP());
		boolean isAsyncRequest = requestHeaders.isAsyncRequest(); // probably js request
		
		if (false) {
			try {
				// TODO custom exception catch
			} catch (Throwable t1) {
				logger.error("CustomExceptionResponse fail, default implementation continue", t1);
			}
		}
		TemplateResponse response = null; // TODO getTemplate(status, request, identity, mappedUrl, t);
		if (isAsyncRequest) {
			saveToFile(response, charset);
			if (isDevelopResponseAllowed) {
				return Response.getText(status, t.getClass() + ": " + t.getMessage());
			}
			return Response.getText(status, status.getDescription());
		}
		if (isDevelopResponseAllowed) {
			return response;
		}
		return getSyncException(status, response, charset);
	}
	/*
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
		variables.put("urlParameters", request.getQueryParameters());
		variables.put("bodyParameters", request.getBodyInParameters());
		variables.put("body", request.getBody());
		variables.put("identity", identity);
		variables.put("mappedUrl", mappedUrl);
		variables.put("t", t);
		return new TemplateResponse(code, "/errors/exception.jsp", variables);
	}
	*/
	
	private Response getSyncException(StatusCode code, TemplateResponse response, String charset) {
		saveToFile(response, charset);
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", code);
		return Response.getTemplate(code, "/errors/error.jsp", variables);
	}

	private String saveToFile(TemplateResponse response, String charset) {
		if (logsPath == null || logsPath.isEmpty()) {
			logger.debug("Extended HTML request log is disabled");
			return "-- log exception detail is disabled --";
		}	
		try {
			// TODO some way to minimalize log files - cache? hash?
			String dirName = logsPath + (logsPath.endsWith("/") ? "" : "/");
            File dir = new File(dirName);
            dir.setExecutable(true, false);
            dir.setReadable(true, false);
            dir.setWritable(true, false);
            dir.mkdirs();
            
			String fileName =
					dirName
					+ "/exception-"
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) 
					+ "__"
					+ new Random().nextInt()
					+ ".html";
			Text.get().write((bw)->{
				bw.write(
					response.createResponse(new ResponseContainer(translator, null, null, totiTemplateFactory, null))
				);
			}, fileName, charset, false);
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
}
