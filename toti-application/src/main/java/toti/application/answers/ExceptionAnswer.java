package toti.application.answers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import toti.application.answers.request.Identity;
import toti.application.answers.request.Request;
import toti.application.answers.response.FinalResponse;
import toti.application.answers.response.Response;
import toti.application.answers.response.ResponseContainer;
import toti.application.answers.response.TextResponse;
import toti.application.application.register.MappedAction;
import toti.application.application.register.Register;
import toti.application.extensions.TranslatorExtension;
import toti.application.logging.ExceptionHashCode;
import toti.application.logging.FileName;
import toti.application.logging.Page;
import toti.lib.files.text.Text;
import toti.lib.tcpip.enums.StatusCode;

public class ExceptionAnswer {

	private final Function<String, Boolean> isDevelop;
	private final Logger logger;
	private final String logsPath;
	private final TranslatorExtension translator;
	private final Register register;
	
	private final Map<ExceptionHashCode, String> exceptionFileName = new HashMap<>();
	
	public ExceptionAnswer(
			Register register, Function<String, Boolean> isDevelop,
			String logsPath, TranslatorExtension translator, Logger logger) {
		this.register = register;
		this.logger = logger;
		this.isDevelop = isDevelop;
		this.translator = translator;
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
		.prepare(
			responseHeaders, identity,
			new ResponseContainer(
				translator.getTranslator(identity), null, mappedAction, null, null
			),
			charset
		);
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
		boolean isAsyncRequest = request.getHeaders().isAsyncRequest(); // probably js request
		
		if (register.getCustomExceptionResponse() != null) {
			try {
				return register.getCustomExceptionResponse()
					.catchException(request, status, identity, translator, t, isDevelopResponseAllowed, isAsyncRequest);
			} catch (Throwable t1) {
				logger.error("CustomExceptionResponse fail, default implementation continue", t1);
			}
		}
		String exceptionDetail = getExceptionDetail(request, status, t, identity, mappedAction);
		if (isAsyncRequest) {
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
		String title = String.format("Exception %s", status);
		StringBuilder body = new StringBuilder();

		body.append(String.format("<h1>Exception occured: %s %s</h1>", status.getCode(), status.getDescription()));
		body.append(Page.paragraph(String.format("<div>In %s %s</div>", request.getMethod(), request.getUri())));

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
		body.append(Page.section(2, "Exception", Page.code(errorStackTrace.toString()), false));

		body.append("<h2>Request info</h2>");
		if (mappedAction != null) {
			body.append(Page.section(
				3, "Mapping",
				Page.paragraph(String.format(
					"<strong>Module</strong> %s<br>"
					+ "<strong>Controller</strong> %s<br>"
					+ "<strong>Method</strong> %s<br>",
					mappedAction.getModuleName(),
					mappedAction.getClassName(),
					mappedAction.getMethodName()
				)),
				true
			));
		}
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
		body.append(Page.section(3, "Identity", "TODO", false));
		

		// probably not show/save parameters - not save - password and other secrets leak
		//body.append(Page.section(3, "Parameters", "TODO", false));
		/*
					<div class="section">
				<div>
					<h3>Paramenters</h3>
					<img src="" class="block-show" width="20px">
					<img src="" class="block-hide" width="20px">
				</div>
				<div class="block">
					<h3>Query parameters</h3>
					<table>
						<t:foreach key="String key" value="Object value" map="${request.getQueryParameters()}">
							<tr>
								<th>${key}</th>
								<t:if cond="value == null">
									<td>${value}</td>
									<td></td>
								<t:else>
									<td>${value}</td>
									<td>${value.getClass().getName()}</td>
								</t:if>
								
							</tr>
						</t:foreach>
					</table>
					<h3>Binary body:</h3>
					<div>
						<t:if cond="${request.getBody()} == null">
							No binary data
						<t:else>
							Contains ${request.getBody().length} bytes
						</t:if>
					</div>
					<h3>Body as parameters</h3>
					<table>
						<t:foreach key="String key" value="Object value" map="${request.getBodyInParameters()}">
							<tr>
								<th>${key}</th>
								<t:if cond="value == null">
									<td>${value}</td>
									<td></td>
								<t:else>
									<td>${value}</td>
									<td>${value.getClass().getName()}</td>
								</t:if>
								
							</tr>
						</t:foreach>
					</table>
				</div>
			</div>
			
			<div class="section">
				<div>
					<h3>Headers</h3>
					<img src="" class="block-show" width="20px">
					<img src="" class="block-hide" width="20px">
				</div>
				<div class="block">
					<table>
						<t:foreach key="String key" value="Object list" map="${requestHeaders.getHeaders()}">
							<t:foreach item="Object value" collection="${list}">
								<tr>
									<th>${key}</th>
									<t:if cond="value == null">
										<td>${value}</td>
										<td></td>
									<t:else>
										<td>${value}</td>
										<td>${value.getClass().getName()}</td>
									</t:if>
								</tr>
							</t:foreach>
						</t:foreach>
					</table>
				</div>
			</div>
			
		</div>
	</div>
		*/
		return Page.error(title, body.toString());
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
