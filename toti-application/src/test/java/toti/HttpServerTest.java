package toti;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.functions.Env;
import ji.common.structures.IntegerBuilder;
import ji.common.structures.MapInit;
import ji.socketCommunication.Server;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.answers.Answer;

@RunWith(JUnitParamsRunner.class)
public class HttpServerTest {

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
		HttpServer toti = spy(new HttpServer(
			server, mock(Env.class), "charset",
			mock(ServerConsumer.class), n->logger, logger
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
		HttpServer toti = new HttpServer(
			server, mock(Env.class), "charset",
			mock(ServerConsumer.class), n->logger, logger
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
		assertEquals(3, callCount.getInteger());
		
		verify(server, times(1)).stop();
		verifyNoMoreInteractions(server);
		//*/
	}

	@Test
	@Parameters({
		"false, false, 0",
		"false, true, 0",
		"true, false, 0",
		"true, true, 1"
	})
	public void testAddApplication(boolean isAppAutoStart, boolean isServerRunning, int times) throws Exception {
		Application app = mock(Application.class);
		when(app.isAutoStart()).thenReturn(isAppAutoStart);
		
		Env env = mock(Env.class);
		when(env.getModule(any())).thenReturn(env);
		Logger logger = mock(Logger.class);
		HttpServer server = spy(new HttpServer(
			mock(Server.class), env, "charset",
			mock(ServerConsumer.class), n->logger, logger
		));
		server.setRunning(isServerRunning);
		doNothing().when(server).startApplication(any());
		
		assertTrue(server.getApplications().isEmpty());
		
		server.addApplication("testId", (e, factory)->{
			return app;
		}, "host", "a1", "a2");
		
		assertEquals(
			MapInit.create().append("testId", app).toMap(),
			server.getApplications()
		);
		verify(server, times(times)).startApplication("testId");
		// called in test
		verify(server, times(1)).setRunning(anyBoolean());
		verify(server, times(2)).getApplications();
		verify(server, times(1)).addApplication(eq("testId"), any(), eq("host"), eq("a1"), eq("a2"));
		verifyNoMoreInteractions(server);
	}

	@Test
	@Parameters({
		"0, false, true, true",
		"0, true, true, true",
		"1, true, true, true",
		"1, false, false, false"
	})
	public void testRemoveApplication(int stopTimes, boolean stopRes, boolean expected, boolean empty) {
		Application app = mock(Application.class);
		Logger logger = mock(Logger.class);
		Env env = mock(Env.class);
		when(env.getModule(any())).thenReturn(env);
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
		HttpServer server = new HttpServer(
			mock(Server.class), env, "charset",
			mock(ServerConsumer.class), n->logger, logger
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
		assertEquals(stopTimes, callCount.getInteger());
		// verify(server, times(stopTimes)).stopApplication("host", app);
		//*/
	}

	@Test
	public void testStartApplication() throws Exception {
		Logger logger = mock(Logger.class);
		ServerConsumer consumer = mock(ServerConsumer.class);
		HttpServer server = new HttpServer(
			mock(Server.class), mock(Env.class), "charset",
			consumer, n->logger, logger
		);
		Answer answer = mock(Answer.class);
		String[] aliases = new String[] {"h1", "h2"};
		Application app = mock(Application.class);
		when(app.getRequestAnswer()).thenReturn(answer);
		when(app.getAliases()).thenReturn(aliases);
		when(app.getHostname()).thenReturn("host");
		
		server.getApplications().put("testId", app);
		
		server.startApplication("testId");
		
		verify(app, times(1)).start();
		verify(app, times(1)).getAliases();
		verify(app, times(1)).getHostname();
		verify(app, times(1)).getRequestAnswer();
		verify(consumer, times(1)).addApplication(answer, "host", aliases);
		verifyNoMoreInteractions(app, consumer);
	}

	@Test
	public void testStopApplicationWorking() throws Exception {
		Logger logger = mock(Logger.class);
		ServerConsumer consumer = mock(ServerConsumer.class);
		HttpServer server = new HttpServer(
			mock(Server.class), mock(Env.class), "charset",
			consumer, n->logger, logger
		);
		Application app = mock(Application.class);
		assertTrue(server.stopApplication("host", app));
		verify(consumer, times(1)).removeApplication("host");
		verify(app, times(1)).stop();
		verifyNoMoreInteractions(app, consumer);
	}

	@Test
	public void testStopApplicationAppThrowsException() throws Exception {
		Logger logger = mock(Logger.class);
		ServerConsumer consumer = mock(ServerConsumer.class);
		HttpServer server = new HttpServer(
			mock(Server.class), mock(Env.class), "charset",
			consumer, n->logger, logger
		);
		Application app = mock(Application.class);
		doThrow(new RuntimeException()).when(app).stop();
		
		assertFalse(server.stopApplication("host", app));
		verify(consumer, times(1)).removeApplication("host");
		verify(app, times(1)).stop();
		verifyNoMoreInteractions(app, consumer);
	}
	
}
