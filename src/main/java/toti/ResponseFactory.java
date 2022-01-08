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
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ji.common.Logger;
import ji.common.structures.DictionaryValue;
import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.server.RequestParameters;
import ji.socketCommunication.http.server.RestApiResponse;
import ji.socketCommunication.http.server.RestApiServerResponseFactory;
import ji.socketCommunication.http.server.WebSocket;
import toti.annotations.Domain;
import toti.profiler.Profiler;
import toti.registr.Register;
import toti.response.Response;
import toti.response.ResponseException;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.IdentityFactory;
import toti.security.exceptions.AccessDeniedException;
import toti.security.exceptions.NotAllowedActionException;
import toti.templating.DirectoryTemplate;
import toti.templating.TemplateException;
import toti.templating.TemplateFactory;
import toti.url.MappedUrl;
import toti.url.UrlPart;
import ji.translator.Translator;

public class ResponseFactory implements RestApiServerResponseFactory {
	
	private final Register register;
	private final ResponseHeaders responseHeaders;
	private final String charset;
	private final boolean dirResponseAllowed;
	private final Logger logger;
	//private final List<String> developIps;
	
	private final MapDictionary<UrlPart, Object> mapping;	
	private final String resourcesDir;
	private final Router router;
	
	private final Map<String, TemplateFactory> modules;
	//private final TemplateFactory totiTemplateFactory;
	private final Translator translator;
	
	private final Authorizator authorizator;
	private final Authenticator authenticator;
	private final IdentityFactory identityFactory;
	
	private final Profiler profiler; // TODO maybe remove dependency - now required
	
	private final ResponseFactoryToti totiRes;
	private final ResponseFactoryExceptions expRes;
	
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
		//this.totiTemplateFactory = totiTemplateFactory;
		this.logger = logger;
		this.dirResponseAllowed = dirResponseAllowed;
		//this.developIps = developIps;
	//	this.dbViewer = new DbViewerRouter();
		this.profiler = profiler;
		this.mapping = mapping;
		
		this.totiRes = new ResponseFactoryToti(profiler, developIps, translator, totiTemplateFactory, charset);
		this.expRes = new ResponseFactoryExceptions(translator, totiTemplateFactory, responseHeaders, charset, developIps, logger);
	}
	
	@Override
	public RestApiResponse accept(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			RequestParameters params,
			String ip,
			Optional<WebSocket> websocket) throws IOException {
		/*
		System.err.println("URL: " + fullUrl);
		System.err.println("Header: " + header);
		System.err.println("Params: " + params);
		//*/
		Identity identity = identityFactory.createIdentity(header, ip, profiler.isUse());
		return getCatchedResponse(method, url, fullUrl, protocol, header, params, identity);
	}

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
			return getNormalizedResponse(method, url, fullUrl, protocol, params, identity);
		/*} catch (AuthentizationException e) {
			return onException(401, method, url, fullUrl, protocol, header, params, locale, ip, e);
		} catch (NotAllowedActionException | AccessDeniedException e) {
			return expRes.onException(403, method, url, fullUrl, protocol, header, params, identity, e);*/
		} catch (ServerException e) {
			return expRes.getExceptionResponse(
				e.getStatusCode(), method, url, fullUrl, protocol, 
				header, params, identity, e.getUrl(), e.getCause() == null ? e : e.getCause()
			);
		} catch (Throwable t) {
			return expRes.getExceptionResponse(
				StatusCode.INTERNAL_SERVER_ERROR, method, url, fullUrl, protocol, header, params, identity, null, t
			);
		}
	}
	
	@Override
	public void catchException(Exception e) throws IOException {
		logger.fatal("Uncaught exception", e);
		RestApiServerResponseFactory.super.catchException(e);
	}
	
	private RestApiResponse getNormalizedResponse(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			RequestParameters params,
			Identity identity) throws ServerException {
		return getTotiFilteredResponse(
				method, url.endsWith("/") ? url.substring(0, url.length()-1) : url,
				fullUrl, protocol, params, identity
		);
	}
	
	private RestApiResponse getTotiFilteredResponse(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			RequestParameters params,
			Identity identity) throws ServerException {
		ResponseHeaders headers = responseHeaders.get();
		// toti exclusive
		if (url.startsWith("/toti")) {
			return totiRes.getTotiResponse(method, url.substring(5), params, identity, headers);
		}
		return getRoutedResponse(method, url, fullUrl, protocol, params, identity, headers);
	}
	
	private RestApiResponse getRoutedResponse(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			RequestParameters params,
			Identity identity,
			ResponseHeaders headers) throws ServerException {
		if (router.getUrlMapping(url) == null) {
			return getMappedResponse(method, url, fullUrl, protocol, params, identity, headers);
		}
		return getMappedResponse(method, router.getUrlMapping(url), fullUrl, protocol, params, identity, headers);
	}
	
	private RestApiResponse getMappedResponse(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			RequestParameters params,
			Identity identity,
			ResponseHeaders headers) throws ServerException {
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
			last = last.getDictionaryMap().getDictionaryValue(new UrlPart(method));
		}
		if (last.isPresent() && last.is(MappedUrl.class)) {
			MappedUrl mapped = last.getValue(MappedUrl.class);
			if (mapped.isRegex()) {
				Pattern p = Pattern.compile(String.format("(%s)", mapped.createParametrizedLink()));
		    	Matcher m = p.matcher(url);
		    	if (m.find()) {
		    		for (int i = 2; i <= m.groupCount(); i++) { // group 0 is origin text, 1 match url
		    			params.put(mapped.getParamName(i - 2), m.group(i));
		    		}
		    	}
			}
			if (profiler != null) {
		   		profiler.setPageId(identity.getPageId());
		   		profiler.logRequest(identity, method, url, fullUrl, protocol, params);
		   	}
		   	return getControllerResponse(headers, fullUrl, mapped, params, identity);
		}
		// files
		File file = new File(resourcesDir + url);
		if (!file.exists() || (file.isDirectory() && !dirResponseAllowed)) {
			throw new ServerException(StatusCode.NOT_FOUND, String.format("URL not fouded: %s (%s)", url, method));
		}
		if (file.isDirectory()) {
			return getDirResponse(headers, file.listFiles(), url);
		}
		return Response.getFile(resourcesDir + url).getResponse(headers, charset);
	}

	private RestApiResponse getControllerResponse(
			ResponseHeaders headers, String fullUrl,
			MappedUrl mapped, RequestParameters params,
			Identity identity) throws ServerException {
		try {
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
				return Response.getJson(StatusCode.BAD_REQUEST, errors).getResponse(headers, charset);
			}
			/** preparing params*/
			List<Class<?>> classesList = new ArrayList<>();
			List<Object> valuesList = new ArrayList<>();
			mapped.forEachParams((clazz, name, isRequestParam)->{
				classesList.add(clazz);
				addValueToList((isRequestParam ? params : validatorParams), name, clazz, valuesList);
			});
			/** authorize */
			try {
				authorize(mapped, params, identity, params);
			} catch (ServerException e) {
				if (mapped.isApi() || router.getRedirectOnNotLogedUser() == null) {
					throw e;
				}
				logger.debug(fullUrl + " Redirect to login page: " + e.getMessage());
				// TODO secure link
				String backlink = "";
				if (!fullUrl.equals("/")) {
					backlink = "?backlink=" + getBackLink(fullUrl);
				}
				return Response.getRedirect(
					router.getRedirectOnNotLogedUser() + backlink
			     ).getResponse(headers, charset);
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
	    	headers.addHeaders(identityFactory.getResponseHeaders(identity)); // FIX for cookies
	    	
			return response.getResponse(
				headers, templateFactory, 
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

	private void addValueToList(RequestParameters params, String name, Class<?> clazz, List<Object> valuesList) {
		if (name == null) {
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
	private void authorize(MappedUrl mapped, RequestParameters params, Identity identity, RequestParameters prop) throws ServerException {
		if (mapped.isSecured()) {
			if (identity.isAnonymous()) {
				throw new ServerException(StatusCode.UNAUTHORIZED, mapped, "Method require logged user");
			}
			if (mapped.isApi() && !identity.isApiAllowed()) {
				throw new ServerException(StatusCode.FORBIDDEN, mapped, "For this url you cannot use cookie token");
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
					bw.write(new DirectoryTemplate(files, path).create(null, null, null, null, null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		});
	}

}
