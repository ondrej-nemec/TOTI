package toti.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import toti.core.application.Task;
import toti.core.application.register.MappedAction;
import toti.core.application.register.Param;
import toti.core.extensions.Extension;
import toti.lib.tcpip.enums.HttpMethod;

public class ApplicationTest {

	@Test
	public void testStartStartsOnlyOnce() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		
		Extension ext1 = mock(Extension.class);
		Extension ext2 = mock(Extension.class);
		
		Application application = new Application(
			Arrays.asList(task1, task2),
			null, null, null,
			Arrays.asList(ext1, ext2),
			null, false,
			null, null
		);

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
		Application application = new Application(
			Arrays.asList(task1, task2),
			null, null, null,
			Arrays.asList(), null, false, null, null
		);
		assertTrue(application.start());
		verify(task1, times(1)).start();
		verify(task2, times(1)).start();
		verifyNoMoreInteractions(task1, task2);
	}
	
	@Test
	public void testStartStartExtensions() throws Exception {
		Extension ext1 = mock(Extension.class);
		Extension ext2 = mock(Extension.class);
		
		Application application = new Application(
			Arrays.asList(), null, null, null,
			Arrays.asList(ext1, ext2),
			null, false, null, null
		);
		assertTrue(application.start());

		verify(ext1, times(1)).onApplicationStart();
		verify(ext2, times(1)).onApplicationStart();
		verifyNoMoreInteractions(ext1, ext2);
	}

	@Test
	public void testStartTasksNotStartedIfExtensionFail() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		
		Extension ext1 = mock(Extension.class);
		Extension ext2 = mock(Extension.class);
		
		doThrow(new SQLException()).when(ext1).onApplicationStart();
		
		Application application = new Application(
			Arrays.asList(task1, task2),
			null, null, null,
			Arrays.asList(ext1, ext2),
			null, false, null, null
		);
		
		try {
			application.start();
			fail("Expected exception");
		} catch (SQLException e) {
			// expected
		}

		verify(ext1, times(1)).onApplicationStart();
		verifyNoMoreInteractions(ext1, ext2);
		verifyNoMoreInteractions(task1, task2);
	}
	
	@Test
	public void testStopStopsTasks() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Application application = new Application(
			Arrays.asList(task1, task2),
			null, null, null, Arrays.asList(),
			null, false, null, null
		);
		application.stop();
		verify(task1, times(1)).stop();
		verify(task2, times(1)).stop();
		verifyNoMoreInteractions(task1, task2);
	}
	
	@Test
	public void testStopStopsExtensions() throws Exception {
		Extension ext1 = mock(Extension.class);
		Extension ext2 = mock(Extension.class);
		Application application = new Application(
			Arrays.asList(), null, null, null,
			Arrays.asList(ext1, ext2),
			null, false, null, null
		);
		application.stop();
		
		verify(ext1, times(1)).onApplicationStop();
		verify(ext2, times(1)).onApplicationStop();
		verifyNoMoreInteractions(ext1, ext2);
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
		
		Application application = new Application(Arrays.asList(), root, null, null, null, null, false, null, null);
		
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
