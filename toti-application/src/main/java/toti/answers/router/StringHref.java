package toti.answers.router;

import java.util.List;
import java.util.Map;

public class StringHref {

	private final String controller;
	private final String method;
	private final Map<String, Object> queryParams;
	private final List<Object> pathParams;
	
	public StringHref(String controller, String method, Map<String, Object> queryParams, List<Object> pathParams) {
		this.controller = controller;
		this.method = method;
		this.queryParams = queryParams;
		this.pathParams = pathParams;
	}

	public String getController() {
		return controller;
	}

	public String getMethod() {
		return method;
	}

	public Map<String, Object> getQueryParams() {
		return queryParams;
	}

	public List<Object> getPathParams() {
		return pathParams;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((controller == null) ? 0 : controller.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((pathParams == null) ? 0 : pathParams.hashCode());
		result = prime * result + ((queryParams == null) ? 0 : queryParams.hashCode());
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
		StringHref other = (StringHref) obj;
		if (controller == null) {
			if (other.controller != null) {
				return false;
			}
		} else if (!controller.equals(other.controller)) {
			return false;
		}
		if (method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!method.equals(other.method)) {
			return false;
		}
		if (pathParams == null) {
			if (other.pathParams != null) {
				return false;
			}
		} else if (!pathParams.equals(other.pathParams)) {
			return false;
		}
		if (queryParams == null) {
			if (other.queryParams != null) {
				return false;
			}
		} else if (!queryParams.equals(other.queryParams)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "StringHref [controller=" + controller + ", method=" + method + ", queryParams=" + queryParams
				+ ", pathParams=" + pathParams + "]";
	}
	
}
