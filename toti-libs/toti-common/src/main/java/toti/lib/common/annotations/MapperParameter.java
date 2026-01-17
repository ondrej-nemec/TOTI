package toti.lib.common.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import toti.lib.common.functions.Mapper;

/**
 * Annotation marks class attribute for {@link Mapper}
 * Set behaviour of serialization and deserialization using {@link Mapper}
 * 
 * @author Ondřej Němec
 * 
 * @see Mapper
 *
 */
@Retention(RUNTIME)
public @interface MapperParameter {

	/**
	 * Define array of {@link MapperType}
	 * <p>
	 * If no {@link MapperType} given, annotation has no impact
	 * 
	 * @return array of {@link MapperType}
	 */
	MapperType[] value();
	
}
