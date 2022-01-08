package toti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ji.common.Logger;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RequestParameters;
import ji.socketCommunication.http.server.RestApiResponse;
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
	private final ResponseHeaders responseHeaders;
	
	/*
	 * exceptin resp
	 *   throw in method
	 * 
	 * catch
	 *   missing template file
	 *   exception in template 
	 */
	
	public ResponseFactoryExceptions(
			Translator translator, TemplateFactory templateFactory, ResponseHeaders responseHeaders,
			String charset, List<String> developIps, Logger logger) {
		this.developIps = developIps;
		this.logger = logger;
		this.templateFactory = templateFactory;
		this.translator = translator;
		this.charset = charset;
		this.responseHeaders = responseHeaders;
	}
	
	public RestApiResponse getExceptionResponse(
			StatusCode responseCode, 
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			Identity identity, 
			MappedUrl mappedUrl, // can be null
			Throwable t) {
		return getException(responseCode, method, url, fullUrl, protocol, header, params, identity, mappedUrl, t)
			.getResponse(
				responseHeaders.get(), templateFactory, translator.withLocale(identity.getLocale()),
				null /*authorizator*/, identity, mappedUrl, charset
			);
	}
	
	private Response getException(
			StatusCode status, 
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			Identity identity, 
			MappedUrl mappedUrl,
			Throwable t) {
		logger.error(String.format("Exception occured %s URL: %s", status, fullUrl), t);
		
		String destination = header.getProperty("Sec-Fetch-Dest");
		
		if (destination != null && destination.equals("empty")) { // probably js request
			if (developIps.contains(identity.getIP())) {
				return Response.getText(status, t.getClass() + ": " + t.getMessage());
			}
			return Response.getText(status, status.getDescription());
		}
		
		TemplateResponse response = getTemplate(status, method, url, fullUrl, protocol, header, params, identity, mappedUrl, t);
		if (developIps.contains(identity.getIP())) {
			return response;
		}
		return getSyncException(status, response);
	}
	
	private Response getSyncException(StatusCode code, TemplateResponse response) {
	//	return Response.getFile(code, String.format("toti/errors/%s.html", code));
		// TODO save exception to html response.createResponse(templateFactory, translator, null, null);
		// TODO custom pages
		
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", code);
		return Response.getTemplate(code, "/errors/error.jsp", variables);
	}
	
	private TemplateResponse getTemplate(
			StatusCode code, 
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			Identity identity,
			MappedUrl mappedUrl,
			Throwable t) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", code);
		variables.put("url", url);
		variables.put("fullUrl", fullUrl);
		variables.put("method", method);
		variables.put("protocol", protocol);
		variables.put("headers", header);
		variables.put("parameters", params);
		variables.put("identity", identity);
		variables.put("mappedUrl", mappedUrl);
		variables.put("t", t);
		return new TemplateResponse(code, "/errors/exception.jsp", variables);
	}
}
