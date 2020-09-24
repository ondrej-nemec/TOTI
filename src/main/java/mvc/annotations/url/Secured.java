package mvc.annotations.url;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface Secured {

	Domain[] value() default {};
	
}
