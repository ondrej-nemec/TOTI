package toti;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import ji.common.structures.DictionaryValue;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.socketCommunication.http.structures.WebSocket;
import ji.socketCommunication.http.structures.Request;
import toti.annotations.Domain;
import toti.profiler.Profiler;
import toti.register.Register;
import toti.response.Response;
import toti.response.ResponseException;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.IdentityFactory;
import toti.security.AuthMode;
import toti.security.Owner;
import toti.security.exceptions.AccessDeniedException;
import toti.security.exceptions.NotAllowedActionException;
import toti.templating.DirectoryTemplate;
import toti.templating.TemplateException;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import toti.url.UrlPart;
import ji.translator.Translator;

public class ResponseFactory implements ji.socketCommunication.http.ResponseFactory {
	
	private final Register register;
	private final Headers responseHeaders;
	private final String charset;
	private final boolean dirResponseAllowed;
	private final String dirDefaultFile;
	private final Logger logger;
	
	private final MapDictionary<UrlPart, Object> mapping;
	private final String resourcesDir;
	private final Router router;
	
	private final Map<String, TemplateFactory> modules;
	private final Translator translator;
	
	private final Authorizator authorizator;
	private final Authenticator authenticator;
	private final IdentityFactory identityFactory;
	
	private final Profiler profiler; // TODO maybe remove dependency - now required
	
	private final ResponseFactoryToti totiRes;
	private final ResponseFactoryExceptions expRes;
	
	public ResponseFactory(
			Headers responseHeaders,
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
			String dirDefaultFile,
			String logsPath,
			List<String> developIps,
			Logger logger,
			Profiler profiler,
			Register register,
			MapDictionary<UrlPart, Object> mapping) throws Exception {
		this.register = register;
		this.resourcesDir = resourcesDir;
		this.charset = charset;
		this.translator = translator;
		this.responseHeaders = responseHeaders;
		this.authorizator = authorizator;
		this.identityFactory = identityFactory;
		this.authenticator = authenticator;
		this.router = router; // TODO not need send
		this.modules = modules;
		this.logger = logger;
		this.dirResponseAllowed = dirResponseAllowed;
		this.dirDefaultFile = dirDefaultFile;
		this.profiler = profiler;
		this.mapping = mapping;
		
		this.totiRes = new ResponseFactoryToti(profiler, developIps, translator, totiTemplateFactory, charset);
		this.expRes = new ResponseFactoryExceptions(
			translator, totiTemplateFactory, responseHeaders.clone(),
			router.getCustomExceptionResponse(), charset, logsPath, developIps, logger
		);
	}
	
	@Override
	public ji.socketCommunication.http.structures.Response accept(Request request, String ip, Optional<WebSocket> websocket) throws IOException {
		Identity identity = identityFactory.createIdentity(request.getHeaders(), ip, profiler.isUse());
		return getCatchedResponse(request, identity, websocket);
	}

	private ji.socketCommunication.http.structures.Response getCatchedResponse(Request request, Identity identity, Optional<WebSocket> websocket) {
		authenticator.authenticate(identity, request.getBodyInParameters());
		try {
			return getNormalizedResponse(request.getPlainUri(), request, identity, websocket);
		/*} catch (AuthentizationException e) {
			return onException(401, method, url, fullUrl, protocol, header, params, locale, ip, e);
		} catch (NotAllowedActionException | AccessDeniedException e) {
			return expRes.onException(403, method, url, fullUrl, protocol, header, params, identity, e);*/
		} catch (ServerException e) {
			return expRes.getExceptionResponse(
				e.getStatusCode(), request, identity, e.getUrl(),
				translator.withLocale(identity.getLocale()),
				e.getCause() == null ? e : e.getCause()
			);
		} catch (Throwable t) {
			return expRes.getExceptionResponse(
				StatusCode.INTERNAL_SERVER_ERROR, request, identity, null,
				translator.withLocale(identity.getLocale()), t
			);
		}
	}
	
	@Override
	public void catchException(Exception e) throws IOException {
		logger.fatal("Uncaught exception", e);
		ji.socketCommunication.http.ResponseFactory.super.catchException(e);
	}
	
	private ji.socketCommunication.http.structures.Response getNormalizedResponse(
			String url,
			Request request,
			Identity identity, 
			Optional<WebSocket> websocket) throws ServerException {
		return getTotiFilteredResponse(
				url.endsWith("/") ? url.substring(0, url.length()-1) : url,
				request, identity, websocket
		);
	}
	
	private ji.socketCommunication.http.structures.Response getTotiFilteredResponse(
			String url,
			Request request,
			Identity identity, 
			Optional<WebSocket> websocket) throws ServerException {
		Headers responseHeaders = this.responseHeaders.clone();
		// toti exclusive
		if (url.startsWith("/toti")) {
			return totiRes.getTotiResponse(url.substring(5), request, identity, responseHeaders, websocket);
		}
		return getRoutedResponse(url, request, identity, responseHeaders, websocket);
	}
	
	private ji.socketCommunication.http.structures.Response getRoutedResponse(
			String url,
			Request request,
			Identity identity,
			Headers responseHeaders,
			Optional<WebSocket> websocket) throws ServerException {
		if (router.getUrlMapping(url) == null) {
			return getMappedResponse(url, request, identity, responseHeaders, websocket);
		}
		return getMappedResponse(router.getUrlMapping(url), request, identity, responseHeaders, websocket);
	}
	
	private ji.socketCommunication.http.structures.Response getMappedResponse(
			String url, Request request,
			Identity identity,
			Headers responseHeaders,
			Optional<WebSocket> websocket) throws ServerException {
		// controllers
		String[] urls = url.length() == 0 ? new String[] {} : url.substring(1).split("/");
		DictionaryValue last = new DictionaryValue(mapping);
		for (int i = 0; i < urls.length; i++) {
			if (!last.isPresent()) {
				break;
			} else if (last.is(MapDictionary.class)) {
				DictionaryValue aux = last.getDictionaryMap().getDictionaryValue(new UrlPart(urls[i], false));
				if (!aux.isPresent()) {
					aux = last.getDictionaryMap().getDictionaryValue(new UrlPart(toti.url.UrlParam.PARAM_REGEX, true));
				}
				last = aux;
			} else {
				last = new DictionaryValue(null);
				break;
			}
		}
		if (last.isPresent()) {
			last = last.getDictionaryMap().getDictionaryValue(new UrlPart(request.getMethod()));
		}
		if (last.isPresent() && last.is(MappedUrl.class)) {
			MappedUrl mapped = last.getValue(MappedUrl.class);
			if (mapped.isRegex()) {
				Pattern p = Pattern.compile(String.format("(%s)", mapped.createParametrizedLink()));
		    	Matcher m = p.matcher(url);
		    	if (m.find()) {
		    		for (int i = 2; i <= m.groupCount(); i++) { // group 0 is origin text, 1 match url
		    			request.getBodyInParameters().put(mapped.getParamName(i - 2), m.group(i));
		    		}
		    	}
			}
			if (profiler != null) {
		   		profiler.setPageId(identity.getPageId());
		   		profiler.logRequest(identity,request, mapped);
		   	}
		   	return getControllerResponse(responseHeaders, mapped, request, identity, websocket);
		}
		// files
		File file = new File(resourcesDir + url);
		try {
			File resDir = new File(resourcesDir);
			if (!file.getCanonicalFile().toString().startsWith(resDir.getCanonicalFile().toString())) {
				throw new ServerException(StatusCode.NOT_FOUND, String.format("URL not fouded: %s (%s)", url, request.getMethod()));
			}
		} catch (IOException e) {
			logger.warn("Cannot validate URL: " + url, e);
			throw new ServerException(StatusCode.NOT_FOUND, String.format("URL not fouded: %s (%s)", url, request.getMethod()));
		}
		
		
		if (!file.exists() || (file.isDirectory() && !dirResponseAllowed)) {
			if (file.isDirectory() && dirDefaultFile != null && new File(resourcesDir + url + "/" + dirDefaultFile).exists()) {
                return Response.getFile(resourcesDir + url + "/" + dirDefaultFile)
                		.getResponse(request.getProtocol(), responseHeaders, charset);
			}
			throw new ServerException(StatusCode.NOT_FOUND, String.format("URL not fouded: %s (%s)", url, request.getMethod()));
		}
		if (file.isDirectory()) {
			return getDirResponse(request, responseHeaders, file.listFiles(), url);
		}
		return Response.getFile(resourcesDir + url).getResponse(request.getProtocol(), responseHeaders, charset);
	}

	private ji.socketCommunication.http.structures.Response getControllerResponse(
			Headers responseHeaders, MappedUrl mapped, Request request,
			Identity identity, Optional<WebSocket> websocket) throws ServerException {
		try {
			logger.debug(
				"Request servant: {} {} {}",
				mapped.getModuleName(), mapped.getClassName(), mapped.getMethodName()
			);
			RequestParameters params = new RequestParameters();
			params.putAll(request.getBodyInParameters().toMap());
			params.putAll(request.getUrlParameters().toMap());
			
			// create instance here - validator can be method of that object
			Object o = register
					.getFactory(mapped.getClassName())
					.apply(translator.withLocale(identity.getLocale()), identity, authorizator, authenticator);
			/** validation */
			Map<String,  Object> errors = new HashMap<>();
			RequestParameters validatorParams = new RequestParameters();
			if (mapped.isValidatorPresent()) {
				errors.putAll(mapped.getValidator(o).validate(params, validatorParams, translator.withLocale(identity.getLocale())));
			}
			// TODO authorize and bad request response order
			if (!errors.isEmpty()) {
				// check errors after authrization
				// https://stackoverflow.com/a/6123801/8240462
				return Response.getJson(StatusCode.BAD_REQUEST, errors)
						.getResponse(request.getProtocol(), responseHeaders, charset);
			}
			/** preparing params*/
			List<Class<?>> classesList = new ArrayList<>();
			List<Object> valuesList = new ArrayList<>();
			mapped.forEachParams((clazz, name, isRequestParam)->{
				classesList.add(clazz);
				addValueToList(
					(isRequestParam ? params : validatorParams), request.getBody(),
					name, clazz, valuesList, websocket
				);
			});
			/** authorize */
			try {
				authorize(mapped, params, identity, validatorParams);
			} catch (ServerException e) {
				if (mapped.getSecurityMode() == AuthMode.HEADER || router.getRedirectOnNotLoggedInUser() == null) {
					throw e;
				}
				logger.debug(request.getUri() + " Redirect to login page: " + e.getMessage());
				String backlink = "";
				if (!request.getUri().equals("/")) {
					backlink = "?backlink=" + getBackLink(request.getUri());
				}
				return Response.getRedirect(
					router.getRedirectOnNotLoggedInUser() + backlink
			     ).getResponse(request.getProtocol(), responseHeaders, charset);
			}
			/** response */
			TemplateFactory templateFactory = modules.get(mapped.getModuleName());

			Class<?>[] classes = new Class<?>[classesList.size()];
			classesList.toArray(classes);
			Object[] values = new Object[valuesList.size()];
			valuesList.toArray(values);
			
			Response response = (Response)o.getClass()
	    				.getMethod(mapped.getMethodName(), classes)
	    				.invoke(o, values);
	    	identityFactory.setResponseHeaders(identity, responseHeaders); // for cookies and custom headers
	    	try {
	    		authenticator.saveIdentity(identity);
	    	} catch (Exception e) {
	    		logger.warn("Identity save fail", e);
	    	}
	    	
			return response.getResponse(
				request.getProtocol(), responseHeaders, templateFactory, 
				translator.withLocale(identity.getLocale()), 
				authorizator, identity, mapped, charset
			);
		} catch (ServerException e){
			throw e;
		} catch (NotAllowedActionException | AccessDeniedException e) {
			throw new ServerException(StatusCode.FORBIDDEN, mapped, e);
		} catch (TemplateException e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e);
		} catch (InvocationTargetException e) { // if exception throwed in method
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, (e.getCause() == null ? e : e.getCause()));
		} catch (ResponseException e){
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e.getCause());
		} catch (Throwable e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, mapped, e);
		}
	}

	private void addValueToList(RequestParameters params, byte[] body, String name, Class<?> clazz, List<Object> valuesList, Optional<WebSocket> websocket) {
		if (WebSocket.class.equals(clazz) && name == null) {
			valuesList.add(websocket.orElse(null));
		} else if (clazz.isArray() && clazz.getComponentType().isAssignableFrom(byte.class) && name == null) {
			valuesList.add(body);
		} else if (name == null) {
			valuesList.add(new DictionaryValue(params).getValue(clazz));
		} else if (clazz.isInstance(params.get(name))) {
			valuesList.add(params.get(name));
		} else {
			Object v = params.getDictionaryValue(name).getValue(clazz);
			valuesList.add(v);
			params.put(name, v);
		}
	}
	
    private String getBackLink(String fullUrl) {
        try {
             return URLEncoder.encode(fullUrl, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
             return fullUrl;
        }
    }
	private void authorize(MappedUrl mapped, RequestParameters params, Identity identity, RequestParameters validator) throws ServerException {
		if (mapped.isSecured()) {
			if (identity.isAnonymous()) {
				throw new ServerException(StatusCode.UNAUTHORIZED, mapped, "Method require logged user");
			}
			if (mapped.getSecurityMode() == AuthMode.HEADER && identity.getLoginMode() != AuthMode.HEADER) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you cannot use cookie token");
			}
			if (mapped.getSecurityMode() == AuthMode.COOKIE_AND_CSRF
					&& (identity.getLoginMode() == AuthMode.COOKIE || identity.getLoginMode() == AuthMode.NO_TOKEN)) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you need CSRF token");
			}
			/*if (mapped.isApi() && !identity.isApiAllowed()) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you cannot use cookie token");
			}
			if (mapped.isTokenRequired() && !identity.isCsrfOk() && !identity.isApiAllowed()) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you need CSRF token");
			}*/
			for (Domain domain : mapped.getSecured()) {
				if (domain.owner().isEmpty() && domain.mode() != Owner.USER_ID) {
					authorizator.authorize(identity.getUser(), domain.name(), domain.action());
				} else {
					authorizator.authorize(
						identity.getUser(),
						domain.name(), domain.action(),
						getOwner(domain.mode(), domain.owner(), identity, params, validator)
					);
				}
			}
		}
	}
	
	private Object getOwner(
			Owner ownerMode, String ownerName, Identity identity,
			RequestParameters request, RequestParameters validator) {
		switch (ownerMode) {
			case REQUERST: return request.get(ownerName);
			case USER_DATA: return identity.getUser().getContent().get(ownerName);
			case USER_ID: return identity.getUser().getId();
			case VALIDATOR: return validator.get(ownerName);
			default: return null;
		}
	}

	// TODO use as standart TOTI response?
	private ji.socketCommunication.http.structures.Response getDirResponse(
			Request request, Headers responseHeaders, File[] files, String path) throws ServerException {
		responseHeaders.addHeader("Content-Type", "text/html; charset=" + charset);
		ji.socketCommunication.http.structures.Response response = new ji.socketCommunication.http.structures.Response(
				StatusCode.OK, request.getProtocol()
		);
		response.setHeaders(responseHeaders.getHeaders());
		try {
			response.setBody(new DirectoryTemplate(files, path).create(null, null, null, null, null).getBytes());
		} catch (Exception e) {
			throw new ServerException(StatusCode.INTERNAL_SERVER_ERROR, null, "Directory list fail: " + path);
		}
		return response;
	}

}
