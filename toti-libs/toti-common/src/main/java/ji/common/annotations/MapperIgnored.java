package ji.common.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import ji.common.functions.Mapper;

/**
 * Ignore parameter during serialization
 * Annotation for {@link Mapper}. If class attribute has this annotation, will be ignored during serialization.
 * <p>
 * Active only if annotation value is not specified or if serialization key is not included in annotation value.
 * 
 * @author Ondřej Němec
 * 
 * @see Mapper
 *
 */
@Retention(RUNTIME)
public @interface MapperIgnored {

	/**
	 * Active cases. Empty means active always.
	 * 
	 * @return array of {@link String}
	 */
	String[] value() default {};
	
}
