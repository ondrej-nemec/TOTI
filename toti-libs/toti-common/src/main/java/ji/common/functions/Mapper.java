package ji.common.functions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import ji.common.annotations.MapperIgnored;
import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;
import ji.common.structures.DictionaryValue;
import ji.common.structures.ListDictionary;
import ji.common.structures.MapDictionary;
import ji.common.structures.Tuple2;

/**
 * Class is able to convert data class (entity) to {@link Map} (serialize) and {@link Map} back to entity (parse).
 * 
 * @author Ondřej Němec
 *
 */
public class Mapper {
	
	/**
	 * Returns new instance of {@link Mapper}
	 * 
	 * @return {@link Mapper}
	 */
	public static Mapper get() {
		return new Mapper();
	}
	
	/**
	 * Serialize data class to {@link Map}
	 * 
	 * During serialization JI come thru all fields of entity and uses then as key in new {@link Map}.
	 * Values of fields become values. 
	 * Serializations works recursivelly.
	 * <p>
	 * If you wish ignore field during serialization, just add {@link MapperIgnored} annotation.
	 * <p>
	 * The null field value will not be serialized if <code>ignoreOnNull</code> is set. 
	 * <p>
	 * If you do not wish use field name as key, you need to annotate this field with {@link MapperParameter}
	 *  and as value to put {@link MapperType}.
	 * 
	 * @param value Object what will be serialized
	 * @return {@link Map} serialized object
	 */
	public Map<String, Object> serialize(Object value) {
		return serialize(value, null);
	}
	
	/**
	 * Serialize data class to {@link Map}
	 * 
	 * One entity can be serialized in more ways that are defined by string key.
	 * <p>
	 * {@link MapperIgnored} contains array of strings. If one of then is equals to given key, field will be ignored.
	 * If the array is empty (default state), field will be always ignored. Otherwise will be used. 
	 * <p>
	 * {@link MapperParameter} can has more than one {@link MapperType}.
	 * {@link MapperType} has <code>key</code> parameter that specified the 'way'.
	 * Default is empty string and means 'use if no others options'.
	 * 
	 * For more details see {@link Mapper#serialize(Object)}
	 * 
	 * @param value Object what will be serialized
	 * @param key String use case identifier
	 * @return {@link Map} serialized object
	 */
	public Map<String, Object> serialize(Object value, String key) {
		try {
			Map<String, Object> json = new HashMap<>();
			Field[] fields = value.getClass().getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals("this$0")) {
					continue;
				}
				if (field.isAnnotationPresent(MapperIgnored.class)) {
					String[] annotationKeys = field.getAnnotation(MapperIgnored.class).value();
					if (annotationKeys.length == 0 || Arrays.asList(annotationKeys).contains(key)) {
						continue;
					}
				}
				field.setAccessible(true);
				String name = field.getName();
				if (field.isAnnotationPresent(MapperParameter.class)) {
					MapperType toUse = null;
					for (MapperType mapperType : field.getAnnotation(MapperParameter.class).value()) {
						if (mapperType.key().equals("")) {
							toUse = mapperType; // default
						} else if (key != null && mapperType.key().equals(key)) {
							toUse = mapperType; // key match
						}
					}
					if (toUse != null && toUse.ignoreOnNull() && field.get(value) == null) {
						continue;
					} else if (toUse != null) {
						name = toUse.value();
					}
				}
				json.put(name, field.get(value));
			}
			return json;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Parse {@link Map} to given data object
	 * 
	 * Parsing is reverse of serialize. From {@link Map} create entity (Or from list of map, result will be list of entity).
	 * During serialization JI come thru all fields of entity and try finds for it value in {@link Map}.
	 * As key is used field name. Parsing works recursively.
	 * <p>
	 * If you do not wish use field name as key, you need to annotate this field with {@link MapperParameter}
	 *  and as value to put {@link MapperType}.
	 * <p>
	 * {@link LocalTime}, {@link LocalDate}, {@link LocalDateTime} and {@link ZonedDateTime}
	 * is parsed from string using standart java formats. 
	 * If datetime string is in different format, use <code>dateTimeFormat</code> attribute of {@link MapperType}.
	 * 
	 * @param <T> data object class
	 * @param clazz {@link Class} final data object class
	 * @param source {@link Map} or {@link Collection} source data to parse
	 * @return new instance of given class
	 * @throws Exception
	 */
	public <T> T parse(Class<T> clazz, Object source) throws Exception {
		return read(clazz, source, null, null, null, (v)->{});
	}
	
	/**
	 * Parse {@link Map} to given data object
	 * <p>
	 * One entity can be parsed in more ways that are defined by string key.
	 * This key is the second (optional) parameter to <code>parse</code> method.
	 * {@link MapperParameter} can has more than one {@link MapperType}.
	 * {@link MapperType} has <code>key</code> parameter that specified the 'way'.
	 * Default is empty string and means 'use if no others options'.
	 * <p> 
	 * For more details see {@link Mapper#parse(Class, Object)}
	 * 
	 * @param <T> data object class
	 * @param clazz {@link Class} final data object class
	 * @param source {@link Map} or {@link Collection} source data to parse
	 * @param key
	 * @return new instance of given class
	 * @throws Exception
	 */
	public <T> T parse(Class<T> clazz, Object source, String key) throws Exception {
		return read(clazz, source, key, null, null, (v)->{});
	}
	
	private <T> T read(Class<T> clazz, Object source, String only, Object valueCandidate, Type generic, Consumer<DictionaryValue> onValue) throws Exception {
		DictionaryValue parameterValue = new DictionaryValue(source);
		onValue.accept(parameterValue);
		T target = createNewInstance(valueCandidate, clazz);
		if (source == null) {
			return null;
		} else if ( Map.class.isAssignableFrom(source.getClass()) && !Map.class.isAssignableFrom(clazz)) {
			Field[] fields = clazz.getDeclaredFields();
			MapDictionary<String> values = parameterValue.getDictionaryMap();
			for (Field field : fields) {
				field.setAccessible(true);
				String parameterName = field.getName();
				
				MapperType toUse = null;
				if (field.isAnnotationPresent(MapperParameter.class)) {
					for (MapperType mapperType : field.getAnnotation(MapperParameter.class).value()) {
						if (mapperType.key().equals("")) {
							toUse = mapperType; // default
						} else if (only != null && mapperType.key().equals(only)) {
							toUse = mapperType; // key match
						}
					}
					if (toUse != null) {
						parameterName = toUse.value();
					}
				}
				
				Object newCandidate = values.get(parameterName);
				if (newCandidate == null) {
					continue;
				}
				
				Object value = read(field.getType(), newCandidate, only, field.get(target), field.getGenericType(), setDV(toUse));
				Method m = getMethod("set", parameterName, clazz, field.getType());
				if (m == null) {
					field.set(target, value);
				} else {
					m.invoke(target, value);
				}
			}
		} else if (Collection.class.isAssignableFrom(clazz) || ListDictionary.class.isAssignableFrom(clazz)) {
			Tuple2<Class<?>, Type> types = getGenericClass(generic, 0);
			Class<?> collectionItemClass = types._1();
			Method m = getMethod("add", clazz, Object.class);
			parameterValue.getDictionaryList().forEach((index, item)->{
				m.invoke(target, read(collectionItemClass, item.getValue(), only, null, types._2(), onValue));
			});
		} else if (Map.class.isAssignableFrom(clazz) || MapDictionary.class.isAssignableFrom(clazz)) {
		//	Class<?> mapKeyClass = getGenericClass(generic, 0);
			Tuple2<Class<?>, Type> types = getGenericClass(generic, 1);
			Class<?> mapValueClass = types._1();
			Method m = getMethod("put", clazz, Object.class, Object.class);
			parameterValue.getDictionaryMap().forEach((key, item)->{
				m.invoke(target, key, read(mapValueClass, item.getValue(), only, null, types._2(), onValue));
			});
		} else {
			return parameterValue.getValue(clazz);
		}
		return target;
	}
	
	private Consumer<DictionaryValue> setDV(MapperType toUse) {
		return (v)->{
			if (toUse != null && !toUse.dateTimeFormat().equals("")) {
				v.withDateTimeFormat(toUse.dateTimeFormat());
				if (!toUse.key().equals("")) {
					v.withOnlyKey(toUse.key());
				}
			}
		};
	}
		
	@SuppressWarnings("unchecked")
	private <T> T createNewInstance(Object valueCandidate, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (valueCandidate != null) {
			return new DictionaryValue(valueCandidate).getValue(clazz);
		}
		if (clazz.equals(Map.class)) {
			return (T)new HashMap<>();
		}
		if (clazz.isInterface() && List.class.isAssignableFrom(clazz)) {
			return (T)new LinkedList<>();
		}
		if (clazz.isInterface() && Set.class.isAssignableFrom(clazz)) {
			return (T)new HashSet<>();
		}
		try {
			clazz.getConstructor();
		} catch (NoSuchMethodException e) {
			// not non-parameters constructor - enum, primitives, numbers, ...
			return null;
		}
		/*if (clazz.isEnum()) {
			return null;
		}*/
		 // return clazz.newInstance();
		return clazz.getDeclaredConstructor().newInstance();
	}

	private Tuple2<Class<?>, Type> getGenericClass(Type field, int index) {
		Type type = ParameterizedType.class.cast(field)
				.getActualTypeArguments()[index];
		if (ParameterizedType.class.isInstance(type)) {
			ParameterizedType parametrized = ParameterizedType.class.cast(type);
			return new Tuple2<>(Class.class.cast(parametrized.getRawType()),parametrized);
		}
		return new Tuple2<>(Class.class.cast(type), null);
	}
	
	private Method getMethod(String prefix, String parameterName, Class<?> clazz, Class<?>...classes) {
		String name = prefix + (parameterName.charAt(0) + "").toUpperCase() + parameterName.substring(1);
		return getMethod(name, clazz, classes);
	}
	
	private Method getMethod(String name, Class<?> clazz, Class<?>...classes) {
		try {
			return clazz.getMethod(name, classes);
		} catch (NoSuchMethodException | SecurityException e) {
			return null;
		}
	}

}
