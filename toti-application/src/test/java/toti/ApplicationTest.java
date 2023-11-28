package toti;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import toti.application.Task;
import toti.application.register.MappedAction;
import toti.application.register.Param;

public class ApplicationTest {

	@Test
	public void testStartStartsOnlyOnce() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Database database = mock(Database.class);
		
		Application application = new Application(Arrays.asList(task1, task2), null, null, database, null, null, null, null, false);

		assertFalse(application.isRunning());
		assertTrue(application.start()); // start
		assertTrue(application.isRunning());
		
		verify(database, times(1)).createDbIfNotExists();
		verify(database, times(1)).migrate();
		verify(task1, times(1)).start();
		verify(task2, times(1)).start();
		verifyNoMoreInteractions(task1, task2, database);

		assertFalse(application.start()); // second start fails
		assertTrue(application.isRunning());
		
		verifyNoMoreInteractions(task1, task2, database);
	}
	
	@Test
	public void testStartStartsTasks() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, null, null, false);
		assertTrue(application.start());
		verify(task1, times(1)).start();
		verify(task2, times(1)).start();
		verifyNoMoreInteractions(task1, task2);
	}
	
	@Test
	public void testStartCreateAndMigrateDatabase() throws Exception {
		Database database = mock(Database.class);
		Application application = new Application(Arrays.asList(), null, null, database, null, null, null, null, false);
		assertTrue(application.start());
		
		verify(database, times(1)).createDbIfNotExists();
		verify(database, times(1)).migrate();
		verifyNoMoreInteractions(database);
	}

	@Test
	public void testStartTasksNotStartedIfCreateDbFail() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Database database = mock(Database.class);
		doThrow(new SQLException()).when(database).createDbIfNotExists();
		
		Application application = new Application(Arrays.asList(task1, task2), null, null, database, null, null, null, null, false);
		
		try {
			application.start();
			fail("Expected exception");
		} catch (SQLException e) {
			// expected
		}
		verify(database).createDbIfNotExists();
		verifyNoMoreInteractions(task1, task2, database);
	}

	@Test
	public void testStartTasksNotStartedIfMigrateFail() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Database database = mock(Database.class);
		doThrow(new SQLException()).when(database).migrate();
		
		Application application = new Application(Arrays.asList(task1, task2), null, null, database, null, null, null, null, false);
		
		try {
			application.start();
			fail("Expected exception");
		} catch (SQLException e) {
			// expected
		}
		verify(database).migrate();
		verify(database).createDbIfNotExists();
		verifyNoMoreInteractions(task1, task2, database);
	}
	
	@Test
	public void testStopStopsTasks() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, null, null, false);
		application.stop();
		verify(task1, times(1)).stop();
		verify(task2, times(1)).stop();
		verifyNoMoreInteractions(task1, task2);
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
		
		Application application = new Application(Arrays.asList(), null, root, null, null, null, null, null, false);
		
		List<String> actual = new LinkedList<>();
		application.iterate((item)->{
			actual.add(item.getMethod() + " " + item.getUri());
		});
		assertEquals(Arrays.asList(
			"GET /module/controller",
			"POST /module/controller/method1",
			"DELETE /module/controller/method1",
			"GET /extra/index",
			"POST /extra/index/{}",
			"GET /extra/index/{}",
			"POST /extra/{}",
			"GET /extra/{}/string"
		), actual);
	}
	
}
