package toti;

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

import org.junit.Test;

import ji.database.Database;
import toti.application.Task;

public class ApplicationTest {

	@Test
	public void testStartStartsOnlyOnce() throws Exception {
		Task task1 = mock(Task.class);
		Task task2 = mock(Task.class);
		Database database = mock(Database.class);
		
		Application application = new Application(Arrays.asList(task1, task2), null, database, null, null, null, null, false);

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
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, null, false);
		assertTrue(application.start());
		verify(task1, times(1)).start();
		verify(task2, times(1)).start();
		verifyNoMoreInteractions(task1, task2);
	}
	
	@Test
	public void testStartCreateAndMigrateDatabase() throws Exception {
		Database database = mock(Database.class);
		Application application = new Application(Arrays.asList(), null, database, null, null, null, null, false);
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
		
		Application application = new Application(Arrays.asList(task1, task2), null, database, null, null, null, null, false);
		
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
		
		Application application = new Application(Arrays.asList(task1, task2), null, database, null, null, null, null, false);
		
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
		Application application = new Application(Arrays.asList(task1, task2), null, null, null, null, null, null, false);
		application.stop();
		verify(task1, times(1)).stop();
		verify(task2, times(1)).stop();
		verifyNoMoreInteractions(task1, task2);
	}
}
