package ji.common.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class IntegerBuilderTest {

	@Test
	public void testConstructorAndGetter() {
		IntegerBuilder ib = new IntegerBuilder(42);
		
		assertEquals(42, ib.get());
		// second test to verify no change
		assertEquals(42, ib.get());
	}
	
	@ParameterizedTest
	@CsvSource({"0, 42", "10, 52", "-10, 32"})
	public void testAdd(int add, int expected) {
		IntegerBuilder ib = new IntegerBuilder(42);
		ib.add(add);
		assertEquals(expected, ib.get());
	}
	
	@ParameterizedTest
	@CsvSource({"0, 42", "10, 32", "-10, 52"})
	public void testRemove(int remove, int expected) {
		IntegerBuilder ib = new IntegerBuilder(42);
		ib.remove(remove);
		assertEquals(expected, ib.get());
	}
	
	@Test
	public void testChange() {
		IntegerBuilder ib = new IntegerBuilder(42);
		ib.change((origin)->{
			assertEquals(42, origin);
			return 10;
		});
		assertEquals(10, ib.get());
	}
	
	@Test
	public void testGetIncrease() {
		IntegerBuilder ib = new IntegerBuilder(10);
		
		assertEquals(10, ib.get());
		
		assertEquals(10, ib.getIncrease());
		assertEquals(11, ib.get());
		
		assertEquals(11, ib.getIncrease());
		assertEquals(12, ib.get());
	}
	
	@Test
	public void testIncreaseGet() {
		IntegerBuilder ib = new IntegerBuilder(10);
		
		assertEquals(10, ib.get());
		
		assertEquals(11, ib.increaseGet());
		assertEquals(11, ib.get());
		
		assertEquals(12, ib.increaseGet());
		assertEquals(12, ib.get());
	}
	
	@Test
	public void testGetDecrease() {
		IntegerBuilder ib = new IntegerBuilder(10);
		
		assertEquals(10, ib.get());
		
		assertEquals(10, ib.getDecrease());
		assertEquals(9, ib.get());
		
		assertEquals(9, ib.getDecrease());
		assertEquals(8, ib.get());
	}
	
	@Test
	public void testDecreaseGet() {
		IntegerBuilder ib = new IntegerBuilder(10);
		
		assertEquals(10, ib.get());
		
		assertEquals(9, ib.decreaseGet());
		assertEquals(9, ib.get());
		
		assertEquals(8, ib.decreaseGet());
		assertEquals(8, ib.get());
	}
	
}
