package toti;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import toti.answers.Answer;
import toti.common.structures.IntegerBuilder;
import toti.common.structures.MapInit;
import toti.files.env.Env;
import toti.http.parsers.StreamReader;

public class TotiServerTest {

	@Test
	public void testStart() throws Exception {
		Application app1 = mock(Application.class);
		when(app1.isAutoStart()).thenReturn(true);

		Application app2 = mock(Application.class);
		when(app2.isAutoStart()).thenReturn(true);

		Application app3 = mock(Application.class);
		when(app3.isAutoStart()).thenReturn(false);
		
		Server server = mock(Server.class);
		Logger logger = mock(Logger.class);
		TotiServer toti = spy(new TotiServer(
			server, mock(StreamReader.class), mock(Env.class), "charset", 60000, logger
		));
		doNothing().when(toti).startApplication(any());
		toti.getApplications().put("h1", app1);
		toti.getApplications().put("h2", app2);
		toti.getApplications().put("h3", app3);
		
		toti.start();
		assertTrue(toti.isRunning());
		
		verify(server, times(1)).start();
		verify(toti, times(2)).startApplication(any());
		verify(toti, times(1)).startApplication("h1");
		verify(toti, times(1)).startApplication("h2");
		// called in test
		verify(toti, times(3)).getApplications();
		verify(toti, times(1)).start();
		verify(toti, times(1)).isRunning();
		verifyNoMoreInteractions(toti, server);
	}

	@Test
	public void testEnd() throws Exception {
		Application app1 = mock(Application.class);
		when(app1.isAutoStart()).thenReturn(true);

		Application app2 = mock(Application.class);
		when(app2.isAutoStart()).thenReturn(true);

		Application app3 = mock(Application.class);
		when(app3.isAutoStart()).thenReturn(false);
		
		Server server = mock(Server.class);
		Logger logger = mock(Logger.class);
		/*
		HttpServer toti = spy(new HttpServer(
			server, mock(Env.class), "charset",
			mock(ServerConsumer.class), n->logger, logger
		));
		when(toti.stopApplication(any(), any())).thenReturn(false);
		toti.getApplications().put("h1", app1);
		toti.getApplications().put("h2", app2);
		toti.getApplications().put("h3", app2);
		
		toti.stop();
		assertFalse(toti.isRunning());
		
		verify(server, times(1)).stop();
		verify(toti, times(3)).stopApplication(any(), any());
		verify(toti, times(1)).startApplication("h1", app1);
		verify(toti, times(1)).startApplication("h2", app2);
		verify(toti, times(1)).startApplication("h3", app3);
		verifyNoMoreInteractions(toti, server);
		/*/
		IntegerBuilder callCount = new IntegerBuilder(0);
		TotiServer toti = new TotiServer(
			server, mock(StreamReader.class), mock(Env.class), "charset", 60000, logger
		) {
			@Override
			protected boolean stopApplication(String host, Application application) {
				callCount.add(1);
				return false;
			}
		};
		toti.getApplications().put("h1", app1);
		toti.getApplications().put("h2", app2);
		toti.getApplications().put("h3", app2);
		
		toti.stop();
		assertFalse(toti.isRunning());
		assertEquals(3, callCount.get());
		
		verify(server, times(1)).stop();
		verifyNoMoreInteractions(server);
		//*/
	}

	@ParameterizedTest
	@CsvSource({
		"false, false, 0",
		"false, true, 0",
		"true, false, 0",
		"true, true, 1"
	})
	public void testAddApplication(boolean isAppAutoStart, boolean isServerRunning, int times) throws Exception {
		Application app = mock(Application.class);
		when(app.isAutoStart()).thenReturn(isAppAutoStart);
		
		Env env = mock(Env.class);
		when(env.getSection(any())).thenReturn(env);
		Logger logger = mock(Logger.class);
		TotiServer server = spy(new TotiServer(
			mock(Server.class), mock(StreamReader.class), env, "charset", 60000, logger
		));
		server.setRunning(isServerRunning);
		doNothing().when(server).startApplication(any());
		
		assertTrue(server.getApplications().isEmpty());
		
		server.addApplication("testId", (e, factory)->{
			return app;
		}, Arrays.asList("host"), Arrays.asList("a1", "a2"));
		
		assertEquals(
			MapInit.create().append("testId", app).toMap(),
			server.getApplications()
		);
		verify(server, times(times)).startApplication("testId");
		// called in test
		verify(server, times(1)).setRunning(anyBoolean());
		verify(server, times(2)).getApplications();
		verify(server, times(1)).addApplication(
			eq("testId"), any(), eq(Arrays.asList("host")), eq(Arrays.asList("a1", "a2"))
		);
		verifyNoMoreInteractions(server);
	}

	@ParameterizedTest
	@CsvSource({
		"0, false, true, true",
		"0, true, true, true",
		"1, true, true, true",
		"1, false, false, false"
	})
	public void testRemoveApplication(int stopTimes, boolean stopRes, boolean expected, boolean empty) {
		Application app = mock(Application.class);
		Logger logger = mock(Logger.class);
		Env env = mock(Env.class);
		when(env.getSection(any())).thenReturn(env);
		/*
		HttpServer server = spy(new HttpServer(
			mock(Server.class), env, "charset",
			mock(ServerConsumer.class), n->logger, logger
		));
		if (stopTimes > 0) {
			server.getApplications().put("host", app);
		}
		when(server.stopApplication("host", app)).thenReturn(stopRes);
		
		assertEquals(expected, server.removeApplication("host"));
		assertTrue(server.getApplications().isEmpty());
		verify(server, times(stopTimes)).stopApplication("host", app);
		/*/
		IntegerBuilder callCount = new IntegerBuilder(0);
		TotiServer server = new TotiServer(
			mock(Server.class), mock(StreamReader.class), env, "charset", 60000, logger
		) {
			@Override
			protected boolean stopApplication(String host, Application application) {
				callCount.add(1);
				return stopRes;
			}
		};
		if (stopTimes > 0) {
			server.getApplications().put("host", app);
		}
		
		assertEquals(expected, server.removeApplication("host"));
		assertEquals(empty, server.getApplications().isEmpty());
		assertEquals(stopTimes, callCount.get());
		// verify(server, times(stopTimes)).stopApplication("host", app);
		//*/
	}

	@Test
	public void testStartApplication() throws Exception {
		Logger logger = mock(Logger.class);
		TotiServer server = new TotiServer(
			mock(Server.class), mock(StreamReader.class),
			mock(Env.class), "charset", 60000, logger
		);
		Answer answer = mock(Answer.class);
		Application app = mock(Application.class);
		when(app.getRequestAnswer()).thenReturn(answer);
		when(app.getPaths()).thenReturn(Arrays.asList("h1", "h2"));
		when(app.getHostnames()).thenReturn(Arrays.asList("host"));
		
		server.getApplications().put("testId", app);
		
		server.startApplication("testId");
		
		verify(app, times(1)).start();
		verify(app, times(1)).getPaths();
		verify(app, times(1)).getHostnames();
		verify(app, times(1)).getRequestAnswer();
	}

	@Test
	public void testStopApplicationWorking() throws Exception {
		Logger logger = mock(Logger.class);
		TotiServer server = new TotiServer(
			mock(Server.class), mock(StreamReader.class),
			mock(Env.class), "charset", 60000, logger
		);
		Application app = mock(Application.class);
		assertTrue(server.stopApplication("host", app));
		verify(app, times(1)).stop();
	}

	@Test
	public void testStopApplicationAppThrowsException() throws Exception {
		Logger logger = mock(Logger.class);
		TotiServer server = new TotiServer(
			mock(Server.class), mock(StreamReader.class),
			mock(Env.class), "charset", 60000, logger
		);
		Application app = mock(Application.class);
		doThrow(new RuntimeException()).when(app).stop();
		
		assertFalse(server.stopApplication("host", app));
		verify(app, times(1)).stop();
	}
	
}
