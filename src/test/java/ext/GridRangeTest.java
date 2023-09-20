package ext;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import ext.GridRange;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class GridRangeTest {

	@Test
	@Parameters(method = "dataOffsetAndLimit")
	// indexing from 0
	public void testOffsetAndLimit(int count, int index, int size, int limit, int offset) {
		GridRange grid = GridRange.create(count, index, size);
		assertEquals("offset", offset, grid.getOffset());
		assertEquals("limit", limit, grid.getLimit());
	}

	public Object[] dataOffsetAndLimit() {
		return new Object[] { new Object[] { 0, 1, 10, 0, 0 }, new Object[] { 0, 2, 10, 0, 0 },
				new Object[] { 5, 1, 10, 5, 0 }, new Object[] { 10, 1, 10, 10, 0 }, new Object[] { 12, 1, 10, 10, 0 },
				new Object[] { 5, 2, 10, 5, 0 }, new Object[] { 10, 2, 10, 10, 0 }, new Object[] { 15, 2, 10, 5, 10 },
				new Object[] { 25, 2, 10, 10, 10 }, new Object[] { 25, 3, 11, 3, 22 },
				new Object[] { 10, 0, 10, 10, 0 }, new Object[] { 10, -1, 10, 10, 0 },
				new Object[] { 10, 1, 0, 10, 0 },
				new Object[] { 10, 1, -1, 10, 0 },
				new Object[] { 37, 2, 20, 17, 20 } };
	}

}
