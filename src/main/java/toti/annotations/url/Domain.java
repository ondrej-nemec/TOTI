package toti.annotations.url;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import helper.Action;

@Retention(RUNTIME)
public @interface Domain {

	String name();
	
	Action[] actions();
	
}
