package toti;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ji.socketCommunication.http.HttpMethod;
import toti.application.Task;
import toti.application.register.MappedAction;
import toti.application.register.Param;

public class ApplicationTest {

	@Test
	public void testStartStartsOnlyOnce() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, false, null);

		assertFalse(application.isRunning());
		assertTrue(application.start()); // start
		assertTrue(application.isRunning());
		
		verify(task1, times(1)).start();
		verify(task2, times(1)).start();

		assertFalse(application.start()); // second start fails
		assertTrue(application.isRunning());
	}
	
	@Test
	public void testStartStartsTasks() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, false, null);
		assertTrue(application.start());
		verify(task1, times(1)).start();
		verify(task2, times(1)).start();
		verifyNoMoreInteractions(task1, task2);
		
		fail("verify extensions");
	}
	
	@Test
	public void testStartStartExtensions() throws Exception {
		Application application = new Application(Arrays.asList(), null, null, null, null, null, false, null);
		assertTrue(application.start());

		fail("TODO");
	}

	@Test
	public void testStartTasksNotStartedIfExtensionFail() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, false, null);
		
		try {
			application.start();
			fail("Expected exception");
		} catch (SQLException e) {
			// expected
		}
		
		fail("TODO");
	}
	
	@Test
	public void testStopStopsTasks() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, false, null);
		application.stop();
		verify(task1, times(1)).stop();
		verify(task2, times(1)).stop();
		verifyNoMoreInteractions(task1, task2);
	}
	
	@Test
	public void testStopStopsExtensions() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, false, null);
		application.stop();
		fail("TODO");
	}
	
	@Test
	public void testIterate() {
		Param root = new Param(null);
		
		Param module = root.addChild("module");
		Param controller = module.addChild("controller");
		controller.addAction(HttpMethod.GET, MappedAction.test("module", "list", "GET"));
		
		Param method1 = controller.addChild("method1");
		method1.addAction(HttpMethod.POST, MappedAction.test("module", "method1", "POST"));
		method1.addAction(HttpMethod.DELETE, MappedAction.test("module", "method1", "DELETE"));
		
		Param extra = root.addChild("extra");
		
		Param param = extra.addChild(null);
		param.addAction(HttpMethod.POST, MappedAction.test("extra", "x", "GET"));
		
		Param param1 = param.addChild("string");
		param1.addAction(HttpMethod.GET, MappedAction.test("extra", "string", "GET"));
		
		Param index = extra.addChild("index");
		index.addAction(HttpMethod.GET, MappedAction.test("extra", "index", "GET"));
		
		Param indexParam = index.addChild(null);
		indexParam.addAction(HttpMethod.GET, MappedAction.test("extra", "indexParam", "GET"));
		indexParam.addAction(HttpMethod.POST, MappedAction.test("extra", "indexParam", "POST"));
		
		Application application = new Application(Arrays.asList(), root, null, null, null, null, false, null);
		
		List<String> actual = new LinkedList<>();
		application.iterate((item)->{
			actual.add(item.getMethod() + " " + item.getUri());
		});
		List<String> expected = Arrays.asList(
			"GET /module/controller",
			"DELETE /module/controller/method1",
			"POST /module/controller/method1",
			"GET /extra/index",
			"GET /extra/index/{}",
			"POST /extra/index/{}",
			"POST /extra/{}",
			"GET /extra/{}/string"
		);
		
		try {
			assertEquals(expected, actual);
		} catch (Throwable t) {
			assertEquals(expected.toString(), actual.toString());
			throw t;
		}
	}
	
}
