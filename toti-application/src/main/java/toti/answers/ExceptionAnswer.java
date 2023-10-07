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
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.answers.response.ResponseContainer;
import toti.answers.response.TemplateResponse;
import toti.application.register.MappedAction;
import toti.application.register.Register;
import toti.security.Identity;
import toti.templating.TemplateFactory;

public class ExceptionAnswer {

	private final List<String> developIps;
	private final Logger logger;
	private final String logsPath;
	private final Translator translator;
	private final TemplateFactory totiTemplateFactory;
	private final Register register;
	
	public ExceptionAnswer(
			Register register, List<String> developIps, TemplateFactory totiTemplateFactory, String logsPath, Translator translator, Logger logger) {
		this.register = register;
		this.logger = logger;
		this.developIps = developIps;
		this.translator = translator;
		this.totiTemplateFactory = totiTemplateFactory;
		if (logsPath == null || logsPath.isEmpty()) {
			this.logsPath = null;
			logger.debug("Extended HTML request log is disabled");
		} else {
			this.logsPath = logsPath;
		}
	}

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
				translator.withLocale(identity.getLocale()), null, mappedAction, totiTemplateFactory, null
			),
			charset
		);
	}
	
	protected Response getResponse(
			ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders,
			StatusCode status, Throwable t, Identity identity, MappedAction mappedAction, String charset) {
		logger.error(String.format("Exception occured %s URL: %s", status, request.getUri()), t);
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
		TemplateResponse exceptionDetail = getExceptionDetail(request, requestHeaders, status, t, identity, mappedAction);
		if (isAsyncRequest) {
			saveToFile(exceptionDetail, charset);
			if (isDevelopResponseAllowed) {
				return Response.getText(status, t.getClass() + ": " + t.getMessage());
			}
			return Response.getText(status, status.getDescription());
		}
		if (isDevelopResponseAllowed) {
			return exceptionDetail;
		}
		saveToFile(exceptionDetail, charset);
		return getExceptionInfo(status);
	}
	
	protected TemplateResponse getExceptionInfo(StatusCode status) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", status);
		return new TemplateResponse(status, "/errors/error.jsp", variables);
	}
	
	protected TemplateResponse getExceptionDetail(ji.socketCommunication.http.structures.Request request,
			Headers requestHeaders, StatusCode status, Throwable t, Identity identity, MappedAction mappedAction) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", status);
		variables.put("request", request);
		variables.put("requestHeaders", requestHeaders);
		variables.put("identity", identity);
		variables.put("mappedUrl", mappedAction);
		variables.put("t", t);
		return new TemplateResponse(status, "/errors/exception.jsp", variables);
	}
	
	private void saveToFile(TemplateResponse response, String charset) {
		saveToFile(response, charset, Text.get());
	}
	
	protected void saveToFile(TemplateResponse response, String charset, Text text) {
		if (logsPath == null) {
			return;
		}
		// TODO
		/*	
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
			logger.error("-- exception saved to " + fileName);
			return fileName;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("-- file not saved -- (" + e.getMessage() + ")");
			return "-- file not saved -- (" + e.getMessage() + ")";
		}
		*/
	}
	
}
