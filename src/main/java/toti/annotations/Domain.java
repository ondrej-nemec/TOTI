package toti.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import toti.security.Owner;

import toti.security.Action;

@Retention(RUNTIME)
public @interface Domain {

	String name();
	
	Action action();
	
	Owner mode() default Owner.REQUERST;
	
	String owner() default "";
	
}
