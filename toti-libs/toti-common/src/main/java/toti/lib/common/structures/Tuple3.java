package toti.lib.common.structures;

/**
 * Class holds three objects. Class is immutable.
 * 
 * @author Ondřej Němec
 *
 *
 * @param <F> the type of first object
 * @param <S> the type of second object
 * @param <T> the type of third object
 */
public class Tuple3<F, S, T> {

	private final F _1;
	
	private final S _2;
	
	private final T _3;
	
	/**
	 * Create new {@link Tuple3} and set object
	 * 
	 * @param _1 first object
	 * @param _2 second object
	 * @param _3 third insobjecttance
	 */
	public Tuple3(F _1, S _2, T _3) {
		this._1 = _1;
		this._2 = _2;
		this._3 = _3;
	}

	/**
	 * Returns first object
	 * 
	 * @return object
	 */
	public F _1() {
		return _1;
	}

	/**
	 * Returns second object
	 * 
	 * @return object
	 */
	public S _2() {
		return _2;
	}

	/**
	 * Returns third object
	 * 
	 * @return object
	 */
	public T _3() {
		return _3;
	}

	@Override
	public String toString() {
		return String.format("Tuple(%s,%s. %s)", _1, _2, _3);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof Tuple3<?, ?, ?>)) {
			return false;
		}
		Tuple3<?, ?, ?> t = Tuple3.class.cast(obj);
		if (!check(_1, t._1)) {
			return false;
		}
		if (!check(_2, t._2)) {
			return false;
		}
		if (!check(_3, t._3)) {
			return false;
		}
		return true;
	}

	private boolean check(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}
	
}
