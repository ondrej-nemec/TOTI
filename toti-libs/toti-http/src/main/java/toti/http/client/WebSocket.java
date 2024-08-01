package toti.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


import ji.common.structures.Tuple2;
import toti.http.streams.OutputStreamWrapper;

// https://tools.ietf.org/html/rfc6455#section-5.5.1
// https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers#format
// https://stackoverflow.com/questions/18368130/how-to-parse-and-validate-a-websocket-frame-in-java
public class WebSocket {

	private final OutputStreamWrapper os;
	private final InputStream is;
	private Boolean closed;
	
	private BiConsumer<Boolean, ByteArrayOutputStream> onMessage;
	private Consumer<IOException> onError;
	private Consumer<String> onClose;
	
	private final String key;
	private final String origin;
	
	public WebSocket(OutputStreamWrapper os, InputStream is, Exchange exchange) {
		this.os = os;
		this.is = is;
		this.closed = null;
		
		this.key = exchange.getHeader("Sec-WebSocket-Key") + "";
		this.origin = exchange.getHeader("Origin").toString();
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public String getKey() {
		return key;
	}
	
	public void close() {
		this.closed = true;
	}

	public void send(String message) throws IOException {
		send(message.getBytes(), false);
	}

	public void send(byte[] content) throws IOException {
		send(content, true);
	}
	
	/**
	 * Indicate if message receiving process is running
	 * 
	 * @return <code>true</code> if websocket can receive message, <code>false</code> if is stopped
	 *  or not start yet
	 */
	public boolean isRunning() {
		return closed != null && !closed;
	}
	
	/**
	 * Indicate if websocket is finished
	 * 
	 * @return <code>true</code> if websocket was closed, <code>false</code> if reading process is running
	 *  or not start yet
	 */
	public boolean isClosed() {
		return closed != null && closed;
	}
	
	/**
	 * Indicate if user accept the websocket
	 * <p>
	 * Internal
	 * 
	 * @return <code>true</code> if user set <code>onMessage</code>
	 */
	public boolean isAccepted() {
		return onMessage != null || onError != null;
	}
	
	public void accept(BiConsumer<Boolean, ByteArrayOutputStream> onMessage, Consumer<IOException> onError, Consumer<String> onClose) {
		this.onError = onError;
		this.onMessage = onMessage;
		this.onClose = onClose;
	}
	
	public void waitOnMessages() {
		closed = false;
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		Boolean isBinary = null;
	    while(!closed) { // code != 8 && 
	    	try {
	    		ByteArrayOutputStream b = new ByteArrayOutputStream();
	    		// first read one byte, then get available
	    		int i = is.read(); // block until some data available or timeout
	    		while (i > -1 && is.available() > 0) {
	    			b.write(i);
	    			i = is.read();
	    		}
    			b.write(i);
	    		if (i == -1) {
	    			closed = true;
	    		}
		   		if (b.size() > 0) {
		   			Tuple2<Boolean, Integer> r = parse(b.toByteArray(), message);
		   			switch (r._2()) {
			   			case 0:
			   				// ignore
			   				break;
			   			case 1:
			   				isBinary = false;
			   				break;
			   			case 2:
			   				isBinary= true;
			   				break;
			   			case 8:
			   				closed = true;
			   				isBinary = null;
			   				break;
			   			case 9:
			   				send(message.toByteArray(), null);
			   				message = new ByteArrayOutputStream();
			   				isBinary = null;
				    		break;
				    	// other are ignored
					}
		   			
		   			if (r._1() && isBinary != null) {
		   				onMessage.accept(isBinary, message);
		   				message = new ByteArrayOutputStream();
		   				isBinary = null;
		   			}
		   			
		   		}
			} catch (IOException e) {
				if (!closed) {
					onError.accept(e);
				}
			}
	    }
	    closed = true;
		onClose.accept(new String(message.toByteArray()));
	}
	
    /* this is how and header should be made:
     *   - first byte  -> FIN + RSV1 + RSV2 + RSV3 + OPCODE
     *   - second byte -> MASK + payload length (only 7 bits)
     *   - third, fourth, fifth and sixth bytes -> (optional) XOR encoding key bytes
     *   - following bytes -> the encoded (if a key has been used) payload
     *
     *   FIN    [1 bit]      -> 1 if the whole message is contained in this frame, 0 otherwise
     *   RSVs   [1 bit each] -> MUST be 0 unless an extension is negotiated that defines meanings for non-zero values
     *   OPCODE [4 bits]     -> defines the interpretation of the carried payload
     *
     *   MASK           [1 bit]  -> 1 if the message is XOR masked with a key, 0 otherwise
     *   payload length [7 bits] -> can be max 1111111 (127 dec), so, the payload cannot be more than 127 bytes per frame
     *
     * valid OPCODES:
     *   - 0000 [0]             -> continuation frame
     *   - 0001 [1]             -> text frame
     *   - 0010 [2]             -> binary frame
     *   - 0011 [3] to 0111 [7] -> reserved for further non-control frames
     *   - 1000 [8]             -> connection close
     *   - 1001 [9]             -> ping
     *   - 1010 [A]             -> pong
     *   - 1011 [B] to 1111 [F] -> reserved for further control frames
     */
	// https://stackoverflow.com/questions/45015525/c-sharp-websocket-create-masked-frame/67247742#67247742
	private void send(byte[] content, Boolean binary) throws IOException {
		if (os.isClosed()) {
			closed = true;
			throw new IOException("Connection is closed or not opened yet.");
		}
		if (closed == null || closed) {
			throw new IOException("Connection is closed or not opened yet.");
		}
		int maxFrameSize = 125;
		for (int i = 0; i < content.length; i+=maxFrameSize) {
			int len = Math.min(maxFrameSize, content.length - i);
			boolean last = len < maxFrameSize;
			boolean first = i == 0;
			byte fByte = 0x0;
			if (last) { // (last && first) || 
				fByte += 0x80; // 1000 XXXX
			}
			if (first) { // (last && first) || 
				if (binary == null) { // PONG
					fByte += 0x0A; // XXX 0010
				} else if (binary) {
					fByte += 0x2; // XXXX 0002
				} else {
					fByte += 0x1; // XXXX 0001
				}
			}
			byte sByte = (byte)(0x0 + len); // 0x0 i mask - not masked
			os.write(new byte[] {fByte, sByte});
		    os.write(content, i, len);
			os.flush();
		}
	}
	
	// https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers
	// https://stackoverflow.com/questions/18368130/how-to-parse-and-validate-a-websocket-frame-in-java
	protected Tuple2<Boolean, Integer> parse(byte raw[], ByteArrayOutputStream output) throws IOException {
        // easier to do this via ByteBuffer
        ByteBuffer buf = ByteBuffer.wrap(raw);

        // Fin + RSV + OpCode byte
        byte b = buf.get();
        boolean fin = ((b & 0x80) != 0);
   //     System.err.println("FIN: " + fin);
        /*
        // RSV 1-3 are ignored, they are for extensions
        boolean rsv1 = ((b & 0x40) != 0);
        boolean rsv2 = ((b & 0x20) != 0);
        boolean rsv3 = ((b & 0x10) != 0);
        */
        byte opcode = (byte)(b & 0x0F);
        // 0 - continuation
        // 1 - text
        // 2 - binary
        // 8 - end
        // Masked + Payload Length
        b = buf.get();
        boolean masked = ((b & 0x80) != 0);
        if (!masked) {
        	System.err.println("TODO fail: not masked");
        }
        int payloadLength = (byte)(0x7F & b);
        int byteCount = 0;
        if (payloadLength == 0x7F) { // 127
            // 8 byte extended payload length
            byteCount = 8;
        } else if (payloadLength == 0x7E) { // 126
            // 2 bytes extended payload length
            byteCount = 2;
        }
        // Decode Payload Length
        while (--byteCount > 0) {
            b = buf.get();
            payloadLength |= (b & 0xFF) << (8 * byteCount);
        }
        // TODO: add control frame payload length validation here
        byte maskingKey[] = null;
        if (masked) {
            // Masking Key
            maskingKey = new byte[4];
            buf.get(maskingKey,0,4);
        }
        // TODO: add masked + maskingkey validation here
        // Payload itself
        byte[] payload = new byte[payloadLength];
        buf.get(payload,0,payloadLength);

        // Demask (if needed)
        if (masked) {
            for (int i = 0; i < payload.length; i++) {
                payload[i] ^= maskingKey[i % 4];
            }
        }
        output.write(payload);
        return new Tuple2<>(fin, (int)opcode);
    }
	
}
