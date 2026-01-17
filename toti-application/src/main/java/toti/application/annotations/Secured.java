package toti.application.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import toti.application.answers.request.AuthMode;

@Retention(RUNTIME)
public @interface Secured {
	
	AuthMode value() default AuthMode.HEADER;
	
}
