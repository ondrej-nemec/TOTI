package toti.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import toti.tcpip.enums.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
public @interface Action {
	
	String path() default "";
	
	HttpMethod[] methods() default {HttpMethod.GET};
	
}
