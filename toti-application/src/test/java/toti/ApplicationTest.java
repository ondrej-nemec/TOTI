package toti;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

public class ApplicationTest {

	public void testStartStarts() throws Exception {
		Application application = new Application(Arrays.asList(), null, null, null, null, null, null, false);
		assertFalse(application.isRunning());
		assertTrue(application.start()); // start
		assertTrue(application.isRunning());
		assertFalse(application.start()); // second start fails
		assertTrue(application.isRunning());
	}
	
	public void test() {
		// start: run database, all tasks, if throw in database, task
		// stop
		fail("TODO");
	}
	
}
