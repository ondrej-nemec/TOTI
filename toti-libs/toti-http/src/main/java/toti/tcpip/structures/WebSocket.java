package toti.tcpip.structures;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;

import toti.lib.common.structures.ThrowingCallback;

public class WebSocket implements Session.Listener.AutoDemanding {
	
	private Session session;
	
	private BiConsumer<Boolean, ByteBuffer> onMessage;
	private Consumer<Throwable> onError;
	private Consumer<String> onClose;
	private toti.lib.common.structures.Callback onOpen;
	
	private boolean isAccepted = false;
	
	private final ThrowingCallback<Exception> onAccept;
	
	public WebSocket(ThrowingCallback<Exception> onAccept) {
		this.onAccept = onAccept;
	}

	@Override
	public void onWebSocketOpen(Session session) {
		this.session = session;
		onOpen.call();
	}
	
	@Override
	public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
		onMessage.accept(true, payload);
	}
	
	@Override
	public void onWebSocketText(String message) {
		onMessage.accept(false, ByteBuffer.wrap(message.getBytes()));
	}
	
	@Override
	public void onWebSocketError(Throwable cause) {
		onError.accept(cause);
	}
	
	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		onClose.accept(reason);
	}

	public void send(String message) throws IOException {
		if (!session.isOpen()) {
			return;
		}
		this.session.sendText(message, Callback.NOOP);
	}

	public void send(byte[] content) throws IOException {
		if (!session.isOpen()) {
			return;
		}
		this.session.sendBinary(ByteBuffer.wrap(content), Callback.NOOP);
	}

	public void sendPing() {
		if (!session.isOpen()) {
			return;
		}
		this.session.sendPing(ByteBuffer.wrap(new byte[]{1}), Callback.NOOP);
	}

	public void close() {
		session.close();
		// session.disconnect();
	}
	
	/**
	 * Indicate if message receiving process is running
	 * 
	 * @return <code>true</code> if websocket can receive message, <code>false</code> if is stopped
	 *  or not start yet
	 */
	public boolean isRunning() {
		return session != null && session.isOpen();
	}
	
	/**
	 * Indicate if websocket is finished
	 * 
	 * @return <code>true</code> if websocket was closed, <code>false</code> if reading process is running
	 *  or not start yet
	 */
	public boolean isClosed() {
		return session != null && !session.isOpen();
	}
	
	public boolean isAccepted() {
		return isAccepted;
	}
	
	public void accept(
		toti.lib.common.structures.Callback onOpen,
		BiConsumer<Boolean, ByteBuffer> onMessage,
		Consumer<Throwable> onError,
		Consumer<String> onClose
	) throws Exception {
		this.onOpen = onOpen;
		this.onError = onError;
		this.onMessage = onMessage;
		this.onClose = onClose;
		this.onAccept.call();
		this.isAccepted = true;
	}

}
