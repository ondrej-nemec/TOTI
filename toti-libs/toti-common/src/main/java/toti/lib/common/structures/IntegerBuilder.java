package toti.lib.common.structures;

import java.util.function.Function;

/**
 * Class holds primite int
 * 
 * @author Ondřej Němec
 *
 */
public class IntegerBuilder {

	private int value;
	
	/**
	 * Create new instance with initial value zero
	 */
	public IntegerBuilder() {
		this(0);
	}
	
	/**
	 * Create new instance with given initial value
	 * 
	 * @param value int initial value
	 */
	public IntegerBuilder(int value) {
		this.value = value;
	}
	
	/**
	 * Add given {@code plus} to current value
	 * 
	 * @param plus int
	 * @return {@link IntegerBuilder} self
	 */
	public IntegerBuilder add(int plus) {
		value += plus;
		return this;
	}
	
	/**
	 * Remove given {@code minus} from current value
	 * 
	 * @param minus int
	 * @return {@link IntegerBuilder} self
	 */
	public IntegerBuilder remove(int minus) {
		value -= minus;
		return this;
	}
	
	/**
	 * Change current value using given function
	 * 
	 * @param change {@link Function} how will be value changed. Current value is argument for this function
	 * @return {@link IntegerBuilder} self
	 */
	public IntegerBuilder change(Function<Integer, Integer> change) {
		value = change.apply(value);
		return this;
	}
	
	@Override
	public String toString() {
		return value + "";
	}
	
	/**
	 * Get current value
	 * 
	 * @return int current value
	 */
	public int get() {
		return value;
	}
	
	public int getIncrease() {
		return value++;
	}
	
	public int increaseGet() {
		return ++value;
	}
	
	public int getDecrease() {
		return value--;
	}
	
	public int decreaseGet() {
		return --value;
	}
	
}
