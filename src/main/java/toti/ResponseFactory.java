package toti;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Logger;
import common.structures.DictionaryValue;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.RestApiResponse;
import socketCommunication.http.server.RestApiServerResponseFactory;
import toti.annotations.LoadUrls;
import toti.annotations.MappedUrl;
import toti.annotations.url.Domain;
import toti.dbviewer.DbViewerRouter;
import toti.registr.Registr;
import toti.response.Response;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.IdentityFactory;
import toti.security.exceptions.AccessDeniedException;
import toti.security.exceptions.NotAllowedActionException;
import toti.templating.DirectoryTemplate;
import toti.templating.TemplateFactory;
import translator.Translator;

public class ResponseFactory implements RestApiServerResponseFactory {
	
	private final ResponseHeaders responseHeaders;
	private final String charset;
	//private final Language language;
	private final boolean dirResponseAllowed;
	private final Logger logger;
	private final List<String> developIps;
	
	private final List<MappedUrl> mapping;	
	private final String resourcesDir;
	private final Router router;
	
	private final Map<String, TemplateFactory> modules;
	private final TemplateFactory totiTemplateFactory;
	private final Translator translator;
	
	private final Authorizator authorizator;
	private final Authenticator authenticator;
	private final IdentityFactory identityFactory;
	// private final String redirectUrlNoLoggedUser;
	
	private final DbViewerRouter dbViewer;
	
	public ResponseFactory(
			ResponseHeaders responseHeaders,
			String resourcesDir,
			Router router,
			Map<String, TemplateFactory> modules,
			TemplateFactory totiTemplateFactory,
			Translator translator,
			IdentityFactory identityFactory,
			Authenticator authenticator,
			Authorizator authorizator,
			String charset,
			boolean dirResponseAllowed,
			List<String> developIps,
			Logger logger) throws Exception {
		this.resourcesDir = resourcesDir;
		this.charset = charset;
		this.translator = translator;
	//	this.mapping = loadUrlMap(modules);
		this.mapping = LoadUrls.loadUrlMap(modules); // TODO get in constructor
		this.responseHeaders = responseHeaders;
		this.authorizator = authorizator;
		this.identityFactory = identityFactory;
		this.authenticator = authenticator;
		// this.redirectUrlNoLoggedUser = ""; // TODO security.getRedirectUrlNoLoggedUser();
		this.router = router;
		this.modules = modules;
		this.totiTemplateFactory = totiTemplateFactory;
		this.logger = logger;
		this.dirResponseAllowed = dirResponseAllowed;
		this.developIps = developIps;
		this.dbViewer = new DbViewerRouter();
	}

	@Override
	public RestApiResponse accept(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			String ip) throws IOException {
		/*
		System.err.println("URL: " + fullUrl);
		System.err.println("Header: " + header);
		System.err.println("Params: " + params);
		//*/
		// Locale locale = language.getLocale(header);
		Identity identity = identityFactory.createIdentity(header, ip);
		return getCatchedResponse(method, url, fullUrl, protocol, header, params, identity);
	}
	/*
	private RestApiResponse getAuthenticatedResponse(HttpMethod method,
			String url,
			Properties header,
			RequestParameters params,
			String ip,
			Locale locale) throws Exception {
		Identity identity = authenticator.authenticate(header);
		//System.err.println("Identity: " + identity);
		return getNormalizedResponse(method, url, params, identity, ip, locale);
	}
	*/
	private RestApiResponse getCatchedResponse(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			Identity identity) {
		authenticator.authenticate(identity);
		try {
			return getNormalizedResponse(method, url, params, identity);
			// return getAuthenticatedResponse(method, url, header, params, ip, locale);
		/*} catch (AuthentizationException e) {
			return onException(401, method, url, fullUrl, protocol, header, params, locale, ip, e);*/
		} catch (NotAllowedActionException | AccessDeniedException e) {
			return onException(403, method, url, fullUrl, protocol, header, params, identity, e);
		} catch (ServerException e) {
			return onException(e.getCode(), method, url, fullUrl, protocol, header, params, identity, e);
		} catch (Exception e) {
			return onException(500, method, url, fullUrl, protocol, header, params, identity, e);
		}
		
	}
	
	private RestApiResponse onException(int responseCode, 
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			Identity identity, 
			Throwable t) {
		logger.error(String.format("Exception occured %s URL: %s", responseCode, fullUrl), t);
		StatusCode code = StatusCode.forCode(responseCode);
		if (developIps.contains(identity.getIP())) {
			return printException(code, method, url, fullUrl, protocol, header, params, identity, t);
		}
		// TODO own exception catcher
		/*return Response.getFile(StatusCode.forCode(code), String.format("toti/errors/%s.html", code))
				.getResponse(responseHeaders.get(), null, null, charset);*/
		Map<String, Object> variables = new HashMap<>();
		variables.put("code", code);
		return Response.getTemplate(code, "/errors/error.jsp", variables)
				.getResponse(
					responseHeaders.get(), 
					totiTemplateFactory, 
					translator.withLocale(identity.getLocale()), 
					authorizator,
					identity,
					charset
				);
	}
	
	private RestApiResponse printException(StatusCode code, 
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			Identity identity,
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
		variables.put("t", t);
		return Response.getTemplate(code, "/errors/exception.jsp", variables)
				.getResponse(
					responseHeaders.get(), totiTemplateFactory, 
					translator.withLocale(identity.getLocale()),
					authorizator, identity, charset
				);
	}
	
	private RestApiResponse getNormalizedResponse(
			HttpMethod method,
			String url,
			RequestParameters params,
			Identity identity) throws ServerException {
		//System.err.println("Locale: " + locale);
		return getRoutedResponse(method, url.endsWith("/") ? url.substring(0, url.length()-1) : url, params, identity);
	}
	
	private RestApiResponse getRoutedResponse(
			HttpMethod method,
			String url,
			RequestParameters params,
			Identity identity) throws ServerException {
		if (router.getUrlMapping(url) == null) {
			return getMappedResponse(method, url, params, identity);
		}
		return getMappedResponse(method, router.getUrlMapping(url), params, identity);
	}
	
	private RestApiResponse getMappedResponse(
			HttpMethod method,
			String url,
			RequestParameters params,
			Identity identity) throws ServerException {
		ResponseHeaders headers = responseHeaders.get();
		// toti exclusive
		if (url.startsWith("/toti/")) {
			return getTotiResponse(method, url, params, identity, headers).getResponse(
				headers, totiTemplateFactory, translator.withLocale(identity.getLocale()),
				authorizator, identity, charset
			);
		}
		// controllers
		for (MappedUrl mapped : mapping) {
			boolean is = false;
			boolean methodMatch = Arrays.asList(mapped.getAllowedMethods()).contains(method);
			if (mapped.isRegex()) {
				Pattern p = Pattern.compile(String.format("(%s)", mapped.getUrl()));
		    	Matcher m = p.matcher(url);
		    	if (m.find() && methodMatch) {
		    		for (int i = 2; i <= m.groupCount(); i++) { // group 0 is origin text, 1 match url
		    			params.put(mapped.getParamName(i - 2), m.group(i));
		    		}
		    		is = true;
		    	}
			} else {
				is = url.equals(mapped.getUrl()) && methodMatch;
			}
	    	if (is) {
	    		return getControllerResponse(headers, mapped, params, identity);
	    	}
		}
		// files
		File file = new File(resourcesDir + url);
		if (!file.exists() || (file.isDirectory() && !dirResponseAllowed)) {
			throw new ServerException(404, String.format("URL not fouded: %s (%s)", url, method));
		}
		if (file.isDirectory()) {
			return getDirResponse(headers, file.listFiles(), url);
		}
		return Response.getFile(resourcesDir + url).getResponse(headers, null, null, null, null, charset);
	}
	
	private Response getTotiResponse(HttpMethod method, String url, RequestParameters params, Identity identity, ResponseHeaders headers) {
		if (url.substring(6).startsWith("db")) {
			return dbViewer.getResponse(method, url.substring(8), params, identity, headers);
		}
		return Response.getTemplate(url.substring(5), new HashMap<>());
	}

	private RestApiResponse getControllerResponse(
			ResponseHeaders headers,
			MappedUrl mapped, RequestParameters params, Identity identity) throws ServerException {
		Map<String,  Object> errors = new HashMap<>();
		if (mapped.getValidator().isPresent()) {
			errors.putAll(mapped.getValidator().get().validate(params, translator.withLocale(identity.getLocale())));
		}
		// params for method
		List<Class<?>> classesList = new ArrayList<>();
		List<Object> valuesList = new ArrayList<>();
		if (errors.isEmpty()) {
			try {
				mapped.forEachParams((clazz, name)->{
					classesList.add(clazz);
					// TODO simplify - to test
					if (name == null) {
					//	valuesList.add(ParseObject.parse(clazz, params));
						valuesList.add(new DictionaryValue(params).getValue(clazz));
					} else if (clazz.isInstance(params.get(name))) {
						valuesList.add(params.get(name));
					} else {
						Object v = params.getDictionaryValue(name).getValue(clazz);
						valuesList.add(v);
						params.put(name, v);
					}
				});
			} catch (Throwable e) {
				throw new RuntimeException(mapped.getClassName() + ":" + mapped.getMethodName(), e);
			}	
			try {
				authorize(mapped, params, identity, params);
			} catch (ServerException e) {
				if (mapped.isApi() || authorizator.getRedirectUrlNoLoggedUser() == null) {
					throw e;
				}
				return Response.getRedirect(authorizator.getRedirectUrlNoLoggedUser())
						.getResponse(headers, null, null, null, null, charset);
			}
		} else {
			// check errors after authrization
			return Response.getJson(StatusCode.BAD_REQUEST, errors).getResponse(headers, null, null, null, null, charset);
		}
		try {
		//	Object o = Registr.get().getFactory(mapped.getClassName()).get();
			Object o = Registr.get()
					.getFactory(mapped.getClassName())
					.apply(translator.withLocale(identity.getLocale()), identity, authorizator, authenticator);
			TemplateFactory templateFactory = modules.get(mapped.getFolder());

			Class<?>[] classes = new Class<?>[classesList.size()];
			classesList.toArray(classes);
			Object[] values = new Object[valuesList.size()];
			valuesList.toArray(values);
			
	    	Response response = (Response)o.getClass()
	    				.getMethod(mapped.getMethodName(), classes).invoke(o, values);
	    	
	    	headers.addHeaders(identityFactory.getResponseHeaders(identity)); // FIX for cookies
	    	
			return response.getResponse(
				headers, templateFactory, 
				translator.withLocale(identity.getLocale()), 
				authorizator, identity, charset
			);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void authorize(MappedUrl mapped, RequestParameters params, Identity identity, RequestParameters prop) throws ServerException {
		if (mapped.isSecured()) {
			if (identity.isAnonymous()) {
				throw new ServerException(401, "Method require logged user");
			}
			if (mapped.isApi() && !identity.isApiAllowed()) {
				throw new ServerException(StatusCode.FORBIDDEN.getCode(), "For this url you cannot use cookie token");
			}
			for (Domain domain : mapped.getSecured()) {
				if (domain.owner().isEmpty()) {
					authorizator.authorize(identity.getUser(), domain.name(), domain.action());
				} else {
					authorizator.authorize(identity.getUser(), domain.name(), domain.action(), params.get(domain.owner()));
				}
			}
		}
	}

	private RestApiResponse getDirResponse(ResponseHeaders headers, File[] files, String path) {
		headers.addHeader("Content-Type: text/html; charset=" + charset);
		return RestApiResponse.textResponse(
			StatusCode.OK,
			headers.getHeaders(),
			(bw)->{
				try {
					bw.write(new DirectoryTemplate(files, path).create(null, null, null, null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		});
	}

}
