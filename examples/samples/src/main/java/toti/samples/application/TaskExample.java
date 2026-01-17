package toti.samples.application;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;

import toti.application.Task;
import toti.tcpip.structures.WebSocket;

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
	
	public BiConsumer<Boolean, ByteBuffer> onMessage() {
		return (isBinary, message)->{
			String text = new String(message.array());
			if ("end".equals(text)) {
				websocket.close();
				websocket = null;
				return;
			}
			lastMesage = text;
			count++;
			try {
				websocket.send("Thank you");
			} catch (IOException e) {
				logger.warn("Sending message fail",e);
			}
		};
	}
	
	public final Consumer<Throwable> onError() {
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
