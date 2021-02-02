package toti;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import acl.AuthorizationHelper;
import acl.exception.AccessDeniedException;
import acl.exception.NotAllowedActionException;
import common.Logger;
import common.exceptions.LogicException;
import core.FilesList;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RequestParameters;
import socketCommunication.http.server.RestApiResponse;
import socketCommunication.http.server.RestApiServerResponseFactory;
import toti.annotations.MappedUrl;
import toti.annotations.inject.Authenticate;
import toti.annotations.inject.Authorize;
import toti.annotations.inject.ClientIdentity;
import toti.annotations.inject.Lang;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Domain;
import toti.annotations.url.Method;
import toti.annotations.url.Param;
import toti.annotations.url.ParamUrl;
import toti.annotations.url.Params;
import toti.annotations.url.Secured;
import toti.authentication.Authenticator;
import toti.authentication.AuthentizationException;
import toti.authentication.Identity;
import toti.authentication.Language;
import toti.authentication.UserSecurity;
import toti.registr.Registr;
import toti.response.Response;
import toti.templating.DirectoryTemplate;
import toti.templating.TemplateFactory;
import toti.validation.ParseObject;
import toti.validation.Validator;
import translator.Translator;

public class ResponseFactory implements RestApiServerResponseFactory {
	
	private final ResponseHeaders responseHeaders;
	private final String charset;
	private final Language language;
	private final boolean dirResponseAllowed;
	private final Logger logger;
	
	private final List<MappedUrl> mapping;	
	private final String resourcesDir;
	private final Router router;
	
	private final Map<String, TemplateFactory> modules;
	private final TemplateFactory totiTemplateFactory;
	private final Translator translator;
	
	private final AuthorizationHelper authorizator;
	private final Authenticator authenticator;
	private final String redirectUrlNoLoggedUser;	
	
	public ResponseFactory(
			ResponseHeaders responseHeaders,
			Language language,
			String resourcesDir,
			Router router,
			Map<String, TemplateFactory> modules,
			TemplateFactory totiTemplateFactory,
			Translator translator,
			UserSecurity security,
			String charset,
			boolean dirResponseAllowed,
			Logger logger) throws Exception {
		this.resourcesDir = resourcesDir;
		this.charset = charset;
		this.translator = translator;
		this.mapping = loadUrlMap(modules);
		this.responseHeaders = responseHeaders;
		this.authorizator = security.getAuthorizator();
		this.authenticator = security.getAuthenticator();
		this.redirectUrlNoLoggedUser = security.getRedirectUrlNoLoggedUser();
		this.router = router;
		this.modules = modules;
		this.totiTemplateFactory = totiTemplateFactory;
		this.logger = logger;
		this.language = language;
		this.dirResponseAllowed = dirResponseAllowed;
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
		/*System.err.println("URL: " + fullUrl);
		System.err.println("Header: " + header);
		System.err.println("Params: " + params);*/
		try {
			return getAuthenticatedResponse(method, url, params, header, ip);
		} catch (AuthentizationException e) {
			return onException(401, e, fullUrl);
		} catch (NotAllowedActionException | AccessDeniedException e) {
			return onException(403, e, fullUrl);
		} catch (ServerException e) {
			return onException(e.getCode(), e, fullUrl);
		} catch (Exception e) {
			return onException(500, e, fullUrl);
		}
	}
	
	private RestApiResponse onException(int code, Throwable t, String fullUrl) {
		logger.error(String.format("Exception occured %s URL: %s", code, fullUrl), t);
		// TODO maybe some custom handler
		/*List<String> h = headers.getHeaders();
		h.add("WWW-Authenticate: basic realm=\"User Visible Realm\"");*/
		return Response.getFile(StatusCode.forCode(code), String.format("toti/errors/%s.html", code))
				.getResponse(responseHeaders.get(), null, null, charset);
	}
	
	private RestApiResponse getAuthenticatedResponse(HttpMethod method,
			String url,
			RequestParameters params,
			Properties header,
			String ip) throws Exception {
		Identity identity = authenticator.authenticate(header);
		//System.err.println("Identity: " + identity);
		return getLocalizedResponse(method, url, header, params, identity, ip);
	}
	
	private RestApiResponse getLocalizedResponse(
			HttpMethod method,
			String url,
			Properties header,
			RequestParameters params,
			Identity identity,
			String ip) throws ServerException {
		Locale locale = language.getLocale(header);
		return getNormalizedResponse(method, url, params, identity, ip, locale);
	}
	
	private RestApiResponse getNormalizedResponse(
			HttpMethod method,
			String url,
			RequestParameters params,
			Identity identity,
			String ip,
			Locale locale) throws ServerException {
		//System.err.println("Locale: " + locale);
		return getRoutedResponse(method, url.endsWith("/") ? url.substring(0, url.length()-1) : url, params, identity, ip, locale);
	}
	
	private RestApiResponse getRoutedResponse(
			HttpMethod method,
			String url,
			RequestParameters params,
			Identity identity,
			String ip,
			Locale locale) throws ServerException {
		if (router.getUrlMapping(url) == null) {
			return getMappedResponse(method, url, params, identity, ip, locale);
		}
		return getMappedResponse(method, router.getUrlMapping(url), params, identity, ip, locale);
	}
	
	private RestApiResponse getMappedResponse(
			HttpMethod method,
			String url,
			RequestParameters params,
			Identity identity,
			String ip,
			Locale locale) throws ServerException {
		ResponseHeaders headers = responseHeaders.get();
		// toti exclusive
		if (url.startsWith("/toti/")) {
			return Response.getTemplate(url.substring(5), new HashMap<>())
					.getResponse(headers, totiTemplateFactory, translator.withLocale(locale), charset);
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
	    		return getControllerResponse(headers, mapped, params, identity, locale);
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
		return Response.getFile(resourcesDir + url).getResponse(headers, null, null, charset);
	}
	
	private RestApiResponse getControllerResponse(
			ResponseHeaders headers,
			MappedUrl mapped, RequestParameters params, Identity identity, Locale locale) throws ServerException {
		Map<String,  Object> errors = new HashMap<>();
		if (mapped.getValidator().isPresent()) {
			errors.putAll(mapped.getValidator().get().validate(params, translator.withLocale(locale)));
		}
		// params for method
		List<Class<?>> classesList = new ArrayList<>();
		List<Object> valuesList = new ArrayList<>();
		if (errors.isEmpty()) {
			try {
				mapped.forEachParams((clazz, name)->{
					classesList.add(clazz);
					if (name == null) {
						valuesList.add(params);
					} else if (clazz.isInstance(params.get(name))) {
						valuesList.add(params.get(name));
					} else {
						valuesList.add(ParseObject.parse(clazz, params.get(name)));
						params.put(name, ParseObject.parse(clazz, params.get(name)));
					}
				});
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}	
			try {
				authorize(mapped, params, identity, params);
			} catch (ServerException e) {
				if (mapped.isApi() || redirectUrlNoLoggedUser == null) {
					throw e;
				}
				return Response.getRedirect(redirectUrlNoLoggedUser).getResponse(headers, null, null, charset);
			}
		} else {
			// check errors after authrization
			return Response.getJson(StatusCode.BAD_REQUEST, errors).getResponse(headers, null, null, charset);
		}
		try {			
			Object o = Registr.get().getFactory(mapped.getClassName()).get();
			// inject
			Field[] fields = o.getClass().getDeclaredFields();
			for (Field field : fields) {
				String method = "set" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);
				if (field.isAnnotationPresent(toti.annotations.inject.Translate.class)) {
					o.getClass().getMethod(method, Translator.class).invoke(o, translator.withLocale(locale));
				} else if (field.isAnnotationPresent(Authenticate.class)) {
					o.getClass().getMethod(method, Authenticator.class).invoke(o, authenticator);
				} else if (field.isAnnotationPresent(Authorize.class)) {
					o.getClass().getMethod(method, AuthorizationHelper.class).invoke(o, authorizator);
				} else if (field.isAnnotationPresent(ClientIdentity.class)) {
					o.getClass().getMethod(method, Identity.class).invoke(o, identity);
				} else if (field.isAnnotationPresent(Lang.class)) {
					o.getClass().getMethod(method, Locale.class).invoke(o, locale);
				}
			}			
			
			TemplateFactory templateFactory = modules.get(mapped.getFolder());

			Class<?>[] classes = new Class<?>[classesList.size()];
			classesList.toArray(classes);
			Object[] values = new Object[valuesList.size()];
			valuesList.toArray(values);
				
	    	Response response = (Response)o.getClass()
	    				.getMethod(mapped.getMethodName(), classes).invoke(o, values);
	    	
	    	headers.addHeaders(authenticator.getHeaders(identity)); // FIX for cookies
	    	headers.addHeaders(language.getHeaders(locale)); // FIX for cookies
	    	
			return response.getResponse(headers, templateFactory, translator.withLocale(locale), charset);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void authorize(MappedUrl mapped, RequestParameters params, Identity identity, RequestParameters prop) throws ServerException {
		if (mapped.isSecured()) {
			if (!identity.isPresent()) {
				throw new ServerException(401, "Method require logged user");
			}
			if (mapped.isApi() && !identity.isApiAllowed()) {
				throw new ServerException(StatusCode.FORBIDDEN.getCode(), "For this url you cannot use cookie token");
			}
			
			Collection<Object> ids = null;
			for (Domain domain : mapped.getSecured()) {
				if (domain.owner().isEmpty()) {
					Collection<Object> allowedIds = authorizator.allowed(identity.getUser(), ()->{
						return domain.name();
					}, domain.action());
					if (ids == null) {
						ids = allowedIds;
					} else {
						ids.retainAll(allowedIds);
					}
				} else {
					authorizator.throwIfIsNotAllowed(identity.getUser(), ()->{
						return domain.name();
					}, domain.action(), prop.get(domain.owner()));
				}
			}
			if (ids != null) {
				identity.setIds(ids);
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
					bw.write(new DirectoryTemplate(files, path).create(null, null, null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		});
	}
	/*
	@Override
	public RestApiResponse onException(HttpMethod method, String url, String fullUrl, String protocol,
			Properties header, Properties params, Throwable t) throws IOException {
		t.printStackTrace();
		
		return RestApiResponse.textResponse(
			StatusCode.OK,
			headers.getHeaders("Content-Type: text/html; charset=" + charset),
			(bw)->{
				try {
					bw.write(new ExceptionTemplate(t).create(null, null, null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		});
	}
	*/
	private List<MappedUrl> loadUrlMap(Map<String, TemplateFactory> modules) throws Exception {
		List<MappedUrl> mapping = new LinkedList<>();
		for (String folder : modules.keySet()) {
			map(FilesList.get(folder, true).getFiles(), folder, mapping, modules.get(folder).getModuleName());
		}
	    return mapping;
	}
	
	private void map(List<String> files, String folder, List<MappedUrl> mapping, String moduleName) throws Exception {
		for (String file : files) {
	    	int index = file.lastIndexOf("/");
			String prefix = (moduleName.length() > 0 ? "/" + moduleName : "")
					+ (index > 0 ? "/" + file.replace(file.substring(index), "") : "");
	    	String classPath = (folder + "/" + file).replaceAll("/", ".");
		    Class<?> clazz =  Class.forName(classPath.replace(".class", ""));
			if ( ! (clazz.isInterface() || clazz.isAnonymousClass() || clazz.isPrimitive()) 
		    		&& clazz.isAnnotationPresent(Controller.class)) {
				Domain[] classDomains = null;
				if (clazz.isAnnotationPresent(Secured.class)) {
					classDomains = clazz.getAnnotation(Secured.class).value();
				}
		    	for (java.lang.reflect.Method m : clazz.getMethods()) {
		    		if (m.isAnnotationPresent(Action.class)) {
		    			HttpMethod[] methods = m.isAnnotationPresent(Method.class)
		    							? m.getAnnotation(Method.class).value()
		    							: HttpMethod.values();
		    			Optional<Validator> validator = m.getAnnotation(Action.class).validator().isEmpty()
		    					? Optional.empty()
		    					: Optional.of(Registr.get().getService(m.getAnnotation(Action.class).validator(), Validator.class));
		    			String controllerUrl = clazz.getAnnotation(Controller.class).value();
		    			String methodUrl = m.getAnnotation(Action.class).value();
		    			String url = prefix + (controllerUrl.isEmpty() ? "" : "/" + controllerUrl)
								+ (methodUrl.isEmpty() ? "" : "/" + methodUrl);
		    			String className = clazz.getName();
		    			String methodName = m.getName();
		    			
		    			Domain[] methodDomains = null;
		    			boolean isApi = false;
		    			if (m.isAnnotationPresent(Secured.class)) {
		    				methodDomains = m.getAnnotation(Secured.class).value();
		    				isApi = m.getAnnotation(Secured.class).isApi();
		    			}
		    			
		    			MappedUrl mappedUrl = new MappedUrl(
		    					url, methods, className, methodName, folder,
		    					ArrayUtils.addAll(classDomains, methodDomains), isApi,
		    					validator
		    			);
		    			for (Parameter p : m.getParameters()) {
		    				if (p.isAnnotationPresent(ParamUrl.class)) {
		    					mappedUrl.appendUrl("([a-zA-Z0-9_]*)");
		    					String name = p.getAnnotation(ParamUrl.class).value();
		    					mappedUrl.addParam(p.getType(), name);
		    					mappedUrl.addParamName(name);
		    					mappedUrl.setRegex(true);
		    				} else if (p.isAnnotationPresent(Param.class)) {
		    					mappedUrl.addParam(p.getType(), p.getAnnotation(Param.class).value());
		    				} else if (p.isAnnotationPresent(Params.class)) {
		    					mappedUrl.addParam(p.getType(), null);
		    				} else {
		    					throw new LogicException(
		    						"Not anotated param " + p.getName()
		    						+ ", required anotation: " + Param.class
		    						+ " or " + ParamUrl.class
		    					);
		    				}
		    			}
		    			mapping.add(mappedUrl);
		    		}
		    	}
	    	}
	    }
	}

}
