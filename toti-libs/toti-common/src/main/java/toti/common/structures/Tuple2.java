package toti.common.structures;

/**
 * Class holds two objects. Class is immutable.
 * 
 * @author Ondřej Němec
 *
 * @param <F> the type of first object
 * @param <S> the type of second object
 */
public class Tuple2<F, S> {

	private final F _1;
	
	private final S _2;
	
	/**
	 * Create new {@link Tuple2} and set objects
	 * 
	 * @param _1  first object
	 * @param _2 second object
	 */
	public Tuple2(F _1, S _2) {
		this._1 = _1;
		this._2 = _2;
	}

	/**
	 * Returns first object
	 * 
	 * @return Object
	 */
	public F _1() {
		return _1;
	}

	/**
	 * Returns second object
	 * 
	 * @return Object
	 */
	public S _2() {
		return _2;
	}
	
	@Override
	public String toString() {
		return String.format("Tuple(%s,%s)", _1, _2);
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( ! (obj instanceof Tuple2<?, ?>)) {
			return false;
		}
		Tuple2<?, ?> t = Tuple2.class.cast(obj);
		if (!check(_1, t._1)) {
			return false;
		}
		if (!check(_2, t._2)) {
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
