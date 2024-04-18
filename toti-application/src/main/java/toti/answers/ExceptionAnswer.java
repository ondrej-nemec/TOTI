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
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.answers.response.TextResponse;
import toti.application.register.MappedAction;
import toti.application.register.Register;
import toti.answers.response.ResponseContainer;
import toti.extensions.TranslatorExtension;
import toti.logging.ExceptionHashCode;
import toti.logging.FileName;

public class ExceptionAnswer {

	private final List<String> developIps;
	private final Logger logger;
	private final String logsPath;
	private final TranslatorExtension translator;
	private final Register register;
	
	private final Map<ExceptionHashCode, String> exceptionFileName = new HashMap<>();
	
	public ExceptionAnswer(
			Register register, List<String> developIps,
			String logsPath, TranslatorExtension translator, Logger logger) {
		this.register = register;
		this.logger = logger;
		this.developIps = developIps;
		this.translator = translator;
		if (logsPath == null || logsPath.isEmpty()) {
			this.logsPath = null;
			logger.debug("Extended HTML request log is disabled");
		} else {
			this.logsPath = logsPath;
		}
	}
	
	// TODO vyresit template
	// TODO u dev IPs asi neposilat custom handler

	public ji.socketCommunication.http.structures.Response answer(
			ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders,
			StatusCode status, Throwable t,
			Identity identity, MappedAction mappedAction,
			Headers responseHeaders,
			String charset
		) {
		
		return getResponse(request, requestHeaders, status, t, identity, mappedAction, charset)
		.getResponse(
			request.getProtocol(), responseHeaders, identity,
			new ResponseContainer(
				translator.getTranslator(identity), null, mappedAction, null, null
			),
			charset
		);
	}
	
	protected Response getResponse(
			ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders,
			StatusCode status, Throwable t, Identity identity, MappedAction mappedAction, String charset) {
		FileName fileName = getFileName(mappedAction, status, t);
		String message = "Exception occured %s URL: %s.";
		if (fileName.isUsed()) {
			message += " Detail saved: " + fileName.getName();
		}
		logger.error(String.format(message, status, request.getUri()), t);
		
		boolean isDevelopResponseAllowed = developIps.contains(identity.getIP());
		boolean isAsyncRequest = requestHeaders.isAsyncRequest(); // probably js request
		
		if (register.getCustomExceptionResponse() != null) {
			try {
				return register.getCustomExceptionResponse()
					.catchException(Request.fromRequest(request, requestHeaders), status, identity, translator, t, isDevelopResponseAllowed, isAsyncRequest);
			} catch (Throwable t1) {
				logger.error("CustomExceptionResponse fail, default implementation continue", t1);
			}
		}
		String exceptionDetail = getExceptionDetail(request, requestHeaders, status, t, identity, mappedAction);
		if (isAsyncRequest) {
			saveToFile(fileName, exceptionDetail, charset);
			if (isDevelopResponseAllowed) {
				return Response.create(status).getText(t.getClass() + ": " + t.getMessage());
			}
			return Response.create(status).getText(status.getDescription());
		}
		
		if (isDevelopResponseAllowed) {
			return getExceptionResponse(status, exceptionDetail);
		}
		saveToFile(fileName, exceptionDetail, charset);
		
		return getExceptionResponse(status, getExceptionInfo(status));
	}
	
	protected Response getExceptionResponse(StatusCode status, String message) {
		return new TextResponse(status, new Headers().addHeader("Content-Type", "text/html"), message);
	}

	protected String getExceptionInfo(StatusCode status) {
		return String.format(
			"<html>"
			+ "<head>"
				+ "<title>%s - Exception</title>"
			+ "</head>"
			+ "<body>"
				+ "<h1>Error %s: %s</h1>"
			+ "</body>"
			+ "</html>",
			status.getCode(), status.getCode(), status.getDescription()
		);
	}

	protected String getExceptionDetail(ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders, StatusCode status, Throwable t, Identity identity, MappedAction mappedAction) {
		String title = String.format("<title>Exception ${code}</title>", status);
		String headline = String.format(
			"<h1>Exception occured: %s %s</h1>"
			+ "<div>In %s %s</div>", 
			status.getCode(), status.getDescription(),
			request.getMethod(), request.getUri()
		);
		String style = "<style>"
			+ "h1 {"
				+ "text-align: center;"
				+ "padding: 0.5em;"
				+ "color: #e3ffff;"
				+ "background-color: #017CA5;"
			+ "}"
			+ "body {"
				+ "background-color: #7FC6CC;"
				+ "padding-left: 1em;"
				+ "padding-right: 1em;"
			+ "}"
			+ "</style>";
		
		StringBuilder errorStackTrace = new StringBuilder();
		Throwable aux = t;
		while (aux != null) {
			String prefix = "";
			if (!errorStackTrace.toString().isEmpty()) {
				prefix = "Caused by: ";
			}
			errorStackTrace.append(String.format(
				"<h2>%s%s: %s</h2>",
				prefix, aux.getClass(), aux.getMessage()
			));
			errorStackTrace.append("<div>");
			for (StackTraceElement el : aux.getStackTrace()) {
				errorStackTrace.append(String.format(
					"at %s.%s (%s:%s)",
					el.getClassName(), el.getMethodName(),
					el.getFileName(), el.getLineNumber()
				));
				errorStackTrace.append("<br>");
			}
			errorStackTrace.append("</div>");
			
			aux = aux.getCause();
		}
		
		return String.format(
			"<html><head>"
			+ title
			+ style
			+ "</head><body>"
			+ headline
			+ errorStackTrace.toString()
			+ "</body></html>",
			status.getCode(), status.getCode(), status.getDescription()
		);
	}
	
	private FileName getFileName(MappedAction action, StatusCode code, Throwable t) {
		return getFileName(LocalDateTime.now(), new Random().nextInt(), action, code, t);
	}
	
	protected FileName getFileName(LocalDateTime now, int random, MappedAction action, StatusCode code, Throwable t) {
		if (logsPath == null) {
			return new FileName(null, false);
		}
		ExceptionHashCode ehc = new ExceptionHashCode(code, action, t);
		if (exceptionFileName.containsKey(ehc)) {
			return new FileName(exceptionFileName.get(ehc), false);
		}
		String name = logsPath
			+ (logsPath.endsWith("/") ? "" : "/")
			+ "exception-"
			+ now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) 
			+ "__"
			+ random
			+ ".html";
		exceptionFileName.put(ehc, name);
		return new FileName(name, true);
	}
	
	private void saveToFile(FileName fileName, String response, String charset) {
		saveToFile(fileName, response, charset, Text.get());
	}
	
	protected int saveToFile(FileName fileName, String response, String charset, Text text) {
		if (!fileName.isCreate() || !fileName.isUsed()) {
			return -1;
		}
		try {
			/*
			IS needed?
			
			String dirName = logsPath + (logsPath.endsWith("/") ? "" : "/");
            File dir = new File(dirName);
            dir.setExecutable(true, false);
            dir.setReadable(true, false);
            dir.setWritable(true, false);
            dir.mkdirs();
			*/
			text.write((bw)->{ bw.write(response); }, fileName.getName(), charset, false);
			File file = new File(fileName.getName());
			/*
			IS needed?
			
			file.mkdirs();
			*/
			file.setExecutable(true, false);
			file.setReadable(true, false);
			file.setWritable(true, false);
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("-- file not saved -- (" + e.getMessage() + ")");
			return 1;
		}
	}
	
}
