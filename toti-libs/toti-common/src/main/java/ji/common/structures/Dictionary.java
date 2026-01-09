package ji.common.structures;

import java.util.List;
import java.util.Map;

import ji.common.structures.dictionary.ScalarStructure;

/**
 * Interface represents structure holiding another object (items). Each item is accesible by identifier.
 * <p>
 * Each item can be converted or parsed using {@link DictionaryValue}
 * 
 * @author Ondřej Němec
 *
 * @param <S> the type of identifier
 * @see DictionaryValue
 */
public interface Dictionary<S> extends ScalarStructure<S> {

	/**
	 * Returns value by given identificator
	 * 
	 * @param key
	 * @return value on given key in original type
	 */
	@Override
	Object getValue(S key);
	
	void clear();
	
	/**
	 * Returns value by given identificator
	 * 
	 * @param key S identifier
	 * @return {@link DictionaryValue}
	 */
	default DictionaryValue getDictionaryValue(S key) {
		return new DictionaryValue(getValue(key));
	}

	/**
	 * Returns value by given identificator

	 * @param key S identifier
	 * @return not converted value
	 */
	default Object get(S key) {
		return getValue(key);
	}
	
	/**
	 * See {@link DictionaryValue#is(Class)}
	 * 
	 * @param key S identifier
	 * @param clazz
	 * @return true if identifier is contained and item can be converted to given {@link Class}
	 */
	default boolean is(S key, Class<?> clazz) {
		return getDictionaryValue(key).is(clazz);
	}

	/**
	 * See {@link DictionaryValue#getDictionaryList()}
	 * 
	 * @param key S identifier
	 * @return {@link ListDictionary} or null
	 */
	default ListDictionary getDictionaryList(S key) {
		return getDictionaryValue(key).getDictionaryList();
	}

	/**
	 * See {@link DictionaryValue#getDictionaryMap()}
	 * 
	 * @param key S identifier
	 * @return {@link MapDictionary} or null
	 */
	default <T> MapDictionary<T> getDictionaryMap(S key) {
		return getDictionaryValue(key).getDictionaryMap();
	}

	/**
	 * See {@link DictionaryValue#getList()}
	 * 
	 * @param key S identifier
	 * @return {@link List} or null
	 */
	default <T> List<T> getList(S key) {
		return getDictionaryValue(key).getList();
	}

	/**
	 * See {@link DictionaryValue#getMap()}
	 * 
	 * @param key S identifier
	 * @return {@link Map} or null
	 */
	default <K, V> Map<K, V> getMap(S key) {
		return getDictionaryValue(key).getMap();
	}

}
