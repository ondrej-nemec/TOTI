package toti.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import toti.security.Action;

@Retention(RUNTIME)
public @interface Domain {

	String name();
	
	Action action();
	
	String owner() default "";
	
}
