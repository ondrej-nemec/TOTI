package toti.url;

import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import common.exceptions.LogicException;
import common.functions.FilesList;
import common.structures.DictionaryValue;
import common.structures.MapDictionary;
import socketCommunication.http.HttpMethod;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Domain;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.annotations.Secured;
import toti.registr.Registr;
import toti.validation.Validator;

public class LoadUrls {

	public static MapDictionary<UrlPart, Object> loadUrlMap(List<Module> modules) throws Exception {
		MapDictionary<UrlPart, Object> mapped = MapDictionary.hashMap();
		for (Module module : modules) {
			map(mapped, FilesList.get(module.getControllersPath(), true).getFiles(), module);
		}
	    return mapped;
	}
	
	private static void map(MapDictionary<UrlPart, Object> mapped, List<String> files, Module module) throws Exception {
		String moduleName = module.getName();
		String folder = module.getControllersPath();
		for (String file : files) {
	    	int index = file.lastIndexOf("/");
	    	String path = (index > 0 ? "/" + file.replace(file.substring(index), "") : "");
	    	Class<?> clazz =  Class.forName( (folder + "/" + file).replaceAll("/", ".").replace(".class", "") );
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
		    		/*	String url = prefix + (controllerUrl.isEmpty() ? "" : "/" + controllerUrl)
								+ (methodUrl.isEmpty() ? "" : "/" + methodUrl);*/
		    			String className = clazz.getName();
		    			String methodName = m.getName();
		    			
		    			Domain[] methodDomains = null;
		    			boolean isApi = false;
		    			if (m.isAnnotationPresent(Secured.class)) {
		    				methodDomains = m.getAnnotation(Secured.class).value();
		    				isApi = m.getAnnotation(Secured.class).isApi();
		    			}
		    			
		    			MappedUrl mappedUrl = new MappedUrl(
		    					moduleName, controllerUrl, methodUrl, path,
		    					className, methodName,
		    					ArrayUtils.addAll(classDomains, methodDomains), isApi,
		    					validator
		    			);
		    			List<UrlParam> linkParams = new LinkedList<>();
		    			for (Parameter p : m.getParameters()) {
		    				if (p.isAnnotationPresent(ParamUrl.class)) {
		    					String name = p.getAnnotation(ParamUrl.class).value();
		    					mappedUrl.addParam(p.getType(), name);
		    					mappedUrl.addParamName(name);
		    					mappedUrl.setRegex(true);
		    					
		    					linkParams.add(new UrlParam(true));
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
		    			String url = Link.get().create(moduleName, path, controllerUrl, methodUrl, linkParams);
		    			String[] urls = url.substring(1).split("/");
		    			
		    			MapDictionary<UrlPart, Object> last = mapped;
		    			for (int i = 0; i < urls.length; i++) {
		    				boolean isRegex = urls[i].equals(UrlParam.PARAM_REGEX); // TODO another regex check
		    				Object o = last.get(new UrlPart(urls[i], isRegex));
		    				if (o == null) {
		    					o = MapDictionary.hashMap();
		    					last.put(new UrlPart(urls[i], isRegex), o);
		    				}
		    				last = new DictionaryValue(o).getDictionaryMap();
		    			}
		    			for (HttpMethod httpMethod : methods) {
		    				last.put(new UrlPart(httpMethod), mappedUrl);
		    			}
		    		}
		    	}
	    	}
	    }
	}
	
}
