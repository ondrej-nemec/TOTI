package ji.common.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import ji.common.functions.Mapper;

/**
 * One use case of {@link Mapper}
 * 
 * @author Ondřej Němec
 *
 */
@Retention(RUNTIME)
public @interface MapperType {

	/**
	 * Define name of parameter. 
	 * 
	 * @return String
	 */
	String value();
	
	/**
	 * Define case when {@link MapperType} is used. Empty means all cases.
	 * 
	 * @return
	 */
	String key() default "";
	
	/**
	 * If true, parameter will be ignored during serializatin if is null.
	 * 
	 * @return
	 */
	boolean ignoreOnNull() default false;
	
}
