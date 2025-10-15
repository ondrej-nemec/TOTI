package ji.common.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Class wrap {@link Collection} with implementation of {@link Dictionary}
 * 
 * @author Ondřej Němec
 */
public class ListDictionary implements Dictionary<Integer>, Iterable<Tuple2<Integer, Object>> {
	
	private final Collection<Object> collection;
	
	/**
	 * Create new instance as wrapper for {@link LinkedList}
	 * 
	 * @param <S> the type of items
	 * @return {@link ListDictionary}
	 */
	public static ListDictionary linkedList() {
		return new ListDictionary(new LinkedList<>());
	}

	/**
	 * Create new instance as wrapper for {@link ArrayList}
	 * 
	 * @param <S> the type of items
	 * @return {@link ListDictionary}
	 */
	public static ListDictionary arrayList() {
		return new ListDictionary(new ArrayList<>());
	}

	/**
	 * Create new instance as wrapper for {@link HashSet}
	 * 
	 * @param <S> the type of items
	 * @return {@link ListDictionary}
	 */
	public static ListDictionary hashSet() {
		return new ListDictionary(new HashSet<>());
	}

	/**
	 * 
	 * @param list {@link Collection} wrapped collection
	 */
	public ListDictionary(Collection<Object> list) {
		this.collection = list;
	}
	
	@Override
	public Object getValue(Integer key) {
		if (collection instanceof List<?>) {
			return List.class.cast(collection).get(key);
		}
		throw new RuntimeException("Operation is not supported");
	}
	
	/**
	 * Add new item
	 * 
	 * @param index {@link Integer}
	 * @param value S
	 * @return {@link ListDictionary} self
	 */
	@SuppressWarnings("unchecked")
	public ListDictionary add(Integer index, Object value) {
		if (collection instanceof List<?>) {
			List.class.cast(collection).add(index, value);
			return this;
		}
		throw new RuntimeException("Operation is not supported");
	}
	
	/**
	 * Add all items from given {@link Collection}
	 * 
	 * @param values {@link Collection}
	 * @return {@link ListDictionary} self
	 */
	public ListDictionary addAll(Collection<Object> values) {
		collection.addAll(values);
		return this;
	}
	
	/**
	 * Add new item at the end
	 * 
	 * @param value S
	 * @return {@link ListDictionary} self
	 */
	public ListDictionary add(Object value) {
		collection.add(value);
		return this;
	}
	
	/**
	 * Remove item on given index
	 * 
	 * @param index {@link Integer}
	 * @return {@code true} if an element was removed as a result of this call
	 */
	public boolean remove(Integer index) {
		return collection.remove(index);
	}
	
	/**
     * Returns the number of elements in this collection.  If this collection
     * contains more than {@code Integer.MAX_VALUE} elements, returns
     * {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this collection
     */
	public int size() {
		return collection.size();
	}
	
	/**
	 * Convert wrapped {@link Collection} to {@link List}
	 * 
	 * @return {@link List}
	 */
	@SuppressWarnings("unchecked")
	public List<Object> toList() {
		if (collection instanceof List<?>) {
			return List.class.cast(collection);
		}
		return new LinkedList<>(collection);
	}
	
	/**
	 * Convert wrapped {@link Collection} to {@link Set}
	 * 
	 * @return {@link Set}
	 */
	@SuppressWarnings("unchecked")
	public Set<Object> toSet() {
		if (collection instanceof Set<?>) {
			return Set.class.cast(collection);
		}
		return new HashSet<>(collection);
	}

	/**
	 * Convert wrapped {@link Collection} to array
	 * 
	 * @return array
	 */
	public Object[] toArray() {
		return collection.toArray();
	}
	
	/**
	 * Iterate over all items. The ginve action is applied on each item.
	 * 
	 * @param action {@link Consumer} applied on each item, the parameter is item
	 */
	public void forEach2(Consumer<Object> action) {
		collection.forEach(action);
	}
	
	/**
	 * Iterate over all items. The ginve action is applied on each item.
	 * 
	 * @param <E> the type of expected {@link Exception}
	 * @param action {@link ThrowingConsumer} applied on each item, the parameter is item
	 * @throws E expected {@link Exception}
	 */
	/*public <E extends Throwable> void forEach(ThrowingConsumer<DictionaryValue, E> action) throws E {
		for (Object s : collection) {
			action.accept(new DictionaryValue(s));
		}
	}*/
	
	/**
	 * Iterate over all items. The ginve action is applied on each item.
	 * 
	 * @param <E> the type of expected {@link Exception}
	 * @param action {@link ThrowingBiConsumer} applied on each item, parameters are {@link Integer} index and the item
	 * @throws E expected {@link Exception}
	 */
	public <E extends Throwable> void forEach(ThrowingBiConsumer<Integer, DictionaryValue, E> action) throws E {
		int index = 0;
		for (Object s : collection) {
			action.accept(index++, new DictionaryValue(s));
		}
	}
	
	@Override
	public String toString() {
		return collection.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof ListDictionary) && !(obj instanceof List)) {
			return false;
		}
		if (obj instanceof List) {
			List<?> convertedList = (List<?>)obj;
			return collection.equals(convertedList);
		}
		ListDictionary dictionary = (ListDictionary)obj;
		if (collection.size() != dictionary.collection.size()) {
			return false;
		}
		Iterator<?> targetIt = dictionary.collection.iterator();
		for (Object item : collection) {
			if (!item.equals(targetIt.next())) {
				return false;	
			}
		}
		return true;
		// return collection.equals(dictionary.collection);
	}

	@Override
	public void clear() {
		collection.clear();
	}

	@Override
	public Iterator<Tuple2<Integer, Object>> iterator() {
		return new Iterator<Tuple2<Integer, Object>>() {
			private int index = 0;
			private List<Object> values = new ArrayList<>(collection);
			@Override
			public boolean hasNext() {
				return index < collection.size();
			}

			@Override
			public Tuple2<Integer, Object> next() {
				Object value = values.get(index);
				int i = index;
				index++;
				return new Tuple2<>(i, value);
			}
			
		};
	}

}
