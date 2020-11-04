package mvc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import common.Logger;
import common.exceptions.LogicException;
import exception.AccessDeniedException;
import exception.NotAllowedActionException;
import helper.AuthorizationHelper;
import interfaces.AclUser;
import mvc.annotations.MappedUrl;
import mvc.annotations.inject.Authenticate;
import mvc.annotations.inject.Authorize;
import mvc.annotations.inject.ClientIdentity;
import mvc.annotations.inject.Lang;
import mvc.annotations.url.Action;
import mvc.annotations.url.Controller;
import mvc.annotations.url.Domain;
import mvc.annotations.url.Method;
import mvc.annotations.url.Param;
import mvc.annotations.url.ParamUrl;
import mvc.annotations.url.Params;
import mvc.annotations.url.Secured;
import mvc.authentication.Authenticator;
import mvc.authentication.AuthentizationException;
import mvc.authentication.Identity;
import mvc.registr.Registr;
import mvc.response.Response;
import mvc.templating.DirectoryTemplate;
import mvc.templating.TemplateFactory;
import mvc.validation.ParseObject;
import mvc.validation.Validator;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import socketCommunication.http.server.RestApiServerResponseFactory;
import translator.Translator;

public class ResponseFactory implements RestApiServerResponseFactory {
	
	private final ResponseHeaders responseHeaders;
	private final String charset;
	private final String defLang;
	private final Logger logger;
	
	private final List<MappedUrl> mapping;	
	private final String resourcesDir;
	private final Router router;
	
	private final Map<String, TemplateFactory> modules;
	private final Function<Locale, Translator> translator;
	
	private final Function<Identity, AclUser> identityToUser;
	private final AuthorizationHelper authorizator;
	private final Authenticator authenticator;
	
	
	public ResponseFactory(
			ResponseHeaders responseHeaders,
			String resourcesDir,
			Router router,
			Map<String, TemplateFactory> modules,
			Function<Locale, Translator> translator,			
			Authenticator authenticator,
			AuthorizationHelper authorizator,
			Function<Identity, AclUser> identityToUser,
			String charset,
			String defLang,
			Logger logger) throws Exception {
		this.resourcesDir = resourcesDir;
		this.charset = charset;
		this.translator = translator;
		this.mapping = loadUrlMap(modules);
		this.responseHeaders = responseHeaders;
		this.authorizator = authorizator;
		this.authenticator = authenticator;
		this.router = router;
		this.modules = modules;
		this.logger = logger;
		this.identityToUser = identityToUser;
		this.defLang = defLang;
	}

	@Override
	public RestApiResponse accept(
			HttpMethod method,
			String url,
			String fullUrl,
			String protocol,
			Properties header,
			Properties params,
			String ip) throws IOException {
		System.err.println("URL: " + fullUrl);
		System.err.println("Header: " + header);
		System.err.println("Params: " + params);
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
		return Response.getFile(StatusCode.forCode(code), String.format("mvc/errors/%s.html", code))
				.getResponse(responseHeaders, null, null, charset);
	}
	
	private RestApiResponse getAuthenticatedResponse(HttpMethod method,
			String url,
			Properties params,
			Properties header,
			String ip) throws Exception {
		Identity identity = authenticator.authenticate(header);
		System.err.println("Identity: " + identity);
		return getLocalizedResponse(method, url, header, params, identity, ip);
	}
	
	private RestApiResponse getLocalizedResponse(
			HttpMethod method,
			String url,
			Properties header,
			Properties params,
			Identity identity,
			String ip) throws ServerException {
		String lang = header.getProperty("Accept-Language");
		if (lang == null) {
			return getNormalizedResponse(method, url, params, identity, ip, new Locale(defLang));
		} else {
			String locale = lang.split(" ", 2)[0].split(";")[0].trim().replace("-", "_");
			return getNormalizedResponse(method, url, params, identity, ip, new Locale(locale));
		}
	}
	
	private RestApiResponse getNormalizedResponse(
			HttpMethod method,
			String url,
			Properties params,
			Identity identity,
			String ip,
			Locale locale) throws ServerException {
		return getRoutedResponse(method, url.endsWith("/") ? url.substring(0, url.length()-1) : url, params, identity, ip, locale);
	}
	
	private RestApiResponse getRoutedResponse(
			HttpMethod method,
			String url,
			Properties params,
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
			Properties params,
			Identity identity,
			String ip,
			Locale locale) throws ServerException {
		ResponseHeaders headers = responseHeaders.get();
		// toti exclusive
		if (url.startsWith("/toti/")) {
			return Response.getFile("toti/web" + url.substring(5)).getResponse(headers, null, null, charset);
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
	    		if (mapped.getValidator().isPresent()) {
		    		Map<String,  List<String>> errors = mapped.getValidator().get().validate(params);
		    		Map<String,  Object> json = new HashMap<>();
		    		json.putAll(errors);
		    		if (!errors.isEmpty()) {
		    			return Response.getJson(StatusCode.BAD_REQUEST, json).getResponse(headers, null, null, charset);
		    		}
	    		}
	    		return getControllerResponse(headers, mapped, params, identity, locale);
	    	}
		}
		// files
		File file = new File(resourcesDir + url);
		if (!file.exists()) {
			throw new ServerException(404, String.format("URL not fouded: %s (%s)", url, method));
		}
		if (file.isDirectory()) {
			return getDirResponse(headers, file.listFiles(), url);
		}
		return Response.getFile(resourcesDir + url).getResponse(headers, null, null, charset);
	}
	
	private RestApiResponse getControllerResponse(
			ResponseHeaders headers,
			MappedUrl mapped, Properties params, Identity identity, Locale locale) throws ServerException {
		authorize(mapped, params, identity);
		try {
			// params for method
			List<Class<?>> classesList = new ArrayList<>();
			List<Object> valuesList = new ArrayList<>();
			mapped.forEachParams((clazz, name)->{
				classesList.add(clazz);
				if (name == null) {
					valuesList.add(params);
				} else {
					valuesList.add(ParseObject.parse(clazz, params.get(name)));
				}
			});
			
			// TODO authenticate
			
			Object o = Registr.get().getFactory(mapped.getClassName()).get();
			// inject
			Field[] fields = o.getClass().getDeclaredFields();
			for (Field field : fields) {
				String method = "set" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);
				if (field.isAnnotationPresent(mvc.annotations.inject.Translator.class)) {
					o.getClass().getMethod(method, Translator.class).invoke(o, translator);
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
	    	headers.addHeaders(authenticator.getHeaders(identity)); // TODO fix for cookes
			return response.getResponse(headers, templateFactory, translator.apply(locale), charset);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void authorize(MappedUrl mapped, Properties params, Identity identity) throws ServerException {
		if (mapped.isSecured()) {
			if (!identity.isPresent()) {
				throw new ServerException(401, "Method require logged user");
			}
			if (mapped.isApi() && !identity.isApiAllowed()) {
				throw new ServerException(StatusCode.FORBIDDEN.getCode(), "For this url you cannot use cookie token");
			}
			for (Domain domain : mapped.getSecured()) {
				for (helper.Action action : domain.actions()) {
					authorizator.throwIfIsNotAllowed(identityToUser.apply(identity), ()->{
						return domain.name();
					}, action);
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
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		List<MappedUrl> mapping = new LinkedList<>();
		for (String folder : modules.keySet()) {
			URL url = loader.getResource(folder);
		    String path = url.getPath();
		    File dir = new File(path);
		    
		    map(dir, folder, mapping, "");
		}
	    return mapping;
	}
	
	private void map(File dir, String folder, List<MappedUrl> mapping, String prefix) throws Exception {
		for (File f : dir.listFiles()) {
	    	if (f.isDirectory()) {
	    		map(f, folder, mapping, prefix + "/" + f.getName());
	    	} else {
	    		String namespace = (folder + prefix).replaceAll("/", ".").replaceAll("\\\\", ".");
		    	Class<?> clazz =  Class.forName(namespace + "." + f.getName().replace(".class", ""));
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

}
