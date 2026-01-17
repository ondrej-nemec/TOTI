package toti.common.structures;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class serves as {@link Collection} builder
 * 
 * @author Ondřej Němec
 *
 * @param <S> the type of elements
 */
public class ListInit<S> {

	private final Collection<S> list;
	
	/**
	 * Create new {@link Collection} with first item
	 * Item determines collection generic type
	 * 
	 * @param item first collectio item
	 */
	public ListInit(S item) {
		this(create(item));
	}
	
	private static <S> List<S> create(S item) {
		List<S> list = new LinkedList<>();
		list.add(item);
		return list;
	}
	
	/**
	 * Create with empty {@link Collection}
	 */
	public ListInit() {
		this(new LinkedList<>());
	}
	
	/**
	 * Create with given {@link Collection}
	 * 
	 * @param list {@link Collection} initial collection
	 */
	public ListInit(Collection<S> list) {
		this.list = list;
	}
	
	/**
	 * Add item to collection
	 * 
	 * @param item
	 * @return {@link ListInit} self
	 */
	public ListInit<S> add(S item) {
		list.add(item);
		return this;
	}
	
	/**
	 * Convert {@link Collection} to {@link List}
	 * 
	 * @return {@link List}
	 */
	public List<S> toList() {
		return new LinkedList<>(list);
	}
	
	/**
	 * Convert {@link Collection} to {@link Set}
	 * 
	 * @return {@link Set}
	 */
	public Set<S> toSet() {
		return new HashSet<>(list);
	}
	
	/**
	 * Convert {@link Collection} to {@link ListDictionary}
	 * 
	 * @return {@link ListDictionary}
	 */
	public ListDictionary toDictionaryList() {
		return new DictionaryValue(list).getDictionaryList();
	}
	
}
