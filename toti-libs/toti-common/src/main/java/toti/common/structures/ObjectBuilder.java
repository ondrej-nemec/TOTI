package toti.common.structures;

/**
 * Class holds one object. Class is mutable.
 * 
 * @author Ondřej Němec
 *
 * @param <T> the type of object
 */
public class ObjectBuilder<T> {
	private T t;

	/**
	 * Create new instance without object
	 */
    public ObjectBuilder() {}

    /**
     * Create new instance and set object
     *  
     * @param t object
     */
    public ObjectBuilder(T t) {
        this.t = t;
    }

    /**
     * Returns holded object
     * 
     * @return object
     */
    public T get() {
        return t;
    }

    /**
     * Set new object
     * 
     * @param t
     */
    public void set(T t) {
        this.t = t;
    }
    
    /**
     * Remove holded object
     */
    public void clear() {
        this.t = null;
    }

    /**
     * Check if {@link ObjectBuilder} instance holds object
     * 
     * @return true if object is not null
     */
    public boolean isPresent() {
        return t != null;
    }
}
