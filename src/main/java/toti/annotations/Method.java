package toti.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ji.socketCommunication.http.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
public @interface Method {

	HttpMethod[] value();
	
}
