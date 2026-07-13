package toti.core.application.register;

import java.lang.reflect.Method;
import java.util.Arrays;

import toti.lib.tcpip.enums.HttpMethod;

public class MappedAction {

	// for template link
	private final String moduleName;
	private final String className;
	private final String methodName;
	private final String parameters;
	
	private final Method action;
	private final Factory<?> classFactory;
	
	private final HttpMethod[] methods;
	
	public static MappedAction router(String module, Method method, Factory<?> factory,  HttpMethod[] methods) {
		return new MappedAction(
			module, null, null, "[]",
			method, factory, methods
		);
	}
	
	public static MappedAction totiAnswer(String module) {
		return new MappedAction(
			module, null, null,
			null, null, null, null
		);
	}
	
	public static MappedAction test(String moduleName, String className, String methodName) {
		return test(moduleName, className, methodName, "[]");
	}
	
	public static MappedAction test(String moduleName, String className, String methodName, String parameters) {
		return new MappedAction(
			moduleName, className, methodName, parameters,
			null, null, null
		);
	}

	public MappedAction(
			String moduleName, String className, String methodName, String parameters,
			Method action, Factory<?> classFactory, HttpMethod[] methods) {
		this.moduleName = moduleName;
		this.className = className;
		this.methodName = methodName;
		this.parameters = parameters;
		
		this.methods = methods;
		this.action = action;

		this.classFactory = classFactory;
	}
	
	public Factory<?> getClassFactory() {
		return classFactory;
	}
	
	public Method getAction() {
		return action;
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
		return asssertNames(module, controller, method, "[]");
	}
	
	protected boolean asssertNames(String module, String controller, String method, String parameters) {
		return module.equals(this.moduleName) && controller.equals(this.className) 
				&& method.equals(this.methodName) && parameters.equals(this.parameters);
	}
	
	public boolean assertForTest(MappedAction action) {
		if (action == null) {
			return false;
		}
		return moduleName.equals(action.moduleName) && className.equals(action.className)
				&& methodName.equals(action.methodName) && parameters.equals(this.parameters);
	}

	public String simpleString() {
		return String.format("Action[M: %s, C: %s, M: %s, P: %s]", moduleName, className, methodName, parameters);
	}
	
	@Override
	public String toString() {
		return "MappedAction ["
				+ moduleName + ":" + className + ":" + methodName + ":" + parameters
				+ " => action=" + action /*+ ", classFactory=" + classFactory */+ ", methods=" + Arrays.toString(methods)
				+ "]";
	}

	public int logHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + Arrays.hashCode(methods);
		result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
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
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((classFactory == null) ? 0 : classFactory.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		result = prime * result + Arrays.hashCode(methods);
		result = prime * result + ((moduleName == null) ? 0 : moduleName.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MappedAction other = (MappedAction) obj;
		if (action == null) {
			if (other.action != null) {
				return false;
			}
		} else if (!action.equals(other.action)) {
			return false;
		}
		if (classFactory == null) {
			if (other.classFactory != null) {
				return false;
			}
		} else if (!classFactory.equals(other.classFactory)) {
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
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		return true;
	}
	
}
