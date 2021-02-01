package toti.annotations.url;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import acl.Action;

@Retention(RUNTIME)
public @interface Domain {

	String name();
	
	Action action();
	
	String owner() default "";
	
}
