package mvc;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.Logger;
import common.exceptions.LogicException;
import exception.AccessDeniedException;
import exception.NotAllowedActionException;
import helper.AuthorizationHelper;
import mvc.annotations.Action;
import mvc.annotations.Authenticate;
import mvc.annotations.ClientIdentity;
import mvc.annotations.Controller;
import mvc.annotations.Lang;
import mvc.annotations.MappedUrl;
import mvc.annotations.Method;
import mvc.annotations.Param;
import mvc.annotations.ParamUrl;
import mvc.annotations.Params;
import mvc.authentication.Authenticator;
import mvc.authentication.Identity;
import mvc.registr.Registr;
import mvc.response.Response;
import mvc.templating.DirectoryTemplate;
import mvc.templating.TemplateFactory;
import socketCommunication.http.HttpMethod;
import socketCommunication.http.StatusCode;
import socketCommunication.http.server.RestApiResponse;
import socketCommunication.http.server.RestApiServerResponseFactory;
import translator.Translator;

public class ResponseFactory implements RestApiServerResponseFactory {
	
	private final ResponseHeaders headers;
	
	private final List<MappedUrl> mapping;
	
	private final String resourcesDir;
	private final String charset;

	private final TemplateFactory templateFactory;
	private final Function<Locale, Translator> translator;
	// TODO use it
	private final AuthorizationHelper authorizator;
	// TODO inject
	private final Authenticator authenticator;
	private final Router router;
	private final Map<String, String> folders;
	private final Logger logger;
	
	public ResponseFactory(
			ResponseHeaders headers,
			TemplateFactory templateFactory, Router router,
			Function<Locale, Translator> translator, AuthorizationHelper authorizator, Authenticator authenticator,
			Map<String, String> folders, String resourcesDir, String charset, Logger logger) throws Exception {
		this.resourcesDir = resourcesDir;
		this.charset = charset;
		this.templateFactory = templateFactory;
		this.translator = translator;
		this.mapping = loadUrlMap(folders);
		this.headers = headers;
		this.authorizator = authorizator;
		this.authenticator = authenticator;
		this.router = router;
		this.folders = folders;
		this.logger = logger;
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
		System.out.println(fullUrl);
		System.out.println(header);
		try {
			return getAuthenticatedResponse(method, fullUrl, params, header, ip);
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
		return Response.getFile(StatusCode.forCode(code), String.format("mvc/errors/%s.html", code))
				.getResponse(headers, null, null, null, charset);
	}
	
	private RestApiResponse getAuthenticatedResponse(HttpMethod method,
			String url,
			Properties params,
			Properties header,
			String ip) throws ServerException {
		Identity identity = authenticator.authenticate(header);
		RestApiResponse res = getNormalizedResponse(method, url, params, identity, ip);
		res.getHeader().addAll(authenticator.getHeaders(identity));
		return res;
	}
	
	private RestApiResponse getNormalizedResponse(
			HttpMethod method,
			String url,
			Properties params,
			Identity identity,
			String ip) throws ServerException {
		return getRoutedResponse(method, url.endsWith("/") ? url.substring(0, url.length()-1) : url, params, identity, ip);
	}
	
	private RestApiResponse getRoutedResponse(
			HttpMethod method,
			String url,
			Properties params,
			Identity identity,
			String ip) throws ServerException {
		if (router.getUrlMapping(url) == null) {
			return getMappedResponse(method, url, params, identity, ip);
		}
		return getMappedResponse(method, router.getUrlMapping(url), params, identity, ip);
	}
	
	private RestApiResponse getMappedResponse(
			HttpMethod method,
			String url,
			Properties params,
			Identity identity,
			String ip) throws ServerException {
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
	    		return getControllerResponse(mapped, params, identity);
	    	}
		}
		
		File file = new File(resourcesDir + url);
		if (!file.exists()) {
			throw new ServerException(404, String.format("URL not fouded: %s (%s)", url, method));
		}
		if (file.isDirectory()) {
			return getDirResponse(file.listFiles(), url);
		}
		return Response.getFile(resourcesDir + url).getResponse(headers, null, null, null, charset);
	}
	
	private RestApiResponse getControllerResponse(MappedUrl mapped, Properties params, Identity identity) {
		try {
			// params for method
			List<Class<?>> classesList = new ArrayList<>();
			List<Object> valuesList = new ArrayList<>();
			mapped.forEachParams((clazz, name)->{
				classesList.add(clazz);
				if (name == null) {
					valuesList.add(params);
				} else if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)) {
					valuesList.add(Integer.parseInt(params.get(name) + ""));
				} else if (clazz.isAssignableFrom(Boolean.class) || clazz.isAssignableFrom(boolean.class)) {
					valuesList.add(Boolean.parseBoolean(params.get(name) + ""));
				} else if (clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(short.class)) {
					valuesList.add(Short.parseShort(params.get(name) + ""));
				} else if (clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(float.class)) {
					valuesList.add(Float.parseFloat(params.get(name) + ""));
				} else if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(double.class)) {
					valuesList.add(Double.parseDouble(params.get(name) + ""));
				} else if (clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(long.class)) {
					valuesList.add(Long.parseLong(params.get(name) + ""));
				} else {
					valuesList.add(clazz.cast(params.get(name)));
				}
			});
			
			// TODO get locale from request/session, some translator cache
			Locale locale = Locale.getDefault();
			
			Object o = Registr.get().getFactory(mapped.getClassName()).get();
			// inject
			Field[] fields = o.getClass().getDeclaredFields();
			for (Field field : fields) {
				String method = "set" + (field.getName().charAt(0) + "").toUpperCase() + field.getName().substring(1);
				if (field.isAnnotationPresent(mvc.annotations.Translator.class)) {
					o.getClass().getMethod(method, Translator.class).invoke(o, translator);
				} else if (field.isAnnotationPresent(Authenticate.class)) {
					o.getClass().getMethod(method, Authenticator.class).invoke(o, authenticator);
				} else if (field.isAnnotationPresent(ClientIdentity.class)) {
					o.getClass().getMethod(method, Identity.class).invoke(o, identity);
				} else if (field.isAnnotationPresent(Lang.class)) {
					o.getClass().getMethod(method, Locale.class).invoke(o, locale);
				}
			}			
			
			String templatePath = folders.get(mapped.getFolder());
			
			if (classesList.size() > 0) {
				Class<?>[] classes = new Class<?>[classesList.size()];
				classesList.toArray(classes);
				Object[] values = new Object[valuesList.size()];
				valuesList.toArray(values);
				
	    		Response response = (Response)o.getClass()
	    				.getMethod(mapped.getMethodName(), classes).invoke(o, values);
	    		return response.getResponse(headers, templateFactory, translator.apply(locale), templatePath, charset);
			} else {
	    		Response response = (Response)o.getClass().getMethod(mapped.getMethodName()).invoke(o);
	    		return response.getResponse(headers, templateFactory, translator.apply(locale), templatePath, charset);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private RestApiResponse getDirResponse(File[] files, String path) {
		return RestApiResponse.textResponse(
			StatusCode.OK,
			 headers.getHeaders("Content-Type: text/html; charset=" + charset),
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
	private List<MappedUrl> loadUrlMap(Map<String, String>  folders) throws Exception {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		List<MappedUrl> mapping = new LinkedList<>();
		for (String folder : folders.keySet()) {
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
		    		for (java.lang.reflect.Method m : clazz.getMethods()) {
		    			if (m.isAnnotationPresent(Action.class)) {
		    				HttpMethod[] methods = m.isAnnotationPresent(Method.class)
		    								? m.getAnnotation(Method.class).value()
		    								: HttpMethod.values();
		    				String controllerUrl = clazz.getAnnotation(Controller.class).value();
		    				String methodUrl = m.getAnnotation(Action.class).value();
		    				String url = prefix + (controllerUrl.isEmpty() ? "" : "/" + controllerUrl)
			    					+ (methodUrl.isEmpty() ? "" : "/" + methodUrl);
		    				String className = clazz.getName();
		    				String methodName = m.getName();
		    				MappedUrl mappedUrl = new MappedUrl(url, methods, className, methodName, folder);
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
