package toti.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import toti.security.AuthMode;

@Retention(RUNTIME)
public @interface Secured {
	
	AuthMode value() default AuthMode.HEADER;
	
}
