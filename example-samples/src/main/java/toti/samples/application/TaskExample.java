package toti.samples.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import ji.socketCommunication.http.structures.WebSocket;
import toti.application.Task;

public class TaskExample implements Task {
	
	private static ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);
	private Future<?> future;
	
	private String lastMesage = "";
	private int count = 0;
	private WebSocket websocket;
	
	private final Logger logger;
	
	public TaskExample(Logger logger) {
		this.logger = logger;
	}
	
	public void setWebsocket(WebSocket websocket) {
		this.websocket = websocket;
	}
	
	public void removeWebsocket() {
		this.websocket = null;
	}
	
	public BiConsumer<Boolean, ByteArrayOutputStream> onMessage() {
		return (isBinary, message)->{
			String text = new String(message.toByteArray());
			if ("end".equals(text)) {
				websocket.close();
				websocket = null;
				return;
			}
			lastMesage = text;
			try {
				websocket.send("Thank you");
			} catch (IOException e) {
				logger.warn("Sending message fail",e);
			}
		};
	}
	
	public final Consumer<IOException> onError() {
		return (e)->{
			logger.warn("Mesage receiving", e);
		};
	};
	
	@Override
	public void start() throws Exception {
		future = scheduledPool.scheduleWithFixedDelay(()->{
			if (websocket == null) {
				return;
			}
			if (websocket.isClosed()) {
				websocket = null;
				logger.info("Websocket closed by client");
				return;
			}
			if (!websocket.isRunning()) {
				logger.info("Websocket not start yet");
				return;
			}
			try {
				websocket.send("Message " + count + ": " + lastMesage);
			} catch (IOException e) {
				logger.warn("Sending message fail",e);
			}
		}, 0, 5, TimeUnit.SECONDS);
	}

	@Override
	public void stop() throws Exception {
		if (future != null) {
			future.cancel(true);
		}
		scheduledPool.shutdownNow();
	}

}
