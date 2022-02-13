package toti.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import toti.security.Mode;

@Retention(RUNTIME)
public @interface Secured {

	Domain[] value() default {};
	
	Mode mode() default Mode.HEADER;
	
}
