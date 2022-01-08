package toti.url;

import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import ji.common.exceptions.LogicException;
import ji.common.functions.FilesList;
import ji.common.structures.DictionaryValue;
import ji.common.structures.MapDictionary;
import ji.common.structures.ThrowingFunction;
import ji.socketCommunication.http.HttpMethod;
import toti.Module;
import toti.Router;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Domain;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.ParamValidator;
import toti.annotations.Params;
import toti.annotations.ParamsValidator;
import toti.annotations.Secured;
import toti.registr.Register;
import toti.validation.Validator;

public class LoadUrls {
/*
	public static MapDictionary<UrlPart, Object> loadUrlMap(List<Module> modules, Router router, Register register) throws Exception {
		MapDictionary<UrlPart, Object> mapped = MapDictionary.hashMap();
		for (Module module : modules) {
			map(mapped, FilesList.get(module.getControllersPath(), true).getFiles(), module, register);
			module.addRoutes(router);
		}
	    return mapped;
	}
*/
	
	public static void loadUrlMap(MapDictionary<UrlPart, Object> mapped, Module module, Router router, Register register) throws Exception {
		map(mapped, FilesList.get(module.getControllersPath(), true).getFiles(), module, register);
	}
	
	private static void map(MapDictionary<UrlPart, Object> mapped, List<String> files, Module module, Register register) throws Exception {
		String moduleName = module.getName();
		String folder = module.getControllersPath();
		for (String file : files) {
			if (!file.endsWith(".class")) {
				continue;
			}
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
		    			/*Optional<Validator> validator = m.getAnnotation(Action.class).validator().isEmpty()
		    					? Optional.empty()
		    					: Optional.of(Registr.get().getService(m.getAnnotation(Action.class).validator(), Validator.class));
		    			*/
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
		    					getValidator(m, register)
		    			);
		    			List<UrlParam> linkParams = new LinkedList<>();
		    			for (Parameter p : m.getParameters()) {
		    				if (p.isAnnotationPresent(ParamUrl.class)) {
		    					String name = p.getAnnotation(ParamUrl.class).value();
		    					mappedUrl.addParam(p.getType(), name, true);
		    					mappedUrl.addParamName(name);
		    					mappedUrl.setRegex(true);
		    					
		    					linkParams.add(new UrlParam(true));
		    				} else if (p.isAnnotationPresent(Param.class)) {
		    					mappedUrl.addParam(p.getType(), p.getAnnotation(Param.class).value(), true);
		    				} else if (p.isAnnotationPresent(Params.class)) {
		    					mappedUrl.addParam(p.getType(), null, true);
		    				} else if (p.isAnnotationPresent(ParamValidator.class)) {
		    					mappedUrl.addParam(p.getType(), p.getAnnotation(ParamValidator.class).value(), false);
		    				} else if (p.isAnnotationPresent(ParamsValidator.class)) {
		    					mappedUrl.addParam(p.getType(), null, false);
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
			
	private static ThrowingFunction<Object, Validator, Exception> getValidator(java.lang.reflect.Method m, Register register) throws Exception {
		String validator = m.getAnnotation(Action.class).validator();
		if (validator.isEmpty()) {
			return null;
		} else if (register.isServicePresent(validator)) {
			return (o)->register.getService(validator, Validator.class);
		} else {
			return(o)->Validator.class.cast(o.getClass().getMethod(validator).invoke(o));
		}
	}
	
}
