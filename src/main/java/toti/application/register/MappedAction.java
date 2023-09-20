package toti.application.register;

import java.lang.reflect.Method;
import java.util.Arrays;

import ji.socketCommunication.http.HttpMethod;
import toti.security.AuthMode;

public class MappedAction {

	// for template link
	private final String moduleName;
	private final String className;
	private final String methodName;
	
	private final Method action;
	private final Factory<?> classFactory;
	//private final List<Class<?>> methodParameters;
	
	private final HttpMethod[] methods;
	
	private final AuthMode securityMode;
	
	public static MappedAction router(
			String module, Method method, Factory<?> factory,
			AuthMode securityMode, HttpMethod[] methods) {
		return new MappedAction(
			module, null, null,
			method, factory, securityMode, methods
		);
	}

	public MappedAction(
			String moduleName, String className, String methodName,
			Method action, Factory<?> classFactory, //List<Class<?>> methodParameters,
			AuthMode securityMode, HttpMethod[] methods) {
		this.moduleName = moduleName;
		this.className = className;
		this.methodName = methodName;
		
		this.methods = methods;
		this.action = action;
		//this.methodParameters = methodParameters;
		
		this.securityMode = securityMode;
		this.classFactory = classFactory;
	}
	
	public Factory<?> getClassFactory() {
		return classFactory;
	}
	
	public Method getAction() {
		return action;
	}
	
	public boolean isSecured() {
		return securityMode != AuthMode.NO_TOKEN;
	}
	
	public AuthMode getSecurityMode() {
		return securityMode;
	}
	
	public String getModuleName() {
		return moduleName;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}
	
	public HttpMethod[] getMethods() {
		return methods;
	}
	
	protected boolean asssertNames(String module, String controller, String method) {
		return module.equals(this.moduleName) && controller.equals(this.className) && method.equals(this.methodName);
	}

	@Override
	public String toString() {
		return "MappedAction [moduleName=" + moduleName + ", className=" + className + ", methodName=" + methodName
				+ ", action=" + action + ", classFactory=" + classFactory + ", methods=" + Arrays.toString(methods)
				+ ", securityMode=" + securityMode + "]";
	}
	
}
