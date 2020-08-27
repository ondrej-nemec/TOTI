package mvc.urlMapping;

import socketCommunication.http.HttpMethod;

public @interface Method {

	HttpMethod[] value();
	
}
