package toti.core.answers.action;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import toti.lib.common.structures.Callback;

public interface WebSocket {

	void send(String message) throws IOException;

	void send(byte[] content) throws IOException;

	void sendPing();

	void close();
	
	/**
	 * Indicate if message receiving process is running
	 * 
	 * @return <code>true</code> if websocket can receive message, <code>false</code> if is stopped
	 *  or not start yet
	 */
	boolean isRunning();
	
	/**
	 * Indicate if websocket is finished
	 * 
	 * @return <code>true</code> if websocket was closed, <code>false</code> if reading process is running
	 *  or not start yet
	 */
	boolean isClosed();
	
	public boolean isAccepted();
	
	void accept(
		Callback onOpen,BiConsumer<Boolean, ByteBuffer> onMessage,
		Consumer<Throwable> onError, Consumer<String> onClose
	) throws Exception;

}
