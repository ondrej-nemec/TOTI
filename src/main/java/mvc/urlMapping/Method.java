package mvc.urlMapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import socketCommunication.http.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
public @interface Method {

	HttpMethod[] value();
	
}
