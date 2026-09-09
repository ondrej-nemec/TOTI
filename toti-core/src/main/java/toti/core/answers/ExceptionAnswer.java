package toti.core.answers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import toti.core.answers.request.Request;
import toti.core.answers.response.FinalResponse;
import toti.core.answers.response.Response;
import toti.core.answers.response.ResponseContainer;
import toti.core.answers.response.TextResponse;
import toti.core.answers.session.Identity;
import toti.core.application.register.MappedAction;
import toti.core.application.register.Register;
import toti.core.logging.ExceptionHashCode;
import toti.core.logging.FileName;
import toti.core.logging.Page;
import toti.lib.files.text.Text;
import toti.lib.tcpip.enums.StatusCode;

public class ExceptionAnswer {

	private final Function<String, Boolean> isDevelop;
	private final Logger logger;
	private final String logsPath;
	private final Register register;
	
	private final Map<ExceptionHashCode, String> exceptionFileName = new HashMap<>();

	public ExceptionAnswer(
			Register register, Function<String, Boolean> isDevelop,
			String logsPath, Logger logger) {
		this.register = register;
		this.logger = logger;
		this.isDevelop = isDevelop;
		if (logsPath == null || logsPath.isEmpty()) {
			this.logsPath = null;
			logger.debug("Extended HTML request log is disabled");
		} else {
			this.logsPath = logsPath;
		}
	}

	public FinalResponse answer(
		Request request,
		StatusCode status, Throwable t,
		Identity identity, MappedAction mappedAction,
		Headers responseHeaders,
		String charset
	) {
		return getResponse(request, status, t, identity, mappedAction, charset)
			.prepare(new ResponseContainer(charset, responseHeaders, identity, null, mappedAction, null));
	}
	
	protected Response getResponse(
		Request request, StatusCode status, Throwable t,
		Identity identity, MappedAction mappedAction, String charset
	) {
		FileName fileName = getFileName(mappedAction, status, t);
		String message = "Exception occured %s. URL: %s %s.";
		if (fileName.isUsed()) {
			message += " Detail saved: " + fileName.getName();
		}
		logger.error(String.format(message, status, request.getMethod(), request.getUri()), t);
		
		boolean isDevelopResponseAllowed = isDevelop.apply(identity.getIP());
		if (!isDevelopResponseAllowed && register.getCustomErrorHandler() != null) {
			try {
				return register.getCustomErrorHandler().create()
					.onError(status, t).create(request, identity);
			} catch (Throwable t1) {
				logger.error("CustomErroHandler fails, default implementation continue", t1);
			}
		}
		String exceptionDetail = getExceptionDetail(request, status, t, identity, mappedAction);
		if (request.getHeaders().isAsyncRequest()) { // probably js request
			saveToFile(fileName, exceptionDetail, charset);
			if (isDevelopResponseAllowed) {
				return Response.create(status).getText(t.getClass() + ": " + t.getMessage());
			}
			return Response.create(status).getText(status.getDescription());
		}
		
		if (isDevelopResponseAllowed) {
			return getExceptionResponse(StatusCode.OK, exceptionDetail);
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

	protected String getExceptionDetail(Request request, StatusCode status, Throwable t, Identity identity, MappedAction mappedAction) {
		return Page.error(String.format("Exception %s", status), b->{
			b.addH1(String.format("Exception occured: %s %s", status.getCode(), status.getDescription()))
			.addParagraph(p->{
				p.addText(String.format("<div>In %s %s</div>", request.getMethod(), request.getUri()));
				StringBuilder errorStackTrace = new StringBuilder();
				Throwable aux = t;
				while (aux != null) {
					if (!errorStackTrace.isEmpty()) {
						errorStackTrace.append("</br>Caused by: ");
					}
					errorStackTrace.append(String.format("%s: %s<br>", aux.getClass(), aux.getMessage()));
					for (StackTraceElement el : aux.getStackTrace()) {
						errorStackTrace.append(String.format(
							"at %s.%s (%s:%s)<br>",
							el.getClassName(), el.getMethodName(),
							el.getFileName(), el.getLineNumber()
						));
					}
					aux = aux.getCause();
				}
				p.addCard(e->e.addText(errorStackTrace.toString()));
			})
			.addH2("Request info");
				// TODO
/*
<div class="block">
					<table>
						<tr>
							<th>IP</th>
							<td>${identity.getIP()}</td>
						</tr>
						<tr>
							<th>Locale</th>
							<td>${identity.getLocale()}</td>
						</tr>
					<t:if cond="${identity.isPresent()}" >
						<tr>
							<th>Login method</th>
							<td>${identity.getLoginMode()}</td>
						</tr>
						<tr>
							<th>User Id</th>
							<td>${identity.getUser().getId()}</td>
						</tr>
					</t:if>
					</table>
				</div> */
			if (mappedAction != null) {
				b.addSection("Mapping", 3, true, m->{
					m.addParagraph(mp->mp.addText(String.format(
						"<strong>Module</strong> %s<br>"
						+ "<strong>Controller</strong> %s<br>"
						+ "<strong>Method</strong> %s<br>",
						mappedAction.getModuleName(),
						mappedAction.getClassName(),
						mappedAction.getMethodName()
					)));
				});
			}
			b.addSection("Identity", 3, true, m->{
				m.addParagraph(mp->mp.addText("TODO"));
			});
		}).create();
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
	
	@SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
	protected int saveToFile(FileName fileName, String response, String charset, Text text) {
		if (!fileName.isCreate() || !fileName.isUsed()) {
			return -1;
		}
		try {
			File path = new File(logsPath);
			path.mkdirs();

			text.write((bw)->{ bw.write(response); }, fileName.getName(), charset, false);
			File file = new File(fileName.getName());
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
