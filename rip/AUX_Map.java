package toti.register;

import java.util.List;

import ji.common.functions.FilesList;
import ji.socketCommunication.http.HttpMethod;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Secured;
import toti.security.AuthMode;
import toti.url.MappedAction;

public class AUX_Map {

	public void map(Param root, Module module, Register register) throws Exception {
		List<String> files = FilesList.get(module.getControllersPath(), true).getFiles();
		String folder = module.getControllersPath();
		
		Param moduleParam = getParam(module.getName(), root);
		for (String file : files) {
			if (!file.endsWith(".class")) {
				continue;
			}
	    	int index = file.lastIndexOf("/");
	    	String pathPart = (index > 0 ? "/" + file.replace(file.substring(index), "") : "");
	    	Class<?> clazz =  Class.forName( (folder + "/" + file).replaceAll("/", ".").replace(".class", "") );
	    	
			if ( ! (clazz.isInterface() || clazz.isAnonymousClass() || clazz.isPrimitive()) 
		    		&& clazz.isAnnotationPresent(Controller.class)) {
				String controllerPart = clazz.getAnnotation(Controller.class).value();
				Param controllerParam = getParam(controllerPart, moduleParam);
		    	for (java.lang.reflect.Method m : clazz.getMethods()) {
		    		if (m.isAnnotationPresent(Action.class)) {
		    			Action actionAnotation = m.getAnnotation(Action.class);
		    			HttpMethod[] methods = actionAnotation.methods();
		    			String actionPart = actionAnotation.path();
		    			Param actionParam = getParam(actionPart, controllerParam);
		    			AuthMode securityMode = AuthMode.NO_TOKEN;
		    			if (m.isAnnotationPresent(Secured.class)) {
		    				securityMode = m.getAnnotation(Secured.class).value();
		    			}
		    			/*List<Class<?>> methodParameters = new LinkedList<>();
		    			for (Parameter p : m.getParameters()) {
		    				methodParameters.add(p.getType());
		    			}*/
		    			MappedAction action = new MappedAction(
		    				module.getName(), clazz.getName(), m.getName(),
		    				m, register.getFactory(clazz.getName()),// methodParameters,
		    				securityMode, methods
		    			);
		    			for (HttpMethod method : methods) {
		    				actionParam.addAction(method, action);
		    			}
		    		}
		    	}
			}
		}
	}
	
	private Param getParam(String part, Param parent) {
		if (part == null || part.isEmpty()) {
			return parent;
		}
		return parent.addChild(part);
	}
	
}
