package toti.annotations;

import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import common.exceptions.LogicException;
import common.functions.FilesList;
import socketCommunication.http.HttpMethod;
import toti.annotations.url.Action;
import toti.annotations.url.Controller;
import toti.annotations.url.Domain;
import toti.annotations.url.Method;
import toti.annotations.url.Param;
import toti.annotations.url.ParamUrl;
import toti.annotations.url.Params;
import toti.annotations.url.Secured;
import toti.registr.Registr;
import toti.templating.TemplateFactory;
import toti.validation.Validator;

public class LoadUrls {

	public static List<MappedUrl> loadUrlMap(Map<String, TemplateFactory> modules) throws Exception {
		List<MappedUrl> mapping = new LinkedList<>();
		for (String folder : modules.keySet()) {
			map(FilesList.get(folder, true).getFiles(), folder, mapping, modules.get(folder).getModuleName());
		}
	    return mapping;
	}
	
	private static void map(List<String> files, String folder, List<MappedUrl> mapping, String moduleName) throws Exception {
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
