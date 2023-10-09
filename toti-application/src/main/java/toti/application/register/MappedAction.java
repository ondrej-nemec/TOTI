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
	
	public static MappedAction test(String moduleName, String className, String methodName) {
		return new MappedAction(
				moduleName, className, methodName,
				null, null, null, null
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
	
	public boolean assertForTest(MappedAction action) {
		if (action == null) {
			return false;
		}
		return moduleName.equals(action.moduleName) && className.equals(action.className) && methodName.equals(action.methodName);
	}

	public String simpleString() {
		return String.format("Action[M: %s, C: %s, M: %s]", moduleName, className, methodName);
	}
	
	@Override
	public String toString() {
		return "MappedAction [moduleName=" + moduleName + ", className=" + className + ", methodName=" + methodName
				+ ", action=" + action + ", classFactory=" + classFactory + ", methods=" + Arrays.toString(methods)
				+ ", securityMode=" + securityMode + "]";
	}

	public int logHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + Arrays.hashCode(methods);
		result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + ((securityMode == null) ? 0 : securityMode.hashCode());
		return result;
	}

	public boolean logEquals(MappedAction other) {
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
		if (className == null) {
			if (other.className != null) {
				return false;
			}
		} else if (!className.equals(other.className)) {
			return false;
		}
		if (methodName == null) {
			if (other.methodName != null) {
				return false;
			}
		} else if (!methodName.equals(other.methodName)) {
			return false;
		}
		if (!Arrays.equals(methods, other.methods)) {
			return false;
		}
		if (moduleName == null) {
			if (other.moduleName != null) {
				return false;
			}
		} else if (!moduleName.equals(other.moduleName)) {
			return false;
		}
		if (securityMode != other.securityMode) {
			return false;
		}
		return true;
	}
	
}
